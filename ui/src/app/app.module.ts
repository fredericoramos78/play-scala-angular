import { NgModule } from '@angular/core';
import { FormsModule }   from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { JwtModule, JWT_OPTIONS } from '@auth0/angular-jwt';

import { AppComponent }  from './app.component';
import { HomeComponent }  from './home.component';
import { LoginComponent }  from './login/login.component';
import { PageNotFoundComponent } from './utils/page-not-found.component'
import { AppRoutingModule } from './app-routing.module'

import { LoggerService } from './utils/logger.service'
import { TOKEN_NAME, AuthService } from './auth/auth.service'
import { LoginService } from './login/login.service'
import { SecuredGuard } from './auth/secured-guard.service';



export function jwtOptionsFactory(): any {
    return {
        tokenGetter: () => {
            return localStorage.getItem(TOKEN_NAME);
        }
    }
}

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        JwtModule.forRoot({
            jwtOptionsProvider: {
                provide: JWT_OPTIONS,
                useFactory: jwtOptionsFactory
            },
            config: { skipWhenExpired: true }
        }),
        AppRoutingModule
    ],
    providers: [
        AuthService,
        LoggerService,
        LoginService,
        SecuredGuard
    ],
    declarations: [
        AppComponent,
        HomeComponent,
        LoginComponent,
        PageNotFoundComponent
    ],
    bootstrap:    [ AppComponent ]
})
export class AppModule { }
