/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { ValidatorFn } from '@angular/forms';
import { IModel } from '@shared/architecture/data';
import { DropDownControl, DropDownControlOption } from './drop-down.control';

export interface INamed {
  name: string;
}

export class ModelDropDownControl<M extends IModel & INamed> extends DropDownControl<string> {

  public constructor(private model: M | null, options: DropDownControlOption<string>[], ...validator: ValidatorFn[]) {
    super(model ? model.name : 'null', [], '', ...validator);
    this.options = options;
    this.logger.addFlag('model-drop-down');
    for (let i = 0; i < this.options.length; i++) {
      if (this.value === this.options[i].option) {
        this.setValue(this.options[i].value);
        break;
      }
    }
  }

  public handelChange(value: string | null): string | null {
    return value ? value : null;
  }

}
