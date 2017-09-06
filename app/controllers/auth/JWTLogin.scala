package controllers.auth

import javax.inject.Inject

import akka.actor.ActorSystem
import com.mohiva.play.silhouette.api.{LoginInfo, LogoutEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.libs.json.{Json, Reads}
import play.api.mvc.{AbstractController, ControllerComponents}
import silhouette.AuthJWTEnvironment

import scala.concurrent.{ExecutionContext, Future}


case class LoginForm(emailAddress: String, password: String)


class JWTLogin @Inject()(components: ControllerComponents, system: ActorSystem,
                         silhouette: Silhouette[AuthJWTEnvironment], credentialsProvider: CredentialsProvider)
    extends AbstractController(components) {

    implicit val LOGIN_JSON_READER: Reads[LoginForm] = Json.reads[LoginForm]
    implicit val EXECUTION_CONTEXT: ExecutionContext = system.dispatchers.lookup("silhouette.dispatcher")


    def login = Action.async(parse.json) { implicit request =>
        request.body.validate[LoginForm](LOGIN_JSON_READER).map { loginForm =>
            // authenticate
            val loginInfo = LoginInfo(credentialsProvider.id, loginForm.emailAddress)
            // Silhouette HTTP-aware actions
            silhouette.env.authenticatorService.create(loginInfo) flatMap { authenticator =>
                silhouette.env.authenticatorService.init(authenticator) map { token =>
                    Ok(Json.obj("token" -> token))
                }
            }
        } recoverTotal { case error => Future.successful(BadRequest(Json.obj("error" -> error.toString()))) }
    }

    def logout = silhouette.SecuredAction.async { implicit request =>
        silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
        silhouette.env.authenticatorService.discard(request.authenticator, Ok)
    }
}
