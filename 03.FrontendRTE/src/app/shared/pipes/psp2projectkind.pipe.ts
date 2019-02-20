/**
 * Transforms the valid PSP element to the existing project art or else to the empty string
 */
import { Pipe, PipeTransform } from '@angular/core';
import { Projectkinds } from '../architecture/services/models/projectkinds';

@Pipe({name: 'projectkind'})
export class PspToProjectkindPipe implements PipeTransform {

  constructor(public projectkindsService: Projectkinds) {
  }

  transform(pspElement: string): string {
    let value: string = '';
    if (!this.isPspElementValid(pspElement)) {
      console.log('PSP element is not valid!: ' + pspElement);
      return value;
    }

    if (new RegExp('^[0-9]{2}-[0-9]-[0-9]{2}-[0-9]{6}-[0-9]{4}$').test(pspElement)) {
      value = pspElement.substr(5, 2);
    } else if (new RegExp('^[0-9]{15}').test(pspElement)) {
      value = pspElement.substr(3, 2);
    }
    return value;
  }

  isPspElementValid(value: string): boolean {
    return !!value && (new RegExp('^[0-9]{2}-[0-9]-[0-9]{2}-[0-9]{6}-[0-9]{4}$').test(value) ||
      new RegExp('^[0-9]{15}').test(value));
  }

}
