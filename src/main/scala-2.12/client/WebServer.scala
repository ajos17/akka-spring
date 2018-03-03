package client

/**
  * Created by ajos21 on 2/21/2018.
  */
import akka.actor.{Actor, ActorLogging, ActorSelection, ActorSystem, Props, RootActorPath}
import akka.cluster.{Cluster, Member}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.remote.ContainerFormats.ActorRef
import akka.stream.ActorMaterializer
import akka.util.Timeout
import authworker.AuthMain.args
import com.typesafe.config.ConfigFactory
import master.MasterServiceActor.AuthenticateUser

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.StdIn

object WebServer {

  case class User(userId: String, pass: String)

  class Request extends Actor with ActorLogging {

    val cluster= Cluster(context.system)
    var master:ActorSelection = null
    override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
    override def postStop(): Unit = cluster.unsubscribe(self)
    def receive = {
      case state: CurrentClusterState =>
        state.members.filter(_.status== MemberStatus.Up) foreach registerMaster
      case MemberUp(mem) => registerMaster(mem)
      case user @ User(userId, pass) =>
        log.info(s"User info: $userId, $pass")
        master ! AuthenticateUser(userId,pass)
      case _ => log.info("Invalid message")
    }

    def registerMaster(member: Member)={
      if(member.hasRole("master")){
        context.system.log.info("set the master ref----------------------")
        master= context.actorSelection(RootActorPath(member.address) /"user" / "master")
      }
    }
  }

  def main(args: Array[String]) {
    val host= if(args.isEmpty) "127.0.0.1" else args(0)
    val port= if(args.isEmpty) "0" else args(1)
    val restConfig= ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.hostname=$host"))
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [rest]"))
      .withFallback(ConfigFactory.load("auth"))
      .withFallback(ConfigFactory.load())
    implicit val system= ActorSystem("big-da-microservices", restConfig)
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val request = system.actorOf(Props[Request], "restservice")

    val route =
      path("auth") {
        put {
          parameter("user", "pass") { (user, pass) =>
            // place a bid, fire-and-forget
            request ! User(user, pass)
            complete((StatusCodes.Accepted, "done"))
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

  }
}

