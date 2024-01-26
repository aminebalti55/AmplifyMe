import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './Login/Login.component';
import { HomeComponent } from './home/home.component';
import { HomeSignedUpComponent } from './HomeSignedUp/HomeSignedUp.component';
import { CallbackComponent } from './Callback/Callback.component';
import { AdminDashboardComponent } from './AdminDashboard/AdminDashboard.component';
import { AuthGuard } from './Classes/AuthGuard'; // Import your AuthGuard


const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'homeSignedup', component: HomeSignedUpComponent ,canActivate: [AuthGuard]},
  { path: 'oauth2/callback/facebook', component: CallbackComponent },
  { path: 'adminDashboard', component: AdminDashboardComponent , canActivate: [AuthGuard]},

];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
