import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleTextinputsGenComponentTOP } from "@targetgui/beispiele-textinputs-gen.component/beispiele-textinputs-gen.component-top";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-textinputs-gen.component/beispiele-textinputs-gen.component.html',
})
export class BeispieleTextinputsGenComponent extends BeispieleTextinputsGenComponentTOP implements OnInit {

  private _isTextfieldVisible: boolean = true;

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
    private _snackBar: MatSnackBar
  ) {
    super(_commandRestService, _route, _router);
  }

  private showSnackbar(text: string) {
    this._snackBar.open(text, "", {
      duration: 2000
    });
  }

  sendTextfield(): void {
    this.showSnackbar(`Model Value Textfield: "${this._textfield1InputControl.getModelValue()}"`);
  }

  switchVisibleState(): void {
    this._isTextfieldVisible = !this._isTextfieldVisible;
  }

  isTextfieldVisible(): boolean {
    return this._isTextfieldVisible;
  }

  sendNumberTextfield(): void {
    const modelValue: number = this._numberTextfieldControl.getModelValue();
    this.showSnackbar(`${modelValue}`);
  }

  sendDateTextfield(): void {
    const actualValue: string | Date = this._dateTextfieldInputControl.actualValue;
    const modelValue: string = this._dateTextfieldInputControl.getModelValue();
    this.showSnackbar(`Actual Value: ${actualValue}, Model Value: ${modelValue}`);
  }

  sendMoneyTextfield(): void {
    this.showSnackbar(this._moneyTextfieldInputControl.getModelValue());
  }

  sendHoursTextfield(): void {
    const modelValue: number = this._hoursTextfieldInputControl.getModelValue();
    this.showSnackbar(`${modelValue}`);
  }

  sendPasswordTextfield(): void {
    this.showSnackbar(this._passwordTextfieldControl.getModelValue());
  }

  sendPercentTextfield(): void {
    const modelValue: number = this._percentTextfieldInputControl.getModelValue();
    this.showSnackbar(`${modelValue}`);
  }
}
