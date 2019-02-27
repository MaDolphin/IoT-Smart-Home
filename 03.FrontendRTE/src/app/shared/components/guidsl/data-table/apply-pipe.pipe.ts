/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'applyPipe',
  pure: true,
})
export class ApplyPipePipe implements PipeTransform {

  transform(value: any, pipe: PipeTransform): any {
    return pipe.transform(value);
  }

}
