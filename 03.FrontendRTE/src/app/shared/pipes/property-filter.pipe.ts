import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'sgPropertyFilter',
  pure: false
})
// Properties and their expected values have to be defined in form of an object. {'name':'Gesamt', 'budget':'200'}
// Equality between the properties and their values might set via the equality parameter. Default is true.
export class PropertyFilterPipe implements PipeTransform {
  transform(items: any[], properties?: any, equals?: boolean): any {

    return items.filter(item => {
      if (properties) {
        let propertyNames = Object.getOwnPropertyNames(properties);
        return propertyNames.filter(key => {
            let result = false;
            if (item.hasOwnProperty(key)) {
              result = item[key] === properties[key];
            } else {
              return false;
            }

            if (!(equals == null) && equals === false) {
              result = !result;
            }

            return result;
          }).length === propertyNames.length;
      }
    });
  }
}