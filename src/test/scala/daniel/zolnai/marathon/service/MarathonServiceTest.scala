package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.entity.AppConfig
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
    val marathonService = new MarathonService(configService)
    val server = new EventStreamServer()
    server.start()
    marathonService.connect()
  }
}
