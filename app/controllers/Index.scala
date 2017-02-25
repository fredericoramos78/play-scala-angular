package controllers

import javax.inject._
import play.api.mvc._
import play.api.cache._
import play.api.i18n._
import play.api.routing._

import org.webjars.play.RequireJS


@Singleton
class IndexController @Inject() (webJarAssets: WebJarAssets, val messagesApi: MessagesApi) 
    extends Controller with I18nSupport {

    
    def index = Action { 
        Ok(views.html.skel(webJarAssets))
    }
    
        def start(path: String) = Action {
        Ok(views.html.skel(webJarAssets))
    }
        
   /**
     * Retrieves all routes via reflection.
     * http://stackoverflow.com/questions/12012703/less-verbose-way-of-generating-play-2s-javascript-router
     * If you have controllers in multiple packages, you need to add each package here.
     */
    val routeCache: Seq[JavaScriptReverseRoute] = {
        Seq(
            classOf[routes.javascript],
            classOf[controllers.rest.catalog.routes.javascript]
        ).flatMap { jsRoutesClass =>
            val controllers = jsRoutesClass.getFields.map(_.get(null))
            controllers.flatMap { controller =>
                controller.getClass.getDeclaredMethods
                    .filter( action => action.getReturnType.isAssignableFrom(classOf[JavaScriptReverseRoute]) )
                    .map( action => action.invoke(controller).asInstanceOf[JavaScriptReverseRoute] )
            }
        }
    }

    /**
     * Returns the JavaScript router that the client can use for "type-safe" routes.
     * Uses browser caching; set duration (in seconds) according to your release cycle.
     *
     * @param varName The name of the global variable, defaults to `jsRoutes`
     */
    def jsRoutes(varName: String = "jsRoutes") = {
        Action { implicit request =>
            Ok(JavaScriptReverseRouter(varName)(routeCache: _*)).as(JAVASCRIPT)
        }
    }    
    
    
    
    
    //
    // routes for all views. For every new view added to the project, a route + OK(view) return must be added here. 
    //
    def displayView(templateName: String) = Action { 
        templateName match {
            case "home/landing" => Ok(views.html.app.home.landing())
            case "customers/listing" => Ok(views.html.app.catalog.customer.list())
            case _ => NotFound
        } 
    }
}