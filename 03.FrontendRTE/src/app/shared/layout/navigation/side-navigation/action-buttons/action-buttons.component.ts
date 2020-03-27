/* (c) https://github.com/MontiCore/monticore */
import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { MatTooltip } from "@angular/material/tooltip";

export interface IActionButton {
  id: string,
  tooltip: string,
  icon: string,
}

@Component({
  selector: 'montigem-action-buttons',
  templateUrl: './action-buttons.component.html',
  styleUrls: ['./action-buttons.component.scss']
})
export class ActionButtonsComponent implements OnInit {

  @Output() clicked = new EventEmitter<string>();

  @Input() expanded: boolean = true;

  _actionButtons: IActionButton[][];

  private static chunk(array: IActionButton[], size): IActionButton[][] {
    const chunked_arr: IActionButton[][] = [];
    let index = 0;
    while (index < array.length) {
      chunked_arr.push(array.slice(index, size + index));
      index += size;
    }
    return chunked_arr;
  }

  @Input() set actionButtons(actionButtons: IActionButton[]) {
    this._actionButtons = ActionButtonsComponent.chunk(actionButtons, this.expanded ? 3 : 1);
  }

  constructor() {
  }

  ngOnInit() {
  }

  onClick(tooltip: any, id: string): void {
    (tooltip as MatTooltip).hide();

    // wait until tooltip is closed - this is necessary for the print button in order for the tooltip to not get displayed when printing
    setTimeout(() => {
      this.clicked.emit(id);
    }, 1);
  }

}
