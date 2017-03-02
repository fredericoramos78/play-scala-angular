package business.auth

class AuthenticationException(val errorCode: Long, val cause: Exception) extends Exception(s"AuthenticationException: ERR#${errorCode}", cause) {
 
    def this(errorCode: Long) = this(errorCode, null)
}


object AuthenticationException {
    
    final val AUTH_ERR_BASE = 1000

    final val INCORRECT_PASSWORD_ERROR = AUTH_ERR_BASE + 1

}