import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleAutocompletesAndDropdownsGenComponentTOP } from "@targetgui/beispiele-autocompletes-and-dropdowns-gen.component/beispiele-autocompletes-and-dropdowns-gen.component-top";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-autocompletes-and-dropdowns-gen.component/beispiele-autocompletes-and-dropdowns-gen.component.html',
})
export class BeispieleAutocompletesAndDropdownsGenComponent extends BeispieleAutocompletesAndDropdownsGenComponentTOP implements OnInit {

  // Options for dropdown/autocomplete from controls should be an array of strings
  public years: string[];

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

  ngOnInit() {
    super.ngOnInit();

    this.years = [
      String((new Date()).getFullYear() - 2),
      String((new Date()).getFullYear() - 1),
      String((new Date()).getFullYear()),
      String((new Date()).getFullYear() + 1),
      String((new Date()).getFullYear() + 2),
    ];

    this._myDropdownInput1Control.setOptions(this.years);
    this._myAutocompleteInput1Control.setOptions(this.years);
  }


  sendDropdown1(): void {
    this.showSnackbar(`Model Value Dropdown 1: ${this._myDropdownInput1Control.getModelValue()}`);
  }


  sendAutocomplete1(): void {
    this.showSnackbar(`Model Value Autocomplete 1: ${this._myAutocompleteInput1Control.getModelValue()}`);
  }
}
