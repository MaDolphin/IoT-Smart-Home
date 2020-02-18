import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleLabelsGenComponentTOP } from "@targetgui/beispiele-labels-gen.component/beispiele-labels-gen.component-top";

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
  ) {
    super(_commandRestService, _route, _router);
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

}
