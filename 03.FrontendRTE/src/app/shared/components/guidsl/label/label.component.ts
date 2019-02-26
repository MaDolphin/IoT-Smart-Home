import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'macoco-label',
  templateUrl: './label.component.html',
})

export class LabelComponent implements OnInit {

  private _style: any;
  private _classes: string[];

  @Input()
  public text;

  @Input()
  public set classes(value: string[]) {
    this._classes = value;
  }

  public get classes(): string[] {
    return this._classes;
  }

  @Input()
  public set style(value: any) {
    this._style = value;
  }

  public get style(): any {
    return this._style;
  }

  public onClick(): void {
  }

  ngOnInit() { }


}
