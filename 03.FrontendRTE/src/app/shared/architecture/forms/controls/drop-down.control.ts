import { ValidatorFn } from '@angular/forms';
import { FormularControl } from './formular.control';

export type DropDownControlOption<T> = { option: string, value: T }

export class DropDownControl<T extends string = string> extends FormularControl<string> {

  public disabledOptions: T[] = [];

  public constructor(formState: string = 'null',
                     public options: DropDownControlOption<T>[] = [],
                     public defaultOption: string = '',
                     ...validator: ValidatorFn[]) {
    super(formState ? formState : 'null', ...validator);
    this.logger.addFlag('drop-down');
  }

  public handelChange(value: string | null): string | null {
    return value ? value : null;
  }

}
