package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.TestSuite
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

  test("testCreateNode") {
    val nodeToCreate = "/node_create_test/testdir"
    val zooKeeperService = new ZooKeeperService(_getZooKeeperUrl(), "/node_create_test")
    // Should not exist before we created it
    assert(!zooKeeperService.nodeExists(nodeToCreate))
    zooKeeperService.createNode(nodeToCreate, ephemeral = false)
    // Should exist after we created it
    assert(zooKeeperService.nodeExists(nodeToCreate))
  }

  test("testListChildren") {
    val nodeToCheck = "/node_children_test/testdir"
    val zooKeeperService = new ZooKeeperService(_getZooKeeperUrl(), "/node_children_test")
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
    class TestZooKeeperService extends ZooKeeperService(zooKeeperUrl = _getZooKeeperUrl(), zooKeeperPath = "/leader_test") {
      override def becomeLeader() {
        throw new TestException()
      }
    }
    // First service should throw an exception.
    intercept[TestException] {
      new TestZooKeeperService
    }
    // Second service should not throw an exception, because it is not the leader.
    new TestZooKeeperService
  }

}
