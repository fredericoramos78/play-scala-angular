package controllers.rest.auth


import javax.inject._
import scala.concurrent.Future
import play.api.mvc._
import play.api.libs.json.Json
import play.api.i18n._
import play.api.Configuration
import akka.actor.ActorSystem

import jsmessages.JsMessagesFactory
import org.webjars.play.RequireJS

import utils.ExecutionContexts
import business.auth._
import utils.AsyncEnabled
import controllers._


class AuthentationController @Inject() (webJarAssets: WebJarAssets, requireJS: RequireJS, val messagesApi: MessagesApi, 
                                    jsMessagesFactory: JsMessagesFactory, authService: AuthenticationService, 
                                    configuration: Configuration, system: ActorSystem, ec: ExecutionContexts) 
    extends AsyncEnabled(ec) with Controller with I18nSupport { 
  
    implicit val threadpool = ec.services
    
    
    case class LoginForm(userEmail: String, password: String)
    implicit val JSON_LOGIN_READER = Json.reads[LoginForm]
    
    def login = SimpleAction(configuration, system) { Action.async(parse.json) { request =>
        request.body.validate[LoginForm].map { form =>
            authService.authenticate(form.userEmail, form.password) map { sessionId =>
                Ok( Json.obj("session" -> sessionId))
            } recover { 
                case ex: AuthenticationException => BadRequest(ex.getMessage)
                case ex: Throwable => BadRequest(ex.getMessage)
            }
        } recoverTotal { case error => Future.successful(BadRequest(error.toString())) }
    } }
    
    def logout = SimpleAction(configuration, system) { Action.async(parse.json) { request =>
        
        request.headers.get(SecuredAction.USER_KEY) map { userEmail =>
            authService.disconect(userEmail) map { sessionCount =>
                Ok( Json.obj("sessions" -> sessionCount))
            } recover { 
                case ex: AuthenticationException => BadRequest(ex.getMessage)
                case ex: Throwable => BadRequest(ex.getMessage)
            }
        // should never get here, but...
        } getOrElse { Future.successful(Results.Unauthorized) }
    } }
}