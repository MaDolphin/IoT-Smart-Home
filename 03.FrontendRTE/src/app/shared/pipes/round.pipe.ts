/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'round',
  pure: true,
})
export class RoundPipe implements PipeTransform {

  transform(value: number, exp: number = 2): number {
    if (typeof exp === 'undefined' || +exp === 0)
      return Math.round(value);

    // ensure that the passed value is a number
    let workingValue: any = +value;
    exp = +exp;

    // return NaN if not valid parameter are used
    if (isNaN(workingValue) || !(typeof exp === 'number' && exp % 1 === 0))
      return NaN;

    // Shift
    workingValue = workingValue.toString().split('e');
    workingValue = Math.round(+(workingValue[0] + 'e' + (workingValue[1] ? (+workingValue[1] + exp) : exp)));

    // Shift back
    workingValue = workingValue.toString().split('e');
    return +(workingValue[0] + 'e' + (workingValue[1] ? (+workingValue[1] - exp) : -exp));
  }

}
