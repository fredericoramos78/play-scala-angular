import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { AuthService } from '../auth/auth.service'
import { LoggerService } from '../utils/logger.service'

export const TOKEN_NAME: string = 'token'

@Injectable()
export class LoginService {

    constructor(private logger: LoggerService, private auth: AuthService, private http: HttpClient){}

    login(username: String, password: String): Observable<boolean> {
        return this.http.post('/api/auth/login.json', {emailAddress: username, password: password})
            .map((result) => {
                this.auth.clearToken();
                var token = result['token'];
                this.logger.debug('Received auth token => ' + token);
                if (token !== null) {
                    this.auth.saveToken(token);
                }
                var isAuthenticated = (token !== null);
                this.logger.debug('User [' + username + '] is authenticated ? => ' + isAuthenticated);
                return isAuthenticated;
            });
    }
}
