import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { CommandManager } from "@shared/architecture/command/rte/command.manager";
import { EditableTableExampleForm } from "@targetgui/beispiele-datatables-gen.component/editabletableexample.form";
import { NotificationService } from "@shared/notification/notification.service";
import { DownloadFileService } from "@shared/utils/download-file.service";
import { CopyToClipboardService } from "@shared/utils/copy-to-clipboard.service";
import { BeispieleDatatablesGenComponentTOP } from "@targetgui/beispiele-datatables-gen.component/beispiele-datatables-gen.component-top";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-datatables-gen.component/beispiele-datatables-gen.component.html',
  providers: [EditableTableExampleForm] // necessary for 'editierbareTabelle'
})
export class BeispieleDatatablesGenComponent extends BeispieleDatatablesGenComponentTOP implements OnInit {

  constructor(
    _notificationService: NotificationService, // necessary for context menu
    _dfs: DownloadFileService, // necessary for context menu
    _ctc: CopyToClipboardService, // necessary for context menu
    form: EditableTableExampleForm,
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _ctc, _dfs, _notificationService, _route, _router);
    this.commandManager = new CommandManager(this._commandRestService);
    this.form = form;
  }

  // you can override the table columns initialization
  public initeinfacheTabelleColumns(): void {
    super.initeinfacheTabelleColumns();
    this.einfacheTabelleColumns[0].name = "Benutzername (HWC)";
  }

  //region Editierbare Tabelle
  protected init_et_entriesTable(): void {
    super.init_et_entriesTable();
    this._et_entries = this.et.entries;
  }

  public onEditEditierbareTabelle(event) {
    let model = event;
    console.log(model);

    this.form.setModel(model);
    this.form.setValues(model);

    this.form._entry1.setValue(event.entry1);
    this.form._entry2.setValue(event.entry2);
    this.form._someList_someEntry.setValue(event.someList.map(value => value.someEntry));
  }

  onSave(event: any): void {
    console.log("Received event ", event);
  }
  //endregion

  //region Tabelle mit Context Menu
  public getString() {
    return "Anderer Methodenname ";
  }

  public isSomething() {
    return true; // hwc logic belongs here...
  }
  //endregion

}
