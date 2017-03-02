package controllers


import javax.inject._
import java.security.MessageDigest

import scala.concurrent._
import scala.concurrent.ExecutionContext
import scala.util.Random

import akka.actor.ActorSystem

import play.api.mvc._
import play.api.Configuration
import play.api.libs.json._

import utils.AppLogger
import business.auth._


@Singleton
case class SimpleAction[A] @Inject() (configuration: Configuration, system: ActorSystem)(action: Action[A])(implicit context: ExecutionContext) 
    extends Action[A] 
    with AppLogger {
  
    lazy val parser = action.parser

    override def apply(request: Request[A]): Future[Result] = {
        action(request)
    }
}


@Singleton
case class SecuredAction[A] @Inject() (configuration: Configuration, system: ActorSystem, authChecker: SecuredChecker[A])
    (action: Action[A])(implicit context: ExecutionContext) 
    extends Action[A] 
    with AppLogger {
  
    import SecuredAction._
    
    lazy val parser = action.parser
    lazy val SKIP_SECURITY_CHECKS: Boolean = configuration.getBoolean("app.auth.skip").getOrElse(false) 
    
    override def apply(request: Request[A]): Future[Result] = {
        // if authorization skip flag is set to true
        if (SKIP_SECURITY_CHECKS) { 
            action(request)
        } else {
            // USER_KEY header must exist
            authChecker.isAuthorized(request) map { isOk => 
                // and the session saved in the authentication record should match the on in the request, considering the
                //   encryption formula defined below. Otherwise we return a HTTP UNAUTHORIZED response. 
                if (isOk) { action(request) }
            } recover { 
                case ex: Throwable => LOGGER.error("Could not check the authentication for this request:", ex) 
            }
            Future.successful( Results.Unauthorized )
        }
    }
}


object SecuredAction {
    
}

trait SecuredChecker[A] {
    def isAuthorized(request: Request[A])(implicit ec: ExecutionContext): Future[Boolean]
    def getUserIdent(request: Request[A]): Option[String]
}

class SecuredCheckerImpl[A] @Inject() (authService: AuthenticationService)
    extends SecuredChecker[A] 
    with AppLogger {
    
    final val USER_KEY = "appauth.user.email"
    final val SESSION_KEY = "appauth.user.session"

    
    override def getUserIdent(request: Request[A]): Option[String] = request.headers.get(USER_KEY)
        
    override def isAuthorized(request: Request[A])(implicit ec: ExecutionContext): Future[Boolean] = {
        this.getUserIdent(request) map { userEmail =>
            // also should exist SESSION_KEY header
            request.headers.get(SESSION_KEY) map { cryptedSession =>
                // the user in session should be have an authentication record in the database
                authService.isSessionValid(userEmail, cryptedSession)
            }  getOrElse( Future.successful(false) )
        } getOrElse( Future.successful(false) )
    }
}