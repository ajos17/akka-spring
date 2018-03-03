package security.spring

/**
  * Created by ajos21 on 3/1/2018.
  */
import akka.actor.{Actor, IndirectActorProducer}
import org.springframework.context.ApplicationContext

class DependencyInjector  (applicationContext: ApplicationContext, beanName: String)
  extends IndirectActorProducer {

  override def actorClass = applicationContext.getType(beanName).asInstanceOf[Class[Actor]]
  override def produce =
    applicationContext.getBean(beanName).asInstanceOf[Actor]

  /*def this(beanName: String) = this("", beanName)*/
}
