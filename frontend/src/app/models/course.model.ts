import { User } from "./user.model";

export interface Course {
  courseId: number;
  courseName: string;
  courseDescription: string;
  instructor: User;
  sections?: Section[];
}

export interface Section {
  sectionId?: number;
  courseId: number;
  title: number;
  orderIndex: number;
  lessons?: Lesson[];
}

export interface Lesson {
  lessonId?: number;
  sectionId: number;
  title: string;
  contentType: "VIDEO" | "TEXT";
  content: string;
  videoUrl?: string;
  orderIndex: number;
}
