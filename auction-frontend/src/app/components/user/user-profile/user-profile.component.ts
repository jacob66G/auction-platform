import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { 
  UserResponse, 
  ChangePasswordRequest, 
  DepositRequest,
  UserRole,
  ChangeEmailRequest
} from '../../../models/user.model';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { PlnCurrencyPipe } from '../../../pipes/pln-currency.pipe';
import { ErrorResponse } from '../../../models/response.model';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DatePipe,
    PlnCurrencyPipe,
    DecimalPipe
  ]
})
export class UserProfileComponent implements OnInit {
  user: UserResponse | null = null;
  passwordForm: FormGroup;
  depositForm: FormGroup;
  emailForm: FormGroup;
  
  isLoading = false;
  isChangingPassword = false;
  isChangingEmail = false;
  isDepositing = false;
  
  errorMessage = '';
  successMessage = '';
  
  passwordErrorMessage = '';
  passwordSuccessMessage = '';
  
  emailErrorMessage = '';
  emailSuccessMessage = '';
  
  depositErrorMessage = '';
  depositSuccessMessage = '';
  
  activeTab = 'profile'; // 'profile', 'security', 'balance'
  
  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private userService: UserService
  ) {
    this.passwordForm = this.formBuilder.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(5)]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
    
    this.depositForm = this.formBuilder.group({
      amount: ['', [Validators.required, Validators.min(1)]]
    });

    this.emailForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }
  
  ngOnInit(): void {
    // Check if user is authenticated
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: '/user/profile' } 
      });
      return;
    }
    
    // Check for tab parameter in URL
    this.route.queryParams.subscribe(params => {
      if (params['tab'] && ['profile', 'security', 'balance'].includes(params['tab'])) {
        this.activeTab = params['tab'];
      }
    });
    
    this.loadUserData();
  }
  
  loadUserData(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;
        this.isLoading = false;
        
        // Populate the email form with current email
        this.emailForm.patchValue({
          email: user.email || ''
        });
      },
      error: (error) => {
        console.error('Error loading user data:', error);
        this.errorMessage = 'Nie udało się załadować danych użytkownika.';
        this.isLoading = false;
      }
    });
  }
  
  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;
    
    if (newPassword !== confirmPassword) {
      form.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    return null;
  }
  
  changeEmail(): void {
    if (this.emailForm.invalid) {
      return;
    }
    
    this.isChangingEmail = true;
    this.emailErrorMessage = '';
    this.emailSuccessMessage = '';
    
    const request: ChangeEmailRequest = {
      email: this.emailForm.value.email
    };
    
    this.userService.changeEmail(request).subscribe({
      next: (response) => {
        this.isChangingEmail = false;
        this.emailSuccessMessage = response || 'Pomyślnie zmieniono adres email.';
        
        // Update auth service with new user data
        this.authService.getCurrentUserInfo().subscribe();
        
        // Reload user data to get updated email
        this.loadUserData();
      },
      error: (error) => {
        console.error('Error changing email:', error);
        this.isChangingEmail = false;
        
        if (error.status === 409) {
          this.emailErrorMessage = error.error?.message || 'Adres email można zmienić tylko raz na 30 dni.';
        } else if (error.status === 400) {
          this.emailErrorMessage = error.error?.message || 'Nieprawidłowy adres email.';
        } else {
          this.emailErrorMessage = 'Nie udało się zmienić adresu email.';
        }
      }
    });
  }
  
  changePassword(): void {
    if (this.passwordForm.invalid) {
      return;
    }
    
    this.isChangingPassword = true;
    this.passwordErrorMessage = '';
    this.passwordSuccessMessage = '';
    
    const request: ChangePasswordRequest = {
      currentPassword: this.passwordForm.value.currentPassword,
      newPassword: this.passwordForm.value.newPassword,
      confirmPassword: this.passwordForm.value.confirmPassword
    };
    
    this.userService.changePassword(request).subscribe({
      next: (response) => {
        this.isChangingPassword = false;
        this.passwordSuccessMessage = response || 'Pomyślnie zmieniono hasło.';
        this.passwordForm.reset();
      },
      error: (error) => {
        console.error('Error changing password:', error);
        if (error.status === 400) {
          this.passwordErrorMessage = error.error?.message || 'Nieprawidłowe hasło.';
        } else {
          this.passwordErrorMessage = 'Nie udało się zmienić hasła.';
        }
        this.isChangingPassword = false;
      }
    });
  }
  
  deposit(): void {
    if (this.depositForm.invalid) {
      return;
    }
    
    this.isDepositing = true;
    this.depositErrorMessage = '';
    this.depositSuccessMessage = '';
    
    const request: DepositRequest = {
      amount: this.depositForm.value.amount
    };
    
    this.userService.deposit(request).subscribe({
      next: (response) => {
        console.log('Deposit successful, server response:', response);
        this.isDepositing = false;
        this.depositSuccessMessage = 'Wpłata została zrealizowana.';
        this.depositForm.reset();
        
        // Reload user data to get updated balance
        this.loadUserData();
      },
      error: (error) => {
        console.error('Error depositing:', error);
        this.depositErrorMessage = 'Nie udało się zrealizować wpłaty.';
        this.isDepositing = false;
      }
    });
  }
  
  setActiveTab(tab: string): void {
    this.activeTab = tab;
    // Update URL query parameter without reloading the page
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { tab: tab },
      queryParamsHandling: 'merge',
      replaceUrl: true
    });
  }
  
  isRegularUser(): boolean {
    return this.user?.role === UserRole.USER;
  }
  
  isModeratorUser(): boolean {
    return this.user?.role === UserRole.MODERATOR;
  }
} 