package business.catalog.impl

import javax.inject.Inject
import scala.concurrent.Future

import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.bson.conversions.Bson
import org.mongodb.scala._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Projections
import org.mongodb.scala.model.Sorts

import business.catalog.CustomerRepository
import business.RepositoryException
import _root_.model.catalog._
import utils._
import utils.mongodb._


class MongoCustomerRepository @Inject() (ec: ExecutionContexts, mongoHelper: MongoDBHelper) 
    extends AsyncEnabled(ec) with CustomerRepository with AppLogger {

    
    
    implicit val threadpool = ec.repos
    
    
    
    lazy val SORT_BY = Sorts.descending("name")
    
    final val COLLECTION_NAME: String = "CustomerCollection"
    lazy val db: MongoDatabase = mongoHelper.mongoDB
    
    
    
    override def selectBy(criteria: Option[String], offset: Int, length: Int): Future[Iterable[Customer]] = {
        var filterBy: Option[Bson] = criteria.map { filter =>
            LOGGER.debug(s"Querying with filter: '${filter}'")
            Filters.or(Filters.eq("name", filter), Filters.eq("taxId", filter))
        } 
        this._list(filterBy, offset, length)
    }
    
    override def countBy(criteria: Option[String]): Future[Long] = {
        var filterBy: Option[Bson] = criteria.map { filter =>
            LOGGER.debug(s"Querying with filter: '${filter}'")
            Filters.or(Filters.eq("name", filter), Filters.eq("taxId", filter))
        } 
        this._count(filterBy)
    }
    
    override def selectById(id: String): Future[Option[Customer]] = {
        var filters = Filters.eq("_id", id)
        LOGGER.debug(s"Filtering for customer with _id=${id}")
        this._list(Some(filters), 0, 1) map { list => 
            val result = list.headOption 
            LOGGER.debug(s"Customer info was found? = ${result.isDefined}")
            result
        }
    }

    override def selectByTaxId(taxId: String): Future[Option[Customer]] = {
        var filters = Filters.eq("taxId", taxId)
        this._list(Some(filters), 0, 1) map { _.headOption }
    }
    
    override def insert(customer: Customer): Future[String] = {
        customer._id = Some(mongoHelper.reserveObjectId())
        db.getCollection(COLLECTION_NAME).insertOne( toDoc(customer) ).head().map { x => customer._id.get }
    }
    
    override def update(customer: Customer): Future[String] = { 
        db.getCollection(COLLECTION_NAME).replaceOne( Filters.eq("_id", customer._id), toDoc(customer) )
            .head().map { x => 
                if (x.getModifiedCount == 1) { customer._id.get } else { throw new RepositoryException("No rows were updated.") } 
            }
    }
    
    
    //
    // Private actions
    //
    
    
    private[this] def _list(filterBy: Option[Bson], offset: Int, length: Int): Future[Iterable[Customer]] = {
        // query
        var observable: FindObservable[Document] = null
        // filter, if set
        filterBy.map { filter => observable = db.getCollection(COLLECTION_NAME).find(filter) } 
                .getOrElse { observable = db.getCollection(COLLECTION_NAME).find() }
        // projecting only the necessary fields
        LOGGER.debug(s"Querying collection ${COLLECTION_NAME}")
        observable.sort(SORT_BY)
            .skip(offset).limit(length)
            .toFuture().map { _.map(x => fromDoc(x)).toIterable }
    }
    
    private[this] def _count(filterBy: Option[Bson] = None): Future[Long] = 
        filterBy.map(f => db.getCollection(COLLECTION_NAME).count(f).head())
            .getOrElse(db.getCollection(COLLECTION_NAME).count().head())
    
    private[this] def fromDoc(message: Document): Customer = {
        try {
            val jsValue = Json.parse(message.toJson())
            Json.fromJson(jsValue) match { 
                case x: JsError => throw new IllegalArgumentException(s"Error when reading fromDoc() for IncomingMessage: ${jsValue} : ${x}" )
                case x: JsSuccess[Customer] => x.get
            }
        } catch { case ex: Throwable => throw new IllegalArgumentException(s"Error when reading fromDoc() for IncomingMessage", ex) }
    }
    
    private[this] def toDoc(customer: Customer): Document = {
        val jsonObj: JsValue = Json.toJson(customer)(CUSTOMER_FORMATTER)
        LOGGER.debug(s"Customer in Json format => ${jsonObj}")
        Document(jsonObj.toString())
    }
    
    
    //
    // Mongo JSON formatters
    //
    import Address._
    
    implicit val CUSTOMER_FORMATTER : Format[Customer] = (
            (__ \ "_id").format[String] ~
            (__ \ "name").format[String] ~
            (__ \ "taxId").format[String] ~
            (__ \ "address").format[Option[Address]]
         )(jsonApply _, unlift(jsonUnapply))
    
    def jsonApply(_id: String, name: String, taxId: String, address: Option[Address] = None): Customer = {
        var optId: Option[String] = None
        if (_id != null) { optId = Some(_id) }
        Customer(optId, name, taxId, address)
    }
        
    def jsonUnapply(x: Customer): Option[(String, String, String, Option[Address])] =
        Some( (x._id.getOrElse(null), x.name, x.taxId, x.address) )
}