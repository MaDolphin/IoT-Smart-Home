import { Component } from '@angular/core';
import { animations } from '../animation';
import { LoadingComponent } from '../loading.component';
import { LoadingService } from '../loading.service';

@Component({
  selector:    'macoco-loading-spinner',
  templateUrl: './loading-spinner.component.html',
  styleUrls:   ['./loading-spinner.component.scss'],
  animations:  animations,
})
export class LoadingSpinnerComponent extends LoadingComponent {
  constructor(l: LoadingService) {
    super(l);
  }
}
