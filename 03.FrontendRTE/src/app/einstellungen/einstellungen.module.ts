import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material';
import { ButtonDirectivesModule, FormDirectivesModule, PanelDirectivesModule } from '@upe/ngx-bootstrap-directives';
import { ContextMenuModule, ContextMenuService } from 'ngx-contextmenu';
import { PipesModule } from '@shared/pipes';
import { routing } from './einstellungen.routing';
import { GuiDslComponentsModule } from '@shared/components/guidsl/guidsl-components.module';
import { InputModule } from '@shared/components/guidsl/input/input.module';
import { EinstellungenRollenGenComponent } from "./einstellungen-rollen-gen.component";
import { EinstellungenBenutzerGenComponent } from './einstellungen-benutzer-gen.component';
import { EinstellungenMeinbenutzerGenComponent } from './einstellungen-meinbenutzer-gen.component';

@NgModule({
  declarations: [
    EinstellungenRollenGenComponent,
    EinstellungenMeinbenutzerGenComponent,
    EinstellungenBenutzerGenComponent
  ],
  imports: [
    MatIconModule,
    CommonModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    ButtonDirectivesModule,
    FormDirectivesModule,
    InputModule,
    GuiDslComponentsModule,
    InputModule,
    PanelDirectivesModule,
    routing,
    PipesModule,
    ContextMenuModule,
  ],
  providers: [
    ContextMenuService,
  ],
  entryComponents: []
})
export class EinstellungenModule {
}
