/**
 * Copyright (C) 2015 Roland Kuhn <http://rolandkuhn.com>
 */
package com.reactivedesignpatterns.chapter15

import akka.typed._
import akka.typed.ScalaDSL._
import akka.typed.AskPattern._
import scala.concurrent.duration._
import akka.util.Timeout
import akka.pattern.AskTimeoutException

object Aggregator {

  case class GetTheme(user: String, replyTo: ActorRef[ThemeResult])
  case class ThemeResult(css: String)

  case class GetPersonalNews(user: String, replyTo: ActorRef[PersonalNewsResult])
  case class PersonalNewsResult(news: List[String])

  case class GetTopNews(replyTo: ActorRef[TopNewsResult])
  case class TopNewsResult(news: List[String])

  case class GetFrontPage(user: String, replyTo: ActorRef[FrontPageResult])
  case class FrontPageResult(user: String, css: String, news: List[String])

  case class GetOverride(replyTo: ActorRef[OverrideResult])
  sealed trait OverrideResult
  case object NoOverride extends OverrideResult
  case class Override(css: String, news: List[String]) extends OverrideResult

  class FrontPageResultBuilder(user: String) {
    private var css: Option[String] = None
    private var personalNews: Option[List[String]] = None
    private var topNews: Option[List[String]] = None

    def addCSS(css: String): Unit = this.css = Option(css)
    def addPersonalNews(news: List[String]): Unit = this.personalNews = Option(news)
    def addTopNews(news: List[String]): Unit = this.topNews = Option(news)

    def timeout(): Unit = {
      if (css.isEmpty) css = Some("default.css")
      if (personalNews.isEmpty) personalNews = Some(Nil)
      if (topNews.isEmpty) topNews = Some(Nil)
    }

    def isComplete: Boolean = css.isDefined && personalNews.isDefined && topNews.isDefined

    def result: FrontPageResult = {
      val topSet = topNews.get.toSet
      val allNews = topNews.get ::: personalNews.get.filterNot(topSet.contains)
      FrontPageResult(user, css.get, allNews)
    }
  }

  private def pf(p: PartialFunction[AnyRef, Unit]): p.type = p

  def frontPage(themes: ActorRef[GetTheme], personalNews: ActorRef[GetPersonalNews], topNews: ActorRef[GetTopNews]): Behavior[GetFrontPage] =
    ContextAware { ctx =>
      Static {
        case GetFrontPage(user, replyTo) =>
          val childRef = ctx.spawnAnonymous(Props {
            val builder = new FrontPageResultBuilder(user)
            Partial[AnyRef](
              pf {
                case ThemeResult(css)         => builder.addCSS(css)
                case PersonalNewsResult(news) => builder.addPersonalNews(news)
                case TopNewsResult(news)      => builder.addTopNews(news)
                case ReceiveTimeout           => builder.timeout()
              } andThen { _ =>
                if (builder.isComplete) {
                  replyTo ! builder.result
                  Stopped
                } else Same
              })
          })
          themes ! GetTheme(user, childRef)
          personalNews ! GetPersonalNews(user, childRef)
          topNews ! GetTopNews(childRef)
          ctx.schedule(1.second, childRef, ReceiveTimeout)
      }
    }

  def futureFrontPage(themes: ActorRef[GetTheme], personalNews: ActorRef[GetPersonalNews], topNews: ActorRef[GetTopNews]): Behavior[GetFrontPage] =
    ContextAware { ctx =>
      import ctx.executionContext
      implicit val timeout = Timeout(1.second)

      Static {
        case GetFrontPage(user, replyTo) =>
          val cssFuture =
            (themes ? (GetTheme(user, _: ActorRef[ThemeResult])))
              .map(_.css)
              .recover {
                case _: AskTimeoutException => "default.css"
              }
          val personalNewsFuture =
            (personalNews ? (GetPersonalNews(user, _: ActorRef[PersonalNewsResult])))
              .map(_.news)
              .recover {
                case _: AskTimeoutException => Nil
              }
          val topNewsFuture =
            (topNews ? (GetTopNews(_: ActorRef[TopNewsResult])))
              .map(_.news)
              .recover {
                case _: AskTimeoutException => Nil
              }
          for {
            css <- cssFuture
            personalNews <- personalNewsFuture
            topNews <- topNewsFuture
          } {
            val topSet = topNews.toSet
            val allNews = topNews ::: personalNews.filterNot(topSet.contains)
            replyTo ! FrontPageResult(user, css, allNews)
          }
      }
    }

  def futureFrontPageWithOverride(themes: ActorRef[GetTheme], personalNews: ActorRef[GetPersonalNews],
                                  topNews: ActorRef[GetTopNews], overrides: ActorRef[GetOverride]): Behavior[GetFrontPage] =
    ContextAware { ctx =>
      import ctx.executionContext
      implicit val timeout = Timeout(1.second)

      Static {
        case GetFrontPage(user, replyTo) =>
          val cssFuture =
            (themes ? (GetTheme(user, _: ActorRef[ThemeResult])))
              .map(_.css)
              .recover {
                case _: AskTimeoutException => "default.css"
              }
          val personalNewsFuture =
            (personalNews ? (GetPersonalNews(user, _: ActorRef[PersonalNewsResult])))
              .map(_.news)
              .recover {
                case _: AskTimeoutException => Nil
              }
          val topNewsFuture =
            (topNews ? (GetTopNews(_: ActorRef[TopNewsResult])))
              .map(_.news)
              .recover {
                case _: AskTimeoutException => Nil
              }
          val overrideFuture =
            (overrides ? (GetOverride(_: ActorRef[OverrideResult])))
              .recover {
                case _: AskTimeoutException => NoOverride
              }
          for {
            css <- cssFuture
            personalNews <- personalNewsFuture
            topNews <- topNewsFuture
            ovr <- overrideFuture
          } ovr match {
            case NoOverride =>
              val topSet = topNews.toSet
              val allNews = topNews ::: personalNews.filterNot(topSet.contains)
              replyTo ! FrontPageResult(user, css, allNews)
            case other => // nothing to do here
          }
          for {
            ovr <- overrideFuture
          } ovr match {
            case NoOverride => // nothing to do here
            case Override(css, news) =>
              replyTo ! FrontPageResult(user, css, news)
          }
      }
    }

}
