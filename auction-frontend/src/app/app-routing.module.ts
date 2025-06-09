import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

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

// Guards
import { AuthGuard } from './guards/auth.guard';

// User roles
import { UserRole } from './models/user.model';

const routes: Routes = [
  // Public routes
  { path: '', redirectTo: '/auctions', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'auctions', component: AuctionListComponent },
  
  // More specific routes must come before parameterized routes
  { 
    path: 'auctions/create', 
    component: AuctionCreateComponent,
    canActivate: [AuthGuard],
    data: { 
      roles: [UserRole.USER]
    }
  },
  { path: 'auctions/search/my', component: AuctionListComponent, canActivate: [AuthGuard], data: { view: 'my' } },
  
  // Parameterized route comes last
  { path: 'auctions/:id', component: AuctionDetailsComponent },
  
  // Protected routes
  { 
    path: 'user/profile', 
    component: UserProfileComponent,
    canActivate: [AuthGuard]
  },
  { 
    path: 'user/bids', 
    component: MyBidsComponent,
    canActivate: [AuthGuard]
  },
  
  // Admin routes
  { 
    path: 'admin/auctions', 
    component: AuctionListComponent, 
    canActivate: [AuthGuard],
    data: { 
      roles: [UserRole.ADMIN],
      view: 'admin' 
    }
  },
  { 
    path: 'admin/categories', 
    component: CategoryListComponent, 
    canActivate: [AuthGuard],
    data: { 
      roles: [UserRole.ADMIN]
    }
  },
  
  // Fallback route
  { path: '**', redirectTo: '/auctions' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { } 