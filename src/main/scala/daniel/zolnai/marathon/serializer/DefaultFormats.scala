package daniel.zolnai.marathon.serializer

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import org.joda.time.DateTime
import org.json4s.prefs.EmptyValueStrategy
import org.json4s.{DateFormat, Serializer}

import scala.util.Try

/**
  * json4s default formats which treats timestamps in the UTC timezone, and has some custom serializers.
  * Created by Daniel Zolnai on 2016-07-14.
  */
class DefaultFormats extends org.json4s.DefaultFormats {
  override val emptyValueStrategy: EmptyValueStrategy = EmptyValueStrategy.preserve

  override val customSerializers: List[Serializer[_]] = List(new MarathonEventSerializer(), new DateTimeSerializer())


  override val dateFormat = new DateFormat {
    def parse(date: String) = Try(DateTime.parse(date).toDate).toOption

    def format(date: Date) = dateFormatter.format(date)
  }
}