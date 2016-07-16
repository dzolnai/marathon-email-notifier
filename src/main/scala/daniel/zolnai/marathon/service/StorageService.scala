package daniel.zolnai.marathon.service

import daniel.zolnai.marathon.storage.{LocalStorage, Storage, ZookeeperStorage}


/**
  * Service provider which is responsible for handling the saving and restoring of application state and configuration.
  * Created by Daniel Zolnai on 2016-07-04.
  */
class StorageService(configService: ConfigService, zooKeeperService: ZooKeeperService) extends Storage {

  private val _storage: Storage = {
    if (zooKeeperService.enabled) {
      new ZookeeperStorage(zooKeeperService)
    } else {
      new LocalStorage(configService.appConfig.localDirectory.get)
    }
  }

  /**
    * Saves a content string to a file.
    *
    * @param path    The relative path to save to. Absolute paths can not be used (although you can set the working
    *                directory to the root path).
    * @param content The contents to save in the file.
    */
  override def saveToFile(path: String, content: String): Unit = {
    _storage.saveToFile(path, content)
  }

  /**
    * Retrieves the contents of a file.
    *
    * @param path The relative path to the file.
    * @return The contents of the file as a string. None if the file does not exist.
    */
  override def getFileContents(path: String): String = {
    _storage.getFileContents(path)
  }
}
