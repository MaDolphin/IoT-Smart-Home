/* (c) https://github.com/MontiCore/monticore */

import { Component, Input, OnInit } from '@angular/core';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';

import { AbstractInputComponent } from '../abstract.input.component';


@Component({
  selector: 'macoco-text-input',
  templateUrl: './text-input.component.html',
  styleUrls: ['../input.scss']
})
export class TextInputComponent extends AbstractInputComponent<FormularControl<string>> implements OnInit {


  @Input() public maxLength: number = 250;

  @Input() public isPassword: boolean = false;

  ngOnInit() {
  }

}
