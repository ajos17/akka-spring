package security.spring

import javax.annotation.PreDestroy

import akka.actor.{Actor, ActorSystem, Props}
import authworker.AuthWorkerActor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.stereotype.Component
//import security.spring.SpringExt

/**
  * Created by ajos21 on 3/1/2018.
  */
@Component
class ActorSystemBean @Autowired()(applicationContext: ApplicationContext) {

  private val system = ActorSystem("big-da-microservices")

  SpringExtId(system).applicationContext = applicationContext

  lazy val securityService = system.actorOf(Props(SpringExtId(system).applicationContext.getBean(classOf[AuthWorkerActor])), "authWorker")//change the class

  import scala.concurrent.ExecutionContext.Implicits.global
  @PreDestroy
  def shutdown() = system.terminate().foreach(_=> println("Actor system was shutdown"))
}
