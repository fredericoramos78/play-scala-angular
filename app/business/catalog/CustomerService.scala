package business.catalog

import javax.inject._
import scala.concurrent.Future

import utils.ExecutionContexts
import utils.AsyncEnabled
import utils.TaxIdHelper

import model.catalog._


trait CustomerService {
    
    def listByFilter(criteria: Option[String] = None, offset: Int, length: Int): Future[Iterable[Customer]]
    def countByFilter(criteria: Option[String] = None): Future[Long] 
    
    def create(customer: Customer): Future[String]
    def modify(customer: Customer): Future[String]
    
    def findById(id: String): Future[Option[Customer]] 
    def findByTaxId(taxId: String): Future[Option[Customer]]
}



trait CustomerRepository {
  
    def selectBy(criteria: Option[String], offset: Int, length: Int): Future[Iterable[Customer]]
    def countBy(criteria: Option[String]): Future[Long]
    
    def insert(customer: Customer): Future[String]
    def update(customer: Customer): Future[String]
    
    def selectById(id: String): Future[Option[Customer]]
    def selectByTaxId(taxId: String): Future[Option[Customer]]
}