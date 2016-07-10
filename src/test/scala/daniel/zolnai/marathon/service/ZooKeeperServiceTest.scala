package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.TestSuite
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
    val nodeToCreate = "/test/testdir"
    val zooKeeperService = new ZooKeeperService(_getZooKeeperUrl(), "/test")
    // Should not exist before we created it
    assert(!zooKeeperService.nodeExists(nodeToCreate))
    zooKeeperService.createNode(nodeToCreate, ephemeral = false)
    // Should exist after we created it
    assert(zooKeeperService.nodeExists(nodeToCreate))
  }

  test("testListChildren") {
    // TODO
  }

  test("testBecomeLeader") {
    // TODO
  }

}
