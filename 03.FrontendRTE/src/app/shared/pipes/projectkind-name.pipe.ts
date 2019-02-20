/**
 * Transforms the existing project art number to the project art name
 * or else to the empty string
 */
import { Pipe, PipeTransform } from '@angular/core';
import { Projectkinds } from '../architecture/services/models/projectkinds';

@Pipe({name: 'projectkindName'})
export class ProjectkindNamePipe implements PipeTransform {

  constructor(public projectkindsService: Projectkinds) {
  }

  transform(value: string, isSummaryAccount?: boolean): string {
    let projectkind = this.projectkindsService.projectkind(value, isSummaryAccount);

    return !!projectkind ? value + '-' + projectkind.name :
      'Die Projektart ' + value + ' existiert nicht' + (isSummaryAccount ? ' für Sammelkonten' : '') +
        '. Bitte überprüfen Sie das PSP-Element.';
  }
}
