package com.reactivedesignpatterns.chapter13

import org.scalatest.WordSpec
import org.scalatest.Matchers
import org.scalactic.ConversionCheckedTripleEquals

class MultiMasterCRDTSpec extends WordSpec with Matchers with ConversionCheckedTripleEquals {
  import MultiMasterCRDT._

  "A CRDT" must {
    val allStatus = List(New, Scheduled, Executing, Finished, Cancelled, Aborted)

    "have a correct mergeStatus function" in {
      mergeStatus(New, New) should ===(New)
      mergeStatus(New, Scheduled) should ===(Scheduled)
      mergeStatus(New, Executing) should ===(Executing)
      mergeStatus(New, Finished) should ===(Finished)
      mergeStatus(New, Cancelled) should ===(Cancelled)
      mergeStatus(New, Aborted) should ===(Aborted)
      mergeStatus(Scheduled, New) should ===(Scheduled)
      mergeStatus(Scheduled, Scheduled) should ===(Scheduled)
      mergeStatus(Scheduled, Executing) should ===(Executing)
      mergeStatus(Scheduled, Finished) should ===(Finished)
      mergeStatus(Scheduled, Cancelled) should ===(Cancelled)
      mergeStatus(Scheduled, Aborted) should ===(Aborted)
      mergeStatus(Executing, New) should ===(Executing)
      mergeStatus(Executing, Scheduled) should ===(Executing)
      mergeStatus(Executing, Executing) should ===(Executing)
      mergeStatus(Executing, Finished) should ===(Finished)
      mergeStatus(Executing, Cancelled) should ===(Aborted)
      mergeStatus(Executing, Aborted) should ===(Aborted)
      mergeStatus(Finished, New) should ===(Finished)
      mergeStatus(Finished, Scheduled) should ===(Finished)
      mergeStatus(Finished, Executing) should ===(Finished)
      mergeStatus(Finished, Finished) should ===(Finished)
      mergeStatus(Finished, Cancelled) should ===(Finished)
      mergeStatus(Finished, Aborted) should ===(Finished)
      mergeStatus(Cancelled, New) should ===(Cancelled)
      mergeStatus(Cancelled, Scheduled) should ===(Cancelled)
      mergeStatus(Cancelled, Executing) should ===(Aborted)
      mergeStatus(Cancelled, Finished) should ===(Finished)
      mergeStatus(Cancelled, Cancelled) should ===(Cancelled)
      mergeStatus(Cancelled, Aborted) should ===(Aborted)
      mergeStatus(Aborted, New) should ===(Aborted)
      mergeStatus(Aborted, Scheduled) should ===(Aborted)
      mergeStatus(Aborted, Executing) should ===(Aborted)
      mergeStatus(Aborted, Finished) should ===(Finished)
      mergeStatus(Aborted, Cancelled) should ===(Aborted)
      mergeStatus(Aborted, Aborted) should ===(Aborted)
    }

    "have a symmetrical mergeStatus function" in {
      for {
        left <- allStatus
        right <- allStatus
      } withClue(s"mergeStatus($left, $right): ") {
        mergeStatus(left, right) should ===(mergeStatus(right, left))
      }
    }
    
    "merge Finished always to Finished" in {
      for {
        other <- allStatus
      } withClue(s"mergeStatus(Finished, $other): ") {
        mergeStatus(Finished, other) should ===(Finished)
      }
    }

  }

}