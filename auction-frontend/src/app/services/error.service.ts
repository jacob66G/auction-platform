import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../models/response.model';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {
  
  /**
   * Wyodrębnia komunikat błędu z odpowiedzi HTTP
   * @param error Błąd HTTP
   * @returns Komunikat błędu
   */
  getErrorMessage(error: HttpErrorResponse): string {
    if (error.error instanceof ErrorEvent) {
      // Błąd po stronie klienta
      return `Błąd: ${error.error.message}`;
    }
    
    // Błąd z backendu
    if (error.error && typeof error.error === 'object') {
      const errorResponse = error.error as Partial<ErrorResponse>;
      
      if (errorResponse.message) {
        return errorResponse.message;
      }
      
      if (errorResponse.details) {
        return errorResponse.details;
      }
    }
    
    // Domyślny komunikat błędu
    return `Wystąpił błąd: ${error.status} ${error.statusText || ''}`;
  }
  
  /**
   * Sprawdza, czy błąd jest określonego typu
   * @param error Błąd HTTP
   * @param statusCode Kod statusu HTTP
   * @returns Czy błąd jest określonego typu
   */
  isErrorType(error: HttpErrorResponse, statusCode: number): boolean {
    return error.status === statusCode;
  }
} 