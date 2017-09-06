import { Component } from '@angular/core';
import { Router } from '@angular/router';

import 'rxjs/add/operator/toPromise';

import { LoginService } from './login.service'
import { LoggerService } from '../utils/logger.service'



@Component({
  templateUrl: 'login.component.html'
})
export class LoginComponent {

    constructor(private logger: LoggerService, private login: LoginService, private router: Router){}

    errorMessage: String = null;
    username: String = '';
    password: String = '';

    onLogin(): void {
        this.errorMessage = null;
        this.logger.debug('onLogin() acionado');
        if (this.username !== '') {
            this.logger.debug('Existe um userame => ' + this.username);
            this.login.login(this.username, this.password)
                .toPromise()
                .then((isLoginOk) => {
                    this.logger.debug("Login OK?=" + isLoginOk);
                    this.router.navigate(['']);
                })
                .catch((error) => {
                    console.log(error);
                    this.errorMessage = error;
                });
        }
    }
}
