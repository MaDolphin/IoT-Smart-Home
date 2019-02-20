import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DashboardGenComponentTOP } from '@targetgui/dashboard-gen.component/dashboard-gen.component-top';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { CommandManager } from '@shared/architecture/command/rte/command.manager';

@Component({
  templateUrl: '../../../target/generated-sources/gui/dashboard-gen.component/dashboard-gen.component.html',
})

export class DashboardGenComponent extends DashboardGenComponentTOP implements OnInit {

  public constructor(
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService
  ) {
    super(_router, _route, _commandRestService);
    this.commandManager = new CommandManager(this._commandRestService);
  }

  public navigateToF1Overview() {
    this._router.navigate(['../fakultaet', 'overview'], { relativeTo: this._route });
  }

  public navigateToFinanzenOverview() {
    this._router.navigate(['/finanzen/konten/', 'overview'], { relativeTo: this._route });
  }

  public navigateToZahlungen() {
    this._router.navigate(['/finanzen/zahlungen', 'buchungen'], { relativeTo: this._route });
  }

  public navigateToPersonalOverview() {
    this._router.navigate(['/personal/mitarbeiter/', 'overview'], { relativeTo: this._route });
  }

  public navigateToFinanzenDashboard() {
    this._router.navigate(['/finanzen/dashboard'], { relativeTo: this._route });
  }
}
