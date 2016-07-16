package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.entity.History
import daniel.zolnai.marathon.entity.event.MarathonEvent
import daniel.zolnai.marathon.serializer.DefaultFormats
import org.slf4j.{Logger, LoggerFactory}
import org.json4s.native.JsonMethods._


/**
  * Stores the history of job failures, and initiates the triggers if needed.
  * Created by Daniel Zolnai on 2016-07-13.
  */
class HistoryService(storageService: StorageService, implicit val formats: DefaultFormats) {

  final val HISTORY_PATH = "/history"

  private val _logger: Logger = LoggerFactory.getLogger(classOf[HistoryService])

  private var _history: History = _

  def newEvent(marathonEvent: MarathonEvent) = {
    _logger.info(s"New event: ${marathonEvent.eventType.toString.toLowerCase}")
    save()
  }

  def save() = {
    storageService.saveToFile(HISTORY_PATH, "TODO")
  }

  def load() = {
    val contents = storageService.getFileContents(HISTORY_PATH)
    _history = parse(contents).extract[History]
  }


}
