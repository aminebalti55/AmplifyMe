import { Component, OnInit,Inject  } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { User } from '../Classes/User';
import { UserService } from '../Classes/UserService';
@Component({
  selector: 'app-gift-points-modal',
  templateUrl: './gift-points-modal.component.html',
  styleUrls: ['./gift-points-modal.component.css']
})
export class GiftPointsModalComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<GiftPointsModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any ,
    private userService: UserService ) { }


  ngOnInit() {
  }




  onGiftPoints(): void {
    const { username, pointsToAdd } = this.data; // Destructure the data object
    console.log('Username:', username);
    console.log('Points to Add:', pointsToAdd);

    this.userService.addPointsToUser(username, pointsToAdd)
      .subscribe(
        response => {
          // Handle success and close the dialog
          console.log('Points added successfully:', response);
          this.dialogRef.close(response);
        },
        error => {
          // Handle error and potentially show a message to the user
          console.error('Error adding points:', error);
          // Optionally, provide feedback to the user about the error.
        }
      );
  }


  onClose(): void {
    this.dialogRef.close();
  }

}
