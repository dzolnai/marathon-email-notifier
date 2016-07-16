package daniel.zolnai.marathon.entity

/**
  * Trigger definition object.
  * Created by Daniel Zolnai on 2016-07-10.
  */
class Trigger() {

  final val NO_WINDOWING = 0
  final val NO_MIN_FAILURES = 0
  final val NO_EMAIL_SUSPEND = 0

  var id: String = _
  var windowSeconds: Long = NO_WINDOWING
  var minFailures: Long = NO_MIN_FAILURES
  var suspendEmailsForSeconds: Long = NO_EMAIL_SUSPEND

  // Custom email related properties, which can override the global properties if defined
  var emailText: Option[String] = None
  var emailSubject: Option[String] = None
}
