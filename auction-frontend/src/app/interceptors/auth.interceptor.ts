import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';
import { TokenStorageService } from '../services/token-storage.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private router: Router,
    private tokenStorage: TokenStorageService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Only add auth header for API requests
    if (request.url.startsWith(environment.apiUrl)) {
      const authHeader = this.tokenStorage.getAuthorizationHeader();
      
      if (authHeader) {
        request = request.clone({
          setHeaders: {
            Authorization: authHeader
          }
        });
      }
    }
    
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle 401 Unauthorized errors
        if (error.status === 401) {
          this.tokenStorage.clearStorage();
          this.router.navigate(['/login']);
        }
        
        return throwError(() => error);
      })
    );
  }
} 