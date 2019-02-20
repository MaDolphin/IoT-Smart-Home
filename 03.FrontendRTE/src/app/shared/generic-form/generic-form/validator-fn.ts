import { GenericFormControl } from './generic-form-control';

export type ValidatorFn = (this: GenericFormControl<any>, ...depControls: Array<GenericFormControl<any>>) => void;