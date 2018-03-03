package security.spring

import javax.xml.ws.spi.WebServiceFeatureAnnotation

import akka.actor.ActorSystem
import authworker.AuthMain.args
import authworker.AuthWorkerActor
import com.typesafe.config.ConfigFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{AnnotationConfigApplicationContext, ComponentScan, Configuration}
import security.spring.CustomSecurityConfig

/**
  * Created by ajos21 on 3/1/2018.
  */

object SpringSecurityMain extends App{

  val ctx= new AnnotationConfigApplicationContext()
  ctx.scan("security.spring","authworker")
  ctx.refresh()

  val services= ctx.getBean(classOf[ActorSystemBean])/*actorOf(SpringExtId(system).props("ldap_verifier"), "authWorker")*/
  val springSecu= services.securityService

  val host= if(args.isEmpty) "127.0.0.1" else args(0)
  val port= if(args.isEmpty) "0" else args(1)
  val authConfig= ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
    .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname=$host"))
    .withFallback(ConfigFactory.parseString("akka.cluster.roles = [auth]"))
    .withFallback(ConfigFactory.load("auth"))
    .withFallback(ConfigFactory.load())
  //val system= ActorSystem("big-da-microservices", authConfig)
  //system.actorOf(AuthWorkerActor.props,"authWorker")

}

