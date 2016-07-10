package daniel.zolnai.marathon

import java.io.IOException
import java.util.Properties

import org.apache.zookeeper.server.quorum.QuorumPeerConfig
import org.apache.zookeeper.server.{ServerConfig, ZooKeeperServerMain}
import org.slf4j.{Logger, LoggerFactory}

/**
  * A local Zookeeper server for running unit tests.
  * Reference: https://gist.github.com/fjavieralba/7930018/
  * Created by Daniel Zolnai on 2016-07-10.
  */
class ZooKeeperLocal(zkProperties: Properties) {
  private val quorumConfiguration: QuorumPeerConfig = new QuorumPeerConfig
  private final val logger: Logger = LoggerFactory.getLogger(classOf[ZooKeeperLocal])
  private val zooKeeperServer: ZooKeeperServerMain = new ZooKeeperServerMain
  private val configuration: ServerConfig = new ServerConfig
  try {
    quorumConfiguration.parseProperties(zkProperties)
  }
  catch {
    case e: Exception =>
      throw new RuntimeException(e)
  }
  configuration.readFrom(quorumConfiguration)
  private val thread = new Thread {
    override def run() {
      try {
        zooKeeperServer.runFromConfig(configuration)
      }
      catch {
        case e: IOException =>
          logger.error("Zookeeper startup failed.", e)
      }
    }
  }
  thread.start()

  def shutdown(): Unit = {
    thread.interrupt()
  }
}