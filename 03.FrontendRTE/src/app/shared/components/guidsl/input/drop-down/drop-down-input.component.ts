/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Component, OnInit } from '@angular/core';
import { DropDownControl, DropDownControlOption } from '@shared/architecture/forms/controls/drop-down.control';

import { AbstractInputComponent } from '../abstract.input.component';

@Component({
  selector: 'macoco-drop-down-input',
  templateUrl: './drop-down-input.component.html',
  styleUrls: ['../input.scss']
})
export class DropDownInputComponent extends AbstractInputComponent<DropDownControl<any>> implements OnInit {

  private _options: string[] = [];


  constructor() {
    super();
  }

  public set json(options: DropDownControlOption<string>[]) {
    for (let entry of options) {
      console.log(entry);
    }
    this.modelFormControl.options = options;
  }

  public get options(): DropDownControlOption<any>[] {
    return this.modelFormControl.options;
  }

  public get defaultOption(): any {
    return this.modelFormControl.defaultOption;
  }

  // TODO: revisit if ever needed (currently it is not)
  public isDisabledOption(optionValue: any): boolean {
    return false
    // return this.modelFormControl.disabledOptions.indexOf(optionValue) !== -1;
  }

  public ngOnInit() {}

}
