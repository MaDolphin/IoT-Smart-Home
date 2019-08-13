/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'clone',
  pure: false,
})
export class ClonePipe implements PipeTransform {
  transform<T>(obj: T): T {
    return JSON.parse(JSON.stringify(obj));
  }
}
