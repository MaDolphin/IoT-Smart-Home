/* (c) https://github.com/MontiCore/monticore */
import {Component} from '@angular/core';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import { GaugeDataGroup } from "@components/charts/gauge-chart/gauge-chart.component";
import { BeispieleGaugeChartDTO } from "@targetdtos/beispielegaugechart.dto";
import {BeispieleGaugechartsGenComponentTOP} from "@targetgui/beispiele-gaugecharts-gen.component/beispiele-gaugecharts-gen.component-top";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-gaugecharts-gen.component/beispiele-gaugecharts-gen.component.html',
})
export class BeispieleGaugechartsGenComponent extends BeispieleGaugechartsGenComponentTOP {

  public constructor(
    protected _webSocketService: WebSocketService,
    protected _router: Router,
    protected _route: ActivatedRoute,
    protected _commandRestService: CommandRestService) {
    super(_commandRestService, _route, _router, _webSocketService);
  }

  public getImageSource() {
    // return image url
    return 'assets/img/rwth_se_rgb.png';
  }

  transformedGaugeChartData: GaugeDataGroup[] = [];

  public transformDTO(dto: BeispieleGaugeChartDTO): GaugeDataGroup[] {
    const groups: GaugeDataGroup[] = [];

    dto.entries.forEach(entry => {
      const data = [];
      const group: any = {
        "label": entry.label,
        "data": data
      };
      entry.dataEntries.forEach(dataEntry => {
        data.push({
          x: dataEntry.x,
          y: dataEntry.y
        })
      })

      groups.push(group);
    });

    console.log("GROUPS:", groups);
    return groups;
  }

  public colorstransformedGaugeChartData = [];

  initBeispieleGaugeChartDTOgaugeChartData2(): void {
    BeispieleGaugeChartDTO.getAll(this.commandManager).then(
      (model: BeispieleGaugeChartDTO) => {
        this.transformedGaugeChartData = this.transformDTO(model);
      }
    );
  }

}
