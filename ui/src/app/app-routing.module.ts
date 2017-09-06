import { NgModule }              from '@angular/core';
import { RouterModule, Routes }  from '@angular/router';

import { SecuredGuard } from './auth/secured-guard.service';

import { AppComponent } from './app.component';
import { HomeComponent }  from './home.component';
import { LoginComponent } from './login/login.component';
import { PageNotFoundComponent } from './utils/page-not-found.component';

const appRoutes: Routes = [
    { path: 'home',
      component: HomeComponent,
      canActivate: [SecuredGuard],
    },
    { path: 'login',
      component: LoginComponent
    },
    { path: '',
      redirectTo: '/home',
      pathMatch: 'full'
    },
    { path: '**',
      component: PageNotFoundComponent
    }
];

@NgModule({
  imports: [
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: false }
    )
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {}
