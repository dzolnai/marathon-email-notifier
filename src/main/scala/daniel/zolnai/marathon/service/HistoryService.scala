package daniel.zolnai.marathon.service

/**
  * Stores the history of job failures, and initiates the triggers if needed.
  * Created by Daniel Zolnai on 2016-07-13.
  */
class HistoryService(storageService: StorageService) {

  final val HISTORY_PATH = "/history"

  def newEvent() = {
    // TODO update internal state and send email if needed
    save()
  }

  def save() = {
    storageService.saveToFile(HISTORY_PATH, "TODO")
  }

  def load() = {
    val contents = storageService.getFileContents(HISTORY_PATH)
    // TODO parse and store
  }


}
