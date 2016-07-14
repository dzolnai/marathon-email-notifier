package daniel.zolnai.marathon.entity.event

import daniel.zolnai.marathon.entity.event.MarathonEvent.EventType.EventType
import org.joda.time.DateTime

/**
  * Base class for all marathon events.
  * Created by Daniel Zolnai on 2016-07-14.
  */
object MarathonEvent {

  object EventType extends Enumeration {
    type EventType = Value
    val EVENT_STREAM_ATTACHED, STATUS_UPDATE_EVENT = Value
  }

}

abstract class MarathonEvent {
  var eventType: EventType = _
  var timestamp: DateTime = _
}
