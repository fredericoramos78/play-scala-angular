package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}


class API @Inject()(components: ControllerComponents, system: ActorSystem, configuration: Configuration)
    extends AbstractController(components) {

    val APP_VERSION: String = configuration.get[String]("app.version")
    implicit val EXECUTION_CONTEXT: ExecutionContext = system.dispatchers.lookup("silhouette.dispatcher")


    def version = Action.async { implicit request =>
        Future.successful( Ok(Json.obj("version" -> APP_VERSION)) )
    }
}
