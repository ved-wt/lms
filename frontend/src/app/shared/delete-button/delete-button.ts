import { Component, EventEmitter, Input, Output } from "@angular/core";

import { Observable } from "rxjs";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { ConfirmDialog } from "../confirm-dialog/confirm-dialog";

@Component({
  selector: "app-delete-button",
  imports: [MatDialogModule, MatSnackBarModule, MatButtonModule, MatIconModule],
  templateUrl: "./delete-button.html",
  styleUrl: "./delete-button.css",
})
export class DeleteButton {
  @Input() label: string = "";
  @Input() confirmMessage: string = "Are you sure you want to delete this?";
  @Input() deleteFn?: () => Observable<any>;
  @Output() deleted = new EventEmitter<void>();

  isDeleting = false;

  constructor(
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
  ) {}

  onDelete() {
    // 1. Open Modern Dialog
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: "350px",
      data: { message: this.confirmMessage },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.executeDelete();
      }
    });
  }

  private executeDelete() {
    if (!this.deleteFn) return;

    this.isDeleting = true;
    this.deleteFn().subscribe({
      next: () => {
        this.snackBar.open("Successfully deleted", "Close", { duration: 3000 });
        this.deleted.emit();
      },
      error: (error) => {
        this.isDeleting = false;
        this.snackBar.open("Error deleting item. Please try again.", "Close", {
          panelClass: ["error-snackbar"],
          duration: 5000,
        });
      },
      complete: () => {
        this.isDeleting = false;
      },
    });
  }
}
