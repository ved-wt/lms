import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({ providedIn: "root" })
export class ProgressService {
  private baseUrl = "http://localhost:8080/api/progress";

  constructor(private http: HttpClient) {}

  completeLesson(lessonId: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/complete/${lessonId}`, {});
  }

  getCourseProgress(courseId: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/course/${courseId}/percentage`);
  }
}
