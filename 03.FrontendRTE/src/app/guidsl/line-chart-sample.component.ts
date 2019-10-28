import {Component} from '@angular/core';
import {LineChartSampleComponentTOP} from '@targetgui/line-chart-sample.component/line-chart-sample.component-top';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';

@Component({
  templateUrl: '../../../target/generated-sources/gui/line-chart-sample.component/line-chart-sample.component.html',
})
export class LineChartSampleComponent extends LineChartSampleComponentTOP {

  public constructor(
    protected _webSocketService: WebSocketService,
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService) {
    super(_webSocketService, _router, _route, _commandRestService);
  }

  public isImageVisible(): boolean {
    // show image if there is data about some object
    return this.lineChartData.some(group => group.label !== undefined);
  }

  public getImageSource() {
    // return image url
    return 'assets/img/rwth_se_rgb.png';
  }
}
