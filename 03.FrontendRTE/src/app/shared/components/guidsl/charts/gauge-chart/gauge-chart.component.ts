import {Component, Input, OnInit} from '@angular/core';
import {GaugeChartDataEntryDTO} from "@targetdtos/gaugechartdataentry.dto";

/**
 * Component to display a gauge chart, needs data in the data field
 * and can be adjusted with textValue, min, max, ...
 */
@Component({
    selector: 'gauge-chart',
    templateUrl: './gauge-chart.component.html',
    styleUrls: ['./gauge-chart.component.css']
})
export class GaugeChartComponent implements OnInit {
    /** text that is displayed inside the gauge chart */
    @Input() textValue: string;
    /** manually set how many ticks should be displayed */
    @Input() bigTicks: number = 10; // todo calculate ticks from selectedRange
    /** manually set how many small ticks between the big ticks should be displayed */
    @Input() smallTicks: number = 5; // todo calculate ticks from selectedRange
    /** when set it is displayed under the text value */
    @Input() unit: string;
    /** manually set the min value */
    @Input() min: number;
    /** manually set the max value */
    @Input() max: number;
    /** colorScheme can be adjusted */
    @Input()
    colorScheme = {
        domain: ['#E44D25', '#7aa3e5', '#5AA454', '#CFC0BB', '#a8385d', '#aae3f5']
    };

    // default settings
    view: any[] = [400, 400];
    /** init value: showing a legend */
    legend: boolean = true;
    /** init value: position the legend below */
    legendPosition: string = 'below';
    /** used intern for showing the axis when function showAxis() is called  */
    private _showAxis: boolean = true;

    /** realDataMin is used to set the end of the slider     */
    realDataMin: number;
    /** realDataMax is used to set the end of the slider     */
    realDataMax: number;
    /** this is the actual value of the min slider */
    selectedMin: number;
    /** this is the actual value of the max slider */
    selectedMax: number;

    /** data (changed by the data-field with the realtime data) */
    public dataSet: any = [];

    /**
     * init internal values
     */
    ngOnInit(): void {
        // to force resetting when first data is load
        this.realDataMin = this.max;
        this.realDataMax = this.min;

        // select biggest span
        this.selectedMin = this.min;
        this.selectedMax = this.max;
    }

    /**
     * @ignore
     */
    constructor() {
    }

    /**
     * refreshes the displayed data, when the new given data has at least one element
     * when there is no data given it uses the old data
     * @param {GaugeChartDataEntryDTO[]} gaugeData
     */
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

    /**
     * returns wether to show a when a text is set or don't show it when no text is set
     */
    public get showText(): boolean {
        return !!this.textValue;
    }

    /**
     * changes the axis visibility and resets the slider
     * @param checked
     */
    public set showAxis(checked: boolean) {
        if (!checked) {
            this.selectedMin = this.min; // select biggest span
            this.selectedMax = this.max; // select biggest span
        }
        this._showAxis = checked;
    }

    /**
     * returns if the axis are visible
     */
    public get showAxis(): boolean {
        return this._showAxis;
    }

    /**
     * returns if the data has any values,
     * used for deciding wether to show the diagram or a text with the information that no data is available
     */
    public hasData(): boolean {
        return this.dataSet !== null && this.dataSet.length > 0;
    }

}
