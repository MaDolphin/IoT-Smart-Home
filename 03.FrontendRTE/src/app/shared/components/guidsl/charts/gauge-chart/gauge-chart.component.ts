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
    @Input() showAxis: boolean = true;
    @Input() min: number = 0;
    @Input() max: number = 100;

    view: any[] = [400, 400]; // todo change dynamically with screen size
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
