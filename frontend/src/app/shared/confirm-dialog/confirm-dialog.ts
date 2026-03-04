import { Component, Inject } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";

@Component({
  selector: "app-confirm-dialog",
  imports: [MatDialogModule, MatButtonModule],
  // templateUrl: './confirm-dialog.html',
  // styleUrl: './confirm-dialog.css',
  template: `
    <h2 mat-dialog-title>Confirm Action</h2>
    <mat-dialog-content>{{ data.message }}</mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onNoClick()">Cancel</button>
      <button mat-flat-button color="warn" [mat-dialog-close]="true">{{ data.confirmText || "Confirm" }}</button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      mat-dialog-content {
        font-size: 1.1rem;
        padding: 20px 0;
      }
      mat-dialog-actions {
        padding-bottom: 10px;
      }
    `,
  ],
})
export class ConfirmDialog {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialog>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string; confirmText?: string },
  ) {}
  onNoClick(): void {
    this.dialogRef.close(false);
  }
}
