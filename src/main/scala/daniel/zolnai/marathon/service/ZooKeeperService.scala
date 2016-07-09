package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.zookeeper.NodeWatcher
import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper.{CreateMode, ZooKeeper}

import scala.collection.JavaConverters._

/**
  * Created by Daniel Zolnai on 2016-07-09.
  */
class ZooKeeperService(zookeeperUrl: String, zooKeeperPath: String) {

  val _watcher = new NodeWatcher(this, zooKeeperPath)
  val _zkClient = new ZooKeeper(zookeeperUrl, 3000, _watcher)


  def becomeLeader() = {
    // TODO activate ourselves
  }

  def watchNode(path: String) = {
    val nodeStat =_zkClient.exists(path, _watcher)
    if (nodeStat == null) {
      throw new IllegalStateException("Unable to monitor node path!")
    }
  }

  def createNode(path: String, ephemeral: Boolean) : String = {
    val nodeStat = _zkClient.exists(path, false)
    val createMode =  if (ephemeral) CreateMode.EPHEMERAL_SEQUENTIAL else CreateMode.PERSISTENT
      if (nodeStat == null) {
        _zkClient.create(path, new Array[Byte](0), Ids.OPEN_ACL_UNSAFE, createMode)
      } else {
        path
      }
  }

  def listChildren(path: String): List[String] = {
    _zkClient.getChildren(path, false).asScala.sorted.toList
  }

}
