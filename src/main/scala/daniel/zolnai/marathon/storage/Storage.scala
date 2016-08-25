package daniel.zolnai.marathon.storage

/**
  * Interface for saving and restoring application contents.
  * Created by Daniel Zolnai on 2016-07-03.
  */
trait Storage {

  /**
    * Saves a content string to a file.
    *
    * @param path    The relative path to save to. Absolute paths can not be used (although you can set the working
    *                directory to the root path).
    * @param content The contents to save in the file.
    */
  def saveToFile(path: String, content: String)

  /**
    * Retrieves the contents of a file.
    *
    * @param path The relative path to the file.
    * @return The contents of the file as a string. None if the file does not exist.
    */
  def getFileContents(path: String): Option[String]

}
