package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.service.ZooKeeperService.ConnectCallback
import daniel.zolnai.marathon.zookeeper.NodeWatcher
import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper.{CreateMode, ZooKeeper}

import scala.collection.JavaConverters._

/**
  * Service which is responsible for all the communication with ZooKeeper.
  * Created by Daniel Zolnai on 2016-07-09.
  */
object ZooKeeperService {
  trait ConnectCallback {
    def becomeLeader()
  }
}
class ZooKeeperService(configService: ConfigService) {

  val enabled = configService.appConfig.zooKeeperURL.isDefined

  private var _zooKeeperUrl: String = _
  private var _zooKeeperPath: String = _
  private var _watcher: NodeWatcher = _
  private var _zooKeeperClient: ZooKeeper = _

  private var _connectCallback: ConnectCallback = _


  /**
    * Connects ZooKeeper to the cluster, and starts monitoring if it can become a leader.
    *
    * @return True if the connection happened, false if the application is not running in HA mode.
    */
  def connectIfRequired(connectCallback: ConnectCallback): Boolean = {
    if (enabled) {
      _connectCallback = connectCallback
      splitPathFromZooKeeperUrl(configService.appConfig.zooKeeperURL.get) match {
        case (zooKeeperUrl, zooKeeperPath) => _zooKeeperUrl = zooKeeperUrl; _zooKeeperPath = zooKeeperPath
        case _ =>
      }
      _watcher = new NodeWatcher(this, _zooKeeperPath)
      _zooKeeperClient = new ZooKeeper(_zooKeeperUrl, 3000, _watcher)
      _watcher.init()
    }
    enabled
  }

  /**
    * Split the ZooKeeper specific URL to a hostname and port combination and the path from the ZooKeeper root.
    *
    * @param zookeeperUrlWithPath The string to split.
    * @return The URL and path as a tuple.
    */
  def splitPathFromZooKeeperUrl(zookeeperUrlWithPath: String): (String, String) = {
    if (zookeeperUrlWithPath.indexOf("/") > 0) {
      val splitAt = zookeeperUrlWithPath.indexOf("/")
      zookeeperUrlWithPath.splitAt(splitAt)
    } else {
      (zookeeperUrlWithPath, "/")
    }
  }

  /**
    * This method is called internally when this instance of the application becomes the leader.
    */
  def becomeLeader() = {
    _connectCallback.becomeLeader()
  }

  /**
    * Starts watching a node with the internal watcher.
    *
    * @param path The path to monitor.
    */
  def watchNode(path: String) = {
    val nodeStat = _zooKeeperClient.exists(path, _watcher)
    if (nodeStat == null) {
      throw new IllegalStateException("Unable to monitor node path!")
    }
  }

  /**
    * Creates a new node without any content.
    *
    * @param path      The path to create the node at.
    * @param ephemeral True if it should be ephemeral, otherwise it is persistent.
    * @return The path to the created node.
    */
  def createNode(path: String, ephemeral: Boolean): String = {
    val nodeStat = _zooKeeperClient.exists(path, false)
    val createMode = if (ephemeral) CreateMode.EPHEMERAL_SEQUENTIAL else CreateMode.PERSISTENT
    if (nodeStat == null) {
      _zooKeeperClient.create(path, new Array[Byte](0), Ids.OPEN_ACL_UNSAFE, createMode)
    } else {
      path
    }
  }

  /**
    * Checks if a node exists on ZooKeeper.
    *
    * @param path The path to the node to check.
    * @return True if it exists, false if not.
    */
  def nodeExists(path: String): Boolean = {
    val nodeStat = _zooKeeperClient.exists(path, false)
    nodeStat != null
  }

  /**
    * Lists the children of a ZooKeeper node.
    *
    * @param path The path to the node of which the children have to be listed.
    * @return The children in a list.
    */
  def listChildren(path: String): List[String] = {
    _zooKeeperClient.getChildren(path, false).asScala.sorted.toList
  }

}
