/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';
import { IModel, Model } from '../architecture/data/models/model';
import { DateToStringPipe } from './date-to-string.pipe';
import { NORMAL_DATE_REGEX, StringToDatePipe } from './string-to-date.pipe';
// since we initialize the bookingFormProxys in ngOnInit function, we have to set the filter pipe to impure
// so that it will be called after new element pushed into the array.
@Pipe({
  name: 'sgSearchFilter',
  pure: false
})
export class SearchFilter implements PipeTransform {

  private logger: Logger = new Logger({name: 'SearchFilter', flags: ['pipe']});

  constructor(private dateToString: DateToStringPipe, private stringToDate: StringToDatePipe) {
  }

  transform<M extends IModel>(models: Array<Model<M>> | null, searchValue: string): Array<Model<M>> {

    if (models && searchValue) {

      return models.filter((model: Model<any>) => {
        // region transform model to array
        const json = model.serialize();
        const array = Object
          .keys(json)
          .map((key) => json[key]);
        // endregion
        // region find matches
        return array.findIndex((attr: any) => {
            // region check if seachValue and attr are number
            if (!isNaN(Number(searchValue)) && !isNaN(Number(attr))) {
              return Number(searchValue) === Number(attr);
            }
            // endregion
            // region check if serachValue is a date
            if (searchValue.match(NORMAL_DATE_REGEX)) {
              return attr.toString() === this.dateToString.transform(this.stringToDate.transform(searchValue), 'ISO');
            }
            // endregion
            if (isNaN(attr)) {
              return attr.toString().indexOf(searchValue) !== -1;
            }
            return false;
          }) !== -1;
        // endregion
      });

    } else {
      return models ? models : [];
    }

  }
}
