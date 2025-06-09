import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, DecimalPipe } from '@angular/common';
import { BidRequest } from '../../../models/bid.model';
import { BidService } from '../../../services/bid.service';

@Component({
  selector: 'app-bid-form',
  templateUrl: './bid-form.component.html',
  styleUrls: ['./bid-form.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DecimalPipe
  ]
})
export class BidFormComponent implements OnInit {
  @Input() auctionId!: number;
  @Input() minBidAmount: number = 0;
  @Input() currentPrice: number = 0;
  @Output() bidPlaced = new EventEmitter<boolean>();
  
  bidForm: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private bidService: BidService
  ) {
    this.bidForm = this.formBuilder.group({
      amount: ['', [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    // Calculate minimum bid amount (1% higher than current price)
    const minimumBid = Math.max(this.minBidAmount, this.currentPrice * 1.01);
    
    this.bidForm.get('amount')?.setValidators([
      Validators.required,
      Validators.min(minimumBid)
    ]);
    
    this.bidForm.get('amount')?.setValue(Math.ceil(minimumBid * 100) / 100);
    this.bidForm.get('amount')?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.bidForm.invalid) {
      return;
    }

    // Validate auctionId
    if (!this.auctionId || isNaN(Number(this.auctionId))) {
      this.errorMessage = 'Nieprawidłowy identyfikator aukcji.';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const bidRequest: BidRequest = {
      amount: parseFloat(this.bidForm.value.amount),
      auctionId: Number(this.auctionId)
    };

    this.bidService.placeBid(bidRequest).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.successMessage = 'Oferta została złożona pomyślnie.';
        this.bidPlaced.emit(true);
        
        // Reset form with new minimum bid
        this.currentPrice = bidRequest.amount;
        this.ngOnInit();
      },
      error: (error) => {
        this.isSubmitting = false;
        if (error.status === 400) {
          this.errorMessage = error.error?.message || 'Nieprawidłowa oferta. Spróbuj ponownie.';
        } else if (error.status === 403) {
          this.errorMessage = 'Nie masz uprawnień do składania ofert w tej aukcji.';
        } else if (error.status === 409) {
          this.errorMessage = error.error?.message || 'Konflikt - cena aukcji została już zmieniona przez inną ofertę. Odśwież stronę, aby zobaczyć aktualną cenę.';
          // Emit event to refresh auction details
          this.bidPlaced.emit(true);
        } else {
          this.errorMessage = 'Wystąpił błąd podczas składania oferty. Spróbuj ponownie później.';
        }
        console.error('Error placing bid:', error);
      }
    });
  }
} 