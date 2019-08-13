/* (c) https://github.com/MontiCore/monticore */

import { Input, OnInit } from '@angular/core';
import { Logger } from '@upe/logger';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';

// import {MatInput} from '@angular/material';

export abstract class AbstractInputComponent<F extends FormularControl<any>> implements OnInit {
  private _name: string;

  get name(): string {
    return this.modelFormControl.name || this._name;
  }

  @Input('inputName') set name(value: string) {
    this._name = value;
  }

  public get placeholder(): string {
    return this.modelFormControl.placeholder || this._placeholder;
   // return this._placeholder;
  }

  // @ViewChild(MatInput) public matInput: MatInput;

  @Input() public modelFormControl: F;

  public get label(): string {
    const label = this.modelFormControl ? (this._label || this.modelFormControl.label || '') : '';
    return label ? label + (this.required ? ' *' : '') : '';
  }

  @Input()
  public set placeholder(value: string) {
    this._placeholder = value;
  }

  private _placeholder: string = '';

  @Input() public inline: boolean = false;

  // use http://fontawesome.io/icons/
  @Input() public icon: string | null = null;

  public get required(): boolean {
    return this.modelFormControl.isRequired || this._required || this.modelFormControl.required;
  }

  public get valid(): boolean {
    return this.modelFormControl.valid;
  }

  public get invalid(): boolean {
    return this.modelFormControl.invalid;
  }

  public get value(): any {
    return this.modelFormControl.value;
  }

  public get dirty(): boolean {
    return this.modelFormControl.dirty
  }

  public get pristine(): boolean {
    return this.modelFormControl.pristine;
  }

  public get touched(): boolean {
    return this.modelFormControl.touched;
  }

  public get untouched(): boolean {
    return this.modelFormControl.untouched;
  }

  public get errors(): any {
    return this.modelFormControl.errors
  }

  public get hasError(): boolean {
    return this.errors ? !!this.errors.error : false;
  }

  private _readonly: boolean = false;

  get readonly(): boolean {
    return this.modelFormControl.isReadonly || this._readonly;
  }

  public get htmlId(): string {
    return `${this.name}-form-group`;
  }

  public get labelAbove(): boolean {
    return this._labelAbove;
  }

  @Input()
  public set disabled(value: boolean) {
    if (value) {
      this.modelFormControl.disable();
    } else {
      this.modelFormControl.enable();
    }
  }

  public get disabled(): boolean {
    return this.modelFormControl.disabled;
  }

  @Input('required')
  public set required(value: boolean) {
    this.modelFormControl.required = value;
    this._required = value;
  };

  protected logger: Logger = new Logger({
    name: this['constructor']['name'],
    flags: ['component', 'input']
  });

  // tslint:disable:no-input-rename
  @Input('label') private _label: string;

  @Input('labelAbove') private _labelAbove: boolean = false;

  @Input('fromGroupStyle') public fromGroupStyle: { [key: string]: string } = {};

  @Input() set readonly(value: boolean) {
    this._readonly = value;
  }

  private _required: boolean = false;

  public ngOnInit(): void {

    if (!this.modelFormControl) {
      this.logger.error('MAF0x0090: modelFormControl is not set');
      throw new Error('MAF0x0090: modelFormControl is not set');
    }
    if (!this.name) {
      this.logger.error('MAF0x0091: name is not set');
      throw new Error('MAF0x0091: name is not set');
    }
    // TODO : wait for update to angular 4.4 and material beta.12
    /*if (this.matInput) {
      console.log('found');
      const ref = this.modelFormControl.valueChanges
        .skipWhile(v => !v)
        .subscribe(() => {
          this.matInput.focus();
          ref.unsubscribe();
        });
    }*/
  }

}
