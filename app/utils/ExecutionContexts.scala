package utils

import javax.inject._
import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem

@Singleton
class ExecutionContexts @Inject() (system: ActorSystem){
  
    val BASE_CONF_NAME = "app.thread-pools"
  
    val repos: ExecutionContext = system.dispatchers.lookup(s"${BASE_CONF_NAME}.repos")
    val services: ExecutionContext = system.dispatchers.lookup(s"${BASE_CONF_NAME}.services")
}