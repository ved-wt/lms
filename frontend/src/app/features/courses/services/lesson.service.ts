import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class LessonService {
  private baseUrl = "http://localhost:8080/api/lessons";

  constructor(private http: HttpClient) {}

  getLesson(lessonId: number): Observable<any> {
    const response = this.http.get(`${this.baseUrl}/${lessonId}`);
    return response;
  }
}
