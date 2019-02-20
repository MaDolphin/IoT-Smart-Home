import { Pipe, PipeTransform } from '@angular/core';
import { Logger } from '@upe/logger';

@Pipe({
  name: 'toPspElement',
  pure: false,
})
export class ToPspElementPipe implements PipeTransform {

  private logger: Logger = new Logger({name: 'ToPspElementPipe', flags: ['pipe']});

  transform(value: string) {
    if (value && value.length !== 15) {
      this.logger.error('MAF0x00CA: passed value is not a pspElement', value);
      throw new Error(`MAF0x00CA: passed value is not a pspElement '${value}'`);
    }
    return value ? `${value.substr(0, 5)} ${value.substr(5, 6)} ${value.substr(11, 4)}` : '';
  }
}