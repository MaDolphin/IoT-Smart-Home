import {Component, Input, OnInit} from '@angular/core';
import {GaugeChartDataEntryDTO} from "@targetdtos/gaugechartdataentry.dto";

@Component({
    selector: 'gauge-chart',
    templateUrl: './gauge-chart.component.html',
    styleUrls: ['./gauge-chart.component.css']
})
export class GaugeChartComponent implements OnInit {
    @Input() textValue: string;
    @Input() bigTicks: number = 10; // todo calculate ticks from selectedRange
    @Input() smallTicks: number = 5; // todo calculate ticks from selectedRange
    @Input() unit: string;
    @Input() min: number;
    @Input() max: number;
    @Input()
    colorScheme = {
        domain: ['#E44D25', '#7aa3e5', '#5AA454', '#CFC0BB', '#a8385d', '#aae3f5']
    };

    // default settings
    view: any[] = [400, 400];
    legend: boolean = true;
    legendPosition: string = 'below';
    private _showAxis: boolean = true;

    // realData(Min|Max) is used to set the end of the slider
    realDataMin: number;
    realDataMax: number;
    // this is the value of the slider
    selectedMin: number;
    selectedMax: number;

    // data (changed by the data-field with the realtime data)
    public dataSet: any = [];

    ngOnInit(): void {
        // to force resetting when first data is load
        this.realDataMin = this.max;
        this.realDataMax = this.min;

        // select biggest span
        this.selectedMin = this.min;
        this.selectedMax = this.max;
    }

    constructor() {
    }

    @Input()
    public set data(gaugeData: GaugeChartDataEntryDTO[]) {
        let t = this;
        //console.log(gaugeData.entries);
        //console.log("received data");

        if (gaugeData && gaugeData.entries && gaugeData.entries.length > 0) {
            t.dataSet = gaugeData.entries;

            t.dataSet.forEach(function (entry: any) {
                if (entry.value < t.realDataMin) t.realDataMin = Math.floor(entry.value); // Abrunden
                if (entry.value > t.realDataMax) t.realDataMax = Math.ceil(entry.value); // Aufrunden
            });
        }
    }

    public get showText(): boolean {
        return !!this.textValue;
    }

    public set showAxis(checked: boolean) {
        if (!checked) {
            this.selectedMin = this.min; // select biggest span
            this.selectedMax = this.max; // select biggest span
        }
        this._showAxis = checked;
    }

    public get showAxis(): boolean {
        return this._showAxis;
    }

    public hasData(): boolean {
        return this.dataSet !== null && this.dataSet.length > 0;
    }

}
