package daniel.zolnai.marathon.service

import org.http4s.Http4s._
import org.http4s.client.blaze.PooledHttp1Client
import org.http4s.{Uri, _}


/**
  * The service responsible for communicating with Marathon.
  * Created by Daniel Zolnai on 2016-07-13.
  */
class MarathonService(configService: ConfigService) {

  final val EVENT_STREAM_API_ENDPOINT = "v2/events"

  def connect() = {
    val client = PooledHttp1Client()
    val uri = Uri.fromString(_getEventStreamURL()).valueOr(throw _)
    val request = Request(
      method = Method.GET,
      uri = uri,
      headers = Headers(Header("Accept", "text/event-stream")))
    val response = client.expect[String](request)
    response.map(response => {
      println(response)
    })
  }

  private def _getEventStreamURL(): String = {
    var url = configService.appConfig.marathonURL
    if (!url.startsWith("http")) {
      url = "https://" + url
    }
    if (!url.endsWith("/")) {
      url = url + "/"
    }
    url + EVENT_STREAM_API_ENDPOINT
  }
}
