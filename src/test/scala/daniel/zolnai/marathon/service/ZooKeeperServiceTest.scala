package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.TestSuite
import daniel.zolnai.marathon.entity.AppConfig
import org.apache.zookeeper.KeeperException
import org.scalatest.BeforeAndAfterAll

/**
  * Test which checks the ZooKeeper service functionality.
  * Created by Daniel Zolnai on 2016-07-10.
  */
class ZooKeeperServiceTest extends TestSuite with BeforeAndAfterAll {

  override def beforeAll() {
    _startZooKeeperServer()
  }

  override def afterAll() {
    _stopZooKeeperServer()
  }

  private def _generateZkService(rootNode: String): ZooKeeperService = {
    val configService = new ConfigService() {
      override val appConfig = AppConfig(Some(_getZooKeeperUrl() + rootNode), null, None, null, null)
    }
    val zooKeeperService = new ZooKeeperService(configService)
    zooKeeperService.connectIfRequired()
    zooKeeperService
  }

  test("testCreateNode") {
    val zooKeeperService = _generateZkService("/node_create_test")
    val nodeToCreate = "/node_create_test/testdir"
    // Should not exist before we created it
    assert(!zooKeeperService.nodeExists(nodeToCreate))
    zooKeeperService.createNode(nodeToCreate, ephemeral = false)
    // Should exist after we created it
    assert(zooKeeperService.nodeExists(nodeToCreate))
  }

  test("testListChildren") {
    val zooKeeperService = _generateZkService("/node_children_test")
    val nodeToCheck = "/node_children_test/testdir"
    // Path does not exist, should throw an exception
    intercept[KeeperException] {
      zooKeeperService.listChildren(nodeToCheck)
    }
    // Create a path, and test again if it is empty
    zooKeeperService.createNode(nodeToCheck, ephemeral = false)
    assert(zooKeeperService.listChildren(nodeToCheck).isEmpty)
    // Add two children
    zooKeeperService.createNode(s"$nodeToCheck/child1", ephemeral = false)
    zooKeeperService.createNode(s"$nodeToCheck/child2", ephemeral = false)
    // Check if they are there
    assert(zooKeeperService.listChildren(nodeToCheck).length == 2)
  }

  test("testBecomeLeader") {
    // The problem is that the becomeLeader() will be called from the constructor in our case.
    // So Mockito can not detect it, because when we give to object to spy, it has already executed the method.
    // So instead, we override the method to throw an exception, which will be catched during the test.
    class TestException extends Exception("This service became the leader!")
    val configService = new ConfigService() {
      override val appConfig = AppConfig(Some(_getZooKeeperUrl() + "/leader_test"), null, None, null, null)
    }
    class TestZooKeeperService extends ZooKeeperService(configService) {
      override def becomeLeader() {
        throw new TestException()
      }
    }
    // First service should throw an exception.
    intercept[TestException] {
      new TestZooKeeperService().connectIfRequired()
    }
    // Second service should not throw an exception, because it is not the leader.
    new TestZooKeeperService().connectIfRequired()
  }

}
