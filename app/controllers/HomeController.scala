package controllers

import javax.inject.{Singleton, _}

import akka.stream.scaladsl.Source
import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.libs.iteratee.streams.IterateeStreams.enumeratorToPublisher
import play.api.libs.iteratee.{Concurrent, Enumerator}
import play.api.libs.json.JsValue
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  val (chatOut: Enumerator[JsValue], chatChannel) = Concurrent.broadcast[JsValue]

  def index = Action { implicit req =>
    Ok(views.html.index(routes.HomeController.chatFeed(), routes.HomeController.postMessage()))
  }

  def chatFeed = Action {req =>
//    console.log(s"Someone joined at: ${req.remoteAddress}")
    Ok.chunked(
      Source.fromPublisher(enumeratorToPublisher(chatOut))
         via EventSource.flow
    ).as(ContentTypes.EVENT_STREAM)
  }

  def postMessage = Action(parse.json) { req =>
    chatChannel.push(req.body); Ok
  }
}
