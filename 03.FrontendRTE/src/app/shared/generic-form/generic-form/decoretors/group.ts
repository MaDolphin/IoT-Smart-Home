/* (c) https://github.com/MontiCore/monticore */

import 'reflect-metadata';
import 'rxjs/add/operator/skipWhile';
import { GenericFormGroup } from '../generic-form-group';
import { init } from './group/init';
import { countMultiSubGroup, createMultiSubGroup, removeMultiSubGroup } from './group/multi-sub-group';
import { afterSubmit, beforeSubmit, onSubmit } from './group/onSubmit';
import { getControlsByNames } from './group/utils';
import { afterValidate, beforeValidate, onValidate } from './group/validate';
import { ApplyAddIf, ApplyRemoveIf } from './if';


export function Group<T extends GenericFormGroup<any>>() {
  return function (target: new (...args: any[]) => T | any) {

    target.prototype.init = init;

    target.prototype.addIf = ApplyAddIf;

    target.prototype.removeIf = ApplyRemoveIf;

    target.prototype.getControlsByNames = getControlsByNames;

    target.prototype.beforeSubmit = beforeSubmit;

    target.prototype.submit = onSubmit;

    target.prototype.afterSubmit = afterSubmit;

    target.prototype.beforeValidate = beforeValidate;

    target.prototype.validate = onValidate;

    target.prototype.afterValidate = afterValidate;

    target.prototype.createMultiSubGroup = createMultiSubGroup;

    target.prototype.removeMultiSubGroup = removeMultiSubGroup;

    target.prototype.countMultiSubGroup = countMultiSubGroup;

    return target;

  };
}
