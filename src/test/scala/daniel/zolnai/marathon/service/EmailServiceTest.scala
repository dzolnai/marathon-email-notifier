package daniel.zolnai.marathon.service

import com.dumbster.smtp.SimpleSmtpServer
import daniel.zolnai.marathon.TestSuite
import daniel.zolnai.marathon.entity.{AppConfig, EmailConfig, Trigger}
import scala.collection.JavaConverters._

/**
  * Tests the email client with a fake SMTP server.
  * Created by Daniel Zolnai on 2016-07-21.
  */
class EmailServiceTest extends TestSuite {

  private final val SENDER = "Test sender <test-sender@example.com>"
  private final val SUBJECT = "Test subject"
  private final val TEXT = "This is a test message."
  private final val SEND_TO = List("alice@example", "bob@example.com")

  test("sendEmail") {
    val fakeSmtpServer = SimpleSmtpServer.start(SimpleSmtpServer.AUTO_SMTP_PORT)
    val emailConfig = EmailConfig("localhost", fakeSmtpServer.getPort, SENDER, None, None,
      SUBJECT, SEND_TO.mkString(","), TEXT, isSSL = false)
    val configService = new ConfigService {
      override val appConfig: AppConfig = AppConfig(None, "", None, emailConfig, null)
    }
    val emailService = new EmailService(configService)
    // Test only the default values
    val trigger1 = new Trigger()
    emailService.sendEmail(trigger1)
    var emails = fakeSmtpServer.getReceivedEmails.asScala
    assert(emails.length == 1)
    var email = emails.head
    assert(email.getBody == TEXT)
    assert(email.getHeaderValue("Subject") == SUBJECT)
    assert(email.getHeaderValue("To") == SEND_TO.mkString(", "))
    fakeSmtpServer.reset()
    // Test with overrides from a trigger
    val trigger2 = new Trigger()
    trigger2.emailSendTo = Some("charlie@example.com")
    trigger2.emailSubject = Some("Different subject")
    trigger2.emailText = Some("Different text")
    emailService.sendEmail(trigger2)
    emails = fakeSmtpServer.getReceivedEmails.asScala
    assert(emails.length == 1)
    email = emails.head
    assert(email.getBody == trigger2.emailText.get)
    assert(email.getHeaderValue("Subject") == trigger2.emailSubject.get)
    assert(email.getHeaderValue("To") == trigger2.emailSendTo.get)
    fakeSmtpServer.stop()
  }

}
