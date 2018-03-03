package authworker

import akka.actor.{Actor, ActorLogging, Props, RootActorPath}
import akka.cluster.{Cluster, Member}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus
import authworker.AuthWorkerActor.{AuthSuccessfull, Authenticate}
import master.MasterServiceActor.AuthWorkerRegistration
import msg.CommunicationMsg.{AssignWork, WorkAvailable}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.{Scope}
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.stereotype.Component


/**
  * Created by ajos21 on 2/20/2018.
  */

object AuthWorkerActor{

  case class Authenticate(user: String, password: String)
  case object AuthSuccessfull
  case object AuthFailure

  def props= Props[AuthWorkerActor]
}

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class AuthWorkerActor @ Autowired()(springSec: AuthenticationManager) extends Actor with ActorLogging{

  val cluster= Cluster(context.system)
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive ={
    case state: CurrentClusterState =>
      state.members.filter(_.status== MemberStatus.Up) foreach register
    case MemberUp(mem) => register(mem)
    case Authenticate(user, pass) =>
      //springSec.authenticate(null)
      context.system.log.info(s"auth successfull************************* $springSec")
      sender ! AuthSuccessfull
    case WorkAvailable=>
      sender() ! AssignWork
  }

  def register(member: Member)={
    if(member.hasRole("master")){
      log.info("Sending register request to master-----------------------")
      context.actorSelection(RootActorPath(member.address) /"user" / "master")! AuthWorkerRegistration
    }
  }

}
