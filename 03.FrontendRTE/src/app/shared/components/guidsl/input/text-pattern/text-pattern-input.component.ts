/* (c) https://github.com/MontiCore/monticore */

import { Component, Input, OnInit } from '@angular/core';
import { TextInputComponent } from '../text/text-input.component';

@Component({
  selector: 'macoco-text-pattern-input',
  templateUrl: './text-pattern-input.component.html',
  styleUrls: ['../input.scss']
})
export class TextPatternInputComponent extends TextInputComponent implements OnInit {

  @Input() public pattern: string;

  public ngOnInit(): void {
    super.ngOnInit();
    if (!this.pattern) {
      this.logger.error('MAF0x00C1: text mask is not set');
      throw new Error('MAF0x00C1: text pattern is not set');
    }
  }

}
