package daniel.zolnai.marathon.storage

import daniel.zolnai.marathon.service.ZooKeeperService

/**
  * Created by Daniel Zolnai on 2016-07-09.
  */
class ZookeeperStorage(zooKeeperService: ZooKeeperService) extends Storage{

  override def saveToFile(path: String, content: String): Unit = ???

  override def getFileContents(path: String): String = ???
}
