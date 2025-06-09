import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UserRole } from '../../../models/user.model';
import { AuthService } from '../../../services/auth.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterLink]
})
export class NavbarComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  username = '';
  userRole: UserRole | null = null;
  isMenuCollapsed = true;
  
  // Zmienne do obsługi dropdown menu
  isAdminDropdownOpen = false;
  isUserDropdownOpen = false;
  
  // Subject for managing subscriptions
  private destroy$ = new Subject<void>();
  
  constructor(
    private router: Router,
    private authService: AuthService
  ) {}
  
  ngOnInit(): void {
    this.authService.currentUser$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(user => {
      this.isAuthenticated = !!user;
      if (user) {
        this.username = user.username;
        this.userRole = user.role || null;
      } else {
        this.username = '';
        this.userRole = null;
      }
    });
  }
  
  ngOnDestroy(): void {
    // Complete the subject to unsubscribe from all subscriptions
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  logout(): void {
    this.authService.logout();
  }
  
  logoutWithEvent(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.logout();
    this.isUserDropdownOpen = false;
  }
  
  isAdmin(): boolean {
    return this.userRole === UserRole.ADMIN;
  }
  
  isModerator(): boolean {
    return this.userRole === UserRole.MODERATOR;
  }
  
  isRegularUser(): boolean {
    return this.isAuthenticated && this.userRole === UserRole.USER;
  }
  
  hasAdminAccess(): boolean {
    return this.isAdmin() || this.isModerator();
  }
  
  toggleMenu(): void {
    this.isMenuCollapsed = !this.isMenuCollapsed;
  }
  
  // Metody do obsługi dropdown menu
  toggleAdminDropdown(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.isAdminDropdownOpen = !this.isAdminDropdownOpen;
    this.isUserDropdownOpen = false;
  }
  
  toggleUserDropdown(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.isUserDropdownOpen = !this.isUserDropdownOpen;
    this.isAdminDropdownOpen = false;
  }
  
  // Zamyka wszystkie dropdown menu po kliknięciu poza nimi
  @HostListener('document:click', ['$event'])
  closeAllDropdowns(event: MouseEvent): void {
    // Nie zamykaj menu, jeśli kliknięcie było wewnątrz dropdown
    if ((event.target as HTMLElement).closest('.dropdown')) {
      return;
    }
    
    this.isAdminDropdownOpen = false;
    this.isUserDropdownOpen = false;
  }
  
  // Metody nawigacji dla menu administratora
  navigateToAdminAuctions(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.router.navigate(['/admin/auctions']);
    this.isAdminDropdownOpen = false;
  }
  
  navigateToAdminCategories(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.router.navigate(['/admin/categories']);
    this.isAdminDropdownOpen = false;
  }
  
  navigateToAdminUsers(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.router.navigate(['/admin/users']);
    this.isAdminDropdownOpen = false;
  }
  
  // Metody nawigacji dla menu użytkownika
  navigateToUserProfile(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.router.navigate(['/user/profile']);
    this.isUserDropdownOpen = false;
  }
  
  navigateToMyAuctions(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    // Use direct URL navigation to ensure correct path
    window.location.href = '/#/auctions/search/my';
    this.isUserDropdownOpen = false;
  }
  
  navigateToMyBids(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    // Use direct URL navigation to ensure correct path
    window.location.href = '/#/user/bids';
    this.isUserDropdownOpen = false;
  }
  
  // Metody zamykania dropdown menu
  closeAdminDropdown(): void {
    this.isAdminDropdownOpen = false;
  }
  
  closeUserDropdown(): void {
    this.isUserDropdownOpen = false;
  }
} 