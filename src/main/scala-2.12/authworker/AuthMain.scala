package authworker

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by ajos21 on 2/20/2018.
  */
object AuthMain extends App{

  val host= if(args.isEmpty) "127.0.0.1" else args(0)
  val port= if(args.isEmpty) "0" else args(1)
  val authConfig= ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
    .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname=$host"))
    .withFallback(ConfigFactory.parseString("akka.cluster.roles = [auth]"))
    .withFallback(ConfigFactory.load("auth"))
    .withFallback(ConfigFactory.load())
  val system= ActorSystem("big-da-microservices", authConfig)
  system.actorOf(AuthWorkerActor.props,"authWorker")

}
