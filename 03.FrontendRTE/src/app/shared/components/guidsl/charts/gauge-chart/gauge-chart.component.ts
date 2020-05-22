import {Component, Input, OnInit} from '@angular/core';
import {LineDataGroup} from "@components/charts/line-chart/line-chart.component";
import {GaugeDummyData} from "@components/charts/gauge-chart/gauge-dummy-data";

@Component({
    selector: 'gauge-chart',
    templateUrl: './gauge-chart.component.html',
    styleUrls: ['./gauge-chart.component.css']
})
export class GaugeChartComponent implements OnInit {

    public options: any = {};
    public dataSet: any[] = [{
        'name': 'Kitchen',
        'value': 23
    },
        {
            'name': 'Bathroom',
            'value': 30
        },
        {
            'name': 'Bedroom',
            'value': 21
        },
        {
            'name': 'Office',
            'value': 15
        }];
    view: [400, 500];
    textValue = 'Temperature °C';

    constructor() {
    }

    ngOnInit() {
    }

    counter: number = 0;

    @Input()
    public set data(lineData: LineDataGroup[]) {
        // todo only replace changed data
        let t = this;

        let useDummyData = false;

        if (useDummyData) {
            lineData.forEach(function (lineD: LineDataGroup, i: number) {
                if (lineD.label != null && lineD.data.length > 0) {
                    console.log(lineD.label + ": " + lineD.data[0].y);
                    t.dataSet = [];
                    t.dataSet.push({"name": lineD.label, "value": lineD.data[0].y + 15}); // +15 für realistischere Werte
                }
            });
        } else {
            this.counter++;
            this.counter %= 2; // reduce refresh rate
            if (this.counter == 0) {
                //console.log(GaugeDummyData.getNewData());
                t.dataSet = GaugeDummyData.getNewData();
                //this.dataSet.push(GaugeDummyData.getNewData())
            }
        }
    }

    public hasData(): boolean {
        return this.dataSet !== null && this.dataSet.length > 0;
    }

}
