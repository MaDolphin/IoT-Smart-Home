/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

/**
 * @deprecated
 */
@Pipe({name: 'moment'})
export class MomentPipe implements PipeTransform {

  transform(value: string | Date, format?: string): any {
    return moment(value).format(format);
  }

}
