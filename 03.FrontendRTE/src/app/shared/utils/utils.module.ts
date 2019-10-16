/* (c) https://github.com/MontiCore/monticore */

import { ModuleWithProviders, NgModule } from '@angular/core';
import { NotificationService } from '../notification/notification.service';
import { CopyToClipboardService } from './copy-to-clipboard.service';
import { ExcelImportComponent } from './excelimport/excel-import.component';
import { ColorsService } from '@shared/utils/colors.service';
import { CsvImportComponent } from '@shared/utils/csvimport/csv-import.component';

@NgModule({
  imports: [],
  declarations: [ExcelImportComponent, CsvImportComponent],
  exports: [ExcelImportComponent, CsvImportComponent],
  providers: [NotificationService, ColorsService]
})
export class UtilsModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: UtilsModule,
      providers: [
        CopyToClipboardService
      ]
    };
  }
}
