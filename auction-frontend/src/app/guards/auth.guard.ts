import { Injectable } from '@angular/core';
import { 
  CanActivate, 
  ActivatedRouteSnapshot, 
  RouterStateSnapshot, 
  Router 
} from '@angular/router';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | boolean {
    if (this.authService.isAuthenticated()) {
      // Check if route has data.roles specified
      if (route.data['roles']) {
        // Get the current user
        const user = this.authService.getCurrentUser();
        
        // Check if user has required role
        if (user && route.data['roles'].includes(user.role)) {
          return true;
        } else {
          // User doesn't have required role, redirect to home
          this.router.navigate(['/']);
          return false;
        }
      }
      
      // No specific roles required, just authentication
      return true;
    }
    
    // Not authenticated, redirect to login
    this.router.navigate(['/login'], { 
      queryParams: { returnUrl: state.url } 
    });
    return false;
  }
} 