import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuctionRequestService } from '../../../services/auction-request.service';
import { ErrorService } from '../../../services/error.service';
import { AuthService } from '../../../services/auth.service';
import { 
  AuctionRequestResponse, 
  AuctionRequestComment, 
  AuctionRequestCriteria, 
  RequestStatus, 
  RequestType 
} from '../../../models/auction-request.model';

@Component({
  selector: 'app-auction-requests',
  templateUrl: './auction-requests.component.html',
  styleUrls: ['./auction-requests.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule
  ]
})
export class AuctionRequestsComponent implements OnInit, OnDestroy {
  requests: AuctionRequestResponse[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  searchForm: FormGroup;
  commentForm: FormGroup;
  
  // For filtering
  requestTypes = [
    { value: 'SAVE', label: 'Zapis aukcji' },
    { value: 'CANCEL', label: 'Anulowanie aukcji' }
  ];
  
  requestStatuses = [
    { value: 'PENDING', label: 'Oczekujące' },
    { value: 'APPROVE', label: 'Zatwierdzone' },
    { value: 'REJECT', label: 'Odrzucone' }
  ];
  
  // For confirmation modal
  showModal = false;
  modalType = ''; // 'approve' or 'reject'
  selectedRequest: AuctionRequestResponse | null = null;
  
  // For unsubscribing from observables
  private destroy$ = new Subject<void>();
  
  constructor(
    private auctionRequestService: AuctionRequestService,
    private errorService: ErrorService,
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.searchForm = this.formBuilder.group({
      requestTypes: [null],
      requestStatuses: [['PENDING']],
      sortBy: ['requestDate'],
      ascending: [false]
    });
    
    this.commentForm = this.formBuilder.group({
      comment: ['']
    });
  }
  
  ngOnInit(): void {
    this.loadRequests();
    
    // Subscribe to form value changes
    this.searchForm.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.loadRequests();
      });
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  loadRequests(): void {
    this.isLoading = true;
    this.requests = [];
    this.errorMessage = '';
    
    const criteria: AuctionRequestCriteria = {
      requestTypes: this.searchForm.get('requestTypes')?.value,
      requestStatuses: this.searchForm.get('requestStatuses')?.value,
      sortBy: this.searchForm.get('sortBy')?.value,
      ascending: this.searchForm.get('ascending')?.value,
      page: 0,
      size: 50
    };
    
    console.log('Sending request criteria:', criteria);
    
    this.auctionRequestService.getRequests(criteria)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (requests) => {
          console.log('Received requests:', requests);
          // Debug: Check if auctionId is present in the response
          if (requests.length > 0) {
            console.log('First request auctionId:', requests[0].auctionId);
          }
          this.requests = requests;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading requests:', error);
          
          // Get a more detailed error message
          if (error.status === 404) {
            this.errorMessage = `Endpoint not found: ${error.url}. Please check API URL configuration.`;
          } else {
            this.errorMessage = this.errorService.getErrorMessage(error);
          }
          
          this.isLoading = false;
        }
      });
  }
  
  // New methods for handling checkbox changes with proper event typing
  handleRequestTypeChange(event: Event, typeValue: string): void {
    const checkbox = event.target as HTMLInputElement;
    const isChecked = checkbox.checked;
    const currentTypes = [...(this.searchForm.get('requestTypes')?.value || [])];
    
    if (isChecked) {
      // Add the type if it's not already in the array
      if (!currentTypes.includes(typeValue)) {
        currentTypes.push(typeValue);
      }
    } else {
      // Remove the type
      const index = currentTypes.indexOf(typeValue);
      if (index !== -1) {
        currentTypes.splice(index, 1);
      }
    }
    
    this.searchForm.get('requestTypes')?.setValue(currentTypes);
  }
  
  handleRequestStatusChange(event: Event, statusValue: string): void {
    const checkbox = event.target as HTMLInputElement;
    const isChecked = checkbox.checked;
    const currentStatuses = [...(this.searchForm.get('requestStatuses')?.value || [])];
    
    if (isChecked) {
      // Add the status if it's not already in the array
      if (!currentStatuses.includes(statusValue)) {
        currentStatuses.push(statusValue);
      }
    } else {
      // Remove the status
      const index = currentStatuses.indexOf(statusValue);
      if (index !== -1) {
        currentStatuses.splice(index, 1);
      }
    }
    
    this.searchForm.get('requestStatuses')?.setValue(currentStatuses);
  }
  
  /**
   * Navigate to request details page
   */
  viewRequestDetails(request: AuctionRequestResponse): void {
    if (!request || !request.id) {
      this.errorMessage = 'Brak identyfikatora żądania.';
      return;
    }
    
    this.router.navigate(['/admin/auction-requests', request.id]);
  }
  
  openApproveModal(request: AuctionRequestResponse): void {
    this.selectedRequest = request;
    this.modalType = 'approve';
    this.commentForm.reset();
    this.showModal = true;
  }
  
  openRejectModal(request: AuctionRequestResponse): void {
    this.selectedRequest = request;
    this.modalType = 'reject';
    this.commentForm.reset();
    this.showModal = true;
  }
  
  closeModal(): void {
    this.showModal = false;
    this.selectedRequest = null;
  }
  
  confirmAction(): void {
    if (!this.selectedRequest || !this.selectedRequest.id) return;
    
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    const comment: AuctionRequestComment = {
      comment: this.commentForm.get('comment')?.value || ''
    };
    
    if (this.modalType === 'approve') {
      this.auctionRequestService.approveRequest(this.selectedRequest.id, comment)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            this.successMessage = response || `Zatwierdzono żądanie ${this.selectedRequest?.auctionTitle}.`;
            this.isLoading = false;
            this.closeModal();
            this.loadRequests();
          },
          error: (error) => {
            this.errorMessage = this.errorService.getErrorMessage(error);
            this.isLoading = false;
            this.closeModal();
          }
        });
    } else if (this.modalType === 'reject') {
      this.auctionRequestService.rejectRequest(this.selectedRequest.id, comment)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            this.successMessage = response || `Odrzucono żądanie ${this.selectedRequest?.auctionTitle}.`;
            this.isLoading = false;
            this.closeModal();
            this.loadRequests();
          },
          error: (error) => {
            this.errorMessage = this.errorService.getErrorMessage(error);
            this.isLoading = false;
            this.closeModal();
          }
        });
    }
  }
  
  getRequestTypeLabel(type: string): string {
    const requestType = this.requestTypes.find(t => t.value === type);
    return requestType ? requestType.label : type;
  }
  
  getRequestStatusLabel(status: string): string {
    const requestStatus = this.requestStatuses.find(s => s.value === status);
    return requestStatus ? requestStatus.label : status;
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
      default:
        return 'bi-question-circle';
    }
  }
  
  canModerate(request: AuctionRequestResponse): boolean {
    // Check if the request is pending and the current user is not the requester
    const currentUser = this.authService.getCurrentUser();
    return request.requestStatus === 'PENDING' && 
           currentUser?.username !== request.requesterName;
  }
  
  resetFilters(): void {
    this.searchForm.patchValue({
      requestTypes: null,
      requestStatuses: ['PENDING'],
      sortBy: 'requestDate',
      ascending: false
    });
  }
  
  /**
   * Navigate to auction details page
   */
  viewAuctionDetails(request: AuctionRequestResponse | number): void {
    this.errorMessage = '';
    
    if (!request) {
      this.errorMessage = 'Brak danych żądania.';
      return;
    }
    
    // Get the request object if we were passed a number
    let requestObj: AuctionRequestResponse;
    if (typeof request === 'number') {
      // If we only have the ID, find the request in our list
      const foundRequest = this.requests.find(r => r.id === request);
      if (!foundRequest) {
        this.errorMessage = 'Nie znaleziono żądania o podanym ID.';
        return;
      }
      requestObj = foundRequest;
    } else {
      requestObj = request;
    }
    
    // Extract auction ID from the request object
    // In most backends, the auction ID is stored in a field called "auctionId"
    if (!requestObj.auctionId) {
      this.errorMessage = 'Brak identyfikatora aukcji w danych żądania.';
      console.error('No auctionId found in request:', requestObj);
      return;
    }
    
    // Navigate directly to the auction details page
    console.log('Navigating to auction with ID:', requestObj.auctionId);
    this.router.navigate(['/auctions', requestObj.auctionId]);
  }
} 