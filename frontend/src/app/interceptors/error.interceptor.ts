import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { AuthService } from "../services/auth";
import { catchError } from "rxjs/internal/operators/catchError";
import { throwError } from "rxjs";

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);
    const authService = inject(AuthService);

    return next(req).pipe(
        catchError((err) => {
            if (err.status == 401) {
                console.error('Unauthorized - redirecting to login');
                authService.logout();
                router.navigate(['/login']);
            }

            if (err.status == 403) {
                console.error('Forbidden - insufficient permissions');
                router.navigate(['/dashboard']);
            }

            return throwError(() => err);
        })
    )
}