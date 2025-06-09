import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { AuctionRequestsComponent } from './auction-requests.component';
import { AuctionRequestService } from '../../../services/auction-request.service';
import { ErrorService } from '../../../services/error.service';
import { AuthService } from '../../../services/auth.service';

describe('AuctionRequestsComponent', () => {
  let component: AuctionRequestsComponent;
  let fixture: ComponentFixture<AuctionRequestsComponent>;
  let auctionRequestServiceSpy: jasmine.SpyObj<AuctionRequestService>;
  let errorServiceSpy: jasmine.SpyObj<ErrorService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const auctionRequestSpy = jasmine.createSpyObj('AuctionRequestService', ['getRequests', 'approveRequest', 'rejectRequest']);
    const errorSpy = jasmine.createSpyObj('ErrorService', ['getErrorMessage']);
    const authSpy = jasmine.createSpyObj('AuthService', ['getCurrentUser']);
    
    auctionRequestSpy.getRequests.and.returnValue(of([]));
    authSpy.getCurrentUser.and.returnValue({ username: 'testuser' });

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        AuctionRequestsComponent
      ],
      providers: [
        { provide: AuctionRequestService, useValue: auctionRequestSpy },
        { provide: ErrorService, useValue: errorSpy },
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    auctionRequestServiceSpy = TestBed.inject(AuctionRequestService) as jasmine.SpyObj<AuctionRequestService>;
    errorServiceSpy = TestBed.inject(ErrorService) as jasmine.SpyObj<ErrorService>;
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    
    fixture = TestBed.createComponent(AuctionRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load requests on init', () => {
    expect(auctionRequestServiceSpy.getRequests).toHaveBeenCalled();
  });
}); 