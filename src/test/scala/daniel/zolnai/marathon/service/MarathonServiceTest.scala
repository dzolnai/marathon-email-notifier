package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.entity.AppConfig
import daniel.zolnai.marathon.serializer.DefaultFormats
import daniel.zolnai.marathon.{EventStreamServer, TestSuite}

/**
  * Tests the Marathon service.
  * Created by Daniel Zolnai on 2016-07-13.
  */
class MarathonServiceTest extends TestSuite {

  test("events") {
    val configService = new ConfigService {
      override val appConfig = AppConfig(None, "localhost:8080/", Some("target/"), null, null)
    }
    val defaultFormats = new DefaultFormats
    val zooKeeperService = new ZooKeeperService(configService)
    val storageService = new StorageService(configService, zooKeeperService)
    val emailService = new EmailService(configService)
    val historyService = new HistoryService(configService, storageService, emailService, defaultFormats)
    val marathonService = new MarathonService(configService, historyService, defaultFormats)
    val server = new EventStreamServer()
    server.setEventsToEmit(_getExampleEvents())
    server.start()
    marathonService.connect()
    // TODO add assert
  }
}
