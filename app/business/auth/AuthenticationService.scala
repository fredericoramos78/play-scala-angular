package business.auth

import scala.concurrent.Future
import scala.util.Random

import java.security.MessageDigest



trait AuthenticationService {
    // used for request-based authentication check
    def isSessionValid(userEmail: String, sessionId: String): Future[Boolean]
    
    def authenticate(userEmail: String, passwrd: String): Future[String]
    
    def disconect(userEmail: String): Future[Int]
    
    
    
    def createSessionToken(userEmail: String): String = encrypt( s"[${userEmail}#${Random.nextString(32).mkString}]" )
    
    def encrypt(message: String): String = 
        MessageDigest.getInstance("MD5").digest(message.getBytes).map("%02X" format _).mkString
}

trait AuthenticationRepository {
    
    def insertSession(userEmail: String, sessionId: String): Future[Boolean]
    def deleteSessions(userEmail: String): Future[Int]
    def selectSessions(userEmail: String): Future[Iterable[String]]
}