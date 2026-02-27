import { CommonModule } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { Course } from "../../../../models/course.model";
import { AuthService } from "../../../../services/auth";
import { DeleteButton } from "../../../../shared/delete-button/delete-button";
import { CourseService } from "../../services/course.service";
import { EnrollmentService } from "../../services/enrollment.service";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "app-course-detail",
  imports: [CommonModule, RouterModule, DeleteButton, MatIconModule, MatButtonModule],
  templateUrl: "./course-detail.html",
  styleUrl: "./course-detail.css",
})
export class CourseDetail implements OnInit {
  course: Course | null = null;
  expandedSections: Set<number> = new Set();
  isInstructor: boolean = false;

  isEnrolled: boolean = false;
  showSuccessBanner: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public courseService: CourseService,
    public authService: AuthService,
    private enrollmentService: EnrollmentService,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get("id"));
    if (id) {
      this.courseService.getCourseById(id).subscribe({
        next: (data) => {
          this.course = data;
          if (this.course?.sections?.length) {
            this.toggleSection(this.course.sections[0].sectionId!);
          }
        },
      });

      this.isInstructor = this.authService.isInstructor();

      if (!this.isInstructor) {
        this.enrollmentService.checkEnrollment(id).subscribe({
          next: (enrolled) => {
            this.isEnrolled = enrolled;
          },
          error: (err) => console.error("Could not check enrollment status", err),
        });
      }
    }
  }

  get canManageCourse(): boolean {
    if (!this.authService.isInstructor()) return false;

    const loggedInUsername = this.authService.getUsername();
    const courseInstructorUsername = this.course?.instructor?.username;

    return loggedInUsername === courseInstructorUsername;
  }

  toggleSection(sectionId: number): void {
    if (this.expandedSections.has(sectionId)) {
      this.expandedSections.delete(sectionId);
    } else {
      this.expandedSections.add(sectionId);
    }
  }

  isExpanded(sectionId: number): boolean {
    return this.expandedSections.has(sectionId);
  }

  getDeleteFn(courseId: number) {
    return () => this.courseService.deleteCourse(courseId);
  }

  onDeleted() {
    this.router.navigate(["/dashboard"]);
  }

  enroll() {
    if (this.course) {
      this.enrollmentService.enrollCourse(this.course.courseId).subscribe({
        next: (data) => {
          this.isEnrolled = true;
          this.showSuccessBanner = true;

          this.snackBar.open("Successfully enrolled in " + this.course?.courseName, "Close", {
            duration: 5000,
            panelClass: ["success-snackbar"],
          });

          setTimeout(() => (this.showSuccessBanner = false), 8000);
        },
        error: (err) => {
          this.snackBar.open("Enrollment failed. You might already be enrolled.", "Close", { duration: 3000 });
        },
      });
    }
  }

  fetchCourseData(id: number) {
    this.courseService.getCourseById(id).subscribe({
      next: (data) => {
        this.course = data;
        // Logic to check if user is already enrolled would go here
        // For now, we'll assume they aren't until they click the button
      },
      error: (err) => console.error("Error fetching course", err),
    });
  }

  navigateToLesson(lessonId: number) {
    this.router.navigate(["/courses", this.course?.courseId, "lessons", lessonId]);
  }
}
