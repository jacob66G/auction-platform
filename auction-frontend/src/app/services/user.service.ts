import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { 
  UserResponse, 
  ChangePasswordRequest, 
  DepositRequest,
  ChangeEmailRequest
} from '../models/user.model';
import { AuthService } from './auth.service';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { map, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/api/users`;
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { 
    console.log('UserService initialized with API URL:', this.apiUrl);
  }

  private getAuthHeaders(): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (this.isBrowser) {
      const authHeader = this.authService.getAuthorizationHeader();
      if (authHeader) {
        headers = headers.set('Authorization', authHeader);
        console.log('Adding auth header to user service request:', authHeader ? 'Auth header set' : 'No auth header');
      }
    }

    return headers;
  }

  // Get current user info
  getCurrentUser(): Observable<UserResponse> {
    const headers = this.getAuthHeaders();
    console.log('Getting current user with auth headers');
    return this.http.get<UserResponse>(`${this.apiUrl}/me`, { headers });
  }

  // Change user password
  changePassword(request: ChangePasswordRequest): Observable<string> {
    const headers = this.getAuthHeaders();
    return this.http.put(`${this.apiUrl}/change-password`, request, { 
      headers,
      responseType: 'text'
    });
  }

  // Change user email
  changeEmail(request: ChangeEmailRequest): Observable<string> {
    const headers = this.getAuthHeaders();
    return this.http.put(`${this.apiUrl}/change-email`, request, { 
      headers,
      responseType: 'text'
    });
  }

  // Deposit money to user account
  deposit(request: DepositRequest): Observable<string> {
    const headers = this.getAuthHeaders();
    
    // Używamy responseType: 'text' aby obsłużyć odpowiedź tekstową
    return this.http.put(`${this.apiUrl}/deposit`, request, { 
      headers,
      responseType: 'text'  // Oczekujemy odpowiedzi tekstowej
    });
  }
} 