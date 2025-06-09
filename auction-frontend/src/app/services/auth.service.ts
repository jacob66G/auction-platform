import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, tap, switchMap, shareReplay, finalize } from 'rxjs/operators';
import { Router } from '@angular/router';
import { User, UserRegisterRequest, UserResponse } from '../models/user.model';
import { environment } from '../../environments/environment';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { TokenStorageService } from './token-storage.service';

interface LoginRequest {
  username: string;
  password: string;
}

interface LoginResponse {
  token?: string;
  user?: UserResponse;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private userUrl = `${environment.apiUrl}/api/users/me`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);
  
  // Track if user info is currently being loaded to prevent duplicate calls
  private isLoadingUser = false;
  // Cache the current user info request
  private currentUserRequest: Observable<UserResponse> | null = null;

  constructor(
    private http: HttpClient,
    private router: Router,
    private tokenStorage: TokenStorageService
  ) {
    this.loadStoredUser();
  }

  private loadStoredUser(): void {
    if (!this.isBrowser) {
      // Skip on server-side
      return;
    }

    if (this.tokenStorage.isAuthenticated()) {
      this.getCurrentUserInfo().subscribe({
        next: (user) => {
          this.currentUserSubject.next(user);
        },
        error: () => {
          this.logout();
        }
      });
    }
  }

  register(user: UserRegisterRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.apiUrl}/register`, user);
  }

  login(username: string, password: string): Observable<UserResponse> {
    // Clear any existing auth data
    this.tokenStorage.clearStorage();
    
    // Store credentials for basic auth
    this.tokenStorage.saveCredentials(username, password);
    
    // First try with direct auth header
    const headers = new HttpHeaders({
      'Authorization': `Basic ${btoa(`${username}:${password}`)}`,
      'Content-Type': 'application/json'
    });
    
    // Try to get user info with Basic Auth
    return this.http.get<UserResponse>(this.userUrl, { headers }).pipe(
      tap(user => {
        this.currentUserSubject.next(user);
      }),
      catchError(error => {
        // If Basic Auth fails, try with credentials in body
        const loginRequest: LoginRequest = {
          username,
          password
        };
        
        return this.http.post<LoginResponse>(`${this.apiUrl}/login`, loginRequest, { headers }).pipe(
          switchMap(response => {
            // Store token if provided
            if (response.token) {
              this.tokenStorage.saveToken(response.token);
            }
            
            // If user is provided in the response
            if (response.user) {
              this.currentUserSubject.next(response.user);
              return of(response.user);
            }
            
            // Otherwise get user info
            return this.getCurrentUserInfo();
          }),
          catchError(loginError => {
            this.logout();
            throw loginError;
          })
        );
      })
    );
  }

  getCurrentUserInfo(): Observable<UserResponse> {
    // If we're already loading the user, return the cached request
    if (this.isLoadingUser && this.currentUserRequest) {
      return this.currentUserRequest;
    }
    
    // Get auth headers without using interceptor
    const headers = this.getAuthHeaders();
    
    // Set loading flag
    this.isLoadingUser = true;
    
    // Create and cache the request
    this.currentUserRequest = this.http.get<UserResponse>(this.userUrl, { headers })
      .pipe(
        tap(user => {
          this.currentUserSubject.next(user);
        }),
        catchError(error => {
          if (error.status === 401) {
            this.logout();
          }
          throw error;
        }),
        finalize(() => {
          // Clear loading flags when done
          this.isLoadingUser = false;
          setTimeout(() => {
            this.currentUserRequest = null;
          }, 2000); // Clear the cache after 2 seconds
        }),
        // Share the same response to all subscribers
        shareReplay(1)
      );
    
    return this.currentUserRequest;
  }

  logout(): void {
    this.tokenStorage.clearStorage();
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this.tokenStorage.isAuthenticated();
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  getAuthorizationHeader(): string {
    return this.tokenStorage.getAuthorizationHeader();
  }
  
  private getAuthHeaders(): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    
    if (!this.isBrowser) {
      return headers;
    }
    
    const authHeader = this.getAuthorizationHeader();
    if (authHeader) {
      headers = headers.set('Authorization', authHeader);
    }
    
    return headers;
  }
} 