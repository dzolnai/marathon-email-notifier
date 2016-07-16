package daniel.zolnai.marathon

import java.io._
import java.util.Properties

import daniel.zolnai.marathon.entity.event.MarathonEvent
import daniel.zolnai.marathon.serializer.DefaultFormats
import org.scalatest.FunSuite
import org.slf4j.{Logger, LoggerFactory}
import org.json4s.native.JsonMethods._

import scala.collection.mutable.ListBuffer

/**
  * Collection of utility methods which can be reused between different tests.
  * Created by Daniel Zolnai on 2016-07-09.
  */
class TestSuite extends FunSuite {

  private final val EXAMPLE_EVENTS = "src/test/resources/example_events.json"

  private var _zookeeperServer: ZooKeeperLocal = _
  private val _logger: Logger = LoggerFactory.getLogger(classOf[TestSuite])

  implicit val formats = new DefaultFormats

  /**
    * Starts the ZooKeeper server on the local host. Used for unit testing.
    */
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

  /**
    * Reads the example events from the json file inside the test resources
    * directory, parses them, and returns them in a list.
    *
    * @return The list of example events.
    */
  protected def _getExampleEvents(): ListBuffer[MarathonEvent] = {
    val inputStream = new BufferedInputStream(new FileInputStream(EXAMPLE_EVENTS))
    val bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
    var line = bufferedReader.readLine()
    val lines = new ListBuffer[String]
    // Read the file line by line into a buffer
    while (line != null) {
      lines += line
      line = bufferedReader.readLine()
    }
    _logger.info(s"Read ${lines.size} lines from path: $EXAMPLE_EVENTS")
    lines.map(string => parse(string).extract[MarathonEvent])
  }
}
