/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';
import { RoundPipe } from './round.pipe';
import { SumPipe } from './sum.pipe';

@Pipe({
  name: 'pieChartValues',
  pure: true,
})
export class PieChartPriorityPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'PieChartPriorityPipe', flags: ['pipe']});

  constructor(private round: RoundPipe, private sum: SumPipe) {
  }

  transform(values: number[], total: number): number[] {

    let returned: number[] = [0, 0, 0];
    if (values === null || values.length === 0 || total === null || total === 0) {
      return returned;
    }

    // Cut at anything > 100%
    returned = [];
    for (let i = 0; i < values.length; ++i) {
      let percentage = this.round.transform(values[i] / total * 100);
      let returnedTotal;
      returnedTotal  = this.sum.transform(returned);

      if (percentage + returnedTotal >= 100) {
        returned.push(100 - returnedTotal);
        return returned;
      }
      returned.push(percentage);
    }

    // If sum is < 100% last value is readjusted
    let last = returned.length - 1;
    if (this.sum.transform(returned) !== 100) {
      returned[last] = this.round.transform(100 - this.sum.transform(returned.slice(0, last)));
    }
    return returned;
  }

}
