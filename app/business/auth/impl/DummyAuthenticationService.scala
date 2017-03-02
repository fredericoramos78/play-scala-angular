package business.auth.impl

import javax.inject._

import scala.concurrent.Future

import business.auth._
import utils.AsyncEnabled
import utils.ExecutionContexts
import utils.AppLogger
import scala.collection.mutable.HashMap



class DummyAuthenticationService @Inject() (repo: AuthenticationRepository, ec: ExecutionContexts) 
    extends BaseAuthenticationService(repo, ec) {

    // user and password will always return true, unless the typed password is "invalid" 
    def validateUserAndPassword(userEmail: String, password: String): Future[Boolean] = Future {
        "invalid".equals(password) == false
    }
}


class DummyAuthenticationRepository @Inject() (ec: ExecutionContexts) 
    extends AsyncEnabled(ec) with AuthenticationRepository with AppLogger {

    lazy val SESSION_MAP: HashMap[String, Seq[String]] = HashMap()
   
    
    override def insertSession(userEmail: String, sessionId: String): Future[Boolean] = Future.successful {
        // get current list of sessions or create if this is the first login for this user
        var sessionList: Seq[String] = SESSION_MAP.get(userEmail).getOrElse( Seq() )
        // save the new session id
        sessionList = sessionList.+:(sessionId)
        // update the sessions map
        SESSION_MAP.put(userEmail, sessionList)
        true
    }
    
    def deleteSessions(userEmail: String): Future[Int] = Future.successful {
        SESSION_MAP.remove(userEmail) map { sessionList => sessionList.size } getOrElse { 0 }
    }
    
    def selectSessions(userEmail: String): Future[Iterable[String]] = Future.successful {
        SESSION_MAP.get(userEmail) getOrElse { Seq.empty }
    }

}