import { Component, OnInit } from '@angular/core';
import { User } from '../Classes/User';
import { UserService } from '../Classes/UserService';
import { AuthService } from '../Classes/AuthService';

import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-Login',
  templateUrl: './Login.component.html',
  styleUrls: ['./Login.component.css']
})
export class LoginComponent implements OnInit {
  user: User = new User(); // Create a new instance of User
  username: string = '';
  password: string = '';
  currentUser: any;

  constructor(private userService: UserService ,private router: Router ,private snackBar: MatSnackBar, private authService: AuthService)  {}




  ngOnInit() {
    this.currentUser = sessionStorage.getItem('currentUser');

    if (this.currentUser) {
    
      console.log('User session exists:', this.currentUser);
    } else {

      console.log('User session does not exist.');
    }


  }


  toggleForm() {
    const container = document.querySelector('.container');
    if (container) {
      container.classList.toggle('active');
    }
  }


  signup() {
    this.userService.addUser(this.user).subscribe(
      response => {
        // Show success message in a snack bar
        this.snackBar.open('Signup successful', 'Close', { duration: 3000, panelClass: 'custom-snackbar' });
      },
      error => {
        // Handle signup error
        console.error('Signup error', error);
      }
    );
  }

  onLoginSubmit(): void {
    this.userService.login(this.username, this.password).subscribe(
      (response) => {
        if (response.authenticated) {
          console.log('Login successful', response.user);

          // Store the user's data and ID in sessionStorage
          sessionStorage.setItem('currentUser', JSON.stringify(response.user));
          sessionStorage.setItem('userId', response.user.id);

          // Store the user's token in the AuthService
          this.authService.setTokenx(response.token);

          // Check the user's role and redirect accordingly
          if (response.role === 'ADMIN') {
            this.router.navigate(['/adminDashboard']);
          } else if (response.role === 'USER') {
            this.router.navigate(['/homeSignedup']);
          } else {
            console.log('Invalid role');
          }
        } else {
          console.log('Login failed', response.message);
        }
      },
      (error) => {
        console.error('Error during login', error);
      }
    );
  }


  //homeSignedup

  facebookLogin() {
    this.userService.initiateFacebookLogin().subscribe(
      (authUrl: string) => {
        // Open a new window or redirect the user to the authUrl
        window.location.href = authUrl;

        // After initiating Facebook login, immediately navigate to the HomeSignedUp page
      },
      (error) => {
        console.error('Failed to get Facebook authorization URL', error);
      }
    );
  }


  loginWithLinkedIn(): void {
    this.userService.initiateLinkedInLogin().subscribe(
      (authUrl: string) => {
        // Redirect the user to the LinkedIn login page
        window.location.href = authUrl;
      },
      (error) => {
        console.error('Failed to initiate LinkedIn login', error);
      }
    );
  }


  loginWithGoogle(): void {
    this.userService.initiateGoogleLogin().subscribe(
      (authUrl: string) => {
        // Open a new window or redirect the user to the authUrl
        window.location.href = authUrl;
        // After initiating Google login, immediately navigate to the HomeSignedUp page
      },
      (error) => {
        console.error('Failed to get Google authorization URL', error);
      }
    );
  }

  // After successful login, navigate to the CallbackComponent



}
