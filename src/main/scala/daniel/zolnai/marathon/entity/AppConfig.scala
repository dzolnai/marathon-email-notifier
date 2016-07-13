package daniel.zolnai.marathon.entity

/**
  * Application configuration object.
  * Created by Daniel Zolnai on 2016-07-10.
  */
case class AppConfig(zooKeeperURL: Option[String],
                     marathonURL: String,
                     localDirectory: Option[String],
                     emailConfig: EmailConfig,
                     triggers: List[Trigger]) {
}
