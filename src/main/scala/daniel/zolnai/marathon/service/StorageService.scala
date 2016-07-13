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

  override def saveToFile(path: String, content: String): Unit = {
    _storage.saveToFile(path, content)
  }

  override def getFileContents(path: String): String = {
    _storage.getFileContents(path)
  }
}
