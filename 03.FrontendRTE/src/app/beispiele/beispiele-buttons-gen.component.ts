/* (c) https://github.com/MontiCore/monticore */
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleButtonsGenComponentTOP } from "@targetgui/beispiele-buttons-gen.component/beispiele-buttons-gen.component-top";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-buttons-gen.component/beispiele-buttons-gen.component.html',
})
export class BeispieleButtonsGenComponent extends BeispieleButtonsGenComponentTOP {

  private _isButton7Visible: boolean = true;

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

  pressedButton1(): void {
    this.showSnackbar("Pressed Button 1");
  }

  pressedButton2(): void {
    super.pressedButton2();
    this.showSnackbar("Pressed Button 2");
  }

  pressedButton3(): void {
    super.pressedButton3();
    this.showSnackbar("Pressed Button 3");
  }

  pressedButton4(): void {
    this.showSnackbar("Pressed Button 4");
  }

  pressedButton5(): void {
    this.showSnackbar("Pressed Button 5");
  }

  pressedButton6(): void {
    this.showSnackbar("Pressed Button 6");
    this._isButton7Visible = !this._isButton7Visible;
  }

  pressedButton7(): void {
    this.showSnackbar("Presed Button 7");
  }

  isButton7Visible(): boolean {
    return this._isButton7Visible;
  }

}
