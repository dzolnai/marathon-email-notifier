package daniel.zolnai.marathon.service

import com.typesafe.config.{Config, ConfigFactory, ConfigValueType}
import daniel.zolnai.marathon.entity.{AppConfig, EmailConfig, Trigger}

import scala.collection.mutable.ListBuffer

import scala.collection.JavaConverters._

/**
  * Service for parsing and retrieving the configuration.
  * Created by Daniel Zolnai on 2016-07-10.
  */
class ConfigService() {
  private val KEY_ROOT = "marathon-email-notifier"
  private val KEY_CONFIGURATION_PROTOCOL_VERSION = s"$KEY_ROOT.configuration-protocol-version"
  private val KEY_ZOOKEEPER_URL = s"$KEY_ROOT.zookeeper-url"
  private val KEY_MARATHON_URL = s"$KEY_ROOT.marathon-url"
  private val KEY_LOCAL_DIRECTORY = s"$KEY_ROOT.local-directory"

  private val KEY_EMAIL_HOST = s"$KEY_ROOT.email.host"
  private val KEY_EMAIL_PORT = s"$KEY_ROOT.email.port"
  private val KEY_EMAIL_USERNAME = s"$KEY_ROOT.email.username"
  private val KEY_EMAIL_PASSWORD = s"$KEY_ROOT.email.password"
  private val KEY_EMAIL_SENDER = s"$KEY_ROOT.email.sender"
  private val KEY_EMAIL_SUBJECT = s"$KEY_ROOT.email.subject"
  private val KEY_EMAIL_SEND_TO = s"$KEY_ROOT.email.send-to"
  private val KEY_EMAIL_TEXT = s"$KEY_ROOT.email.text"
  private val KEY_EMAIL_IS_SSL = s"$KEY_ROOT.email.is-ssl"

  private val KEY_TRIGGERS = s"$KEY_ROOT.triggers"

  private val KEY_TRIGGER_ID = "id"
  private val KEY_TRIGGER_WINDOW_SECONDS = "window-seconds"
  private val KEY_TRIGGER_MIN_FAILURES = "min-failures"
  private val KEY_TRIGGER_SUSPEND_EMAILS_FOR_SECONDS = "suspend-emails-for-seconds"
  private val KEY_TRIGGER_EMAIL_TEXT = "email.text"
  private val KEY_TRIGGER_EMAIL_SUBJECT = "email.subject"
  private val KEY_TRIGGER_EMAIL_SEND_TO = "email.send-to"

  private val DEFAULT_SENDER = "Marathon Email Notifier <$MEN_HOSTNAME>"
  private val DEFAULT_SUBJECT = "Marathon app has failed: $MEN_APPLICATION_NAME"

  val appConfig = _parseConfig(ConfigFactory.load().resolve())

