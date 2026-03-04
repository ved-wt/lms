import { Router, RouterModule } from "@angular/router";

import { Component, OnInit } from "@angular/core";
import { Course, CourseProgressDTO } from "../../models/course.model";
import { AuthService } from "../../services/auth";
import { CourseService } from "../../features/courses/services/course.service";
import { DeleteButton } from "../../shared/delete-button/delete-button";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatCardModule } from "@angular/material/card";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatDialog } from "@angular/material/dialog";
import { ConfirmDialog } from "../../shared/confirm-dialog/confirm-dialog";

@Component({
  selector: "app-dashboard",
  standalone: true,
  imports: [RouterModule, DeleteButton, MatCardModule, MatButtonModule, MatIconModule, MatProgressBarModule],
  templateUrl: "./dashboard.html",
  styleUrls: ["./dashboard.css"],
})
export class Dashboard implements OnInit {
  allCourses: Course[] = [];
  myCourses: Course[] = [];
  enrolledProgress: CourseProgressDTO[] = [];
  filteredCourses: Course[] = [];
  dashboardProgress: CourseProgressDTO[] = [];

  isInstructor: boolean = false;

  constructor(
    private courseService: CourseService,
    public authService: AuthService,
    private dialog: MatDialog,
    private router: Router,
  ) {}

  ngOnInit() {
    this.isInstructor = this.authService.isInstructor();

    if (!this.authService.isInstructor()) {
      this.courseService.getDashboardProgress().subscribe((data) => {
        this.enrolledProgress = data;
      });
    }

    if (this.isInstructor) {
      this.courseService.getMyCourses().subscribe((data) => {
        this.myCourses = data;
      });
    }

    this.courseService.getAllCourses().subscribe((data) => {
      this.allCourses = data;
      this.filteredCourses = data;
    });
  }

  onSearch(event: any) {
    const query = event.target.value.toLowerCase();

    this.filteredCourses = this.allCourses.filter(
      (c) => c.courseName?.toLowerCase().includes(query) || c.courseDescription?.toLowerCase().includes(query),
    );
  }

  clearSearch() {
    this.filteredCourses = this.allCourses;
  }

  removeCourseFromList(courseId: number) {
    this.allCourses = this.allCourses.filter((c) => c.courseId !== courseId);

    this.filteredCourses = this.filteredCourses.filter((c) => c.courseId !== courseId);

    this.myCourses = this.myCourses.filter((c) => c.courseId !== courseId);
  }

  getDeleteFn(courseId: number) {
    return () => this.courseService.deleteCourse(courseId);
  }

  logout() {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: "350px",
      data: { message: "Are you sure you want to log out?", confirmText: "Logout" },
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.authService.logout();
        this.router.navigate(["/login"]);
      }
    });
  }
}
