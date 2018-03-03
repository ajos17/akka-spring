import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by ajos21 on 2/19/2018.
  */
object Seed extends App{

    val host= if(args.isEmpty) "127.0.0.1" else args(0)
    val port= if(args.isEmpty) "2551" else args(1)
    val seedConfig= ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname=$host"))
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [seed]"))
      .withFallback(ConfigFactory.load("seed")).withFallback(ConfigFactory.load())
    val actorSystem= ActorSystem("big-da-microservices", seedConfig)


}

