package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.entity.Trigger
import org.apache.commons.mail.SimpleEmail
import org.apache.log4j.Logger

/**
  * The service which is responsible for sending email notifications when a trigger is fired.
  * Created by Daniel Zolnai on 2016-07-21.
  */
class EmailService(configService: ConfigService) {

  private val _logger = Logger.getLogger(this.getClass.getName)

  private def _emailConfig = configService.appConfig.emailConfig

  /**
    * Sends an email when a trigger has been fired.
    *
    * @param trigger The trigger which has been fired. Might contain overrides for some of the email parameters.
    */
  def sendEmail(trigger: Trigger) = {
    val sendTo = if (trigger.emailSendTo.isDefined) {
      trigger.emailSendTo.get
    } else {
      _emailConfig.sendTo
    }
    val sendToList = sendTo.split(',').map(emailAddress => emailAddress.trim)
    var subject = if (trigger.emailSubject.isDefined) {
      trigger.emailSubject.get
    } else {
      _emailConfig.subject
    }
    var text = if (trigger.emailText.isDefined) {
      trigger.emailText.get
    } else {
      _emailConfig.text
    }
    // Do the enrichment
    text = _enrich(text)
    subject = _enrich(subject)
    _sendEmailMessage(sendToList, subject, text)
  }

  /**
    * Replaces predefined variables with their values.
    *
    * @param text The text to replace the variables in.
    * @return The text which contains the values.
    */
  def _enrich(text: String): String = {
    // TODO
    text
  }

  /**
    * Sends an email message using the SMTP client.
    *
    * @param sendTo  The recipients of the email in a list.
    * @param subject The subject of the email.
    * @param text    The message body of the email.
    */
  private def _sendEmailMessage(sendTo: Seq[String], subject: String, text: String) = {
    val emailClient = new SimpleEmail

    emailClient.setHostName(_emailConfig.host)
    emailClient.setSmtpPort(_emailConfig.port)
    emailClient.setSSLOnConnect(_emailConfig.isSSL)

    if (_emailConfig.username.isDefined && _emailConfig.password.nonEmpty) {
      emailClient.setAuthentication(_emailConfig.username.get, _emailConfig.password.get)
    }
    sendTo.foreach(emailClient.addTo)
    emailClient.setFrom(_emailConfig.sender)
    emailClient.setSubject(subject)
    emailClient.setMsg(text)

    val response = emailClient.send()
    _logger.info(s"Email sent to ${sendTo.mkString(",")} with subject $subject. Response: $response")
  }
}
