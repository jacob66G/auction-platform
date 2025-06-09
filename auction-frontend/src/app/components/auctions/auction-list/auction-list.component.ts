import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { AuctionResponse, AuctionSearchCriteria, AuctionStatus } from '../../../models/auction.model';
import { CategoryResponse } from '../../../models/category.model';
import { AuctionService } from '../../../services/auction.service';
import { AuthService } from '../../../services/auth.service';
import { CategoryService } from '../../../services/category.service';
import { PlnCurrencyPipe } from '../../../pipes/pln-currency.pipe';
import { UserRole } from '../../../models/user.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-auction-list',
  templateUrl: './auction-list.component.html',
  styleUrls: ['./auction-list.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    DatePipe,
    PlnCurrencyPipe
  ]
})
export class AuctionListComponent implements OnInit, OnDestroy {
  @Input() viewMode: 'public' | 'my' | 'admin' = 'public';
  
  auctions: AuctionResponse[] = [];
  categories: CategoryResponse[] = [];
  isLoading = false;
  searchForm: FormGroup;
  isAuthenticated = false;
  isAdmin = false;
  
  // Subject for managing subscriptions
  private destroy$ = new Subject<void>();
  
  // Mapowanie statusów aukcji na język polski
  statusTranslations: Record<string, string> = {
    'ACTIVE': 'Aktywna',
    'PENDING_APPROVAL': 'Oczekuje na zatwierdzenie',
    'FINISHED': 'Zakończona',
    'EXPIRED': 'Wygasła',
    'CANCELLED': 'Anulowana'
  };
  
  // Dostępne statusy do filtrowania
  availableStatuses: string[] = [
    'ACTIVE',
    'PENDING_APPROVAL',
    'FINISHED',
    'EXPIRED',
    'CANCELLED'
  ];
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  
  // Selected category IDs
  selectedCategoryIds: number[] = [];
  
  // Category dropdown state
  isCategoryDropdownOpen = false;
  
  constructor(
    private formBuilder: FormBuilder,
    private auctionService: AuctionService,
    private authService: AuthService,
    private categoryService: CategoryService,
    private route: ActivatedRoute
  ) {
    this.searchForm = this.formBuilder.group({
      title: [''],
      username: [''],
      categoryIds: [[]],
      sortBy: ['startTime'],
      ascending: [false],
      status: ['']
    });
    
    // Add click event listener to close dropdown when clicking outside
    document.addEventListener('click', this.onDocumentClick.bind(this));
  }
  
  ngOnInit(): void {
    // First check if view mode is set from route data (for direct paths)
    this.route.data.pipe(takeUntil(this.destroy$)).subscribe(data => {
      if (data['view']) {
        this.viewMode = data['view'];
      }
    });
    
    // Then check query params (for URL with query parameters)
    this.route.queryParams.pipe(takeUntil(this.destroy$)).subscribe(params => {
      if (params['view'] === 'my') {
        this.viewMode = 'my';
      } else if (params['view'] === 'admin') {
        this.viewMode = 'admin';
      } else if (!this.route.snapshot.data['view']) {
        // Only set to public if not already set from route data
        this.viewMode = 'public';
      }
    });
    
    // Load categories and check auth regardless of where view mode comes from
    this.loadCategories();
    this.checkAuth();
  }
  
  ngOnDestroy(): void {
    // Complete the subject to unsubscribe from all subscriptions
    this.destroy$.next();
    this.destroy$.complete();
    
    // Remove document click listener
    document.removeEventListener('click', this.onDocumentClick.bind(this));
  }
  
  checkAuth(): void {
    // Use takeUntil to automatically unsubscribe when component is destroyed
    this.authService.currentUser$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(user => {
      // Only search if authentication state actually changed
      const wasAuthenticated = this.isAuthenticated;
      const wasAdmin = this.isAdmin;
      
      this.isAuthenticated = !!user;
      this.isAdmin = user?.role === UserRole.ADMIN;
      
      // If user is not authenticated and trying to view 'my' auctions, switch to public
      if (!this.isAuthenticated && this.viewMode === 'my') {
        this.viewMode = 'public';
      }
      
      // If user is not admin and trying to view admin auctions, switch to public
      if (!this.isAdmin && this.viewMode === 'admin') {
        this.viewMode = 'public';
      }
      
      // Only search auctions if auth state changed or it's the first load
      if (wasAuthenticated !== this.isAuthenticated || wasAdmin !== this.isAdmin || !this.auctions.length) {
        this.searchAuctions();
      }
    });
  }
  
