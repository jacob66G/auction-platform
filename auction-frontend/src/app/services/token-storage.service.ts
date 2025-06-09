import { Injectable, inject } from '@angular/core';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

/**
 * TokenStorageService
 * 
 * This service is responsible for managing authentication tokens and credentials.
 * It was created to break the circular dependency between AuthService and AuthInterceptor:
 * 
 * - AuthInterceptor needed AuthService to get tokens
 * - AuthService needed HttpClient with interceptors for API calls
 * - HttpClient needed AuthInterceptor
 * 
 * By extracting the token storage logic into this separate service:
 * - AuthInterceptor can depend on TokenStorageService instead of AuthService
 * - AuthService can depend on TokenStorageService
 * - No circular dependency exists
 */
@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  private platformId = inject(PLATFORM_ID);
  private isBrowser = isPlatformBrowser(this.platformId);
  
  constructor() {}
  
  public saveToken(token: string): void {
    if (this.isBrowser) {
      sessionStorage.setItem('token', token);
    }
  }
  
  public saveCredentials(username: string, password: string): void {
    if (this.isBrowser) {
      const credentials = btoa(`${username}:${password}`);
      sessionStorage.setItem('credentials', credentials);
    }
  }
  
  public getToken(): string | null {
    return this.isBrowser ? sessionStorage.getItem('token') : null;
  }
  
  public getCredentials(): string | null {
    return this.isBrowser ? sessionStorage.getItem('credentials') : null;
  }
  
  public getAuthorizationHeader(): string {
    if (!this.isBrowser) {
      return '';
    }
    
    const token = this.getToken();
    const credentials = this.getCredentials();
    
    if (token) {
      return `Bearer ${token}`;
    } else if (credentials) {
      return `Basic ${credentials}`;
    }
    
    return '';
  }
  
  public isAuthenticated(): boolean {
    if (!this.isBrowser) {
      return false;
    }
    return !!this.getToken() || !!this.getCredentials();
  }
  
  public clearStorage(): void {
    if (this.isBrowser) {
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('credentials');
    }
  }
} 