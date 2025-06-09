import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, HostListener } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { AuctionDetailsResponse, AuctionCancelRequest } from '../../../models/auction.model';
import { MessageResponse } from '../../../models/response.model';
import { AuctionService } from '../../../services/auction.service';
import { AuthService } from '../../../services/auth.service';
import { ErrorService } from '../../../services/error.service';
import { BidFormComponent } from '../../bids/bid-form/bid-form.component';
import { BidListComponent } from '../../bids/bid-list/bid-list.component';
import { BidService } from '../../../services/bid.service';
import { BidResponse } from '../../../models/bid.model';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PlnCurrencyPipe } from '../../../pipes/pln-currency.pipe';
import { UserRole } from '../../../models/user.model';

@Component({
  selector: 'app-auction-details',
  templateUrl: './auction-details.component.html',
  styleUrls: ['./auction-details.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    DatePipe,
    BidFormComponent,
    BidListComponent,
    PlnCurrencyPipe,
    DecimalPipe
  ]
})
export class AuctionDetailsComponent implements OnInit, AfterViewInit {
  @ViewChild('bidList') bidList?: BidListComponent;
  @ViewChild('thumbnailsContainer') thumbnailsContainer?: ElementRef;
  
  auction: AuctionDetailsResponse | null = null;
  bids: BidResponse[] = [];
  cancelForm: FormGroup;
  isLoading = true;
  errorMessage = '';
  successMessage = '';
  cancelSubmitting = false;
  isOwner = false;
  isAuthenticated = false;
  isAdmin = false;
  isRegularUser = false;
  selectedImageIndex = 0;
  showCancelModal = false;
  isScrollLeftDisabled = true;
  isScrollRightDisabled = false;
  
  // Close modal when Escape key is pressed
  @HostListener('document:keydown.escape')
  onEscapePress() {
    this.closeCancelModal();
  }
  
