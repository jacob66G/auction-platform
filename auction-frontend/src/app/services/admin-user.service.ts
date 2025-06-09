import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserResponse, UserRole, UserSearchCriteria } from '../models/user.model';
import { AuthService } from './auth.service';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AdminUserService {
  private apiUrl = `${environment.apiUrl}/api/admin/users`;
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getAuthHeaders(): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (this.isBrowser) {
      const authHeader = this.authService.getAuthorizationHeader();
      if (authHeader) {
        headers = headers.set('Authorization', authHeader);
      }
    }

    return headers;
  }

  // Get users by role - accepts string role directly
  getUsersByRole(role: string = 'user'): Observable<UserResponse[]> {
    const headers = this.getAuthHeaders();
    
    // Use the role string directly - it should be 'user', 'moderator', or 'admin'
    let params = new HttpParams().set('role', role);
    
    console.log(`Sending request to backend for users with role: ${role}`);
    
    return this.http.get<UserResponse[]>(`${this.apiUrl}/role`, { headers, params }).pipe(
      map(users => {
        console.log(`Received ${users.length} users from backend with role: ${role}`);
        return users;
      })
    );
  }

  // Search users by username or email
  searchUsers(searchTerm: string): Observable<UserResponse[]> {
    const headers = this.getAuthHeaders();
    let params = new HttpParams().set('user', searchTerm);
    
    return this.http.get<UserResponse[]>(`${this.apiUrl}`, { headers, params });
  }

  // Assign moderator role to a user
  assignModeratorRole(userId: number): Observable<UserResponse> {
    const headers = this.getAuthHeaders();
    
    return this.http.put<UserResponse>(`${this.apiUrl}/${userId}/assign-moderator`, {}, { headers });
  }

  // Revoke moderator role from a user
  revokeModeratorRole(userId: number): Observable<UserResponse> {
    const headers = this.getAuthHeaders();
    
    return this.http.put<UserResponse>(`${this.apiUrl}/${userId}/revoke-moderator`, {}, { headers });
  }

  // Delete a user
  deleteUser(userId: number): Observable<void> {
    const headers = this.getAuthHeaders();
    
    return this.http.delete<void>(`${this.apiUrl}/${userId}`, { headers });
  }
} 