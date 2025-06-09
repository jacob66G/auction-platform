import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AdminUserService } from '../../../services/admin-user.service';
import { ErrorService } from '../../../services/error.service';
import { UserResponse, UserRole } from '../../../models/user.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ]
})
export class UserListComponent implements OnInit, OnDestroy {
  users: UserResponse[] = [];
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  searchForm: FormGroup;
  
  // For role filter dropdown - use string values that match backend expectations
  availableRoles = [
    { value: 'user', label: 'Użytkownicy' },
    { value: 'moderator', label: 'Moderatorzy' },
    { value: 'admin', label: 'Administratorzy' }
  ];
  
  // For confirmation modal
  showDeleteModal = false;
  userToDelete: UserResponse | null = null;
  
  // For unsubscribing from observables
  private destroy$ = new Subject<void>();
  
  constructor(
    private adminUserService: AdminUserService,
    private errorService: ErrorService,
    private formBuilder: FormBuilder
  ) {
    this.searchForm = this.formBuilder.group({
      searchTerm: [''],
      role: ['user'] // Default to 'user' role
    });
  }
  
  ngOnInit(): void {
    this.loadUsers();
    
    // Subscribe to form value changes
    this.searchForm.get('role')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(role => {
        console.log('Role changed to:', role);
        this.loadUsers();
      });
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  loadUsers(): void {
    this.isLoading = true;
    this.users = [];
    this.errorMessage = '';
    
    const role = this.searchForm.get('role')?.value;
    console.log(`Loading users with role: ${role}`);
    
    this.adminUserService.getUsersByRole(role)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (users) => {
          console.log(`Received ${users.length} users with role: ${role}`);
          this.users = users;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = this.errorService.getErrorMessage(error);
          this.isLoading = false;
        }
      });
  }
  
  searchUsers(): void {
    const searchTerm = this.searchForm.get('searchTerm')?.value?.trim();
    
    if (!searchTerm) {
      this.loadUsers();
      return;
    }
    
    this.isLoading = true;
    this.users = [];
    this.errorMessage = '';
    
    this.adminUserService.searchUsers(searchTerm)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (users) => {
          // If a role is selected, filter the search results by role as well
          const selectedRole = this.searchForm.get('role')?.value;
          if (selectedRole) {
            // Convert enum to string for comparison
            const roleString = this.getRoleStringFromEnum(selectedRole);
            this.users = users.filter(user => this.getRoleStringFromEnum(user.role) === roleString);
          } else {
            this.users = users;
          }
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = this.errorService.getErrorMessage(error);
          this.isLoading = false;
        }
      });
  }
  
  onReset(): void {
    this.searchForm.patchValue({
      searchTerm: '',
      role: 'user'
    });
    this.loadUsers();
  }
  
  assignModeratorRole(user: UserResponse): void {
    if (!user.id) return;
    
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.adminUserService.assignModeratorRole(user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedUser) => {
          this.successMessage = `Użytkownik ${updatedUser.username} otrzymał rolę moderatora.`;
          
          // If we're viewing users, remove this user from the list since they're now a moderator
          if (this.searchForm.get('role')?.value === 'user') {
            this.users = this.users.filter(u => u.id !== updatedUser.id);
          } else {
            // Otherwise, update the user in the list
            const index = this.users.findIndex(u => u.id === updatedUser.id);
            if (index !== -1) {
              this.users[index] = updatedUser;
            }
          }
          
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = this.errorService.getErrorMessage(error);
          this.isLoading = false;
        }
      });
  }
  
  revokeModeratorRole(user: UserResponse): void {
    if (!user.id) return;
    
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.adminUserService.revokeModeratorRole(user.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (updatedUser) => {
          this.successMessage = `Użytkownikowi ${updatedUser.username} odebrano rolę moderatora.`;
          
          // If we're viewing moderators, remove this user from the list since they're now a regular user
          if (this.searchForm.get('role')?.value === 'moderator') {
            this.users = this.users.filter(u => u.id !== updatedUser.id);
          } else {
            // Otherwise, update the user in the list
            const index = this.users.findIndex(u => u.id === updatedUser.id);
            if (index !== -1) {
              this.users[index] = updatedUser;
            }
          }
          
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = this.errorService.getErrorMessage(error);
          this.isLoading = false;
        }
      });
  }
  
  openDeleteModal(user: UserResponse): void {
    this.userToDelete = user;
    this.showDeleteModal = true;
  }
  
  closeDeleteModal(): void {
    this.userToDelete = null;
    this.showDeleteModal = false;
  }
  
  confirmDeleteUser(): void {
    if (!this.userToDelete || !this.userToDelete.id) {
      this.closeDeleteModal();
      return;
    }
    
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    const userId = this.userToDelete.id;
    const username = this.userToDelete.username;
    
    this.adminUserService.deleteUser(userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = `Użytkownik ${username} został usunięty.`;
          // Remove user from the list
          this.users = this.users.filter(u => u.id !== userId);
          this.closeDeleteModal();
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = this.errorService.getErrorMessage(error);
          this.closeDeleteModal();
          this.isLoading = false;
        }
      });
  }
  
  getRoleStringFromEnum(role: UserRole): string {
    switch (role) {
      case UserRole.ADMIN:
        return 'admin';
      case UserRole.MODERATOR:
        return 'moderator';
      case UserRole.USER:
      default:
        return 'user';
    }
  }
  
  getRoleBadgeClass(role: UserRole): string {
    switch (role) {
      case UserRole.ADMIN:
        return 'bg-danger';
      case UserRole.MODERATOR:
        return 'bg-success';
      default:
        return 'bg-secondary';
    }
  }
  
  getRoleDisplayName(role: UserRole): string {
    switch (role) {
      case UserRole.ADMIN:
        return 'Administrator';
      case UserRole.MODERATOR:
        return 'Moderator';
      default:
        return 'Użytkownik';
    }
  }
  
  canAssignModeratorRole(user: UserResponse): boolean {
    return user.role === UserRole.USER;
  }
  
  canRevokeModeratorRole(user: UserResponse): boolean {
    return user.role === UserRole.MODERATOR;
  }
  
  canDeleteUser(user: UserResponse): boolean {
    return user.role !== UserRole.ADMIN;
  }
} 