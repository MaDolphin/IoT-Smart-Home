import { Component, Input } from '@angular/core';

@Component({
  selector: 'macoco-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss']
})
export class ButtonComponent {

  private _label: string;
  private _style: any;
  private _class: string[];
  private _disabled: boolean = false;

  public type: string = 'button';

  @Input()
  public set label(label: string) {
    this._label = label;
  }

  public get label(): string {
    return this._label;
  }

  @Input()
  public set classes(value: string[]) {
    this._class = value;
  }

  public get classes(): string[] {
    return this._class;
  }

  @Input()
  public set style(value: any) {
    this._style = JSON.parse(JSON.stringify(value));
  }

  public get style(): any {
    return this._style;
  }

  @Input()
  public set disabled(value: boolean) {
    this._disabled = value;
  }

  public get disabled(): boolean {
    return this._disabled;
  }

}
