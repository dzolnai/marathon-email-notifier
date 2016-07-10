package daniel.zolnai.marathon

import java.io.FileInputStream
import java.net.BindException
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
    _zookeeperServer = new ZooKeeperLocal(zkProperties)
    _logger.info("ZooKeeper instance is successfully started.")
  }

  protected def _stopZooKeeperServer(): Unit = {
    _zookeeperServer.shutdown()
  }

  protected def _getZooKeeperUrl() : String = {
    "localhost:2181"
  }
}
