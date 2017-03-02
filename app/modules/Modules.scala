package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

import play.api.libs.json.JsValue

import business.auth._
import business.catalog._
import business.auth.impl._
import business.catalog.impl._
import utils.mongodb._
import utils.ExecutionContexts
import play.api.mvc.AnyContent
import controllers._



//
// bootstraps all thread pools
//
class UtilitiesModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[ExecutionContexts].asEagerSingleton()
        ()
    }
}

// dummy authentication implementation. When using dummy auth service the repository should also be dummy
class DummyAuthModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[AuthenticationService].to[DummyAuthenticationService]
        bind[AuthenticationRepository].to[DummyAuthenticationRepository]
        bind[SecuredChecker[JsValue]].to[SecuredCheckerImpl[JsValue]] 
        ()
    }
}

class AuthModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[AuthenticationService].to[BaseAuthenticationService]
        ()
    }
}


//
// base implementation for all services. The repository implementation can be selected using specific repo 
//    modules.
//
class ServicesModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[CustomerService].to[BaseCustomerService]
        ()
    }
}


// dummy implementation for all repositories (except authentication, which is only added when using dummy auth service)
class DummyReposModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[CustomerRepository].to[DummyCustomerRepository]
        ()
    }
}

// repository backed by mongo db
class MongoReposModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[MongoDBHelper].to[MongoDBHelperImpl]
        bind[CustomerRepository].to[MongoCustomerRepository]
        bind[AuthenticationRepository].to[MongoAuthenticationRepository]
        ()
    }
}