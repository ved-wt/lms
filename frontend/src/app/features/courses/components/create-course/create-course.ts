import { Component, OnInit, signal } from "@angular/core";
import { Location } from "@angular/common";
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { CourseService } from "../../services/course.service";

import { firstValueFrom } from "rxjs";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatError, MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonToggleModule } from "@angular/material/button-toggle";
import { ConfirmDialog } from "../../../../shared/confirm-dialog/confirm-dialog";
import { MatDialog } from "@angular/material/dialog";

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
    MatInputModule,
    MatError,
  ],

  templateUrl: "./create-course.html",
  styleUrl: "./create-course.css",
})
export class CreateCourse implements OnInit {
  courseForm: FormGroup;
  isSubmitting = false;
  courseId: number | null = null;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private courseService: CourseService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private location: Location,
    private dialog: MatDialog,
  ) {
    this.courseForm = this.fb.group({
      courseName: ["", [Validators.required, Validators.minLength(3)]],
      courseDescription: ["", [Validators.required, Validators.minLength(10)]],
      sections: this.fb.array([], [Validators.required, Validators.minLength(1)]),
    });
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get("id");
    if (id) {
      this.courseId = Number(id);
      this.isEditMode = true;
      this.loadCourseData(this.courseId);
    }

    // else {
    //   this.addSection();
    // }
  }

  private createSectionGroup(section: any): FormGroup {
    return this.fb.group({
      sectionId: [section.sectionId], // ID is now guaranteed
      title: [section.title || "", [Validators.required]],
      description: [section.description || ""],
      lessons: this.fb.array((section.lessons || []).map((l: any) => this.createLessonGroup(l))),
    });
  }

  private createLessonGroup(lesson: any): FormGroup {
    return this.fb.group({
      lessonId: [lesson.lessonId], // ID is now guaranteed
      title: [lesson.title || "", [Validators.required]],
      contentType: [lesson.contentType || "TEXT"],
      content: [lesson.content || ""],
      videoUrl: [lesson.videoUrl || ""],
      orderIndex: [lesson.orderIndex || 0],
    });
  }

  clearIfDefault(event: FocusEvent, defaultValue: string) {
    const input = event.target as HTMLInputElement | HTMLTextAreaElement;
    if (input.value === defaultValue) {
      input.value = "";
      const controlName = input.getAttribute("formControlName");
    }
  }

  async loadCourseData(id: number) {
    try {
      const course = await firstValueFrom(this.courseService.getCourseById(id));

      this.courseForm.patchValue({
        courseName: course.courseName,
        courseDescription: course.courseDescription,
      });

      this.sections.clear();

      if (course.sections) {
        course.sections.forEach((section: any) => {
          this.sections.push(this.createSectionGroup(section));
        });
      }
    } catch (error) {
      this.snackBar.open("Error loading course data", "Close");
    }
  }

  async onSubmit() {
    if (this.courseForm.invalid) {
      this.snackBar.open("Please fill in all required fields", "OK", { duration: 3000 });
      return;
    }

    this.isSubmitting = true;
    try {
      if (this.isEditMode) {
        await firstValueFrom(this.courseService.updateCourseMetadata(this.courseId!, this.courseForm.value));
        this.snackBar.open("Course updated!", "OK", { duration: 2000 });
      } else {
        const initialData = {
          ...this.courseForm.value,
          sections: [
            {
              title: "Introduction",
              description: "Welcome to the course",
              orderIndex: 0,
              lessons: [
                {
                  title: "First Lesson",
                  contentType: "TEXT",
                  content: "Welcome!",
                  orderIndex: 0,
                },
              ],
            },
          ],
        };

        const savedCourse = await firstValueFrom(this.courseService.createCourse(initialData));
        this.courseId = savedCourse.courseId;
        this.isEditMode = true;

        this.loadCourseData(this.courseId!);

        this.location.replaceState(`/courses/edit/${this.courseId}`);
        this.snackBar.open("🚀 Course created with initial section!", "OK", { duration: 4000 });
      }
    } catch (error: any) {
      if (error.status === 400 && error.error) {
        const backendErrors = error.error;
        Object.keys(backendErrors).forEach((key) => {
          const formControl = this.courseForm.get(key);
          if (formControl) {
            formControl.setErrors({ serverError: backendErrors[key] });
          }
        });
        this.snackBar.open("Please check the highlighted fields", "OK");
      }
    } finally {
      this.isSubmitting = false;
    }
  }

  async saveSection(index: number) {
    const sectionGroup = this.sections.at(index);

    if (sectionGroup.invalid || !sectionGroup.dirty) return;

    try {
      const sectionData = sectionGroup.value;
      await firstValueFrom(this.courseService.updateSection(sectionData.sectionId, sectionData));

      sectionGroup.markAsPristine(); // Mark as 'clean' until next change
      this.snackBar.open("Section saved", "OK", { duration: 1000 });
    } catch (error) {
      console.error("Auto-save failed", error);
    }
  }

  async saveLesson(sectionIndex: number, lessonIndex: number) {
    const lessonGroup = this.getLessons(sectionIndex).at(lessonIndex) as FormGroup;

    if (lessonGroup.invalid || !lessonGroup.dirty) return;

    try {
      const lessonData = lessonGroup.value;
      await firstValueFrom(this.courseService.updateLesson(lessonData.lessonId, lessonData));

      lessonGroup.markAsPristine(); // Mark as 'clean'
      this.snackBar.open("Lesson saved", "OK", { duration: 1000 });
    } catch (error) {
      console.error("Lesson auto-save failed", error);
    }
  }

  get sections(): FormArray {
    return this.courseForm.get("sections") as FormArray;
  }

  async addSection() {
    if (!this.courseId) {
      this.snackBar.open("Please create the course first by clicking 'Create Course' below.", "OK");
      return;
    }

    const skeleton = {
      title: "New Section",
      description: "Section description...", // Match Backend @Size(min=10)
      orderIndex: this.sections.length,
      // Add one lesson to the skeleton to satisfy Backend @NotEmpty
      lessons: [{ title: "New Lesson", contentType: "TEXT", orderIndex: 0 }],
    };

    try {
      const savedSection = await firstValueFrom(this.courseService.addSection(this.courseId, skeleton));
      this.sections.push(this.createSectionGroup(savedSection));
      this.snackBar.open("Section added", "OK", { duration: 2000 });
    } catch (e) {
      this.snackBar.open("Failed to add section. Ensure description is 10+ chars.", "Close");
    }
  }

  async addLesson(sectionIndex: number) {
    const sectionGroup = this.sections.at(sectionIndex);
    const sectionId = sectionGroup.get("sectionId")?.value;

    const skeleton = {
      title: "New Lesson",
      contentType: "TEXT",
      orderIndex: this.getLessons(sectionIndex).length,
    };

    try {
      const savedLesson = await firstValueFrom(this.courseService.addLesson(sectionId, skeleton));
      this.getLessons(sectionIndex).push(this.createLessonGroup(savedLesson));
    } catch (e) {
      this.snackBar.open("Failed to add lesson", "Close");
    }
  }
  async removeSection(index: number) {
    if (this.sections.length <= 1) {
      this.snackBar.open("A course must have at least one section", "OK", { duration: 3000 });
      return;
    }

    const courseId = this.courseId!;
    const sectionGroup = this.sections.at(index);
    const sectionId = sectionGroup.get("sectionId")?.value;

    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: "400px",
      data: { message: "Are you sure you want to delete this section and all its lessons?" },
    });

    const confirmed = await firstValueFrom(dialogRef.afterClosed());

    if (confirmed) {
      if (sectionId) {
        try {
          await firstValueFrom(this.courseService.deleteSection(courseId, sectionId));
          this.snackBar.open("Section deleted from server", "OK", { duration: 2000 });
        } catch (error) {
          console.log(error);
          this.snackBar.open("Error deleting section", "Close");
          return;
        }
      }
      this.sections.removeAt(index);
    }
  }

  async removeLesson(sectionIndex: number, lessonIndex: number) {
    if (this.getLessons(sectionIndex).length <= 1) {
      this.snackBar.open("A section must have at least one lesson", "OK", { duration: 3000 });
      return;
    }

    const lessonGroup = this.getLessons(sectionIndex).at(lessonIndex);
    const lessonId = lessonGroup.get("lessonId")?.value;

    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: "350px",
      data: { message: "Are you sure you want to delete this lesson?" },
    });

    const confirmed = await firstValueFrom(dialogRef.afterClosed());

    if (confirmed) {
      if (lessonId) {
        try {
          await firstValueFrom(this.courseService.deleteLesson(lessonId));
          this.snackBar.open("Lesson removed", "OK", { duration: 2000 });
        } catch (error) {
          this.snackBar.open("Error deleting lesson", "Close");
          return;
        }
      }
      this.getLessons(sectionIndex).removeAt(lessonIndex);
    }
  }

  getLessons(sectionIndex: number): FormArray {
    return this.sections.at(sectionIndex).get("lessons") as FormArray;
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
}
