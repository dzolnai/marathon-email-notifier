package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.zookeeper.NodeWatcher
import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper.{CreateMode, ZooKeeper}

import scala.collection.JavaConverters._

/**
  * Service which is responsible for all the communication with ZooKeeper.
  * Created by Daniel Zolnai on 2016-07-09.
  */
class ZooKeeperService(configService: ConfigService) {

  val enabled = configService.appConfig.zooKeeperURL.isDefined

  private var _zooKeeperUrl: String = _
  private var _zooKeeperPath: String = _
  private var _watcher: NodeWatcher = _
  private var _zooKeeperClient: ZooKeeper = _

  /**
    * Connects ZooKeeper to the cluster, and starts monitoring if it can become a leader.
    *
    * @return True if the connection happened, false if the application is not running in HA mode.
    */
  def connectIfRequired(): Boolean = {
    if (enabled) {
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

  // TODO add comments here and below
  def splitPathFromZooKeeperUrl(zookeeperUrlWithPath: String): (String, String) = {
    if (zookeeperUrlWithPath.indexOf("/") > 0) {
      val splitAt = zookeeperUrlWithPath.indexOf("/")
      zookeeperUrlWithPath.splitAt(splitAt)
    } else {
      (zookeeperUrlWithPath, "/")
    }
  }

  def becomeLeader() = {
    // TODO activate emailing and subscribe to the stream
  }

  def watchNode(path: String) = {
    val nodeStat = _zooKeeperClient.exists(path, _watcher)
    if (nodeStat == null) {
      throw new IllegalStateException("Unable to monitor node path!")
    }
  }

  def createNode(path: String, ephemeral: Boolean): String = {
    val nodeStat = _zooKeeperClient.exists(path, false)
    val createMode = if (ephemeral) CreateMode.EPHEMERAL_SEQUENTIAL else CreateMode.PERSISTENT
    if (nodeStat == null) {
      _zooKeeperClient.create(path, new Array[Byte](0), Ids.OPEN_ACL_UNSAFE, createMode)
    } else {
      path
    }
  }

  def nodeExists(path: String): Boolean = {
    val nodeStat = _zooKeeperClient.exists(path, false)
    nodeStat != null
  }

  def listChildren(path: String): List[String] = {
    _zooKeeperClient.getChildren(path, false).asScala.sorted.toList
  }

}
