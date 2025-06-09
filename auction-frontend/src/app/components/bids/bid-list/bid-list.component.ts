import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { BidResponse } from '../../../models/bid.model';
import { BidService } from '../../../services/bid.service';
import { AuthService } from '../../../services/auth.service';
import { UserRole } from '../../../models/user.model';
import { catchError, retry, tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { PlnCurrencyPipe } from '../../../pipes/pln-currency.pipe';

@Component({
  selector: 'app-bid-list',
  templateUrl: './bid-list.component.html',
  styleUrls: ['./bid-list.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    PlnCurrencyPipe
  ]
})
export class BidListComponent implements OnInit, OnChanges {
  @Input() auctionId!: number;
  @Input() bids: BidResponse[] = [];
  isLoading = false;
  errorMessage = '';
  isAdmin = false;
  debugMode = false;
  
  constructor(
    private bidService: BidService,
    private authService: AuthService
  ) {}
  
  ngOnInit(): void {
    if (this.bids.length === 0) {
      this.loadBids();
    }
    this.checkUserRole();
  }
  
  ngOnChanges(changes: SimpleChanges): void {
    console.log('BidListComponent changes:', changes);
    if (changes['auctionId'] && !changes['auctionId'].firstChange && !changes['bids']) {
      this.loadBids();
    }
    if (changes['bids'] && changes['bids'].currentValue) {
      console.log('Bids set from parent:', changes['bids'].currentValue);
    }
  }
  
  checkUserRole(): void {
    const currentUser = this.authService.getCurrentUser();
    this.isAdmin = currentUser?.role === UserRole.ADMIN;
  }
  
  loadBids(): void {
    if (!this.auctionId || isNaN(Number(this.auctionId))) {
      console.error('No valid auction ID provided');
      this.errorMessage = 'Nie można załadować ofert - nieprawidłowy ID aukcji.';
      return;
    }
    
    const auctionId = Number(this.auctionId);
    console.log('Loading bids for auction ID:', auctionId);
    this.isLoading = true;
    this.errorMessage = '';
    
    this.bidService.getBidsByAuctionId(auctionId).pipe(
      retry(1),
      tap(data => console.log('Bids data in component:', data)),
      catchError(error => {
        console.error('Error loading bids (caught):', error);
        this.errorMessage = 'Nie udało się załadować ofert. Spróbuj odświeżyć stronę.';
        this.isLoading = false;
        return of([]);
      })
    ).subscribe({
      next: (data) => {
        console.log('Bids loaded successfully:', data);
        this.bids = data || [];
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading bids (uncaught):', error);
        this.errorMessage = 'Nie udało się załadować ofert.';
        this.isLoading = false;
      }
    });
  }
  
  deleteBid(bidId: number): void {
    if (!this.isAdmin) {
      return;
    }
    
    if (!bidId || isNaN(bidId)) {
      this.errorMessage = 'Nieprawidłowy identyfikator oferty.';
      return;
    }
    
    if (confirm('Czy na pewno chcesz usunąć tę ofertę? Ta operacja jest nieodwracalna.')) {
      this.bidService.deleteBid(bidId).subscribe({
        next: () => {
          this.loadBids();
        },
        error: (error) => {
          console.error('Error deleting bid:', error);
          this.errorMessage = 'Nie udało się usunąć oferty.';
        }
      });
    }
  }
} 