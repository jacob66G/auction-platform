import { Pipe, PipeTransform, NgModule } from '@angular/core';
import { DecimalPipe } from '@angular/common';

@Pipe({
  name: 'plnCurrency',
  standalone: true
})
export class PlnCurrencyPipe implements PipeTransform {
  constructor(private decimalPipe: DecimalPipe) {}

  transform(value: number | string | null | undefined, digitsInfo: string = '1.2-2'): string {
    if (value === null || value === undefined) {
      return '';
    }
    
    const formattedValue = this.decimalPipe.transform(value, digitsInfo);
    return formattedValue ? `${formattedValue} PLN` : '';
  }
}

// Create a module to provide DecimalPipe for standalone components
@NgModule({
  providers: [DecimalPipe]
})
export class PipeProvidersModule {} 