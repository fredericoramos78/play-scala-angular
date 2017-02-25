package utils

import play.api.mvc._
import business.BusinessException



trait AppLogger {
    
    lazy val LOGGER = play.api.Logger(this.getClass)
    
    
    def handleControllerException(ex: Throwable): Result = {
        LOGGER.error(s"Error caught in controller:", ex)
        val exceptionMessage: String = ex match {
            case ex: BusinessException => ex.message
            case ex: Throwable => ex.getLocalizedMessage
        }
        Results.BadRequest(exceptionMessage)
    }
}