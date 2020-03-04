import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleLabelsGenComponentTOP } from "@targetgui/beispiele-labels-gen.component/beispiele-labels-gen.component-top";
import { MatSnackBar } from "@angular/material/snack-bar";

type LabelColor = "black" | "blue";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-labels-gen.component/beispiele-labels-gen.component.html',
})
export class BeispieleLabelsGenComponent extends BeispieleLabelsGenComponentTOP {

  // overridden so that label is shown by default
  isVisibleLabel: any = true;

  private labelColor: LabelColor = "black";

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
    private snackBar: MatSnackBar
  ) {
    super(_commandRestService, _route, _router);
  }

  private showSnackbar(text: string) {
    this.snackBar.open(text, "", {
      duration: 2000
    });
  }

  clickedButton1(): void {
    this.isVisibleLabel = !this.isVisibleLabel;
  }

  clickedButton2(): void {
    this.labelColor = this.labelColor == "black" ? "blue" : "black";
  }

  getLabelColor(): string {
    return this.labelColor;
  }

  clickedLabel(): void {
    this.showSnackbar("Clicked Label");
  }

}
