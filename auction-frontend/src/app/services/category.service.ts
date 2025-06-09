import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CategoryResponse, CategoryRequest } from '../models/category.model';
import { AuthService } from './auth.service';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apiUrl = `${environment.apiUrl}/api/categories`;
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  private getAuthHeaders(): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (this.isBrowser) {
      const authHeader = this.authService.getAuthorizationHeader();
      if (authHeader) {
        headers = headers.set('Authorization', authHeader);
        console.log('Adding auth header to category service request');
      }
    }

    return headers;
  }

  // Get all categories - available to all users
  getCategories(): Observable<CategoryResponse[]> {
    return this.http.get<CategoryResponse[]>(this.apiUrl);
  }

  // Get category by ID - available to all users
  getCategoryById(id: number): Observable<CategoryResponse> {
    return this.http.get<CategoryResponse>(`${this.apiUrl}/${id}`);
  }

  // Create new category - admin only
  createCategory(category: CategoryRequest): Observable<CategoryResponse> {
    const headers = this.getAuthHeaders();
    return this.http.post<CategoryResponse>(this.apiUrl, category, { headers });
  }

  // Update category - admin only
  updateCategory(id: number, category: CategoryRequest): Observable<CategoryResponse> {
    const headers = this.getAuthHeaders();
    return this.http.put<CategoryResponse>(`${this.apiUrl}/${id}`, category, { headers });
  }

  // Delete category - admin only
  deleteCategory(id: number): Observable<void> {
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
  }
} 