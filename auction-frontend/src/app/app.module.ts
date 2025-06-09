import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterModule } from '@angular/router';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Auth components
import { LoginComponent } from './components/auth/login/login.component';
import { RegisterComponent } from './components/auth/register/register.component';

// Auction components
import { AuctionListComponent } from './components/auctions/auction-list/auction-list.component';
import { AuctionDetailsComponent } from './components/auctions/auction-details/auction-details.component';
import { AuctionCreateComponent } from './components/auctions/auction-create/auction-create.component';

// Bid components
import { BidFormComponent } from './components/bids/bid-form/bid-form.component';
import { BidListComponent } from './components/bids/bid-list/bid-list.component';
import { MyBidsComponent } from './components/bids/my-bids/my-bids.component';

// User components
import { UserProfileComponent } from './components/user/user-profile/user-profile.component';

// Admin components
import { CategoryListComponent } from './components/admin/category-list/category-list.component';
import { UserListComponent } from './components/admin/user-list/user-list.component';
import { AuctionRequestsComponent } from './components/admin/auction-requests/auction-requests.component';

// Shared components
import { NavbarComponent } from './components/shared/navbar/navbar.component';

// Pipes
import { PlnCurrencyPipe } from './pipes/pln-currency.pipe';

@NgModule({
  declarations: [
    // Only include non-standalone components here
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    CommonModule,
    RouterModule,
    
    // Standalone components
    AppComponent,
    NavbarComponent,
    LoginComponent,
    RegisterComponent,
    AuctionListComponent,
    AuctionDetailsComponent,
    AuctionCreateComponent,
    BidFormComponent,
    BidListComponent,
    MyBidsComponent,
    UserProfileComponent,
    CategoryListComponent,
    UserListComponent,
    AuctionRequestsComponent,
    PlnCurrencyPipe
  ],
  providers: [
    DecimalPipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { } 