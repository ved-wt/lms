import { Routes } from "@angular/router";
import { Dashboard } from "./components/dashboard/dashboard";
import { CourseDetail } from "./features/courses/components/course-detail/course-detail";
import { Login } from "./components/login/login";
import { Register } from "./components/register/register";
import { CreateCourse } from "./features/courses/components/create-course/create-course";
import { authGuard } from "./guards/auth.guard";
import { instructorGuard } from "./guards/instructor.guard";
import { LessonViewer } from "./features/courses/components/lesson-viewer/lesson-viewer";

export const routes: Routes = [
  { path: "dashboard", component: Dashboard },
  { path: "courses/:id", component: CourseDetail },
  { path: "login", component: Login },
  { path: "register", component: Register },
  { path: "create-course", component: CreateCourse, canActivate: [authGuard, instructorGuard] },
  { path: "courses/:id/lessons/:lessonId", component: LessonViewer },
  { path: "", redirectTo: "/login", pathMatch: "full" },
];
