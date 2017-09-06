import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

import { LoggerService } from '../utils/logger.service'

export const TOKEN_NAME: string = 'token'

@Injectable()
export class AuthService {

    constructor(private logger: LoggerService, private jwtHelper: JwtHelperService){}

    loadToken(): string {
        return localStorage.getItem(TOKEN_NAME);
    }

    saveToken(token: string): void {
        localStorage.setItem(TOKEN_NAME, token);
    }

    clearToken(): void {
        localStorage.removeItem(TOKEN_NAME);
    }

    isLoggedIn(): boolean {
        this.logger.debug('Current token is => ' + this.loadToken());
        if (this.loadToken() !== null) {
            try {
                let isOk = this.jwtHelper.isTokenExpired(this.loadToken());
                return !isOk;
            } catch(exception) {
                this.logger.error(exception);
                this.clearToken();
                return false;
            }
        } else { return false; }
    }
}
