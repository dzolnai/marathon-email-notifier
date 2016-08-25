package daniel.zolnai.marathon.storage

import java.nio.charset.StandardCharsets

import daniel.zolnai.marathon.service.ZooKeeperService

/**
  * Storage implementation for saving on ZooKeeper.
  * Created by Daniel Zolnai on 2016-07-09.
  */
class ZookeeperStorage(zooKeeperService: ZooKeeperService) extends Storage {

  override def saveToFile(path: String, content: String): Unit = {
    zooKeeperService.saveToFile(path, content.getBytes(StandardCharsets.UTF_8))
  }

  override def getFileContents(path: String): Option[String] = {
    val byteContents = zooKeeperService.readFile(path)
    if (byteContents.isEmpty) {
      return None
    }
    Some(new String(byteContents.get, StandardCharsets.UTF_8))
  }
}
