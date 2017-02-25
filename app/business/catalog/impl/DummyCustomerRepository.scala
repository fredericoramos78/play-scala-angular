package business.catalog.impl

import javax.inject.Inject
import scala.concurrent.Future

import business.catalog.CustomerRepository
import model.catalog._
import utils.ExecutionContexts
import utils.AsyncEnabled


class DummyCustomerRepository @Inject() (ec: ExecutionContexts) extends AsyncEnabled(ec) with CustomerRepository {

    
    
    implicit val threadpool = ec.repos
    
    var myCustomers: Seq[Customer] = Seq( 
           Customer(Some("1"), "Customer #1", "11111111000111", Some(Address("Lancaster #1 street", "1001", null, "London", null, "UK"))), 
           Customer(Some("2"), "Customer #2", "11111111000122", Some(Address("Lancaster #2 street", "1002", null, "London", null, "UK"))),
           Customer(Some("3"), "Customer 3", "11111111000133", Some(Address("Lancaster #3 street", "1003", null, "London", null, "UK"))),
           Customer(Some("4"), "Customer #4", "11111111000144", Some(Address("Lancaster #4 street", "1004", null, "London", null, "UK"))) )
    
    override def selectBy(criteria: Option[String], offset: Int, length: Int): Future[Iterable[Customer]] = Future {
           myCustomers.filter(p => !criteria.isDefined || (p.name.toUpperCase().indexOf(criteria.get.toUpperCase()) >= 0))
    }
    
    override def countBy(criteria: Option[String]): Future[Long] = this.selectBy(criteria, 0, 1000).map(customerList => customerList.size) 
    
    override def insert(customer: Customer): Future[String] = Future {
        var id = (myCustomers.length+1).toString
        val newCustomer = Customer(Some(id), customer.name, customer.taxId, None)
        myCustomers = myCustomers :+ newCustomer
        id
    }
    
    override def update(customer: Customer): Future[String] = Future { customer._id.getOrElse("") }
    
    override def selectById(id: String): Future[Option[Customer]] = Future { myCustomers.find(_._id.get.equals(id)) }
    override def selectByTaxId(taxId: String): Future[Option[Customer]] = Future { myCustomers.find(_.taxId.equals(taxId)) }
}