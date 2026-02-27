import { Component } from "@angular/core";
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from "@angular/forms";
import { Router, RouterModule } from "@angular/router";
import { AuthService } from "../../services/auth";


@Component({
  selector: "app-register",
  imports: [ReactiveFormsModule, RouterModule],
  templateUrl: "./register.html",
  styleUrls: ["./register.css"],
})
export class Register {
  registerForm: FormGroup;
  successMessage: string = "";
  errorMessage: string = "";
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
  ) {
    this.registerForm = this.fb.group({
      username: ["", Validators.required],
      email: ["", [Validators.required, Validators.email]],
      password: ["", [Validators.required, Validators.minLength(6)]],
      role: ["STUDENT", Validators.required],
    });
  }

  onSubmit() {
    this.isLoading = true;
    this.errorMessage = "";
    this.successMessage = "";
    if (this.registerForm.valid) {
      this.authService.register(this.registerForm.value).subscribe({
        next: (res) => {
          this.isLoading = false;
          this.successMessage = "Registration successful! Redirecting to login...";
          this.errorMessage = "";
          setTimeout(() => this.router.navigate(["/login"]), 2000);
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = "Registration failed. Username or email might be taken.";
          this.successMessage = "";
        },
      });
    }
  }
}
