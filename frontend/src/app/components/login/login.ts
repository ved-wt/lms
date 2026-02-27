
import { Component } from "@angular/core";
import { Router, RouterModule } from "@angular/router";
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from "@angular/forms";
import { AuthService } from "../../services/auth";

@Component({
  selector: "app-login",
  imports: [ReactiveFormsModule, RouterModule],
  templateUrl: "./login.html",
  styleUrls: ["./login.css"],
})
export class Login {
  loginForm: FormGroup;
  errorMessage: string = "";

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
  ) {
    this.loginForm = this.fb.group({
      username: ["", [Validators.required]],
      password: ["", [Validators.required, Validators.minLength(4)]],
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (user) => {
          this.errorMessage = "";
          console.log("Logged in successfully", user);
          this.router.navigate(["/dashboard"]);
        },
        error: (err) => {
          this.errorMessage = "Invalid username or password";
        },
      });
    }
  }
}
