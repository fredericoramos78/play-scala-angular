package business.auth

class AuthenticationException(val message: String, val cause: Exception) extends Exception(message, cause) {
 
    def this(message: String) = this(message, null)
}