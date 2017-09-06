package silhouette

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator



trait AuthJWTEnvironment extends Env {
    type I = SecuredUser
    type A = JWTAuthenticator
}