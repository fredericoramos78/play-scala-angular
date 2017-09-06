package silhouette

import com.mohiva.play.silhouette.api.repositories.AuthenticatorRepository
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator

import scala.concurrent.Future

trait SessionService extends AuthenticatorRepository[JWTAuthenticator]

/**
  * A do-nothing implementation of the SessionService interface. Note that when this implementation is used for DI,
  * then no session information will be stored.
  *
  * To prevent unexpected behaviours due to 'mis-extension', this class is sealed.
  */
sealed class NoopSessionService extends SessionService {
    override def find(id: String) = Future.successful(None)
    override def add(authenticator: JWTAuthenticator) = Future.successful(authenticator)
    override def update(authenticator: JWTAuthenticator) = Future.successful(authenticator)
    override def remove(id: String) = Future.successful {()}
}
