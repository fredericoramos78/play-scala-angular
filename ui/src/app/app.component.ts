import { Component } from '@angular/core';

import { AuthService } from './auth/auth.service'

@Component({
  selector: 'my-app',
  templateUrl: 'app.component.html'
})
export class AppComponent {

    constructor(private auth: AuthService) {}

    name = 'Angular';

    getUserHash(): String {
        return this.auth.loadToken();
    }
}
