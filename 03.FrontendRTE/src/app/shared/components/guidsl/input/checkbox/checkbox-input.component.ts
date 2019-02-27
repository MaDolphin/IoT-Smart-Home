/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Component, Input, OnInit } from '@angular/core';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';
import { AbstractInputComponent } from '../abstract.input.component';

@Component({
  selector: 'macoco-checkbox-input',
  templateUrl: './checkbox-input.component.html',
  styleUrls: ['checkbox-input.component.scss', '../input.scss']

})
export class CheckBoxInputComponent extends AbstractInputComponent<FormularControl<boolean>> implements OnInit {

  ngOnInit() {

  }

  @Input()
  public useMatStyle: boolean = false;

  public checked: boolean = false;

  private onCheck() { }

  private onUncheck() { }

  public onClick(event) {
    if (event.target.checked) {
      this.onCheck();
    } else {
      this.onUncheck();
    }
  }

}
