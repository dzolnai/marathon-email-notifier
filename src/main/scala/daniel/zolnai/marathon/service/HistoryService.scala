package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.entity.event.{MarathonEvent, StatusUpdateEvent}
import daniel.zolnai.marathon.entity.{History, Trigger, TriggerHistory}
import daniel.zolnai.marathon.serializer.DefaultFormats
import org.joda.time.DateTime
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.write
import org.slf4j.{Logger, LoggerFactory}


/**
  * Stores the history of job failures, and initiates the triggers if needed.
  * Created by Daniel Zolnai on 2016-07-13.
  */
class HistoryService(configService: ConfigService, storageService: StorageService, emailService: EmailService, implicit val formats: DefaultFormats) {

  final val HISTORY_PATH = "/history"

  private val _logger: Logger = LoggerFactory.getLogger(classOf[HistoryService])

  private var _history: History = _
  private var _triggerMap = Map[String, Trigger]()

  /**
    * Called when Marathon receives a new event through the event stream.
    * We are only interested in status update events.
    *
    * @param marathonEvent The event
    */
  def newEvent(marathonEvent: MarathonEvent): Unit = {
    _logger.info(s"New event: ${marathonEvent.eventType.toString.toLowerCase}")
    // We only care about status update events
    if (marathonEvent.eventType != MarathonEvent.EventType.STATUS_UPDATE_EVENT) {
      return
    }
    val statusUpdateEvent = marathonEvent.asInstanceOf[StatusUpdateEvent]
    // We only care about failed statuses
    if (statusUpdateEvent.taskStatus != StatusUpdateEvent.Status.TASK_FAILED) {
      return
    }
    val appId = statusUpdateEvent.appId
    // For each trigger and each app, store it in the history
    // If the history does not exist, create it.
    val triggerIds = _triggerMap.keys
    for (triggerId <- triggerIds) {
      val triggerHistory = _getHistoryForAppAndTrigger(appId, triggerId)
      val sendEmail = triggerHistory.newEvent(statusUpdateEvent.timestamp, _triggerMap(triggerId))
      if (sendEmail) {
        emailService.sendEmail(_triggerMap(triggerId), triggerHistory)
      }
    }
    save()
  }

  /**
    * Returns the history for a specific app ID and trigger ID.
    * If none found in the history object, it will create a new one and add it to the history.
    *
    * @param appId     The application ID.
    * @param triggerId The trigger ID.
    * @return The trigger history. An existing one if found, otherwise a newly create one.
    */
  private def _getHistoryForAppAndTrigger(appId: String, triggerId: String): TriggerHistory = {
    _history.triggerHistories.foreach(triggerHistory => {
      if (triggerHistory.appId == appId && triggerHistory.triggerId == triggerId) {
        return triggerHistory
      }
    })
    val newTriggerHistory = new TriggerHistory()
    newTriggerHistory.appId = appId
    newTriggerHistory.triggerId = triggerId
    _history.triggerHistories = newTriggerHistory :: _history.triggerHistories
    newTriggerHistory
  }

  /**
    * Saves the history of app failures via the storage service
    */
  def save() = {
    _history.lastSaved = Some(DateTime.now())
    storageService.saveToFile(HISTORY_PATH, write(_history))
  }

  /**
    * Loads the history from the storage into the app.
    */
  def load() = {
    val contents = storageService.getFileContents(HISTORY_PATH)
    _history = parse(contents).extract[History]
    configService.appConfig.triggers.foreach(trigger => {
      _triggerMap = _triggerMap + (trigger.id -> trigger)
    })
  }


}
