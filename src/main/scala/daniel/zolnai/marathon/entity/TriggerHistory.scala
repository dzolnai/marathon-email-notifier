package daniel.zolnai.marathon.entity

import org.joda.time.DateTime

/**
  * Contains historical trigger data.
  * Created by Daniel Zolnai on 2016-07-16.
  */
class TriggerHistory {
  var triggerId: String = _
  var lastEmailSent: Option[DateTime] = _
  var previousTriggers: List[DateTime] = List()
}
