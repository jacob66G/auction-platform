import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuctionRequestService } from '../../../services/auction-request.service';
import { ErrorService } from '../../../services/error.service';
import { AuthService } from '../../../services/auth.service';
import { 
  AuctionRequestDetailsResponse, 
  AuctionRequestComment 
} from '../../../models/auction-request.model';

@Component({
  selector: 'app-auction-request-details',
  templateUrl: './auction-request-details.component.html',
  styleUrls: ['./auction-request-details.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ]
})
export class AuctionRequestDetailsComponent implements OnInit, OnDestroy {
  requestId: number | null = null;
  requestDetails: AuctionRequestDetailsResponse | null = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  commentForm: FormGroup;
  
  // For modals
  showApproveModal = false;
  showRejectModal = false;
  
  // For unsubscribing from observables
  private destroy$ = new Subject<void>();
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private auctionRequestService: AuctionRequestService,
    private errorService: ErrorService,
    private authService: AuthService,
    private formBuilder: FormBuilder
  ) {
    this.commentForm = this.formBuilder.group({
      comment: ['']
    });
  }
  
  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.requestId = +idParam;
        this.loadRequestDetails();
      } else {
        this.errorMessage = 'Brak identyfikatora żądania.';
      }
    });
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  loadRequestDetails(): void {
    if (!this.requestId) return;
    
    this.isLoading = true;
    this.errorMessage = '';
    
    this.auctionRequestService.getRequestById(this.requestId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (details) => {
          this.requestDetails = details;
          console.log('Loaded request details:', this.requestDetails);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading request details:', error);
          this.errorMessage = this.errorService.getErrorMessage(error);
          this.isLoading = false;
        }
      });
  }
  
  canModerate(): boolean {
    if (!this.requestDetails) return false;
    
    // Check if the request is pending and the current user is not the requester
    const currentUser = this.authService.getCurrentUser();
    return this.requestDetails.requestStatus === 'PENDING' && 
           currentUser?.username !== this.requestDetails.requesterName;
  }
  
  openApproveModal(): void {
    this.commentForm.reset();
    this.showApproveModal = true;
  }
  
  openRejectModal(): void {
    this.commentForm.reset();
    this.showRejectModal = true;
  }
  
  closeModal(): void {
    this.showApproveModal = false;
    this.showRejectModal = false;
  }
  
  approveRequest(): void {
    if (!this.requestId) return;
    
    this.isLoading = true;
    this.errorMessage = '';
    
    const comment: AuctionRequestComment = {
      comment: this.commentForm.get('comment')?.value || ''
    };
    
    this.auctionRequestService.approveRequest(this.requestId, comment)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.successMessage = response || `Zatwierdzono żądanie ${this.requestDetails?.auctionTitle}.`;
          this.isLoading = false;
          this.closeModal();
          this.navigateBack();
        },
        error: (error) => {
          this.errorMessage = this.errorService.getErrorMessage(error);
          this.isLoading = false;
          this.closeModal();
        }
      });
  }
  
  rejectRequest(): void {
    if (!this.requestId) return;
    
    this.isLoading = true;
    this.errorMessage = '';
    
    const comment: AuctionRequestComment = {
      comment: this.commentForm.get('comment')?.value || ''
    };
    
    this.auctionRequestService.rejectRequest(this.requestId, comment)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.successMessage = response || `Odrzucono żądanie ${this.requestDetails?.auctionTitle}.`;
          this.isLoading = false;
          this.closeModal();
          this.navigateBack();
        },
        error: (error) => {
          this.errorMessage = this.errorService.getErrorMessage(error);
          this.isLoading = false;
          this.closeModal();
        }
      });
  }
  
  getRequestTypeLabel(type: string): string {
    switch (type) {
      case 'SAVE': return 'Zapis aukcji';
      case 'CANCEL': return 'Anulowanie aukcji';
      case 'EDIT': return 'Edycja aukcji';
      default: return type;
    }
  }
  
  getRequestStatusLabel(status: string): string {
    switch (status) {
      case 'PENDING': return 'Oczekujące';
      case 'APPROVE': return 'Zatwierdzone';
      case 'REJECT': return 'Odrzucone';
      default: return status;
    }
  }
  
  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'bg-warning';
      case 'APPROVE':
        return 'bg-success';
      case 'REJECT':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }
  
  getTypeIcon(type: string): string {
    switch (type) {
      case 'SAVE':
        return 'bi-plus-circle';
      case 'CANCEL':
        return 'bi-x-circle';
      case 'EDIT':
        return 'bi-pencil';
      default:
        return 'bi-question-circle';
    }
  }
  
  navigateToAuction(): void {
    if (this.requestDetails?.auctionId) {
      this.router.navigate(['/auctions', this.requestDetails.auctionId]);
    }
  }
  
  navigateBack(): void {
    this.router.navigate(['/admin/auction-requests']);
  }
} 