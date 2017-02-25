package controllers.rest.utils


import play.api.libs.json._
import play.api.libs.functional.syntax._


object SortingDirection extends Enumeration {
    type SortingDirection = Value
    
    val ASC = Value("asc")
    val DESC = Value("desc")
}
 
object Sorting {
    
    implicit val SORT_FORMATTER : Format[Sorting] = (
            (__ \ "column").format[Int] ~
            (__ \ "dir").format[String] )(jsonApply _, unlift(jsonUnapply))
    
    def jsonApply(column: Int, direction: String): Sorting = Sorting(column, SortingDirection.withName(direction)) 
        
    def jsonUnapply(x: Sorting): Option[(Int, String)] = Some( (x.column, x.direction.toString()) )
}


case class Sorting(column: Int, direction: SortingDirection.SortingDirection)