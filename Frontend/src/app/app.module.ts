import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { UserService } from '../app/Classes/UserService';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { OurServicesComponent } from './OurServices/OurServices.component';
import { TestimonialsComponent } from './testimonials/testimonials.component';
import { LoginComponent } from './Login/Login.component';
import { FormsModule,ReactiveFormsModule,FormBuilder, FormGroup, Validators  } from '@angular/forms'; // Import FormsModule
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'; // Import HttpClientModule
import { MatSnackBarModule } from '@angular/material/snack-bar'; // Import MatSnackBarModule
import { HomeSignedUpComponent } from './HomeSignedUp/HomeSignedUp.component';
import { CallbackComponent } from './Callback/Callback.component';
import { AdminDashboardComponent } from './AdminDashboard/AdminDashboard.component';
import { GiftPointsModalComponent } from './gift-points-modal/gift-points-modal.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from './ConfirmationDialog/ConfirmationDialog.component';
import { AuthGuard } from './Classes/AuthGuard';
import { AddSocialPageDialogComponent } from './add-social-page-dialog/add-social-page-dialog.component'; // Import your AuthGuard
import { MatSelectModule } from '@angular/material/select';

@NgModule({
  declarations: [
    AppComponent,
      HomeComponent,
      OurServicesComponent,
      TestimonialsComponent,
      LoginComponent,
      HomeSignedUpComponent,
      CallbackComponent,
      AdminDashboardComponent,
      GiftPointsModalComponent,
      ConfirmationDialogComponent,
      AddSocialPageDialogComponent
   ],
  imports: [
    BrowserModule,
    AppRoutingModule,FormsModule,HttpClientModule, BrowserAnimationsModule,MatSnackBarModule
    ,ReactiveFormsModule, MatFormFieldModule,
    MatInputModule,MatDialogModule,MatSelectModule
  ],
  providers: [AuthGuard],
  bootstrap: [AppComponent]
})
export class AppModule { }
