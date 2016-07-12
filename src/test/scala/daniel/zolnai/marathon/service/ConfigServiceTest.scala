package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.TestSuite

/**
  * Tests the configuration parser service.
  * Created by Daniel Zolnai on 2016-07-12.
  */
class ConfigServiceTest extends TestSuite {

  test("parseExampleConfig") {
    val appConfig = new ConfigService().getAppConfig
    assert(appConfig.emailConfig.host == "localhost")
    assert(appConfig.emailConfig.port == 25)
    assert(appConfig.emailConfig.password == Some("pa55word"))
    assert(appConfig.emailConfig.username == Some("admin"))
    assert(appConfig.emailConfig.sender == "Marathon QA <$MEN_HOSTNAME>")
    assert(appConfig.emailConfig.subject == "$MEN_APPLICATION_NAME application has failed in Marathon!")
    assert(appConfig.triggers.size == 2)
    assert(appConfig.triggers.head.windowSeconds == 120)
    assert(appConfig.triggers.head.minFailures == 3)
    assert(appConfig.triggers.head.suspendEmailsForSeconds == 1800)
    assert(appConfig.triggers.head.emailText == Some("$MEN_APPLICATION_NAME is failing very frequently!\n" +
      "Please check the configuration and logs at $MEN_APPLICATION_URL ASAP!\n" +
      "Cheers, Marathon Email Notifier"))
    assert(appConfig.triggers.head.emailSubject == Some("$MEN_APPLICATION_NAME is failing very frequently in Marathon"))
  }
}
