package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.entity.event.MarathonEvent
import daniel.zolnai.marathon.serializer.DefaultFormats
import org.http4s.client.blaze.PooledHttp1Client
import org.http4s.{Uri, _}
import org.json4s.native.JsonMethods._


/**
  * The service responsible for communicating with Marathon.
  * Created by Daniel Zolnai on 2016-07-13.
  */
object MarathonService {
  final val EVENT_STREAM_API_ENDPOINT = "/v2/events"
}

class MarathonService(configService: ConfigService, implicit val formats: DefaultFormats) {

  def connect() = {
    val client = PooledHttp1Client()
    val uri = Uri.fromString(_getEventStreamURL()).valueOr(throw _)
    val request = Request(
      method = Method.GET,
      uri = uri,
      headers = Headers(Header("Accept", "text/event-stream")))
    val process = client.streaming[String](request)(_.body.pipe(scalaz.stream.text.utf8Decode))
    // I'm probably doing this in a very ugly way, but couldn't find a nicer way to do this.
    // I know that http4s can serialize strings to objects, but I'm a lot more familiar with json4s,
    // which also gives me a lot of control
    process.map(body => {
      parseAndProcessEvent(body)
      body
    }).runLog.run
  }

  /**
    * Serializes the string into an event object, and notifies the other services about the new event.
    *
    * @param body The string body received from the streaming server.
    */
  def parseAndProcessEvent(body: String) = {
    val marathonEvent = parse(body).extract[MarathonEvent]
    // TODO notify history service
  }

  /**
    * Returns the Marathon event stream URL this service should be listening on.
    *
    * @return The URL to make the streaming request to.
    */
  private def _getEventStreamURL(): String = {
    var url = configService.appConfig.marathonURL
    if (!url.startsWith("http")) {
      url = "http://" + url
    }
    if (!url.endsWith("/")) {
      url = url + "/"
    }
    url + MarathonService.EVENT_STREAM_API_ENDPOINT
  }
}
