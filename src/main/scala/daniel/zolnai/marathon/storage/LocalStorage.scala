package daniel.zolnai.marathon.storage

/**
  * Implementation of the storage interface with the local file system storage as the medium.
  * Created by Daniel Zolnai on 2016-07-04.
  */
class LocalStorage(val workingDirectory: String) extends Storage{

  override def saveToFile(path: String, content: String): Unit = ???

  override def getFileContents(path: String): String = ???
}
