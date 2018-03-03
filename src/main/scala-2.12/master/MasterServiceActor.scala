package master

import msg.CommunicationMsg._
import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.routing.FromConfig
import authworker.AuthWorkerActor
import master.MasterServiceActor.{Work, WorkerStatus}

import scala.collection.mutable

/**
  * Created by ajos21 on 2/20/2018.
  *
  */

object MasterServiceActor{

  def props= Props[MasterServiceActor]

  case class WorkerStatus(actorRef: ActorRef, status: String)
  case object AuthWorkerRegistration
  case object ConverterWorkerRegistration
  sealed trait Work
  case class AuthenticateUser(user:String, password:String) extends Work
}

class MasterServiceActor extends Actor with ActorLogging{

  val authWorkers= mutable.Map.empty[ActorRef,WorkerStatus]
  val converterWorkers= mutable.Map.empty[ActorRef,WorkerStatus]
  val workMap= mutable.Map.empty[Work,WorkerStatus]
  val work= mutable.Set.empty[Work]

  import MasterServiceActor._
  import AuthWorkerActor._
  override def receive = {
    case AuthWorkerRegistration =>
      val worker_address= sender().path.address
      context.system.log.info(s"registered worker $worker_address")
      context watch sender()
      authWorkers+= sender() -> WorkerStatus(sender(), "idle")

    case ConverterWorkerRegistration=>
      context watch sender()
      converterWorkers+= sender() -> WorkerStatus(sender(), "idle")

    case Terminated(worker) =>
      val worker_address= sender().path.address
      authWorkers-= worker
      //converterWorkers-= WorkerStatus(worker, _)
      context.system.log.info(s"registered worker $worker_address")
      //todo remove from work map

    case userInfo:AuthenticateUser =>
      work+= userInfo
      val user= userInfo.user
      log.info(s"received work - $user")
      notifyAuthWorker

    case AssignWork =>
      if(work.nonEmpty){
        val address= sender().path.address
        log.info(s"assigned work----------------- $address")
        val authWork= work.last.asInstanceOf[AuthenticateUser]
        sender() ! Authenticate(authWork.user, authWork.password)
        work-= work.last
      }

  }

  def notifyAuthWorker={
    authWorkers.values foreach (x=> if(x.status == "idle") x.actorRef ! WorkAvailable)
  }
}
