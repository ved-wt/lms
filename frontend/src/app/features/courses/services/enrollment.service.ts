import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class EnrollmentService {
  private baseUrl = "http://localhost:8080/api/enrollments";
  constructor(private http: HttpClient) {}

  enrollCourse(courseId: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}`, {
      courseId: courseId,
    });
  }

  checkEnrollment(courseId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/check?courseId=${courseId}`);
  }
}
