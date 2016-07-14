package daniel.zolnai.marathon

import org.http4s._
import org.http4s.dsl._
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.duration._
import scalaz.concurrent.Strategy.DefaultTimeoutScheduler
import scalaz.concurrent.Task
import scalaz.stream.{Process, time}

/**
  * A simple server which fakes a marathon event stream.
  * Created by Daniel Zolnai on 2016-07-13.
  */
class EventStreamServer {

  // A Router can mount multiple services to prefixes.  The request is passed to the
  // service with the longest matching prefix.
  val service = HttpService {
    case GET -> Root / "v2" / "events" =>
      Ok(dataStream(5))
  }

  def dataStream(n: Int): Process[Task, String] = {
    implicit def defaultScheduler = DefaultTimeoutScheduler
    val interval = 500.milliseconds
    val stream: Process[Task, String] = time.awakeEvery(interval)
      .map(_ => s"Current system time: ${System.currentTimeMillis()} ms\n")
      .take(n)

    (Process.emit(s"Starting $interval stream intervals, taking $n results\n\n") ++ stream).asInstanceOf[Process[Task, String]]
  }

  def start(): Unit = {
    val builder = BlazeBuilder.mountService(service)
    builder.run
  }

}
