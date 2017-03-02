package business.auth.impl

import javax.inject._

import scala.concurrent.Future

import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.bson.conversions.Bson
import org.mongodb.scala._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Projections
import org.mongodb.scala.model.Sorts

import _root_.model.auth._
import utils._
import utils.mongodb._
import business.auth.AuthenticationRepository

class MongoAuthenticationRepository @Inject() (ec: ExecutionContexts, mongoHelper: MongoDBHelper) 
    extends AsyncEnabled(ec) with AuthenticationRepository with AppLogger {
    
    
    final val SESSIONS_COLLECTION_NAME = "_sessions"
    lazy val db: MongoDatabase = mongoHelper.mongoDB
    
    implicit val threadPool = ec.repos
    
    
    
    override def insertSession(userEmail: String, sessionId: String): Future[Boolean] = {
         LOGGER.debug(s"Saving session id ${sessionId} for user '${userEmail}'")
        val jsonObj: JsValue = Json.toJson( (userEmail, sessionId) )(SESSIONS_FORMATTER)
         db.getCollection(SESSIONS_COLLECTION_NAME).insertOne( Document(jsonObj.toString()) ).head().map { r => true }
    }
    
    override def deleteSessions(userEmail: String): Future[Int] = Future { 1 }

    override def selectSessions(userEmail: String): Future[Iterable[String]] = Future { List.empty }
    
  
    // Storing and retrieving session structure from mongoDB 
    implicit val SESSIONS_FORMATTER: Format[(String, String)] = (
            (__ \ "emailAddress").format[String] ~
            (__ \ "sessionToken").format[String]
         )(sessionJsonApply _, unlift(sessionJsonUnapply))
    
    def sessionJsonApply(userEmail: String, sessionId: String): (String, String) = (userEmail, sessionId)
    def sessionJsonUnapply(x: (String, String)): Option[(String, String)] = Some( (x._1, x._2) )

}
