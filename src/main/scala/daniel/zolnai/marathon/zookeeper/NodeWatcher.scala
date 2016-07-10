package daniel.zolnai.marathon.zookeeper

import daniel.zolnai.marathon.service.ZooKeeperService
import org.apache.zookeeper.Watcher.Event.EventType
import org.apache.zookeeper.{WatchedEvent, Watcher}

/**
  * Created by Daniel Zolnai on 2016-07-09.
  */
class NodeWatcher(service: ZooKeeperService, zooKeeperPath: String) extends Watcher {

  val ZOOKEEPER_PATH = if (!zooKeeperPath.endsWith("/")) {
    zooKeeperPath
  } else {
    zooKeeperPath.substring(0, zooKeeperPath.length - 1)
  }

  val ELECTION_PATH = s"${ZOOKEEPER_PATH}election"
  val NODE_PREFIX = "/node_"

  private var OWN_NODE: String = _
  private var WATCHED_NODE: String = _

  def init() {
    // Make sure the root path is created
    service.createNode(ZOOKEEPER_PATH, ephemeral = false)
    // Create the path for the election root, if needed
    service.createNode(ELECTION_PATH, ephemeral = false)
    // Now create a sequential node with our prefix
    OWN_NODE = service.createNode(s"$ELECTION_PATH$NODE_PREFIX", ephemeral = true)
    // Check which node we need to monitor
    checkIfLeader()
  }


  def checkIfLeader() = {
    val children = service.listChildren(ELECTION_PATH)
    // The children are sorted in increasing order
    if (children.isEmpty) {
      // Should not happen, we created a node at startup
      throw new IllegalStateException("Creation of nodes in ZooKeeper unsuccessful!")
    }
    // Check if the first one is the same as our node
    val ownNodeFilename = OWN_NODE.substring(OWN_NODE.lastIndexOf("/") + 1, OWN_NODE.length)
    val ownIndex = children.indexOf(ownNodeFilename)
    if (ownIndex == 0) {
      // We are the first in the list, so we are the new leader
      service.becomeLeader()
    } else {
      // Start watching the node before ours, if that one gets deleted, it is probably
      // us who will become the leader.
      WATCHED_NODE = s"$ELECTION_PATH/${children(ownIndex - 1)}"
      service.watchNode(WATCHED_NODE)
    }
  }

  override def process(event: WatchedEvent): Unit = {
    if (event.getType == EventType.NodeDeleted) {
      if (event.getPath == WATCHED_NODE) {
        checkIfLeader()
      }
    }
  }
}
