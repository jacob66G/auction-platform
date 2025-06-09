import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { 
  AuctionResponse, 
  AuctionDetailsResponse, 
  AuctionCreateDto, 
  AuctionSearchCriteria,
  AuctionCancelRequest,
  AuctionCreateResponse
} from '../models/auction.model';
import { MessageResponse } from '../models/response.model';
import { AuthService } from './auth.service';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuctionService {
  private apiUrl = `${environment.apiUrl}/api/auctions`;
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
      }
    }

    return headers;
  }

  // Public search for all users
  searchAuctions(criteria: AuctionSearchCriteria): Observable<AuctionResponse[]> {
    let params = this.buildSearchParams(criteria);
    return this.http.get<AuctionResponse[]>(`${this.apiUrl}/search`, { params });
  }

  // Search for logged-in user's auctions
  getMyAuctions(criteria: AuctionSearchCriteria): Observable<AuctionResponse[]> {
    let params = this.buildSearchParams(criteria);
    const headers = this.getAuthHeaders();
    return this.http.get<AuctionResponse[]>(`${this.apiUrl}/search/my`, { params, headers });
  }

  // Admin search for all auctions
  adminSearchAuctions(criteria: AuctionSearchCriteria): Observable<AuctionResponse[]> {
    let params = this.buildSearchParams(criteria);
    const headers = this.getAuthHeaders();
    return this.http.get<AuctionResponse[]>(`${this.apiUrl}/search/admin`, { params, headers });
  }

  // Get auction details by ID
  getAuctionById(id: number): Observable<AuctionDetailsResponse> {
    return this.http.get<AuctionDetailsResponse>(`${this.apiUrl}/${id}`);
  }

  // Create a new auction
  createAuction(auction: AuctionCreateDto, images?: File[]): Observable<AuctionCreateResponse> {
    const formData = new FormData();
    formData.append('auction', new Blob([JSON.stringify(auction)], { type: 'application/json' }));
    
    if (images && images.length > 0) {
      for (const image of images) {
        formData.append('images', image);
      }
    }

    // For FormData, we don't set Content-Type as the browser will set it with the boundary
    let headers = new HttpHeaders();
    if (this.isBrowser) {
      const authHeader = this.authService.getAuthorizationHeader();
      if (authHeader) {
        headers = headers.set('Authorization', authHeader);
      }
    }

    return this.http.post<AuctionCreateResponse>(this.apiUrl, formData, { headers });
  }

  // Cancel auction
  cancelAuction(id: number, request: AuctionCancelRequest): Observable<MessageResponse> {
    const headers = this.getAuthHeaders();
    return this.http.post<MessageResponse>(`${this.apiUrl}/${id}/cancel`, request, { headers });
  }

  // Admin only - delete auction
  deleteAuction(id: number): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.delete<any>(`${this.apiUrl}/${id}`, { headers });
  }

  private buildSearchParams(criteria: AuctionSearchCriteria): HttpParams {
    let params = new HttpParams();
    
    if (criteria.title) {
      params = params.append('title', criteria.title);
    }
    
    if (criteria.username) {
      params = params.append('username', criteria.username);
    }
    
    if (criteria.categoryIds && criteria.categoryIds.length > 0) {
      criteria.categoryIds.forEach(id => {
        params = params.append('categoryIds', id.toString());
      });
    }
    
    if (criteria.statuses && criteria.statuses.length > 0) {
      criteria.statuses.forEach(status => {
        params = params.append('statuses', status);
      });
    }
    
    if (criteria.sortBy) {
      params = params.append('sortBy', criteria.sortBy);
    }
    
    if (criteria.ascending !== undefined) {
      params = params.append('ascending', criteria.ascending.toString());
    }
    
    if (criteria.page !== undefined) {
      params = params.append('page', criteria.page.toString());
    }
    
    if (criteria.size !== undefined) {
      params = params.append('size', criteria.size.toString());
    }
    
    return params;
  }
} 