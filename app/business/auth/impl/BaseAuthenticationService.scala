package business.auth.impl

import javax.inject._

import scala.concurrent.Future

import business.auth._
import utils.AsyncEnabled
import utils.ExecutionContexts



abstract class BaseAuthenticationService @Inject() (repo: AuthenticationRepository, ec: ExecutionContexts) 
    extends AsyncEnabled(ec) with AuthenticationService {

    
    implicit val threadPool = ec.services
    
    
    override def authenticate(userEmail: String, password: String): Future[String] = {
        validateUserAndPassword(userEmail, password) flatMap { isOk =>
            if (isOk) {
                val sessionId = this.createSessionToken(userEmail)
                repo.insertSession(userEmail, sessionId) map { r => sessionId }
            } else { 
                throw new AuthenticationException(AuthenticationException.INCORRECT_PASSWORD_ERROR)
            }
        }
    }

    /**
     *  This method should handle all user validation and return <code>true</code> when the password is correct. In case of an 
     *    invalid situation like incorrect password or inactive user (and any other possible invalid scenario) it should raise 
     *    an <code>AuthenticationException</code> using the error code for the situation.
     */
    def validateUserAndPassword(userEmail: String, password: String): Future[Boolean]
    
    
    override def disconect(userEmail: String): Future[Int] = 
        repo.deleteSessions(userEmail)
    
    def isSessionValid(userEmail: String, sessionId: String): Future[Boolean] = 
        repo.selectSessions(userEmail) map { listOfSessions => listOfSessions.exists(_.equals(sessionId)) }

}