package daniel.zolnai.marathon.serializer

import daniel.zolnai.marathon.entity.event.{EventStreamAttachedEvent, MarathonEvent, StatusUpdateEvent}
import daniel.zolnai.marathon.entity.event.MarathonEvent.EventType.EventType
import daniel.zolnai.marathon.entity.event.StatusUpdateEvent.Status.Status
import org.joda.time.DateTime
import org.json4s.{CustomSerializer, JValue}
import org.json4s._
import org.json4s.JsonDSL._

/**
  * Serializes a Marathon stream event
  * Created by Daniel Zolnai on 2016-07-14.
  */
class MarathonEventSerializer extends CustomSerializer[MarathonEvent](format => ( {
  case json: JValue =>
    // Deserialization only used for reading from Kafka for unit tests
    implicit val formats = format
    val eventTypeString = (json \ "eventType").extract[String]
    var eventType: EventType = null
    if (eventTypeString == "event_stream_attached") {
      eventType = MarathonEvent.EventType.EVENT_STREAM_ATTACHED
    } else if (eventTypeString == "status_update_event") {
      eventType = MarathonEvent.EventType.STATUS_UPDATE_EVENT
    }
    if (eventType == MarathonEvent.EventType.EVENT_STREAM_ATTACHED) {
      val marathonEvent = new EventStreamAttachedEvent()
      marathonEvent.eventType = eventType
      marathonEvent.remoteAddress = (json \ "remoteAddress").extract[String]
      marathonEvent.timestamp = (json \ "timestamp").extract[DateTime]
      marathonEvent
    } else if (eventType == MarathonEvent.EventType.STATUS_UPDATE_EVENT) {
      val marathonEvent = new StatusUpdateEvent()
      marathonEvent.eventType = eventType
      marathonEvent.timestamp = (json \ "timestamp").extract[DateTime]
      marathonEvent.appId = (json \ "appId").extract[String]
      marathonEvent.host = (json \ "host").extract[String]
      marathonEvent.message = (json \ "message").extract[String]
      marathonEvent.ports = (json \ "ports").extract[List[Long]]
      marathonEvent.taskId = (json \ "taskId").extract[String]
      marathonEvent.version = (json \ "version").extract[String]
      val taskStatusString = (json \ "taskStatus").extract[String]
      var status: Status = null
      if (taskStatusString == "TASK_RUNNING") {
        status = StatusUpdateEvent.Status.TASK_RUNNING
      } else if (taskStatusString == "TASK_FAILED") {
        status = StatusUpdateEvent.Status.TASK_FAILED
      }
      marathonEvent.taskStatus = status
      marathonEvent
    } else {
      throw new IllegalStateException(s"Unknown event type: $eventTypeString")
    }
}, {
  case marathonEvent: MarathonEvent =>
    implicit val formats = format
    var result = ("timestamp" -> marathonEvent.timestamp.toString()) ~
      ("eventType" -> marathonEvent.eventType.toString.toLowerCase())
    if (marathonEvent.eventType == MarathonEvent.EventType.EVENT_STREAM_ATTACHED) {
      val eventStreamAttachedEvent = marathonEvent.asInstanceOf[EventStreamAttachedEvent]
      result = result ~ ("remoteAddress" -> eventStreamAttachedEvent.remoteAddress)
    } else if (marathonEvent.eventType == MarathonEvent.EventType.STATUS_UPDATE_EVENT) {
      val statusUpdateEvent = marathonEvent.asInstanceOf[StatusUpdateEvent]
      result = result ~ ("taskStatus" -> statusUpdateEvent.taskStatus.toString.toLowerCase) ~
        ("version" -> statusUpdateEvent.version) ~
        ("taskId" -> statusUpdateEvent.taskId) ~
        ("ports" -> statusUpdateEvent.ports) ~
        ("appId" -> statusUpdateEvent.appId) ~
        ("host" -> statusUpdateEvent.host) ~
        ("message" -> statusUpdateEvent.message)
    }
    result
}))