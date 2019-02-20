import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'removeNulls',
  pure: false,
})
export class RemoveNullsPipe implements PipeTransform {
  transform(obj: any) {

    for (const key in obj) {
      if (obj[key] !== null) {
        if (Array.isArray(obj[key])) {
          obj[key] = obj[key].filter(item => item !== null);
          for (const arrayItem of obj[key]) {
            this.transform(arrayItem);
          }
        } else if (typeof obj[key] === 'object') {
          this.transform(obj[key]);
        }
      } else {
        delete obj[key];
      }
    }

    return obj;
  }
}
