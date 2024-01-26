import { Component, OnInit } from '@angular/core';
import { User } from '../Classes/User';
import { UserService } from '../Classes/UserService';
import { AuthService } from '../Classes/AuthService';
import { SocialPageService } from '../Classes/SocialPageService';
import { SocialPage } from '../Classes/SocialPage';
import { MatDialog } from '@angular/material/dialog';
import {AddSocialPageDialogComponent} from '../add-social-page-dialog/add-social-page-dialog.component';
import { trigger, state, style, animate, transition } from '@angular/animations';
import { Router } from '@angular/router';


@Component({
  selector: 'app-HomeSignedUp',
  templateUrl: './HomeSignedUp.component.html',
  styleUrls: ['./HomeSignedUp.component.scss']
  ,animations: [
    trigger('menuAnimation', [
      state('void', style({ opacity: 0, transform: 'translateY(-20px)' })),
      transition(':enter, :leave', [
        animate('0.3s ease-in-out'),
      ]),
    ]),
  ],
})
export class HomeSignedUpComponent implements OnInit {
  isMenuVisible = false;
  socialPages: SocialPage[] = []; // Define the socialPages property as an empty array




  constructor(private dialog: MatDialog,private socialPageService: SocialPageService,private userService: UserService, private router: Router,private authService: AuthService) { }

  currentUser: any;
  user: User = new User();
  loggedInUsername: string = ''; // Initialize as an empty string
  UserPoints: string = ''; // Initialize as an empty string
UserId! : number;
  ngOnInit() {

  // Retrieve the currentUser object from session storage
  const currentUserString = sessionStorage.getItem('currentUser');
  if (currentUserString) {
    const currentUser = JSON.parse(currentUserString);

    // Check if the currentUser object has the expected properties (id and username)
    if (currentUser.id && currentUser.username) {
      const userId = currentUser.id;
      const username = currentUser.username;
      const points = currentUser.points;
      this.UserId=userId;
      console.log('currentUser:', currentUser);
      console.log('User ID:', userId);
      console.log('Username:', username);
      this.loggedInUsername = username;
      this.UserPoints=points;
      this.loadSocialPages();

    } else {
      console.log('currentUser object is missing id and/or username properties.');
    }
  } else {
    console.log('User session does not exist.');
  }


}

darkMode = false;
isDropdownVisible: boolean = false;

toggleDarkMode() {
  this.darkMode = !this.darkMode;
}

toggleDropdown(): void {
  this.isDropdownVisible = !this.isDropdownVisible;
}

toggleMenu() {
  this.isMenuVisible = !this.isMenuVisible;
}

logout(): void {
  const userToken = this.authService.getTokenx();

  if (userToken) {
    console.log('User Token:', userToken);

    this.userService.logout(userToken).subscribe(
      (response) => {
        // Handle the logout response
        console.log(response); // "Logged out successfully"

        sessionStorage.removeItem('currentUser');
        this.authService.clearToken();
        this.router.navigate(['/login']); // Replace '/home' with your actual home route

      },
      (error) => {
        // Handle error responses
        console.error('Error logging out:', error);
      }
    );
  }
}

openAddSocialPageDialog(): void {
  const dialogRef = this.dialog.open(AddSocialPageDialogComponent, {
    width: '400px', // Adjust the width as needed
  });
}


loadSocialPages() {
  this.socialPageService.getSocialPagesByUserId(this.UserId).subscribe(
    (socialPages: SocialPage[]) => {
      this.socialPages = socialPages;
            console.log('Social Pages:', socialPages);
      // You can assign the social pages to a component property if needed
    },
    (error) => {
      console.error('Error loading social pages:', error);
    }
  );
}

}
