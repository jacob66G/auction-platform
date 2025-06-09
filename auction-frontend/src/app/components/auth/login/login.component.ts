import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ]
})
export class LoginComponent {
  loginForm: FormGroup;
  isSubmitting = false;
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    
    const username = this.loginForm.value.username;
    const password = this.loginForm.value.password;
    
    console.log('Attempting login with username:', username);
    
    this.authService.login(username, password).subscribe(
      (user) => {
        console.log('Login successful:', user);
        this.isSubmitting = false;
        this.router.navigate(['/auctions']);
      },
      error => {
        this.isSubmitting = false;
        console.error('Login error details:', error);
        
        if (error.status === 401) {
          this.errorMessage = 'Nieprawidłowa nazwa użytkownika lub hasło';
        } else {
          this.errorMessage = 'Wystąpił błąd podczas logowania. Spróbuj ponownie później.';
        }
      }
    );
  }
} 