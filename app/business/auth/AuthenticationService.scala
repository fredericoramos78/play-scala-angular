package business.auth

import scala.concurrent.Future
import scala.util.Random
import java.security.MessageDigest



trait AuthenticationService {
    // used for request-based authentication check
    def loadSessionForUser(userEmail: String): Future[Option[String]]
    
    def authenticate(userEmail: String, passwrd: String): Future[String]
    def disconect(userEmail: String): Future[Int]
    
    
    
    def createSessionToken: String = Random.nextString(32).getBytes.map("%02X" format _).mkString
    
    def cryptSession(userEmail: String, sessionId: String): String = 
        MessageDigest.getInstance("MD5").digest(s"[${userEmail}#${sessionId}]".getBytes).map("%02X" format _).mkString

}

trait AuthenticationRepository {
    def selectSession(userEmail: String): Future[Option[String]]
}