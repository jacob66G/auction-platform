import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { BidResponse } from '../../../models/bid.model';
import { BidService } from '../../../services/bid.service';
import { AuthService } from '../../../services/auth.service';
import { PlnCurrencyPipe } from '../../../pipes/pln-currency.pipe';

@Component({
  selector: 'app-my-bids',
  templateUrl: './my-bids.component.html',
  styleUrls: ['./my-bids.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    DatePipe,
    PlnCurrencyPipe
  ]
})
export class MyBidsComponent implements OnInit {
  bids: BidResponse[] = [];
  isLoading = false;
  errorMessage = '';
  
  constructor(
    private bidService: BidService,
    private authService: AuthService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    // Check if user is authenticated
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: '/user/bids' } 
      });
      return;
    }
    
    this.loadMyBids();
  }
  
  loadMyBids(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.bidService.getMyBids().subscribe(
      data => {
        this.bids = data;
        this.isLoading = false;
      },
      error => {
        console.error('Error loading my bids:', error);
        this.errorMessage = 'Nie udało się załadować Twoich ofert.';
        this.isLoading = false;
      }
    );
  }
  
  goToAuction(auctionId: number | undefined): void {
    if (auctionId !== undefined) {
      this.router.navigate(['/auctions', auctionId]);
    } else {
      console.error('Auction ID is undefined');
      this.errorMessage = 'Nie można przejść do aukcji - brak identyfikatora aukcji.';
    }
  }
} 