import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from './services/auth.service';
import { UserRole } from './models/user.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  isLoggedIn = false;
  username: string | undefined;
  userRole: UserRole | undefined;
  
  constructor(private authService: AuthService) {}
  
  ngOnInit(): void {
    // Subscribe to the currentUser$ observable from AuthService
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
      
      if (user) {
        this.username = user.username;
        this.userRole = user.role as UserRole;
      } else {
        this.username = undefined;
        this.userRole = undefined;
      }
    });
  }
  
  logout(): void {
    this.authService.logout();
  }
  
  isAdmin(): boolean {
    return this.userRole === UserRole.ADMIN;
  }
  
  isModerator(): boolean {
    return this.userRole === UserRole.MODERATOR;
  }
  
  isUser(): boolean {
    return this.userRole === UserRole.USER;
  }
}
