/**
 * Transforms the existing project art number to the project tax classification
 * or else to the empty string
 */
import { Pipe, PipeTransform } from '@angular/core';
import { Projectkinds } from '../architecture/services/models/projectkinds';
import { Projectkind, Selection } from './projectkind';

const SalesControllable_No = 'nicht umsatzsteuerbar';
const SalesControllable_Yes = 'umsatzsteuerpflichtig';
const GainsTax_No = 'nicht ertragsteuerpflichtig';
const GainsTax_Yes = 'ertragsteuerpflichtig';
const DeductionOption_Yes = 'Vorsteuerabzugsmöglichkeit';

@Pipe({name: 'projectkindClassification'})
export class ProjectkindClassificationPipe implements PipeTransform {

  constructor(public projectkindsService: Projectkinds) {
  }

  transform(value: string): string {
    return this.classification(this.projectkindsService.projectkind(value));
  }

  private classification(value: Projectkind | undefined): string {
    if (!value) {
      return '';
    }
    let projectkind: Projectkind = value;
    let classifying: string = '';
    let del = ', ';

    switch (projectkind.salesControllable) {
      case Selection.Yes:
        classifying += SalesControllable_Yes;
        break;
      case Selection.No:
        classifying += SalesControllable_No;
        break;
      case Selection.NotSpecified:
        del = '';
    }

    switch (projectkind.gainsTax) {
      case Selection.Yes:
        classifying += del + GainsTax_Yes;
        del = ', ';
        break;
      case Selection.No:
        classifying += del + GainsTax_No;
        del = ', ';
        break;
    }

    switch (projectkind.deductionOption) {
      case Selection.Yes:
        classifying += del + DeductionOption_Yes;
        break;
    }

    return classifying;
  }

}
