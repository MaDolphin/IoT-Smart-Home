import { Component } from '@angular/core';
import { animations } from '../animation';
import { LoadingComponent } from '../loading.component';
import { LoadingService } from '../loading.service';

@Component({
  selector:    'app-loading-bar',
  templateUrl: './loading-bar.component.html',
  styleUrls:   ['./loading-bar.component.scss'],
  animations:  animations,
})
export class LoadingBarComponent extends LoadingComponent {
  constructor(l: LoadingService) {
    super(l);
  }
}
