import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Course, CourseProgressDTO } from "../../../models/course.model";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class CourseService {
  private baseUrl = "http://localhost:8080/api";
  private instructorBaseUrl = `${this.baseUrl}/instructor/courses`;
  private courseBaseUrl = `${this.baseUrl}/courses`;
  private dashboardBaseUrl = `${this.baseUrl}/dashboard`;

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

  deleteSection(courseId: number, sectionId: number): Observable<void> {
    return this.http.delete<void>(`${this.courseBaseUrl}/${courseId}/sections/${sectionId}`);
  }

  deleteLesson(lessonId: number): Observable<void> {
    return this.http.delete<void>(`${this.courseBaseUrl}/lessons/${lessonId}`);
  }

  getDashboardProgress(): Observable<CourseProgressDTO[]> {
    return this.http.get<CourseProgressDTO[]>(`${this.dashboardBaseUrl}/progress`);
  }

  updateCourseMetadata(id: number, data: any): Observable<any> {
    return this.http.put(`${this.courseBaseUrl}/${id}`, data);
  }

  addSection(courseId: number, data: any): Observable<any> {
    return this.http.post(`${this.courseBaseUrl}/${courseId}/sections`, data);
  }

  updateSection(sectionId: number, data: any): Observable<any> {
    return this.http.put(`${this.courseBaseUrl}/sections/${sectionId}`, data);
  }

  addLesson(sectionId: number, data: any): Observable<any> {
    return this.http.post(`${this.courseBaseUrl}/sections/${sectionId}/lessons`, data);
  }

  updateLesson(lessonId: number, data: any): Observable<any> {
    return this.http.put(`${this.courseBaseUrl}/lessons/${lessonId}`, data);
  }
}
