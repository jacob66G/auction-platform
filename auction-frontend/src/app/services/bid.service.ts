import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { BidRequest, BidResponse } from '../models/bid.model';
import { AuthService } from './auth.service';
import { PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { tap, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class BidService {
  private apiUrl = `${environment.apiUrl}/api/bids`;
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

  // Place a new bid on an auction
  placeBid(bid: BidRequest): Observable<BidResponse> {
    if (!bid.auctionId || isNaN(Number(bid.auctionId))) {
      return throwError(() => new Error('Invalid auction ID'));
    }
    
    // Ensure auctionId is a number
    const validatedBid: BidRequest = {
      ...bid,
      auctionId: Number(bid.auctionId)
    };
    
    const headers = this.getAuthHeaders();
    return this.http.post<BidResponse>(this.apiUrl, validatedBid, { headers });
  }

  // Get all bids for a specific auction
  getBidsByAuctionId(auctionId: number): Observable<BidResponse[]> {
    if (!auctionId || isNaN(Number(auctionId))) {
      return throwError(() => new Error('Invalid auction ID'));
    }
    
    // Ensure auctionId is a number
    const validAuctionId = Number(auctionId);
    
    // Używamy znanego, prawidłowego endpointu
    const url = `${this.apiUrl}/auction/${validAuctionId}`;
    
    const headers = this.getAuthHeaders();
    
    return this.http.get<BidResponse[]>(url, { headers }).pipe(
      map(response => {
        if (Array.isArray(response)) {
          return response;
        } else if (response && typeof response === 'object') {
          return [response as BidResponse];
        } else {
          return [];
        }
      }),
      catchError(error => {
        console.error(`Error fetching bids for auction ${validAuctionId}:`, error);
        return of([]);
      })
    );
  }

  // Get user's bids (for authenticated user)
  getMyBids(): Observable<BidResponse[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<BidResponse[]>(`${this.apiUrl}/my`, { headers });
  }

  // Delete a bid (admin only)
  deleteBid(bidId: number): Observable<void> {
    if (!bidId || isNaN(Number(bidId))) {
      return throwError(() => new Error('Invalid bid ID'));
    }
    
    // Ensure bidId is a number
    const validBidId = Number(bidId);
    
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(`${this.apiUrl}/${validBidId}`, { headers });
  }
} 