package modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

import business.auth._
import business.catalog._
import business.auth.impl._
import business.catalog.impl._
import utils.mongodb._
import utils.ExecutionContexts

//
// bootstraps all thread pools
//
class UtilitiesModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[ExecutionContexts].asEagerSingleton()
        ()
    }
}

//
// authentication implementation
//
class AuthModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[AuthenticationService].to[DummyAuthenticationServiceImpl]
        ()
    }
}

//
// all services for this project
//
class ServicesModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[CustomerService].to[BaseCustomerServiceImpl]
        ()
    }
}


//
// modules with repository implementations (dummy, mongo, etc.)
//
class DummyReposModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[CustomerRepository].to[DummyCustomerRepository]
        ()
    }
}

class MongoReposModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[MongoDBHelper].to[MongoDBHelperImpl]
        bind[CustomerRepository].to[MongoCustomerRepository]
        ()
    }
}