/* (c) https://github.com/MontiCore/monticore */

import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

@Pipe({
  name: 'toColorDiv',
  pure: true,
})
export class StatusToColorPipe implements PipeTransform {

  private logger: Logger = new Logger({ name: 'StringToColorPipe', flags: ['pipe'], muted: true });

  transform(value: any): string {
    switch (value) {
      case 'SAP': {
        return '<div style="background: #1667ae; width: 20px; height: 20px; border-radius: 5px"></div>';
      }
      case 'Fehlerhaft': {
        return '<div style="background: #ae163e; width: 20px; height: 20px; border-radius: 5px"></div>';
      }
      case 'Planung': {
        return '<div style="background: #ae4e16; width: 20px; height: 20px; border-radius: 5px"></div>';
      }
      case 'Eingereicht': {
        return '<div style="background: #3eae16; width: 20px; height: 20px; border-radius: 5px"></div>';
      }
      default: {
        return value
      }
    }
  }
}
