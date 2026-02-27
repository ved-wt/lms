import { Component, signal } from "@angular/core";
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router, RouterModule } from "@angular/router";
import { CourseService } from "../../services/course.service";

import { firstValueFrom } from "rxjs";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonToggleModule } from "@angular/material/button-toggle";

@Component({
  selector: "app-create-course",
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterModule,
    MatSnackBarModule,
    MatIconModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatFormFieldModule,
    MatInputModule
],

  templateUrl: "./create-course.html",
  styleUrl: "./create-course.css",
})
export class CreateCourse {
  courseForm: FormGroup;
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private courseService: CourseService,
    private router: Router,
    private snackBar: MatSnackBar,
  ) {
    this.courseForm = this.fb.group({
      courseName: ["", [Validators.required, Validators.minLength(3)]],
      courseDescription: ["", [Validators.required, Validators.minLength(10)]],
      sections: this.fb.array([]),
    });
  }

  get sections(): FormArray {
    return this.courseForm.get("sections") as FormArray;
  }

  addSection() {
    const section = this.fb.group({
      title: ["", [Validators.required]],
      description: ["", [Validators.minLength(10)]],
      lessons: this.fb.array([]),
    });
    this.sections.push(section);
  }

  removeSection(index: number) {
    this.sections.removeAt(index);
  }

  getLessons(sectionIndex: number): FormArray {
    return this.sections.at(sectionIndex).get("lessons") as FormArray;
  }

  addLesson(sectionIndex: number) {
    const lesson = this.fb.group({
      title: ["", [Validators.required]],
      description: ["", [Validators.minLength(10)]],
      content: ["", [Validators.minLength(10)]],
      contentType: ["TEXT", [Validators.required]],
      videoUrl: [""],
    });
    this.getLessons(sectionIndex).push(lesson);
  }

  onContentTypeChange(sectionIndex: number, lessonIndex: number) {
    const lessonGroup = this.getLessons(sectionIndex).at(lessonIndex) as FormGroup;
    const type = lessonGroup.get("contentType")?.value;

    if (type === "VIDEO") {
      lessonGroup.get("content")?.clearValidators();
      lessonGroup.get("videoUrl")?.setValidators([Validators.required]);
    } else {
      lessonGroup.get("videoUrl")?.clearValidators();
      lessonGroup.get("content")?.setValidators([Validators.required]);
    }
    lessonGroup.get("content")?.updateValueAndValidity();
    lessonGroup.get("videoUrl")?.updateValueAndValidity();
  }

  removeLesson(sectionIndex: number, lessonIndex: number) {
    this.getLessons(sectionIndex).removeAt(lessonIndex);
  }

  async onSubmit() {
    if (this.courseForm.invalid) {
      this.snackBar.open("Please fill in all required fields", "OK", { duration: 3000 });
      return;
    }

    this.isSubmitting = true;
    try {
      await firstValueFrom(this.courseService.createCourse(this.courseForm.value));
      this.snackBar.open("🚀 Course created successfully!", "Dismiss", { duration: 4000 });
      this.router.navigate(["/dashboard"]);
    } catch (error) {
      this.isSubmitting = false;
      this.snackBar.open("Error creating course. Please try again.", "Close");
    }
  }
}
