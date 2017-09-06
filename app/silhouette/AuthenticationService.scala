package silhouette

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import scala.concurrent.Future

trait AuthenticationService extends DelegableAuthInfoDAO[PasswordInfo] with IdentityService[SecuredUser]

class NoopAuthenticationService extends AuthenticationService {
    override def retrieve(loginInfo: LoginInfo) = Future.successful(None)

    override def find(loginInfo: LoginInfo) = Future.successful(None)
    override def add(loginInfo: LoginInfo, authInfo: PasswordInfo) = Future.successful(null)
    override def update(loginInfo: LoginInfo, authInfo: PasswordInfo) = Future.successful(null)
    override def save(loginInfo: LoginInfo, authInfo: PasswordInfo) = Future.successful(null)
    override def remove(loginInfo: LoginInfo) = Future.successful(())
}

