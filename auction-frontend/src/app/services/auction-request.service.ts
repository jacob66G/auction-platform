import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { 
  AuctionRequestResponse, 
  AuctionRequestComment, 
  AuctionRequestCriteria,
  AuctionRequestDetailsResponse 
} from '../models/auction-request.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuctionRequestService {
  private apiUrl = `${environment.apiUrl}/api/requests`;

  constructor(private http: HttpClient) { }

  /**
   * Get auction requests based on criteria
   */
  getRequests(criteria: AuctionRequestCriteria): Observable<AuctionRequestResponse[]> {
    let params = new HttpParams();
    
    if (criteria.requestTypes && criteria.requestTypes.length > 0) {
      criteria.requestTypes.forEach(type => {
        params = params.append('types', type);
      });
    }
    
    if (criteria.requestStatuses && criteria.requestStatuses.length > 0) {
      criteria.requestStatuses.forEach(status => {
        params = params.append('statuses', status);
      });
    }
    
    if (criteria.sortBy) {
      params = params.set('sortBy', criteria.sortBy);
    }
    
    if (criteria.ascending !== undefined) {
      params = params.set('ascending', criteria.ascending.toString());
    }
    
    if (criteria.page !== undefined) {
      params = params.set('page', criteria.page.toString());
    }
    
    if (criteria.size !== undefined) {
      params = params.set('size', criteria.size.toString());
    }
    
    console.log('Making request to:', this.apiUrl);
    console.log('With params:', params.toString());
    
    return this.http.get<AuctionRequestResponse[]>(this.apiUrl, { params })
      .pipe(
        tap(response => {
          console.log('Received response:', response);
          // Log the first item if available
          if (response && response.length > 0) {
            console.log('First request:', response[0]);
            console.log('First request auctionId:', response[0].auctionId);
          }
        }),
        catchError(error => {
          console.error('Error in getRequests:', error);
          throw error;
        })
      );
  }

  /**
   * Get a specific auction request by ID
   */
  getRequestById(id: number): Observable<AuctionRequestDetailsResponse> {
    return this.http.get<AuctionRequestDetailsResponse>(`${this.apiUrl}/${id}`);
  }

  /**
   * Approve an auction request
   */
  approveRequest(id: number, comment: AuctionRequestComment): Observable<string> {
    return this.http.post(`${this.apiUrl}/${id}/approve`, comment, { responseType: 'text' });
  }

  /**
   * Reject an auction request
   */
  rejectRequest(id: number, comment: AuctionRequestComment): Observable<string> {
    return this.http.post(`${this.apiUrl}/${id}/reject`, comment, { responseType: 'text' });
  }
} 