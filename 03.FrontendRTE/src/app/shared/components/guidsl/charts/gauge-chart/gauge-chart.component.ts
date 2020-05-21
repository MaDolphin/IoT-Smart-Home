import {Component, Input, OnInit} from '@angular/core';
import {LineDataGroup} from "@components/charts/line-chart/line-chart.component";

@Component({
    selector: 'gauge-chart',
    templateUrl: './gauge-chart.component.html',
    styleUrls: ['./gauge-chart.component.css']
})
export class GaugeChartComponent implements OnInit {


    public options: any = {};
    public dataSet: any[] = [];

    constructor() {
    }

    ngOnInit() {
    }

    @Input()
    public set data(lineData: LineDataGroup[]) {
        // merge data

        lineData.forEach(function (lineD: LineDataGroup, i: number) {
            if (lineD.label != null && lineD.data.length > 0) {
                console.log(lineD.label + ": " + lineD.data[0].y);
            }
        })
        console.log("--------------");
    }

    public hasData(): boolean {
        return this.dataSet !== null && this.dataSet.length > 0;
    }

}
