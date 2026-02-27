import { RouterModule } from "@angular/router";

import { Component, OnInit } from "@angular/core";
import { Course } from "../../models/course.model";
import { AuthService } from "../../services/auth";
import { CourseService } from "../../features/courses/services/course.service";
import { DeleteButton } from "../../shared/delete-button/delete-button";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatCardModule } from "@angular/material/card";

@Component({
  selector: "app-dashboard",
  standalone: true,
  imports: [RouterModule, DeleteButton, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: "./dashboard.html",
  styleUrls: ["./dashboard.css"],
})
export class Dashboard implements OnInit {
  allCourses: Course[] = [];
  myCourses: Course[] = [];
  filteredCourses: Course[] = [];

  isInstructor: boolean = false;

  constructor(
    private courseService: CourseService,
    public authService: AuthService,
  ) {}

  ngOnInit() {
    this.isInstructor = this.authService.isInstructor();

    this.courseService.getExploreCourses().subscribe((data) => {
      this.allCourses = data;
      this.filteredCourses = data;
    });

    if (this.isInstructor) {
      this.courseService.getMyCourses().subscribe((data) => {
        this.myCourses = data;
      });
    }
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
}
