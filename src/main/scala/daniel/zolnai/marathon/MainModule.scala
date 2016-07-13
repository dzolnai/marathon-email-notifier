package daniel.zolnai.marathon

import daniel.zolnai.marathon.service._
import com.softwaremill.macwire._

/**
  * Service repository
  * Created by Daniel Zolnai on 2016-07-13.
  */
trait MainModule {
  lazy val configService = wire[ConfigService]
  lazy val storageService = wire[StorageService]
  lazy val historyService = wire[HistoryService]
  lazy val zookeeperService = wire[ZooKeeperService]
  lazy val marathonService = wire[MarathonService]
}
