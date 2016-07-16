package daniel.zolnai.marathon

import daniel.zolnai.marathon.entity.event.MarathonEvent
import daniel.zolnai.marathon.serializer.DefaultFormats
import org.http4s._
import org.http4s.dsl._
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.duration._
import scalaz.concurrent.Strategy.DefaultTimeoutScheduler
import scalaz.concurrent.Task
import scalaz.stream.{Process, time}
import org.json4s.native.Serialization.write

import scala.collection.mutable.ListBuffer

/**
  * A simple server which fakes a marathon event stream.
  * Created by Daniel Zolnai on 2016-07-13.
  */
class EventStreamServer {

  implicit val formats = new DefaultFormats

  private var _eventsToEmit : ListBuffer[MarathonEvent] = _

  // A Router can mount multiple services to prefixes.  The request is passed to the
  // service with the longest matching prefix.
  val service = HttpService {
    case GET -> Root / "v2" / "events" =>
      if (_eventsToEmit.nonEmpty) {
        Ok(dataStream())
      } else {
        Ok()
      }
  }

  def dataStream(): Process[Task, String] = {
    implicit def defaultScheduler = DefaultTimeoutScheduler
    val interval = 200.milliseconds
    val stream: Process[Task, String] = time.awakeEvery(interval)
      .map(_ => {
        val event = _eventsToEmit.head
        _eventsToEmit.remove(0)
        write(event)
      })
      .take(_eventsToEmit.size - 1)
    val firstEvent = _eventsToEmit.head
    _eventsToEmit.remove(0)
    (Process.emit(write(firstEvent)) ++ stream).asInstanceOf[Process[Task, String]]
  }

  def setEventsToEmit(events: ListBuffer[MarathonEvent]) = {
    _eventsToEmit = events
  }

  def start(): Unit = {
    val builder = BlazeBuilder.mountService(service)
    builder.run
  }

}
