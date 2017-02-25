package utils.mongodb

import org.mongodb.scala._
import javax.inject._
import play.api.Configuration
import scala.util.Random
import utils.AppLogger
import java.time.LocalDateTime

trait MongoDBHelper extends AppLogger { 
    val mongoClient: MongoClient
    val mongoDB: MongoDatabase
    
    val OBJECT_ID_LEN: Int = 6
    def reserveObjectId(): String = Random.alphanumeric.take(OBJECT_ID_LEN).mkString.getBytes.map("%02X" format _).mkString
}

class MongoDBHelperImpl @Inject() (config: Configuration) extends MongoDBHelper {

//    implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
//        override val converter: (Document) => String = (doc) => doc.toJson
//    }
//
//    implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
//        override val converter: (C) => String = (doc) => doc.toString
//    }
//
//    trait ImplicitObservable[C] {
//        val observable: Observable[C]
//        val converter: (C) => String
//
//        def results(): Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))
//        def headResult(): C = Await.result(observable.head(), Duration(10, TimeUnit.SECONDS))
//    }

    
    val MONGODB_CONNECT_URL: Option[String] = config.getString("app.repository.mongo.db-url")
    val MONGODB_NAME: String = config.getString("app.repository.mongo.db-name")
        .getOrElse(throw new IllegalArgumentException("MongoDB database name is not configured."))
    
    override lazy val mongoClient: MongoClient = MONGODB_CONNECT_URL.map { url => MongoClient(url) } getOrElse { MongoClient() }
    override lazy val mongoDB: MongoDatabase = mongoClient.getDatabase(MONGODB_NAME)
}