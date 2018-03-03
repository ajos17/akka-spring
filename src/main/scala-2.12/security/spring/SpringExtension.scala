package security.spring

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, Props}
import org.springframework.context.ApplicationContext
/**
  * Created by ajos21 on 2/28/2018.
  */
class SpringExt extends Extension{
  @volatile
  var applicationContext: ApplicationContext= _

  /*def props(actorBeanName: String ): Props= {
    return Props(classOf[DependencyInjector], applicationContext, actorBeanName)
  }*/

}

object SpringExtId extends ExtensionId[SpringExt] {

  override def createExtension(system: ExtendedActorSystem) = new SpringExt
  //override def get(system: ActorSystem): SpringExt = super.get(system)
}

