import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {AbstractInputComponent} from "@shared/components/guidsl/input/abstract.input.component";
import {DropDownControl} from "@shared/architecture/forms/controls/drop-down.control";

@Component({
  selector: 'macoco-multi-drop-down-input',
  templateUrl: './multi-drop-down-input.component.html',
  styleUrls: ['./multi-drop-down-input.component.scss']
})
export class MultiDropDownInputComponent extends AbstractInputComponent<DropDownControl<any>> implements OnInit, AfterViewInit {

  ngAfterViewInit(): void {
  }

  ngOnInit() {
  }

  @Input() public set multiple(val: boolean) {
    this.modelFormControl.acceptMultiple = val;
  }

  public get options() {
    return this.modelFormControl.options;
  }

  public isDisabledOption(item) {
    return false;
  }

  public get multiple(): boolean {
    //return this.modelFormControl.acceptMultiple;
    return true;
  }

}
