package controllers.rest.catalog


import javax.inject._

import scala.concurrent.Future

import play.api.mvc._
import play.api.Configuration
import play.api.libs.json._
import play.api.i18n._

import akka.actor.ActorSystem

import jsmessages.JsMessagesFactory

import org.webjars.play.RequireJS

import controllers.WebJarAssets

import controllers.SecuredChecker
import controllers.SecuredAction
import controllers.rest.utils._
import business.catalog.CustomerService
import business.BusinessException
import model.catalog._
import utils.ExecutionContexts
import utils._
import business.auth.AuthenticationService


class CustomerController @Inject() (webJarAssets: WebJarAssets, requireJS: RequireJS, val messagesApi: MessagesApi, 
                                    jsMessagesFactory: JsMessagesFactory, customerService: CustomerService,
                                    authCheker: SecuredChecker[JsValue], configuration: Configuration, 
                                    system: ActorSystem, ec: ExecutionContexts) 
    extends AsyncEnabled(ec) with Controller with AppLogger with I18nSupport { 
  
    import Customer._
    
    
    implicit val threadpool = ec.services
    
    def list = SecuredAction(configuration, system, authCheker) { Action.async(parse.json) { request =>
        request.body.validate[DatatablesPost].map { form =>
            for ( total <- customerService.countByFilter(None);
                  selected <- customerService.countByFilter(form.search.flatMap(x => x.value));
                  data <- customerService.listByFilter(form.search.flatMap(x => x.value), form.offset, form.length)
                ) yield {
                    var dataAsJson = Json.toJson(data.toList)(Customer.JSON_LIST_FORMATTER)
                    Results.Ok( DatatablesPost.prepareResponse(total, selected, dataAsJson) )
                }
        } recoverTotal { case error => Future.successful(BadRequest(error.toString())) }
    } }
    
    
    def newCustomer = SecuredAction(configuration, system, authCheker) { Action.async(parse.json) { request =>
        request.body.validate[Customer].map { form =>
            LOGGER.info(form.toString)
            customerService.create(form) map { _id =>
                Results.Ok( _id )
            } recover { 
                case ex: Throwable => this.handleControllerException(ex)
            }
        } recoverTotal { case error => Future.successful(BadRequest(error.toString())) }
    } }
    
    def editCustomer = SecuredAction(configuration, system, authCheker) { Action.async(parse.json) { request =>
        request.body.validate[Customer].map { form =>
            customerService.modify(form) map { _id => 
                Results.Ok( _id )
            } recover { 
                case ex: Throwable => this.handleControllerException(ex)
            }
        } recoverTotal { case error => Future.successful(BadRequest(error.toString())) }
    } }
    
    def loadCustomer(id: String) = SecuredAction(configuration, system, authCheker) { Action.async(parse.json) { request =>
        customerService.findById(id) map { customerInfo =>
            customerInfo.map { info => Results.Ok(Json.toJson(info)) } getOrElse { Results.BadRequest(messagesApi("cat.customers.errorLoadingCustomer")) }
        } recover { 
            case ex: Throwable => this.handleControllerException(ex)
        }
    } }
}