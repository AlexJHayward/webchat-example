package controllers

import java.net.InetSocketAddress

import akka.actor.{ActorRef, Props}
import play.api.Logger
import redis.RedisClient
import redis.actors.RedisSubscriberActor
import redis.api.pubsub.{Message, PMessage}

//akka actor implementing the redis listening capabilities
class RedisActor(redis: RedisClient, out: ActorRef, channels: Seq[String] = Nil)
    extends RedisSubscriberActor(new InetSocketAddress(redis.host, redis.port),
                                 channels,
                                 patterns = Seq.empty,
                                 onConnectStatus = connected ⇒ { println(s"connected: $connected") }) {

  Logger.logger.debug(s"started")

  //implement/override required members

  //handler for receiving a message
  def onMessage(message: Message) = {
    out ! message.data.decodeString("UTF-8")
  }

  //handler for receiving a pattern message, not used in this application but implementation is required by redis-scala
  //so may as well decode in the same manner
  def onPMessage(patternMessage: PMessage) = {
    out ! patternMessage.data.decodeString("UTF-8")
  }

  //log on channel close/stop messages
  override def onClosingConnectionClosed(): Unit = Logger.logger.debug(s"connection closed")
  override def postStop(): Unit = Logger.logger.debug(s"stopping")
}

//companion object for instace construction
object RedisActor {
  def props(redis: RedisClient, out: ActorRef, channel: String): Props = Props(new RedisActor(redis, out, Seq(channel)))
}
