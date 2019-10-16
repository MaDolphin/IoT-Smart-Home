/* (c) https://github.com/MontiCore/monticore */

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'montigem-general-dialog',
  templateUrl: './montigem-dialog.component.html',
  styleUrls: ['./montigem-dialog.component.scss'],
})
export class MontiGemDialogComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit() {
  }

}
