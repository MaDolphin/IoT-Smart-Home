import { Component } from '@angular/core';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';
import { AbstractInputComponent } from '../abstract.input.component';


@Component({
  selector: 'app-textarea-input',
  templateUrl: './textarea-input.component.html',
  styleUrls: ['../input.scss']
})
export class TextareaInputComponent extends AbstractInputComponent<FormularControl<string>> {

}
