package daniel.zolnai.marathon.entity

import org.joda.time.DateTime

/**
  * Contains all the previous failures.
  * Created by Daniel Zolnai on 2016-07-16.
  */
class History {
  var triggerHistories: List[TriggerHistory] = List()
  var lastSaved: Option[DateTime] = None
}
