package business.catalog.impl

import javax.inject.Inject
import scala.concurrent.Future
import scala.util._

import play.api.i18n.MessagesApi

import model.catalog._
import business.catalog._
import business.BusinessException
import utils._

class BaseCustomerService @Inject() (repository: CustomerRepository, ec: ExecutionContexts, messagesApi: MessagesApi) extends AsyncEnabled(ec) with CustomerService {

    implicit val threadpool = ec.repos
    
    override def listByFilter(criteria: Option[String] = None, offset: Int, length: Int): Future[Iterable[Customer]] = repository.selectBy(criteria, offset, length)
    override def countByFilter(criteria: Option[String] = None): Future[Long] = repository.countBy(criteria)
    
    override def findById(id: String): Future[Option[Customer]] = repository.selectById(id) 
    override def findByTaxId(taxId: String): Future[Option[Customer]] = repository.selectByTaxId(taxId)

    override def create(customer: Customer): Future[String] =
        Future { validate(customer, true) } flatMap { x =>
            repository.selectByTaxId(customer.taxId) flatMap { customerInfo =>
                customerInfo 
                    .map { i => throw new BusinessException(messagesApi("cat.customers.error.duplicateTaxId", customer.taxId)) } 
                    .getOrElse { repository.insert(customer) }
        }
    }
    
    override def modify(customer: Customer): Future[String] = {
        Future { validate(customer, false) } flatMap { x =>
            repository.selectByTaxId(customer.taxId) flatMap { customerInfo =>
                customerInfo 
                    .map { c => 
                        // update if the selected customer is the same being updated!
                        if (c._id.get.equals(customer._id.get)) { repository.update(customer) }
                        else { Future.failed(new BusinessException(messagesApi("cat.customers.error.duplicateTaxId", customer.taxId))) } 
                    } 
                    .getOrElse { repository.update(customer) }
            }
        }
    }
    
    private[this] def validate(customer: Customer, isNew: Boolean): Unit = {
        if (isNew && customer._id.isDefined) { throw new BusinessException(messagesApi("cat.customers.error.newCustomerWithId")) }
        if (!isNew && !customer._id.isDefined) { throw new BusinessException(messagesApi("cat.customers.error.updateCustomerWithoutId")) }
        if (!TaxIdHelper.isTaxIdValid(customer.taxId)) { throw new BusinessException(messagesApi("cat.customers.error.invalidTaxId", customer.taxId)) }
    }
}