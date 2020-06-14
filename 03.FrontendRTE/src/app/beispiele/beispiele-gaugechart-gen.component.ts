import {Component} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {BeispieleGaugechartGenComponentTOP} from '@targetgui/beispiele-gaugechart-gen.component/beispiele-gaugechart-gen.component-top';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {TypedJSON} from "@upe/typedjson";
import {LineDataGroup} from "@components/charts/line-chart/line-chart.component";
import {GaugeChartDataDTO} from '@targetdtos/gaugechartdata.dto';
import {GaugeDummyData} from "@components/charts/gauge-chart/gauge-dummy-data";

@Component({
    templateUrl: '../../../target/generated-sources/gui/beispiele-gaugechart-gen.component/beispiele-gaugechart-gen.component.html',
})
export class BeispieleGaugechartGenComponent extends BeispieleGaugechartGenComponentTOP {
    public constructor(
        protected _commandRestService: CommandRestService,
        protected _route: ActivatedRoute,
        protected _router: Router,
        protected _webSocketService: WebSocketService
    ) {
        super(_commandRestService, _route, _router, _webSocketService);
    }

    public transformedRealtimeData1: LineDataGroup[] = [];
    public transformedRealtimeData2: LineDataGroup[] = [];


    ngOnInit() {
        super.ngOnInit();
        // setInterval(() => this.reloadData(), 2000);
    }

    public reloadData(): void {
        // this.transformedRealtimeData1 = GaugeDummyData.getNewData(0, 30);
        // this.transformedRealtimeData2 = GaugeDummyData.getNewData(150, 450);
    }

    public initchartDataSocket(): void {

        this.chartDataSocket = this._webSocketService.open('Sensor', ['TEMPERATURE']);

    }

    public subscribechartDataSocket(): void {
        if (this.chartDataSocket) {
            console.log("subscibe websocketservice")
            this.subscriptions.push(this.chartDataSocket.subscribe(message => {
                    let receivedData: GaugeChartDataDTO = TypedJSON.parse(message.data, GaugeChartDataDTO);
                    //console.log(receivedData);
                    this.chartData = this.transformSocketData(receivedData);
                    if (this.chartData.length > 0)
                        this.transformedRealtimeData1 = this.chartData;
                    console.log("received data: " + message.data);
                    console.log("transformed Data: ");
                    console.log(this.chartData);
                    console.log("-------------------------------");

                    this.transformedRealtimeData2 = GaugeDummyData.getNewData(150, 450);
                    //console.log(this.transformedRealtimeData2);
                }, err =>
                    console.error(err)
            ));
        } else {
            console.error('Socket is not initialized. Initialize socket in the component constructor');
        }

    }

    public transformSocketData(dataset: any): LineDataGroup[] {
        let data = [];
        if (!dataset.entries.length) {
            /*data.push({
                name: dataset.timestamp,
                value: null
            })*/
        }
        dataset.entries.forEach(entry => {
            data.push({
                name: entry.name,
                value: entry.value
            })
        })
        return data;

    }
}
