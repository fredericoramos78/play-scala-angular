package silhouette

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.crypto.Base64AuthenticatorEncoder
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.ValueReader
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration

import scala.concurrent.ExecutionContext.Implicits.global


//
// Silhouette 
//
class SilhouetteAuthModule extends AbstractModule with ScalaModule {

    override def configure = {
        // Services
        bind[SessionService].to[NoopSessionService]
        bind[Silhouette[AuthJWTEnvironment]].to[SilhouetteProvider[AuthJWTEnvironment]]
        bind[EventBus].toInstance(EventBus())
        bind[Clock].toInstance(Clock())
        bind[DelegableAuthInfoDAO[PasswordInfo]].to[AuthenticationService]
        bind[IDGenerator].toInstance(new SecureRandomIDGenerator)
        bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
        ()
    }


    /**
      * Provides the Silhouette environment.
      *
      * @param authService The user service implementation.
      * @param authenticatorService The authentication service implementation.
      * @param eventBus The event bus instance.
      * @return The Silhouette environment.
      */
    @Provides
    def provideEnvironment(authService: AuthenticationService,
                           authenticatorService: AuthenticatorService[JWTAuthenticator],
                           eventBus: EventBus): Environment[AuthJWTEnvironment] = {
        Environment[AuthJWTEnvironment](authService, authenticatorService, Seq(), eventBus)
    }

    /**
      * Provides the authenticator service.
      *
      * @param idGenerator The ID generator implementation.
      * @param configuration The Play configuration.
      * @param clock The clock instance.
      * @return The authenticator service.
      */
    @Provides
    def provideAuthenticatorService(sessionService: SessionService,
                                    idGenerator: IDGenerator,
                                    configuration: Configuration,
                                    clock: Clock): AuthenticatorService[JWTAuthenticator] = {
        implicit val requestPartsReader: ValueReader[Option[Seq[RequestPart.Value]]] = ValueReader.relative { config =>
            config.as[Option[Seq[String]]]("silhouette.authenticator.requestParts")
                .map(_.map(RequestPart.withName))
        }
        val config = configuration.underlying.as[JWTAuthenticatorSettings]("silhouette.authenticator")
        val authenticatorEncoder = new Base64AuthenticatorEncoder
        val sService: Option[SessionService] = sessionService match {
            case _:  NoopSessionService => None
            case _ => Some(sessionService)
        }
        new JWTAuthenticatorService(config, sService, authenticatorEncoder, idGenerator, clock)
    }

    /**
      * Provides the auth info repository.
      *
      * @param passwordInfoDAO The implementation of the delegable password auth info DAO.
      * @return The auth info repository instance.
      */
    @Provides
    def provideAuthInfoRepository(passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo]): AuthInfoRepository = {
        new DelegableAuthInfoRepository(passwordInfoDAO)
    }

    /**
      * Provides the credentials provider.
      *
      * @param authInfoRepository The auth info repository implementation.
      * @param passwordHasher The default password hasher implementation.
      * @return The credentials provider.
      */
    @Provides
    def provideCredentialsProvider(
                                      authInfoRepository: AuthInfoRepository,
                                      passwordHasher: PasswordHasher): CredentialsProvider = {
        new CredentialsProvider(authInfoRepository, PasswordHasherRegistry(passwordHasher, Seq(passwordHasher)))
    }
}


class NoopSilhouetteAuthModule extends AbstractModule with ScalaModule {
    override def configure = {
        bind[AuthenticationService].to[NoopAuthenticationService]
        bind[SessionService].to[NoopSessionService]
        ()
    }
}