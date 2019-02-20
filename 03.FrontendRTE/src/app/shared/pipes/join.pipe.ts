import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'joinWith',
  pure: false,
})
export class JoinPipe implements PipeTransform {
  public transform(value: any[], seperator: string = ' '): string {
    return value.join(seperator);
  }
}
