import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CanActivate } from '@angular/router';

import { AuthService } from './auth.service';


@Injectable()
export class SecuredGuard implements CanActivate {

    constructor(private logger: LoggerService, private auth: AuthService, private router: Router) {}

    canActivate() {
        this.logger.debug('Auth guard checking... evaluating to => ' + this.auth.isLoggedIn());
        if(this.auth.isLoggedIn()) {
            return true;
        } else {
            this.router.navigate(['/login']);
            return false;
        }
    }
}
