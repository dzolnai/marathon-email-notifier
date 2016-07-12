package daniel.zolnai.marathon.entity

/**
  * Application configuration for sending emails.
  * Created by Daniel Zolnai on 2016-07-10.
  */
case class EmailConfig(host: String,
                       port: Int,
                       sender: String,
                       username: Option[String],
                       password: Option[String],
                       subject: String) {
}
