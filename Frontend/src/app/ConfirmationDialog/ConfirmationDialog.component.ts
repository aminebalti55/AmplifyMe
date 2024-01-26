import { Component, OnInit,Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-ConfirmationDialog',
  templateUrl: './ConfirmationDialog.component.html',
  styleUrls: ['./ConfirmationDialog.component.css']
})
export class ConfirmationDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string }
  ) {}

  ngOnInit() {
  }



  confirmDeletion() {
    this.dialogRef.close('yes'); // Set the result to 'yes'
  }

  cancelDeletion() {
    this.dialogRef.close('no'); // Set the result to 'no'
  }

}
