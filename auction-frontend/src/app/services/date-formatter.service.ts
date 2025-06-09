import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DateFormatterService {
  
  /**
   * Formats a date to ISO format without timezone information
   * Example: 2025-06-05T09:00:00 instead of 2025-06-05T09:00:00.000Z
   */
  formatDateWithoutTimezone(date: Date): string {
    // Format: YYYY-MM-DDThh:mm:ss
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  }
} 