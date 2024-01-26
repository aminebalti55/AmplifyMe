import { Component, OnInit ,ViewEncapsulation ,AfterViewInit } from '@angular/core';
import { User } from '../Classes/User';
import { UserService } from '../Classes/UserService';
import { MatDialog } from '@angular/material/dialog';
import { GiftPointsModalComponent } from '../gift-points-modal/gift-points-modal.component';
import { ConfirmationDialogComponent } from '../ConfirmationDialog/ConfirmationDialog.component';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../Classes/AuthService';
import { Router } from '@angular/router';

@Component({
  selector: 'app-AdminDashboard',
  templateUrl: './AdminDashboard.component.html',
  styleUrls: [
    './AdminDashboard.component.css',
    '../../assets/css/nucleo-icons.css',
    '../../assets/css/nucleo-svg.css',
    '../../assets/css/soft-ui-dashboard.css'
  ],
  encapsulation: ViewEncapsulation.None // Use None for global styles

})
export class AdminDashboardComponent implements OnInit {
  users: User[] = [];
  selectedUserId: string | null = null; // Initialize with null, no user is selected initially

  constructor(private userService: UserService ,private dialog: MatDialog,private authService: AuthService,private router: Router) { }

  ngOnInit() {  this.loadUsers();

  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe(
      (data: User[]) => {
        this.users = data;
      },
      (error) => {
        console.error('Error fetching users:', error);
      }
    );
  }

  openGiftPointsDialog(username: string): void {
    const dialogRef = this.dialog.open(GiftPointsModalComponent, {
      width: '250px',
      data: { username, pointsToAdd: 0 } // Pass the selected username
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined) {
        this.loadUsers();
        console.log('Gifted points:', result);
      }
    });
  }


  openDeleteUserDialog(userId: string): void {
    this.selectedUserId = userId; // Set the selected user's ID
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '250px',
      data: { message: 'Do you want to delete this user?' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'yes') {
        // User confirmed deletion, call the deleteUser service
        this.deleteSelectedUser();
      } else {
        // User canceled the deletion or closed the dialog
        this.selectedUserId = null; // Clear the selected user's ID
      }
    });
  }

  deleteSelectedUser(): void {
    if (this.selectedUserId) {
      this.userService.deleteUser(this.selectedUserId).subscribe(
        () => {
          // Handle successful user deletion
          console.log('User deleted successfully');
          // Reload the user list or update it as needed
          this.loadUsers();
        },
        (error) => {
          if (error instanceof HttpErrorResponse && error.status === 200) {
            // Ignore the JSON parsing error for a successful 200 response
            console.log('User deleted successfully');
            // Reload the user list or update it as needed
            this.loadUsers();
          } else {
            // Handle other errors here
            console.error('Error deleting user:', error);
          }
        }
      );
    }
  }

  logout(): void {
    const userToken = this.authService.getTokenx();

    if (userToken) {
      console.log('User Token:', userToken);

      this.userService.logout(userToken).subscribe(
        (response) => {
          // Handle the logout response
          console.log(response); // "Logged out successfully"

          // Clear the user token from AuthService upon successful logout
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

}
