package daniel.zolnai.marathon.storage

/**
  * Created by Daniel Zolnai on 2016-07-09.
  */
class ZookeeperStorage(zooKeeperUrl: String) extends Storage{
  override def saveToFile(path: String, content: String): Unit = ???

  override def getFileContents(path: String): String = ???
}
