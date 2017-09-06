import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from './auth/auth.service'

@Component({
  templateUrl: 'home.component.html'
})
export class HomeComponent {

    constructor(private auth: AuthService, private router: Router) {}

    name = 'Angular';

    getUserHash(): String {
        return this.auth.loadToken();
    }

    onLogout(): void {
        this.auth.clearToken();
        this.router.navigate(['/']);
    }
}
