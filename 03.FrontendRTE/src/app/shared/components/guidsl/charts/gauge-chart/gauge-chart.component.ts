import {Component, Input, OnInit} from '@angular/core';
import {LineDataGroup} from "@components/charts/line-chart/line-chart.component";

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
        /*let t = this;
        setTimeout(function () {
            // demo change
            t.dataSet = [{
                'name': 'Kitchen',
                'value': -23
            },
                {
                    'name': 'Bathroom',
                    'value': -30
                }];
        }, 5 * 1000);*/
    }

    ngOnInit() {
    }

    @Input()
    public set data(lineData: LineDataGroup[]) {
        // todo merge data
        let t = this;

        lineData.forEach(function (lineD: LineDataGroup, i: number) {
            if (lineD.label != null && lineD.data.length > 0) {
                console.log(lineD.label + ": " + lineD.data[0].y);
                t.dataSet = [];
                t.dataSet.push({"name": lineD.label, "value": lineD.data[0].y + 15}); // +15 für realistischere Werte
            }
        });

        /*console.log(this.dataSet);
        if (this.dataSet.length > 0 && this.dataSet[0])
            console.log(this.dataSet[0].value);
        console.log("--------------");*/
    }

    public hasData(): boolean {
        return this.dataSet !== null && this.dataSet.length > 0;
    }

}
