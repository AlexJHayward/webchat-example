package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject.{Singleton, _}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import redis.RedisClient

@Singleton
class Application @Inject()(cc: ControllerComponents, configuration: play.api.Configuration)( //inject configuration
    implicit system: ActorSystem,
    mat: Materializer)
    extends AbstractController(cc) {

  private val redis           = RedisClient(host = configuration.underlying.getString("redis.host")) //initialise the redis client
  private val channel: String = configuration.underlying.getString("redis.channel")  //store the name of the channel to connect to

  //the following defines actions taken on different HTTP requests (entry points defined in conf/routes)

  //produce the html page, inject the publish and subscribe URLs (really Play calls containing those addresses)
  def index = Action { implicit req ⇒
    Ok(views.html.index(routes.Application.publishMessage(), routes.Application.subscribeToChannel()))
  }

  //return a websocket connected to the Redis actor that is listening to the redis channel
  def subscribeToChannel = WebSocket.accept[String, String] { req ⇒
    ActorFlow.actorRef { out ⇒
      RedisActor.props(redis, out, channel)
    }
  }

  // pulish a new message to the channel
  def publishMessage = Action(parse.json) { req ⇒
    redis.publish(channel, req.body.toString)
    Ok
  }
}
