import { Component, OnInit } from "@angular/core";
import { Lesson } from "../../../../models/course.model";
import { ActivatedRoute } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { CourseService } from "../../services/course.service";
import { LessonService } from "../../services/lesson.service";
import { ProgressService } from "../../services/progress.service";

import { MatIconModule } from "@angular/material/icon";
import { SafePipe } from "../../../../shared/pipes/safe-pipe";

@Component({
  selector: "app-lesson-viewer",
  imports: [MatIconModule, SafePipe],
  templateUrl: "./lesson-viewer.html",
  styleUrl: "./lesson-viewer.css",
})
export class LessonViewer implements OnInit {
  courseId!: number;
  lessonId!: number;
  lesson!: Lesson;
  isCompleted: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private lessonService: LessonService,
    private progressService: ProgressService,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.courseId = +params["courseId"];
      this.lessonId = +params["lessonId"];
      this.loadLesson();
    });
  }

  loadLesson() {
    this.lessonService.getLesson(this.lessonId).subscribe({
      next: (data) => {
        this.lesson = data.lesson;
        this.isCompleted = data.completed;
        console.log(this.isCompleted, data);
      },
    });
  }

  markComplete() {
    this.progressService.completeLesson(this.lessonId).subscribe({
      next: () => {
        this.isCompleted = true;
        this.snackBar.open("Lesson Finished!", "OK");
      },
      error: (err) => {
        this.snackBar.open(err.error.message || "Already completed", "Close");
      },
    });
  }

  isExternalProvider(url: string): boolean {
    return url.includes("youtube.com") || url.includes("youtu.be") || url.includes("vimeo.com");
  }
}
