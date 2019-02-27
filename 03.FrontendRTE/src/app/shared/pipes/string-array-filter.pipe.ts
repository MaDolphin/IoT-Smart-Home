/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'sgStringArrayFilter',
  pure: false
})
export class StringArrayFilter implements PipeTransform {
  transform(items: string[], filterArray: string[]): string[] {
    return items.filter(item => !filterArray.find(filteredItem => filteredItem === item));
  };
}
