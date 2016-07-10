package daniel.zolnai.marathon

import java.io.{File, FileInputStream, FileNotFoundException}
import java.util.Properties

import org.scalatest.FunSuite
import org.slf4j.{Logger, LoggerFactory}

/**
  * Collection of utility methods which can be reused between different tests.
  * Created by Daniel Zolnai on 2016-07-09.
  */
class TestSuite extends FunSuite {

  private var _zookeeperServer: ZooKeeperLocal = _
  private val _logger: Logger = LoggerFactory.getLogger(classOf[TestSuite])

  protected def _startZooKeeperServer(): Unit = {
    val zkProperties: Properties = new Properties
    zkProperties.load(new FileInputStream("src/test/resources/zookeeper.properties"))
    val dataDir = zkProperties.get("dataDir")
    // Delete the directory "version-2" which might contain remaining data from previous runs.
    try {
      _deleteFile(new File(s"$dataDir/version-2"))
    } catch {
      case ex: FileNotFoundException => // Directory did not exist, which is fine
    }
        _zookeeperServer = new ZooKeeperLocal(zkProperties)
        _logger.info("ZooKeeper instance is successfully started.")
  }

  protected def _stopZooKeeperServer(): Unit = {
    _zookeeperServer.shutdown()
  }

  protected def _getZooKeeperUrl(): String = {
    "localhost:2181"
  }

  protected def _deleteFile(file: File): Unit = {
    if (file.isDirectory) {
      for (c: File <- file.listFiles()) {
        _deleteFile(c)
      }
    }
    if (!file.delete()) {
      throw new FileNotFoundException("Failed to delete file: " + file)
    }
  }
}
