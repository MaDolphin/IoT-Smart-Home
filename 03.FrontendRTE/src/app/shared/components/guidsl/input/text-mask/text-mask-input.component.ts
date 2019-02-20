import { Component, Input, OnInit } from '@angular/core';
import { TextInputComponent } from '../text/text-input.component';

@Component({
  selector: 'app-text-mask-input',
  templateUrl: './text-mask-input.component.html',
  styleUrls: ['../input.scss']
})
export class TextMaskInputComponent extends TextInputComponent implements OnInit {

  @Input() public mask: Array<string | RegExp>;

  public ngOnInit(): void {
    super.ngOnInit();
    if (!this.mask) {
      this.logger.error('MAF0x00C0: text mask is not set');
      throw new Error('MAF0x00C0: text mask is not set');
    }
  }

}
