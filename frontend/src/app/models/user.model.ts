export enum RoleType {
    STUDENT = 'STUDENT',
    INSTRUCTOR = 'INSTRUCTOR'
}

export interface User {
    id: number;
    username: string;
    email: string;
    role: RoleType;
    token: string;
}