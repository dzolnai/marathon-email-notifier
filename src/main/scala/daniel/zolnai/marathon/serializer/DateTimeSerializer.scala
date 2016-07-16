package daniel.zolnai.marathon.serializer

import org.joda.time.{DateTimeZone, DateTime}
import org.json4s._
import org.json4s.JsonAST.JString
import org.json4s.ext.DateParser

/**
  * Serializes a string date into a Joda DateTime object which is in the UTC timezone.
  * Created by Daniel Zolnai on 2016-07-16.
  */
class DateTimeSerializer extends CustomSerializer[DateTime](format => ( {
  case JString(s) => new DateTime(DateParser.parse(s, format), DateTimeZone.UTC)
  case JNull => null
}, {
  case d: DateTime => JString(format.dateFormat.format(d.toDate))
})
)