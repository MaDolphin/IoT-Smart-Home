import {Component, Input, OnInit} from '@angular/core';
import {LineDataGroup} from "@components/charts/line-chart/line-chart.component";
import {GaugeDummyData} from "@components/charts/gauge-chart/gauge-dummy-data";

@Component({
    selector: 'gauge-chart',
    templateUrl: './gauge-chart.component.html',
    styleUrls: ['./gauge-chart.component.css']
})
export class GaugeChartComponent implements OnInit {
    @Input() textValue: string;
    @Input() bigTicks: number = 10;
    @Input() smallTicks: number = 5;
    @Input() unit: string;
    @Input() min: number = 0;
    @Input() max: number = 100;

    view: any[] = [400, 400];
    legend: boolean = true;
    showText: boolean = true;

    // realData(Min|Max) is used to set the end of the slider
    realDataMin: number;
    realDataMax: number;
    selectedMin: number;
    selectedMax: number;

    public dataSet: any[] = [];
    legendPosition: string = 'below';

    @Input()
    colorScheme = {
        domain: ['#E44D25', '#7aa3e5', '#5AA454', '#CFC0BB', '#a8385d', '#aae3f5']
    };

    ngOnInit(): void {
        this.showText = !!this.textValue;
        // to force resetting when first data is load
        this.realDataMin = this.max;
        this.realDataMax = this.min;

        // select biggest span
        this.selectedMin = this.min;
        this.selectedMax = this.max;

        this.showAxis = true;
    }

    constructor() {
    }

    counter: number = 0;

    @Input()
    public set data(lineData: LineDataGroup[]) {
        // todo only replace changed data, don't replace all
        let t = this;

        // use the dummyData from the LineGraph or generate new one for the gauge chart
        let useGivenData = false;

        if (useGivenData) {
            lineData.forEach(function (lineD: LineDataGroup, i: number) {
                if (lineD.label != null && lineD.data.length > 0) {
                    console.log(lineD.label + ": " + lineD.data[0].y);
                    t.dataSet = [];
                    t.dataSet.push({"name": lineD.label, "value": lineD.data[0].y + 15}); // +15 für realistischere Werte
                }
            });
        } else {
            this.counter++;
            this.counter %= 2; // reduces refresh rate
            if (this.counter == 0) {
                //console.log(GaugeDummyData.getNewData(this.min, this.max));
                t.dataSet = GaugeDummyData.getNewData(this.min, this.max);
            }
        }

        // Reset realDataMin and realDataMax
        t.dataSet.forEach(function (entry: any, i: number) {
            if (entry.value < t.realDataMin) t.realDataMin = Math.round(entry.value);
            if (entry.value > t.realDataMax) t.realDataMax = Math.round(entry.value);
        })
    }


    private _showAxis: boolean;
    public set showAxis(checked: boolean){
        if(!checked){
            this.selectedMin = this.min; // select biggest span
            this.selectedMax = this.max; // select biggest span
        }
        this._showAxis = checked;
    }

    public get showAxis() : boolean{
        return this._showAxis;
    }

    public hasData(): boolean {
        return this.dataSet !== null && this.dataSet.length > 0;
    }

}
