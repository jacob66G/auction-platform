import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuctionCreateDto } from '../../../models/auction.model';
import { CategoryResponse } from '../../../models/category.model';
import { AuctionService } from '../../../services/auction.service';
import { AuthService } from '../../../services/auth.service';
import { CategoryService } from '../../../services/category.service';
import { DateFormatterService } from '../../../services/date-formatter.service';
import { ErrorService } from '../../../services/error.service';

@Component({
  selector: 'app-auction-create',
  templateUrl: './auction-create.component.html',
  styleUrls: ['./auction-create.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ]
})
export class AuctionCreateComponent implements OnInit {
  auctionForm: FormGroup;
  categories: CategoryResponse[] = [];
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  selectedFiles: File[] = [];
  previewUrls: string[] = [];
  
  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private auctionService: AuctionService,
    private authService: AuthService,
    private categoryService: CategoryService,
    private dateFormatter: DateFormatterService,
    private errorService: ErrorService
  ) {
    this.auctionForm = this.formBuilder.group({
      title: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(30)
      ]],
      description: ['', [
        Validators.required,
        Validators.minLength(30),
        Validators.maxLength(255)
      ]],
      startingPrice: ['', [
        Validators.required,
        Validators.min(0)
      ]],
      startTime: ['', [
        Validators.required
      ]],
      endTime: ['', [
        Validators.required
      ]],
      categoryId: ['', [
        Validators.required
      ]]
    }, {
      validators: this.dateValidator
    });
  }
  
  ngOnInit(): void {
    // Check if user is authenticated
    this.authService.currentUser$.subscribe(user => {
      if (!user) {
        // Redirect to login if not authenticated
        this.router.navigate(['/login'], { 
          queryParams: { returnUrl: '/auctions/create' } 
        });
      }
    });
    
    this.loadCategories();
    
    // Set default start time to now
    const now = new Date();
    this.auctionForm.get('startTime')?.setValue(this.formatDateForInput(now));
    
    // Set default end time to 7 days from now
    const endDate = new Date();
    endDate.setDate(endDate.getDate() + 7);
    this.auctionForm.get('endTime')?.setValue(this.formatDateForInput(endDate));
  }
  
  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.categories = data;
        if (this.categories.length > 0) {
          this.auctionForm.get('categoryId')?.setValue(this.categories[0].id);
        }
      },
      error: (error) => {
        console.error('Error loading categories:', error);
        this.errorMessage = this.errorService.getErrorMessage(error);
      }
    });
  }
  
  dateValidator(form: FormGroup) {
    const startTime = form.get('startTime')?.value;
    const endTime = form.get('endTime')?.value;
    
    if (startTime && endTime) {
      const startDate = new Date(startTime);
      const endDate = new Date(endTime);
      
      if (startDate >= endDate) {
        form.get('endTime')?.setErrors({ endBeforeStart: true });
        return { endBeforeStart: true };
      }
    }
    
    return null;
  }
  
  formatDateForInput(date: Date): string {
    // Format date to local ISO string format for datetime-local input
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }
  
  onFileChange(event: any): void {
    if (event.target.files && event.target.files.length) {
      const filesToAdd = Array.from(event.target.files) as File[];
      
      // Limit to 5 images
      if (this.selectedFiles.length + filesToAdd.length > 5) {
        this.errorMessage = 'Możesz dodać maksymalnie 5 zdjęć.';
        return;
      }
      
      for (const file of filesToAdd) {
        // Check if file is an image
        if (!file.type.startsWith('image/')) {
          this.errorMessage = 'Możesz dodać tylko pliki graficzne.';
          continue;
        }
        
        this.selectedFiles.push(file);
        
        // Create preview URL
        const reader = new FileReader();
        reader.onload = () => {
          this.previewUrls.push(reader.result as string);
        };
        reader.readAsDataURL(file);
      }
    }
  }
  
  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.previewUrls.splice(index, 1);
  }
  
  onSubmit(): void {
    if (this.auctionForm.invalid) {
      // Oznacz wszystkie kontrolki jako dotknięte, aby wyświetlić komunikaty walidacyjne
      Object.keys(this.auctionForm.controls).forEach(key => {
        const control = this.auctionForm.get(key);
        control?.markAsTouched();
      });
      return;
    }
    
    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    // Parse categoryId to a number to prevent NaN error
    const categoryId = parseInt(this.auctionForm.value.categoryId, 10);
    if (isNaN(categoryId)) {
      this.errorMessage = 'Nieprawidłowa kategoria. Proszę wybrać kategorię z listy.';
      this.isSubmitting = false;
      return;
    }
    
    const auctionData: AuctionCreateDto = {
      title: this.auctionForm.value.title,
      description: this.auctionForm.value.description,
      startingPrice: parseFloat(this.auctionForm.value.startingPrice),
      startTime: this.dateFormatter.formatDateWithoutTimezone(new Date(this.auctionForm.value.startTime)),
      endTime: this.dateFormatter.formatDateWithoutTimezone(new Date(this.auctionForm.value.endTime)),
      categoryId: categoryId
    };
    
    console.log('Creating auction with data:', auctionData);
    
    this.auctionService.createAuction(auctionData, this.selectedFiles).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.successMessage = 'Aukcja została utworzona pomyślnie.';
        setTimeout(() => {
          this.router.navigate(['/auctions', response.id]);
        }, 1000);
      },
      error: (error) => {
        console.error('Error creating auction:', error);
        this.isSubmitting = false;
        this.errorMessage = this.errorService.getErrorMessage(error);
      }
    });
  }
} 