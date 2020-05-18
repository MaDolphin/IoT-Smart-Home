/* (c) https://github.com/MontiCore/monticore */
import {Component, ElementRef, HostBinding, Input, OnInit, ViewChild} from '@angular/core';
import 'chartjs-plugin-streaming';

@Component({
    selector: 'gauge-chart',
    templateUrl: './gauge-chart.component.html',
})
export class GaugeChartComponent implements OnInit {

    @Input() values: any[];
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

    legendPosition: string = 'below';

    @Input()
    colorScheme = {
        domain: ['#E44D25', '#7aa3e5', '#5AA454', '#CFC0BB', '#a8385d', '#aae3f5']
    };

    ngOnInit(): void {
        this.showText = !!this.textValue;
    }

    constructor(private el: ElementRef) {

    }

    hasData() {
        return this.values.length > 0;
    }
}
