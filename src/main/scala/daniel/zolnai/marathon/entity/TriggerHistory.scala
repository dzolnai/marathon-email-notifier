package daniel.zolnai.marathon.entity

import org.joda.time.{DateTime, Seconds}

/**
  * Contains historical trigger data for one trigger and one app.
  * Created by Daniel Zolnai on 2016-07-16.
  */
class TriggerHistory {

  var triggerId: String = _
  var appId: String = _
  var lastEmailSent: Option[DateTime] = None
  var previousTriggers: List[DateTime] = List()

  /**
    * Called when a new failure has happened for this specific app ID and trigger ID.
    *
    * @param dateTime The timestamp of the event.
    * @param trigger  The trigger containing the details about when it should fire.
    * @return True if an email should be sent. Otherwise false.
    */
  def newEvent(dateTime: DateTime, trigger: Trigger): Boolean = {
    previousTriggers = dateTime :: previousTriggers
    val shouldSendEmail = _shouldSendEmail(trigger)
    if (shouldSendEmail) {
      lastEmailSent = Some(DateTime.now())
    }
    shouldSendEmail
  }

  /**
    * Checks if the current state is valid for sending an email.
    *
    * @param trigger The trigger containing the specifics about when an email should be sent.
    * @return True if an email should be sent. Otherwise false.
    */
  private def _shouldSendEmail(trigger: Trigger): Boolean = {
    // If the last email was sent within the timeout, don't check anything
    val now = DateTime.now()
    val seconds = if (lastEmailSent.isEmpty) {
      -1
    } else {
      Seconds.secondsBetween(now, lastEmailSent.get).getSeconds
    }
    if (seconds >= 0 && seconds <= trigger.suspendEmailsForSeconds) {
      return false
    }
    if (trigger.windowSeconds > 0) {
      // Check the amount of failures in the window
      val windowBegin = now.minusSeconds(trigger.windowSeconds.toInt)
      val windowOccurrences = previousTriggers.count(windowBegin.isBefore(_))
      return windowOccurrences >= trigger.minFailures
    }
    // If there's no window, we have to send an email for every failure
    true
  }
}
