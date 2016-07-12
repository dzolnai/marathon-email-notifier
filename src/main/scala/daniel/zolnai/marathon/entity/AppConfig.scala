package daniel.zolnai.marathon.entity

/**
  * Application configuration object.
  * Created by Daniel Zolnai on 2016-07-10.
  */
case class AppConfig(emailConfig: EmailConfig,
                triggers: List[Trigger]) {
}
