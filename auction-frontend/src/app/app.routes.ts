import { Routes } from '@angular/router';

// Auth components
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';

// Auction components
import { AuctionListComponent } from './components/auctions/auction-list/auction-list.component';
import { AuctionDetailsComponent } from './components/auctions/auction-details/auction-details.component';
import { AuctionCreateComponent } from './components/auctions/auction-create/auction-create.component';

// Bid components
import { MyBidsComponent } from './components/bids/my-bids/my-bids.component';

// User components
import { UserProfileComponent } from './components/user/user-profile/user-profile.component';

// Admin components
import { CategoryListComponent } from './components/admin/category-list/category-list.component';
import { UserListComponent } from './components/admin/user-list/user-list.component';
import { AuctionRequestsComponent } from './components/admin/auction-requests/auction-requests.component';
import { AuctionRequestDetailsComponent } from './components/admin/auction-requests/auction-request-details.component';

// User roles
import { UserRole } from './models/user.model';
import { inject } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';

// Auth guard function
const authGuard = (requiredRoles?: UserRole[]) => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    
    if (!authService.isAuthenticated()) {
      console.error('Auth guard: User not authenticated');
      return router.parseUrl('/login');
    }
    
    if (requiredRoles && requiredRoles.length > 0) {
      const user = authService.getCurrentUser();
      console.log('Auth guard: Checking roles', { 
        user: user?.username, 
        userRole: user?.role, 
        requiredRoles 
      });
      
      if (!user || !requiredRoles.includes(user.role as UserRole)) {
        console.error('Auth guard: User does not have required role');
        return router.parseUrl('/auctions');
      }
    }
    
    console.log('Auth guard: Access granted');
    return true;
  };
};

// Funkcja pomocnicza do logowania informacji o trasie
const logRoute = (route: string) => {
  console.log(`Route accessed: ${route}`);
  return true;
};

export const routes: Routes = [
  // Public routes
  { path: '', redirectTo: '/auctions', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'auctions', component: AuctionListComponent },
  
  // Protected routes - specific routes must come before parameterized routes
  { 
    path: 'auctions/create', 
    component: AuctionCreateComponent,
    canActivate: [() => authGuard([UserRole.USER])()]
  },
  
  // User specific routes - specific routes must come before parameterized routes
  { 
    path: 'auctions/search/my', 
    component: AuctionListComponent, 
    canActivate: [() => authGuard([UserRole.USER, UserRole.MODERATOR])()],
    data: { view: 'my' }
  },
  
  // Parameterized route must come after specific routes
  { path: 'auctions/:id', component: AuctionDetailsComponent },
  
  { 
    path: 'user/profile', 
    component: UserProfileComponent,
    canActivate: [() => authGuard()()]
  },
  { 
    path: 'user/bids', 
    component: MyBidsComponent,
    canActivate: [() => authGuard([UserRole.USER, UserRole.MODERATOR])()]
  },
  
  // Admin routes - only for ADMIN role
  { 
    path: 'admin/auctions', 
    component: AuctionListComponent, 
    canActivate: [() => authGuard([UserRole.ADMIN])()],
    data: { view: 'admin' }
  },
  { 
    path: 'admin/categories', 
    component: CategoryListComponent, 
    canActivate: [() => authGuard([UserRole.ADMIN])()]
  },
  { 
    path: 'admin/users', 
    component: UserListComponent, 
    canActivate: [() => authGuard([UserRole.ADMIN])()]
  },
  { 
    path: 'admin/auction-requests', 
    component: AuctionRequestsComponent, 
    canActivate: [() => authGuard([UserRole.ADMIN, UserRole.MODERATOR])()]
  },
  { 
    path: 'admin/auction-requests/:id', 
    component: AuctionRequestDetailsComponent, 
    canActivate: [() => authGuard([UserRole.ADMIN, UserRole.MODERATOR])()]
  },
  
  // Moderator routes
  { 
    path: 'moderator/requests', 
    component: AuctionRequestsComponent, 
    canActivate: [() => authGuard([UserRole.MODERATOR])()]
  },
  { 
    path: 'moderator/requests/:id', 
    component: AuctionRequestDetailsComponent, 
    canActivate: [() => authGuard([UserRole.MODERATOR])()]
  },
  
  // Fallback route
  { path: '**', redirectTo: '/auctions' }
];
