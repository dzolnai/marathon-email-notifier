package daniel.zolnai.marathon.serializer

import daniel.zolnai.marathon.entity.{History, TriggerHistory}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.json4s.JsonDSL._
import org.json4s.{CustomSerializer, _}

/**
  * Serializer for the history class.
  * Created by Daniel Zolnai on 2016-07-16.
  */
class HistorySerializer extends CustomSerializer[History](format => ( {
  case json: JValue =>
    implicit val formats = format
    val history = new History()
    history.lastSaved = (json \ "lastSaved").extractOpt[DateTime]
    history.triggerHistories = (json \ "triggerHistories").extract[List[TriggerHistory]]
    history
}, {
  case history: History =>
    implicit val formats = format
    val outputFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    def _fromDate(date: Option[DateTime]): Option[String] = {
      if (date.isEmpty) {
        None
      } else {
        Some(outputFormat.print(date.get))
      }
    }
    val result = ("lastSaved" -> _fromDate(history.lastSaved)) ~
      ("triggerHistories" -> Extraction.decompose(history.triggerHistories))
    result
}))
