package com.reactivedesignpatterns.chapter12

import akka.cluster.ddata.ReplicatedData

object MultiMasterCRDT {

  sealed trait Status extends ReplicatedData {
    type T = Status
    def merge(that: Status): Status = mergeStatus(this, that)
    
    def predecessors: Set[Status]
    def successors: Set[Status]
  }

  case object New extends Status {
    lazy val predecessors: Set[Status] = Set.empty
    lazy val successors: Set[Status] = Set(Scheduled)
  }
  case object Scheduled extends Status {
    lazy val predecessors: Set[Status] = Set(New)
    lazy val successors: Set[Status] = Set(Executing, Cancelled)
  }
  case object Executing extends Status {
    lazy val predecessors: Set[Status] = Set(Scheduled)
    lazy val successors: Set[Status] = Set(Aborted)
  }
  case object Finished extends Status {
    lazy val predecessors: Set[Status] = Set(Executing, Aborted)
    lazy val successors: Set[Status] = Set.empty
  }
  case object Cancelled extends Status {
    lazy val predecessors: Set[Status] = Set(New, Scheduled)
    lazy val successors: Set[Status] = Set(Aborted)
  }
  case object Aborted extends Status {
    lazy val predecessors: Set[Status] = Set(Cancelled, Executing)
    lazy val successors: Set[Status] = Set(Finished)
  }

  def mergeStatus(left: Status, right: Status): Status = {
    /*
     * Keep the left Status in hand and determine whether it is a predecessor of
     * the candidate, moving on to the candidate’s successor if not successful.
     * The list of exclusions is used to avoid performing already determined
     * unsuccessful comparisons again.
     */
    def innerLoop(candidate: Status, exclude: Set[Status]): Status =
      if (isSuccessor(candidate, left, exclude)) {
        candidate
      } else {
        val nextExclude = exclude + candidate
        val branches = candidate.successors.map(succ => innerLoop(succ, nextExclude))
        branches.reduce((l, r) => if (isSuccessor(l, r, nextExclude)) r else l)
      }
    def isSuccessor(candidate: Status, fixed: Status, exclude: Set[Status]): Boolean =
      if (candidate == fixed) true
      else {
        val toSearch = candidate.predecessors -- exclude
        toSearch.exists(pred => isSuccessor(pred, fixed, exclude))
      }

    innerLoop(right, Set.empty)
  }
  
}