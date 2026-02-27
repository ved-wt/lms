import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Course } from "../../../models/course.model";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class CourseService {
  private baseUrl = "http://localhost:8080/api";
  private instructorBaseUrl = `${this.baseUrl}/instructor/courses`;
  private courseBaseUrl = `${this.baseUrl}/courses`;

  constructor(private http: HttpClient) {}

  getAllCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(this.courseBaseUrl);
  }

  getExploreCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.courseBaseUrl}?filter=explore`);
  }

  getCourseById(courseId: number): Observable<Course> {
    return this.http.get<Course>(`${this.courseBaseUrl}/${courseId}`);
  }

  // Instructor specific
  getMyCourses(): Observable<Course[]> {
    console.log("getMyCourses called");
    return this.http.get<Course[]>(this.courseBaseUrl, {
      params: { filter: "my" },
    });
  }

  createCourse(course: Course): Observable<Course> {
    return this.http.post<Course>(this.courseBaseUrl, course);
  }

  updateCourse(courseId: number, course: Course): Observable<Course> {
    return this.http.put<Course>(`${this.courseBaseUrl}/${courseId}`, course);
  }

  deleteCourse(courseId: number): Observable<void> {
    return this.http.delete<void>(`${this.courseBaseUrl}/${courseId}`);
  }
}
