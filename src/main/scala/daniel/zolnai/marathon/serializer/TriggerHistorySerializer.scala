package daniel.zolnai.marathon.serializer

import daniel.zolnai.marathon.entity.TriggerHistory
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.json4s.JsonDSL._
import org.json4s.{CustomSerializer, _}
import org.json4s.native.Serialization.write

/**
  * Serializer for the trigger history class
  *
  * Created by Daniel Zolnai on 2016-07-16.
  */
class TriggerHistorySerializer extends CustomSerializer[TriggerHistory](format => ( {
  case json: JValue =>
    implicit val formats = format
    val triggerHistory = new TriggerHistory()
    triggerHistory.previousTriggers = (json \ "previousTriggers").extract[List[DateTime]]
    triggerHistory.triggerId = (json \ "triggerId").extract[String]
    triggerHistory.lastEmailSent = (json \ "lastEmailSent").extractOpt[DateTime]
    triggerHistory
}, {
  case triggerHistory: TriggerHistory =>
    implicit val formats = format
    val outputFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    def _fromDate(date: Option[DateTime]): Option[String] = {
      if (date.isEmpty) {
        None
      } else {
        Some(outputFormat.print(date.get))
      }
    }
    val result = ("lastEmailSent" -> _fromDate(triggerHistory.lastEmailSent)) ~
      ("triggerId" -> triggerHistory.triggerId) ~
      ("previousTriggers" -> Extraction.decompose(triggerHistory.previousTriggers))
    result
}))

