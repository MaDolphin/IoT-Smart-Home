import { Pipe, PipeTransform } from '@angular/core';

interface Translation {
  source: string;
  target: string;
}

@Pipe({
  name: 'titleTranslation'
})
export class TitleTranslationPipe implements PipeTransform {

  private titleTranslations: Translation[] = [
    {source: 'overview', target: 'Übersicht'},
    {source: 'fakultaet', target: 'Fakultät'},
    {source: 'aendern', target: 'Ändern'},
    {source: 'begruendung', target: 'Begründung'}
  ];

  transform(value: string, args?: any): any {
    for (let translation of this.titleTranslations) {
      value = value.replace(translation.source, translation.target);
    }
    return value.charAt(0).toUpperCase() + value.slice(1);
  }

}
