package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.zookeeper.NodeWatcher
import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper.{CreateMode, ZooKeeper}

import scala.collection.JavaConverters._

/**
  * Service which is responsible for all the communication with ZooKeeper.
  * Created by Daniel Zolnai on 2016-07-09.
  */
class ZooKeeperService(zooKeeperUrl: String, zooKeeperPath: String) {

  val _watcher = new NodeWatcher(this, zooKeeperPath)
  val _zkClient = new ZooKeeper(zooKeeperUrl, 3000, _watcher)
  _watcher.init()


  def becomeLeader() = {
    // TODO activate emailing
  }

  def watchNode(path: String) = {
    val nodeStat = _zkClient.exists(path, _watcher)
    if (nodeStat == null) {
      throw new IllegalStateException("Unable to monitor node path!")
    }
  }

  def createNode(path: String, ephemeral: Boolean): String = {
    val nodeStat = _zkClient.exists(path, false)
    val createMode = if (ephemeral) CreateMode.EPHEMERAL_SEQUENTIAL else CreateMode.PERSISTENT
    if (nodeStat == null) {
      _zkClient.create(path, new Array[Byte](0), Ids.OPEN_ACL_UNSAFE, createMode)
    } else {
      path
    }
  }

  def nodeExists(path: String): Boolean = {
    val nodeStat = _zkClient.exists(path, false)
    nodeStat != null
  }

  def listChildren(path: String): List[String] = {
    _zkClient.getChildren(path, false).asScala.sorted.toList
  }

}
