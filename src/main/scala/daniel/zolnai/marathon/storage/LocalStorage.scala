package daniel.zolnai.marathon.storage

import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/**
  * Implementation of the storage interface with the local file system storage as the medium.
  * Created by Daniel Zolnai on 2016-07-04.
  */
class LocalStorage(val workingDirectory: String) extends Storage{

  override def saveToFile(path: String, content: String): Unit = {
    val fullPath = _createAbsolutePath(path)
    val printWriter = new PrintWriter(fullPath)
    printWriter.write(content)
    printWriter.close()
  }

  override def getFileContents(path: String): String = {
    val fullPath = _createAbsolutePath(path)
    val encoded = Files.readAllBytes(Paths.get(fullPath))
    new String(encoded, StandardCharsets.UTF_8)
  }

  def _createAbsolutePath(relativePath: String): String = {
    val basePath = Paths.get(workingDirectory).toAbsolutePath.normalize().toString
    basePath + relativePath
  }
}
