package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.TestSuite

/**
  * Tests the configuration parser service.
  * Created by Daniel Zolnai on 2016-07-12.
  */
class ConfigServiceTest extends TestSuite {

  test("parseExampleConfig") {
    val appConfig = new ConfigService().appConfig
    assert(appConfig.zooKeeperURL.contains("localhost:2181/marathon-email-notifier"))
    assert(appConfig.marathonURL.contains("localhost:8080/"))
    assert(appConfig.localDirectory.isEmpty)
    assert(appConfig.emailConfig.host == "localhost")
    assert(appConfig.emailConfig.port == 25)
    assert(appConfig.emailConfig.password.contains("pa55word"))
    assert(appConfig.emailConfig.username.contains("admin"))
    assert(appConfig.emailConfig.sender == "Marathon QA <$MEN_HOSTNAME>")
    assert(appConfig.emailConfig.subject == "$MEN_APPLICATION_NAME application has failed in Marathon!")
    assert(appConfig.emailConfig.sendTo == "alice@example.com")
    assert(appConfig.emailConfig.text == "$MEN_APPLICATION_NAME has just failed!")
    assert(appConfig.emailConfig.isSSL)
    assert(appConfig.triggers.size == 2)
    assert(appConfig.triggers.head.id.contains("frequent-failure"))
    assert(appConfig.triggers.head.windowSeconds == 120)
    assert(appConfig.triggers.head.minFailures == 3)
    assert(appConfig.triggers.head.suspendEmailsForSeconds == 1800)
    assert(appConfig.triggers.head.emailText.contains("$MEN_APPLICATION_NAME is failing very frequently!\n" +
      "Please check the configuration and logs at $MEN_APPLICATION_URL ASAP!\n" +
      "Cheers, Marathon Email Notifier"))
    assert(appConfig.triggers.head.emailSubject.contains("$MEN_APPLICATION_NAME is failing very frequently in Marathon"))
    assert(appConfig.triggers.head.emailSendTo.contains("bob@example.com"))
  }
}
