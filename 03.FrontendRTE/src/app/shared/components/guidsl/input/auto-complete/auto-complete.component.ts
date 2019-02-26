import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

import { AutoCompleteControl } from '@shared/architecture/forms/controls/auto-complete.control';
import { AutoCompleteFromArrayPipe } from '@shared/pipes/auto-complete-from-array.pipe';
import { AbstractInputComponent } from '../abstract.input.component';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Component({
  selector: 'macoco-auto-complete',
  templateUrl: './auto-complete.component.html',
  styleUrls: ['../input.scss']
})
export class AutoCompleteInputComponent extends AbstractInputComponent<AutoCompleteControl> implements OnInit, OnChanges {

  filteredOptions: Observable<string[]>;

  ngOnInit() {
    this.filteredOptions = this.modelFormControl.valueChanges.pipe(
      startWith<string>(''),
      map(v => v ? this._filter(v) : this.options.slice())
    );
  }

  ngOnChanges(simpleChange: SimpleChanges) {
    let keys: string[] = Object.keys(simpleChange);
    // Check if only the modelFormControl is changed
    if (keys.length === 1 && keys[0] === 'modelFormControl') {
      // Update the filtered Options
      this.filteredOptions = this.modelFormControl.valueChanges.pipe(
          startWith<string>(''),
          map(v => v ? this._filter(v) : this.options.slice())
      );
    }
  }

  private _filter(name: string): string[] {
    const filterValue = name.toLowerCase();

    return this.options.filter(o => o.option.toLowerCase().indexOf(filterValue) === 0);
  }

  private _patchValue: boolean = true;
  private _noPaddingOrMargin: boolean = false;

  public get allowNewInput(): boolean {
    return this.modelFormControl.allowNewInput;
  }

  @Input()
  public set allowNewInput(value: boolean) {
    this.modelFormControl.allowNewInput = value;
  }

  @Input()
  public set filterByInput(value: boolean) {
    this.modelFormControl.filterByInput = value;
  }

  @Input()
  public set patchValue(value: boolean) {
    this._patchValue = value;
  }

  @Input()
  public set noPaddingOrMargin(noPaddingOrMargin: boolean) {
    this._noPaddingOrMargin = noPaddingOrMargin;
  }

  public get noPaddingOrMargin() {
    return this._noPaddingOrMargin;
  }

  // ISelectOptions
  public get options(): any[] {
    return this.modelFormControl.options ? this.modelFormControl.options : [];
  }

  constructor(private _autoCompleteFromArray: AutoCompleteFromArrayPipe) {
    super();

    this._patchValue = true;
  }

  /**
   * Try to complete the input value with the passed array of string
   * Called after the input lost focus
   * @param list {string[]}
   */
  autoCompleteSelection(): void {
    if (this._patchValue) {
      // this.modelFormControl.patchValue(this._autoCompleteFromArray.transform(this.value, this.options));
    }
  }

  /**
   * Remove the last entered char if then the input value don't match with any item
   * in the passed string array
   * Called after keyup event
   * @param list {string[]}
   */
  /*
  preventWrongInput() {
    this.modelFormControl.updateValueAndValidity();
    if (!this.allowNewInput && this.value && !this._autoCompleteFromArray.transform(this.value, this.options)) {
      this.modelFormControl.patchValue(this.value.slice(0, -1));
    }
  }
  /** */

}