  /**
    * Config parser for the 1st version of the protocol.
    *
    * @param config The raw configuration.
    * @return The processed application config.
    */
  def _parseV1Config(config: Config): AppConfig = {
    // Zookeeper config
    var zooKeeperURL: Option[String] = None
    if (config.hasPath(KEY_ZOOKEEPER_URL)) {
      zooKeeperURL = Some(config.getString(KEY_ZOOKEEPER_URL))
    }
    val marathonURL = config.getString(KEY_MARATHON_URL)
    // Local directory config
    var localDirectory: Option[String] = None
    if (config.hasPath(KEY_LOCAL_DIRECTORY)) {
      localDirectory = Some(config.getString(KEY_LOCAL_DIRECTORY))
    }
    // Configuration for email
    val host = config.getString(KEY_EMAIL_HOST)
    val port = config.getInt(KEY_EMAIL_PORT)
    var sender = DEFAULT_SENDER
    if (config.hasPath(KEY_EMAIL_SENDER)) {
      sender = config.getString(KEY_EMAIL_SENDER)
    }
    var subject = DEFAULT_SUBJECT
    if (config.hasPath(KEY_EMAIL_SUBJECT)) {
      subject = config.getString(KEY_EMAIL_SUBJECT)
    }
    var username: Option[String] = None
    if (config.hasPath(KEY_EMAIL_USERNAME)) {
      username = Some(config.getString(KEY_EMAIL_USERNAME))
    }
    var password: Option[String] = None
    if (config.hasPath(KEY_EMAIL_PASSWORD)) {
      password = Some(config.getString(KEY_EMAIL_PASSWORD))
    }
    val sendTo = config.getString(KEY_EMAIL_SEND_TO)
    val text = _getEmailText(config, KEY_EMAIL_TEXT).get
    var isSSL = false
    if (config.hasPath(KEY_EMAIL_IS_SSL)) {
      isSSL = config.getBoolean(KEY_EMAIL_IS_SSL)
    }
    val emailConfig = EmailConfig(host, port, sender, username,
      password, subject, sendTo, text, isSSL)
    // Triggers
    val triggersList = config.getConfigList(KEY_TRIGGERS)
    val triggerCount = triggersList.size()
    val triggers = ListBuffer[Trigger]()
    for (i <- 0 until triggerCount) {
      val triggerConfig = triggersList.get(i)
      val trigger = new Trigger()
      trigger.id = triggerConfig.getString(KEY_TRIGGER_ID)
      if (triggerConfig.hasPath(KEY_TRIGGER_WINDOW_SECONDS)) {
        trigger.windowSeconds = triggerConfig.getLong(KEY_TRIGGER_WINDOW_SECONDS)
      }
      if (triggerConfig.hasPath(KEY_TRIGGER_MIN_FAILURES)) {
        trigger.minFailures = triggerConfig.getLong(KEY_TRIGGER_MIN_FAILURES)
      }
      if (triggerConfig.hasPath(KEY_TRIGGER_SUSPEND_EMAILS_FOR_SECONDS)) {
        trigger.suspendEmailsForSeconds = triggerConfig.getLong(KEY_TRIGGER_SUSPEND_EMAILS_FOR_SECONDS)
      }
      if (triggerConfig.hasPath(KEY_TRIGGER_EMAIL_TEXT)) {
        trigger.emailText = _getEmailText(triggerConfig, KEY_TRIGGER_EMAIL_TEXT)
      }
      if (triggerConfig.hasPath(KEY_TRIGGER_EMAIL_SUBJECT)) {
        trigger.emailSubject = Some(triggerConfig.getString(KEY_TRIGGER_EMAIL_SUBJECT))
      }
      if (triggerConfig.hasPath(KEY_TRIGGER_EMAIL_SEND_TO)) {
        trigger.emailSendTo = Some(triggerConfig.getString(KEY_TRIGGER_EMAIL_SEND_TO))
      }
      triggers += trigger
    }
    AppConfig(zooKeeperURL, marathonURL,
      localDirectory, emailConfig, triggers.toList)
  }

  /**
    * Parses the email text.
    *
    * @param config The raw configuration.
    * @param key    The path where the property can be found.
    * @return The email text as a string.
    */
  def _getEmailText(config: Config, key: String): Option[String] = {
    if (config.hasPath(key)) {
      val configValue = config.getValue(key)
      if (configValue.valueType() == ConfigValueType.STRING) {
        Some(config.getString(key))
      } else {
        // Get the list of string, and join them
        // This means that the config should explicitly define the newlines!
        val stringList = config.getStringList(key)
        Some(stringList.asScala.mkString(""))
      }
    } else {
      None
    }
  }

  /**
    * Checks the config protocol version, and initiates
    * the correct config parser functions if the protocol is known and supported.
    *
    * @param config The raw configuration object.
    * @return The application config if it is supported and parseable.
    */
  def _parseConfig(config: Config): AppConfig = {
    if (config.hasPath(KEY_CONFIGURATION_PROTOCOL_VERSION)) {
      val configVersion = config.getInt(KEY_CONFIGURATION_PROTOCOL_VERSION)
      if (configVersion == 1) {
        _parseV1Config(config: Config)
      } else {
        throw new ConfigurationException(s"Invalid value $configVersion for configuration parameter: $KEY_CONFIGURATION_PROTOCOL_VERSION!")
      }
    } else {
      throw new ConfigurationException(s"Missing configuration parameter: $KEY_CONFIGURATION_PROTOCOL_VERSION!")
    }
  }

  class ConfigurationException(message: String) extends Exception(message)

}