  // Prevent scrolling when modal is open
  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (this.showCancelModal && event.key === 'Tab') {
      // Keep focus within modal
      const modal = document.querySelector('.modal-content');
      if (modal) {
        const focusableElements = modal.querySelectorAll(
          'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        );
        
        if (focusableElements.length > 0) {
          const firstElement = focusableElements[0] as HTMLElement;
          const lastElement = focusableElements[focusableElements.length - 1] as HTMLElement;
          
          if (event.shiftKey && document.activeElement === firstElement) {
            lastElement.focus();
            event.preventDefault();
          } else if (!event.shiftKey && document.activeElement === lastElement) {
            firstElement.focus();
            event.preventDefault();
          }
        }
      }
    }
  }
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private auctionService: AuctionService,
    private authService: AuthService,
    private bidService: BidService,
    private errorService: ErrorService
  ) {
    this.cancelForm = this.formBuilder.group({
      reason: ['', [Validators.required, Validators.minLength(10)]]
    });
  }
  
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      console.log('Auction ID from route:', id);
      const auctionId = Number(id);
      if (!isNaN(auctionId)) {
        this.loadAuctionDetails(auctionId);
      } else {
        console.error('Invalid auction ID:', id);
        this.errorMessage = 'Nieprawidłowy identyfikator aukcji.';
        this.isLoading = false;
      }
    } else {
      console.error('No auction ID provided in route');
      this.errorMessage = 'Brak identyfikatora aukcji.';
      this.isLoading = false;
    }
    
    this.authService.currentUser$.subscribe(user => {
      this.isAuthenticated = !!user;
      this.isAdmin = user?.role === 'ADMIN';
      this.isRegularUser = user?.role === UserRole.USER;
      
      if (this.auction && user) {
        this.isOwner = user.username === this.auction.username;
      }
    });
  }
  
  ngAfterViewInit(): void {
    // Check scroll status after view initialization
    setTimeout(() => {
      this.checkScrollPosition();
    }, 500);
  }
  
  loadAuctionDetails(id: number): void {
    console.log('Loading auction details for ID:', id);
    this.isLoading = true;
    this.errorMessage = '';
    
    forkJoin({
      auctionDetails: this.auctionService.getAuctionById(id),
      bids: this.bidService.getBidsByAuctionId(id).pipe(
        catchError(error => {
          console.error('Error loading bids in forkJoin:', error);
          return of([]);
        })
      )
    }).subscribe(
      result => {
        console.log('ForkJoin result:', result);
        this.auction = result.auctionDetails;
        this.bids = result.bids;
        this.isLoading = false;
        
        // Check if current user is the owner
        const currentUser = this.authService.getCurrentUser();
        if (currentUser) {
          this.isOwner = currentUser.username === result.auctionDetails.username;
          console.log('Current user is owner:', this.isOwner);
        }
        
        // Manually update bid list if component is available
        setTimeout(() => {
          if (this.bidList) {
            console.log('Manually updating bid list with bids:', this.bids);
            this.bidList.bids = this.bids;
            this.bidList.isLoading = false;
          }
          
          // Check scroll position for thumbnails after data is loaded
          this.checkScrollPosition();
        }, 300);
      },
      error => {
        console.error('Error loading auction details:', error);
        this.isLoading = false;
        this.errorMessage = 'Nie udało się załadować szczegółów aukcji.';
      }
    );
  }
  
  openCancelModal(): void {
    this.showCancelModal = true;
    // Focus the first focusable element in the modal after it appears
    setTimeout(() => {
      const firstInput = document.querySelector('.modal-content textarea') as HTMLElement;
      if (firstInput) {
        firstInput.focus();
      }
    }, 100);
  }
  
  closeCancelModal(): void {
    this.showCancelModal = false;
    this.cancelForm.reset();
  }
  
  cancelAuction(): void {
    if (this.cancelForm.invalid || !this.auction) {
      return;
    }
    
    this.cancelSubmitting = true;
    this.errorMessage = '';
    
    const cancelRequest: AuctionCancelRequest = {
      reason: this.cancelForm.value.reason
    };
    
    this.auctionService.cancelAuction(this.auction.id, cancelRequest).subscribe(
      (response: MessageResponse) => {
        this.cancelSubmitting = false;
        this.showCancelModal = false;
        
        // Wyświetl odpowiednią wiadomość w zależności od odpowiedzi z backendu
        if (response.message.includes('successfully approved')) {
          this.successMessage = 'Aukcja została anulowana.';
        } else if (response.message.includes('submitted for approval')) {
          this.successMessage = 'Twoja prośba o anulowanie aukcji została przesłana do zatwierdzenia. Zostaniesz powiadomiony e-mailem po jej rozpatrzeniu.';
        } else {
          this.successMessage = response.message;
        }
        
        this.loadAuctionDetails(this.auction!.id); // Reload auction details
      },
      error => {
        console.error('Error canceling auction:', error);
        this.cancelSubmitting = false;
        
        if (error.status === 409 && error.error?.message?.includes('can only submit 2 cancel requests')) {
          this.errorMessage = 'Możesz wysłać maksymalnie 2 prośby o anulowanie tej aukcji.';
        } else {
          this.errorMessage = this.errorService.getErrorMessage(error);
        }
        
        this.showCancelModal = false;
      }
    );
  }
  
  deleteAuction(): void {
    if (!this.auction || !this.isAdmin) {
      return;
    }
    
    if (confirm('Czy na pewno chcesz usunąć tę aukcję? Ta operacja jest nieodwracalna.')) {
      this.auctionService.deleteAuction(this.auction.id).subscribe(
        () => {
          this.router.navigate(['/auctions']);
        },
        error => {
          console.error('Error deleting auction:', error);
          this.errorMessage = this.errorService.getErrorMessage(error);
        }
      );
    }
  }
  
  selectImage(index: number): void {
    this.selectedImageIndex = index;
    console.log('Selected image index:', index);
    
    // Scroll the thumbnail into view if needed
    if (this.thumbnailsContainer) {
      const thumbnails = this.thumbnailsContainer.nativeElement.querySelectorAll('.thumbnail-container');
      if (thumbnails[index]) {
        thumbnails[index].scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
      }
    }
  }
  
  scrollThumbnails(direction: 'left' | 'right'): void {
    if (!this.thumbnailsContainer) return;
    console.log('Scrolling thumbnails:', direction);
    
    const container = this.thumbnailsContainer.nativeElement;
    const scrollAmount = 240; // 3 thumbnails width approximately
    
    if (direction === 'left') {
      container.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    } else {
      container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
    
    // Update button states after scrolling
    setTimeout(() => {
      this.checkScrollPosition();
    }, 300);
  }
  
  checkScrollPosition(): void {
    if (!this.thumbnailsContainer) return;
    
    const container = this.thumbnailsContainer.nativeElement;
    
    // Check if scrolled to the start
    this.isScrollLeftDisabled = container.scrollLeft <= 0;
    
    // Check if scrolled to the end
    this.isScrollRightDisabled = container.scrollLeft + container.clientWidth >= container.scrollWidth - 10;
  }
  
  onMouseWheel(event: WheelEvent): void {
    // Prevent default scroll behavior to avoid page scrolling
    event.preventDefault();
    
    if (!this.thumbnailsContainer) return;
    
    const container = this.thumbnailsContainer.nativeElement;
    const scrollAmount = 80; // Adjust scroll amount for wheel
    
    // Scroll horizontally based on wheel delta
    if (event.deltaY > 0) {
      // Scroll right on wheel down
      container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    } else {
      // Scroll left on wheel up
      container.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    }
    
    // Update button states after scrolling
    setTimeout(() => {
      this.checkScrollPosition();
    }, 300);
  }
  
  getTimeRemaining(): string {
    if (!this.auction) return '';
    
    const endTime = new Date(this.auction.endTime).getTime();
    const now = new Date().getTime();
    const timeRemaining = endTime - now;
    
    if (timeRemaining <= 0) {
      return 'Aukcja zakończona';
    }
    
    const days = Math.floor(timeRemaining / (1000 * 60 * 60 * 24));
    const hours = Math.floor((timeRemaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((timeRemaining % (1000 * 60 * 60)) / (1000 * 60));
    
    return `${days}d ${hours}h ${minutes}m`;
  }
  
  isAuctionActive(): boolean {
    return this.auction?.auctionStatus === 'ACTIVE';
  }
  
  // Metoda wywoływana po złożeniu nowej oferty
  onBidPlaced(): void {
    console.log('Bid placed, refreshing auction and bids...');
    if (this.auction) {
      // Odświeżamy szczegóły aukcji wraz z ofertami
      this.loadAuctionDetails(this.auction.id);
    }
  }
} 