import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'enumKeys'})
export class EnumKeysPipe implements PipeTransform {

  transform(value, args: string[]): { key: string, value: string }[] {
    let keys: { key: string, value: string }[] = [];
    for (let enumKey in value) {
      if (value.hasOwnProperty(enumKey)) {
        let isValueProperty = parseInt(enumKey, 10) >= 0;
        if (isValueProperty) {
          keys.push({key: enumKey, value: value[enumKey]});
        }
      }
    }
    return keys;
  }

}