/* (c) https://github.com/MontiCore/monticore */

import { GenericFormGroup } from './generic-form-group';

export class GenericFormError extends Error {
  public constructor(message: string, public group: GenericFormGroup<any>) {
    super(message);
    this.name = 'GenericFormError';
  }
}

export class GenericFormDecoratorError extends Error {
  public constructor(message: string, decoratorName: string, targetName: string, propertyKey: string = '') {
    super(`@${decoratorName} on ${targetName}.${propertyKey}: ${message}`);
    this.name = 'GenericFormDecoratorError';
  }
}
