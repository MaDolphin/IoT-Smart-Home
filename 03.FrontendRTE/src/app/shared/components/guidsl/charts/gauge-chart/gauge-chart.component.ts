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

    view: any[] = [400, 400]; // todo change dynamically with screen size
	showAxis: boolean = true;
    legend: boolean = true;
    showText: boolean = true;

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
    textValue = 'Temperature °C';
    legendPosition: string = 'below';

    @Input()
    colorScheme = {
        domain: ['#E44D25', '#7aa3e5', '#5AA454', '#CFC0BB', '#a8385d', '#aae3f5']
    };

    ngOnInit(): void {
        this.showText = !!this.textValue;
    }

    constructor() {
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