  isRegularUser(): boolean {
    const user = this.authService.getCurrentUser();
    return user?.role === UserRole.USER;
  }
  
  loadCategories(): void {
    this.categoryService.getCategories().pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (data) => {
        this.categories = data;
      },
      error: (error) => {
        // Handle error silently
      }
    });
  }
  
  searchAuctions(): void {
    // Prevent searching if already loading
    if (this.isLoading) {
      return;
    }
    
    this.isLoading = true;
    this.auctions = []; // Clear existing auctions while loading
    
    const criteria: AuctionSearchCriteria = {
      title: this.searchForm.value.title || undefined,
      username: this.searchForm.value.username || undefined,
      categoryIds: this.selectedCategoryIds.length ? this.selectedCategoryIds : undefined,
      sortBy: this.searchForm.value.sortBy,
      ascending: this.searchForm.value.ascending,
      page: this.currentPage,
      size: this.pageSize
    };
    
    if (this.searchForm.value.status) {
      criteria.statuses = [this.searchForm.value.status];
    } else if (this.viewMode === 'public') {
      // For public view, only show active auctions by default
      criteria.statuses = ['ACTIVE'];
    }
    
    switch (this.viewMode) {
      case 'my':
        this.auctionService.getMyAuctions(criteria).pipe(
          takeUntil(this.destroy$)
        ).subscribe({
          next: (data) => {
            this.auctions = data;
            this.isLoading = false;
          },
          error: (error) => {
            this.isLoading = false;
          }
        });
        break;
        
      case 'admin':
        this.auctionService.adminSearchAuctions(criteria).pipe(
          takeUntil(this.destroy$)
        ).subscribe({
          next: (data) => {
            this.auctions = data;
            this.isLoading = false;
          },
          error: (error) => {
            this.isLoading = false;
          }
        });
        break;
        
      default: // 'public'
        this.auctionService.searchAuctions(criteria).pipe(
          takeUntil(this.destroy$)
        ).subscribe({
          next: (data) => {
            this.auctions = data;
            this.isLoading = false;
          },
          error: (error) => {
            this.isLoading = false;
          }
        });
        break;
    }
  }
  
  onCategoryChange(event: Event, categoryId: number): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    
    if (isChecked) {
      // Add to selected categories if not already present
      if (!this.selectedCategoryIds.includes(categoryId)) {
        this.selectedCategoryIds.push(categoryId);
      }
    } else {
      // Remove from selected categories
      this.selectedCategoryIds = this.selectedCategoryIds.filter(id => id !== categoryId);
    }
    
    // Update the form control
    this.searchForm.get('categoryIds')?.setValue(this.selectedCategoryIds);
  }
  
  onPageChange(page: number): void {
    this.currentPage = page;
    this.searchAuctions();
  }
  
  onSearch(): void {
    this.currentPage = 0;
    this.searchAuctions();
  }
  
  onReset(): void {
    this.searchForm.reset({
      title: '',
      username: '',
      categoryIds: [],
      sortBy: 'startTime',
      ascending: false,
      status: ''
    });
    this.selectedCategoryIds = [];
    this.currentPage = 0;
    this.searchAuctions();
  }
  
  toggleCategoryDropdown(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.isCategoryDropdownOpen = !this.isCategoryDropdownOpen;
  }
  
  onDocumentClick(event: MouseEvent): void {
    if (!(event.target as HTMLElement).closest('.category-dropdown')) {
      this.isCategoryDropdownOpen = false;
    }
  }
  
  clearCategories(): void {
    this.selectedCategoryIds = [];
    this.searchForm.get('categoryIds')?.setValue([]);
  }
} 