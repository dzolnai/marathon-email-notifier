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

  def dataStream(n: Int): Process[Task, String] = {
    implicit def defaultScheduler = DefaultTimeoutScheduler
    val interval = 1.seconds
    val stream = time.awakeEvery(interval)
      .map(_ => s"Current system time: ${System.currentTimeMillis()} ms\n")
      .take(n)

    Process.emit(s"Starting $interval stream intervals, taking $n results\n\n")
    stream
  }

  // A Router can mount multiple services to prefixes.  The request is passed to the
  // service with the longest matching prefix.
  val service = HttpService {
    case GET -> Root / "streaming" =>
      // Its also easy to stream responses to clients
      Ok(dataStream(100))
  }

  val builder = BlazeBuilder.mountService(service)
  builder.run



}
