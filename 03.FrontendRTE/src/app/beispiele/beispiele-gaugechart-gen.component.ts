import {Component} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {BeispieleGaugechartGenComponentTOP} from '@targetgui/beispiele-gaugechart-gen.component/beispiele-gaugechart-gen.component-top';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {LineGraphDTO} from '@targetdtos/linegraph.dto';
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
        super(_commandRestService, _route, _router/*, _webSocketService*/);
    }

    public transformedRealtimeData1: LineDataGroup[] = [];
    public transformedRealtimeData2: LineDataGroup[] = [];


    ngOnInit() {
        super.ngOnInit();
        setInterval(() => this.reloadData(), 2000);
    }

    public reloadData(): void {
        console.log(this.chartData);
        if (this.chartData)
            console.log(this.chartData.getData());

        this.transformedRealtimeData1 = GaugeDummyData.getNewData(0, 30);
        this.transformedRealtimeData2 = GaugeDummyData.getNewData(150, 450);
    }


    /*public subscribechartDataSocket(): void {

        if (this.chartDataSocket) {
            this.subscriptions.push(this.chartDataSocket.subscribe(message => {
                    let receivedData: GaugeChartDataDTO = TypedJSON.parse(message.data, GaugeChartDataDTO);
                    this.chartData = this.transformSocketData(receivedData);
                    console.log(receivedData);
                    console.log(this.chartData);

                    this.transformedRealtimeData1 = GaugeDummyData.getNewData(0, 30);
                    this.transformedRealtimeData2 = GaugeDummyData.getNewData(0, 500);
                }, err =>
                    console.error(err)
            ));
        } else {
            console.error('Socket is not initialized. Initialize socket in the component constructor');
        }

    }*/


    /*public subscribedataSocket(): void {
        if (this.chartDataSocket) {
            this.subscriptions.push(this.chartDataSocket.subscribe(message => {
                    //let receivedData: GaugeChartDataDTO = TypedJSON.parse(message.data, GaugeChartDataDTO);
                    //this.chartData = this.transformSocketData(receivedData);
                    console.log("wrong");
                    //console.log(receivedData);
                    //console.log(this.transformSocketData(receivedData));
                    //this.data = this.transformSocketData(receivedData);
                    //this.transformedRealtimeData1 = GaugeDummyData.getNewData(0, 30);
                    //this.transformedRealtimeData2 = GaugeDummyData.getNewData(0, 500);
                    //this.chartData = GaugeDummyData.getNewData(0, 30);
                }, err =>
                    console.error(err)
            ));
        } else {
            console.error('Socket is not initialized. Initialize socket in the component constructor');
        }
    }*/
}
