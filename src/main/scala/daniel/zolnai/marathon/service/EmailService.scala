package daniel.zolnai.marathon.service

import java.net.InetAddress

import daniel.zolnai.marathon.entity.{Trigger, TriggerHistory}
import org.apache.commons.mail.SimpleEmail
import org.apache.log4j.Logger

/**
  * The service which is responsible for sending email notifications when a trigger is fired.
  * Created by Daniel Zolnai on 2016-07-21.
  */
class EmailService(configService: ConfigService) {

  private val _logger = Logger.getLogger(this.getClass.getName)

  private val KEY_APPLICATION_NAME = "$MEN_APPLICATION_NAME"
  private val KEY_HOSTNAME = "$MEN_HOSTNAME"
  private val KEY_APPLICATION_URL = "$MEN_APPLICATION_URL"


  private def _emailConfig = configService.appConfig.emailConfig

  /**
    * Sends an email when a trigger has been fired.
    *
    * @param trigger The trigger which has been fired. Might contain overrides for some of the email parameters.
    */
  def sendEmail(trigger: Trigger, triggerHistory: TriggerHistory) = {
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
    text = _enrich(text, trigger, triggerHistory)
    subject = _enrich(subject, trigger, triggerHistory)
    _sendEmailMessage(sendToList, subject, text)
  }

  /**
    * Replaces predefined variables with their values.
    *
    * @param text The text to replace the variables in.
    * @return The text which contains the values.
    */
  def _enrich(text: String, trigger: Trigger, triggerHistory: TriggerHistory): String = {
    var enriched = text
    if (enriched.contains(KEY_APPLICATION_NAME)) {
      enriched = enriched.replace(KEY_APPLICATION_NAME, triggerHistory.appId)
    }
    if (enriched.contains(KEY_HOSTNAME)) {
      val hostname = InetAddress.getLocalHost.getHostName
      enriched = enriched.replace(KEY_HOSTNAME, hostname)
    }
    if (enriched.contains(KEY_APPLICATION_URL)) {
      val appId = triggerHistory.appId
      val marathonURL = configService.appConfig.marathonURL
      val appURL = marathonURL + "/app/" + appId
      enriched = enriched.replace(KEY_APPLICATION_URL, appURL)
    }
    // TODO add possibility to enrich with env variables
    enriched
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
