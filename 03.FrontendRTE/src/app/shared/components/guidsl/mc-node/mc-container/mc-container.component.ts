/**
 * Component, which manages construction of a leaf node in a DOM tree.
 *
 * Actual DOM structure also includes a component, wrapped by
 * DOMElementComponent, thus making DOMElementComponent a representative of a
 * regular element component (button, text input...) in the interpreter
 * infrastructure. The DOMElementComponent accepts configuration object and
 * handles the creation of regular component according to the configuration.
 */
import { Component, Input, OnInit } from '@angular/core';

import { IDataComponent } from '../common.interface';
import { Command } from '@shared/architecture/command/rte/command.model';


@Component({
  selector: 'mc-container',
  templateUrl: 'mc-container.component.html',
  styleUrls: ['./mc-container.component.scss']
})
export class MCContainerComponent implements OnInit, IDataComponent {

  @Input()
  public commands: Command[];

  constructor() {

  }

  public setData(data) {
    console.log('setData', data);
  }

  ngOnInit(): void {
    // this.commandService.update(this);
  }

}
