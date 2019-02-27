/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'autoCompleteFromArray',
  pure: false,
})
export class AutoCompleteFromArrayPipe implements PipeTransform {
  transform(value: string, array: string[]): string {
    if (value) {
      const fullMatch = array.find(item => item.toLowerCase() === value.toLowerCase());
      if (fullMatch) {
        return fullMatch;
      }
      const completion = array.find(item => new RegExp(`^${value}`, 'gi').test(item));
      return completion ? completion : value;
    }
    return '';
  }
}
