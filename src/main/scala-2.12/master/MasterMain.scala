package master

import akka.actor.ActorSystem
import authworker.AuthMain.args
import authworker.AuthWorkerActor
import com.typesafe.config.ConfigFactory

/**
  * Created by ajos21 on 2/21/2018.
  */
object MasterMain extends App{

  val host= if(args.isEmpty) "127.0.0.1" else args(0)
  val port= if(args.isEmpty) "0" else args(1)
  val masterConfig= ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
    .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname=$host"))
    .withFallback(ConfigFactory.parseString("akka.cluster.roles = [master]"))
    .withFallback(ConfigFactory.load("master"))
    .withFallback(ConfigFactory.load())
  val system= ActorSystem("big-da-microservices", masterConfig)
  system.actorOf(MasterServiceActor.props,"master")

}
