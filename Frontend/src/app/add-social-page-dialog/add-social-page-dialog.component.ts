import { Component ,OnInit ,Inject  } from '@angular/core';
import { SocialMediaType } from '../Classes/SocialMediaType';
import { User } from '../Classes/User';
import { SocialOperation } from '../Classes/SocialOperation';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NgForm } from '@angular/forms'; // Import NgForm
import { SocialPageService } from '../Classes/SocialPageService';
import { SocialPage } from '../Classes/SocialPage';

@Component({
  selector: 'app-add-social-page-dialog',
  templateUrl: './add-social-page-dialog.component.html',
  styleUrls: ['./add-social-page-dialog.component.css']
})
export class AddSocialPageDialogComponent implements OnInit {

  socialMediaTypes = Object.values(SocialMediaType);
  socialOperations = Object.values(SocialOperation);

  userId!: number; // Assuming you have a userId property
  formData: any = {};
  socialPage: any = {
    user: { id: 0 }, // Initialize with a default user ID, which will be updated
    pageLink: '',
    socialMediaType: SocialMediaType.TikTok, // Use a default value from your enum
  socialOperation: SocialOperation.Followers,
    pointsToSpend: 0,
  };


  constructor(
    private socialPageService: SocialPageService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogRef: MatDialogRef<AddSocialPageDialogComponent>
  ) {

  }





  ngOnInit() {

    const currentUserString = sessionStorage.getItem('currentUser');

    if (currentUserString) {
      const currentUser = JSON.parse(currentUserString);

      // Check if the currentUser object has the expected properties (id and username)
      if (currentUser.id) {
        this.socialPage.user.id = currentUser.id; // Set the user ID in the socialPage object
        this.userId = currentUser.id; // Assign the retrieved user ID to the model
        console.log('User ID:', this.userId);
      } else {
        console.log('currentUser object is missing the id property.');
      }
    } else {
      console.log('User session does not exist.');
    }

  }

  onNoClick(): void {
    this.dialogRef.close();
  }




  addSocialPage() {
    // Call the addSocialPage method from your service
    this.socialPageService.addSocialPage(this.socialPage).subscribe(
      (response) => {
        console.log('Social page added successfully:', response);
        // Handle success, e.g., close the dialog
        this.dialogRef.close();
      },
      (error) => {
        console.error('Error adding social page:', error);
        // Handle error, e.g., display an error message
      }
    );
  }

  }



