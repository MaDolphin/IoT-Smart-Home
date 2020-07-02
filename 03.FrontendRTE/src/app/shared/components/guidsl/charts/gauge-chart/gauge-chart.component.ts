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

    /** manually set how many ticks should be displayed */
    bigTicks: number;
    /** manually set how many small ticks between the big ticks should be displayed */
    smallTicks: number;

    /** data (changed by the data-field with the realtime data) */
    public dataSet: any = [];
    /** data that is actually shown */
    public selectedType = "ALL";
    public selectedDataSet: any = [];
    allTypes = ["TEMPERATURE", "CO2"];

    /**
     * init internal values
     */
    ngOnInit(): void {
        this.recalcBigTicks();
    }

    recalcBigTicks() {
        this.bigTicks = (this.max - this.min) / 10; // Alle 10 Schritte
        while (this.bigTicks > 15) { // Solange mehr als 10 Ticks, dann halbiere die Anzahl
            if (this.bigTicks >= 100) {
                this.bigTicks /= 10;
            } else {
                this.bigTicks /= 10;
            }
        }
        this.bigTicks = Math.round(this.bigTicks);
    }

    /**
     * for input range to select smallTicksCount
     */
    get smallTickResizer(): number {
        for (var i = 0; i < 5; i++) {
            if (GaugeChartComponent.recalcFromResizer(i) == this.smallTicks) {
                return i;
            }
        }
        return 4;// Default
    }

    /**
     * input range to select smallTicksCount
     */
    set smallTickResizer(n: number) {
        this.smallTicks = GaugeChartComponent.recalcFromResizer(n);
    }

    /**
     * For the input range to select how many ticks are displayed
     * @param n
     */
    private static recalcFromResizer(n: number): number {
        switch (n) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 4;
            case 4:
            default:
                return 10;
        }
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
        //console.log(gaugeData.entries.length);
        //console.log("received data");
        if (gaugeData && gaugeData.entries && gaugeData.entries.length > 0) {
            //console.log(gaugeData.entries);

            t.dataSet = gaugeData.entries;

            t.dataSet.forEach(function (entry: any) {
                //console.log(entry);
                if (entry.value < t.min) {
                    // Abrunden auf ganze Zehner
                    t.min = Math.floor(entry.value / 10) * 10;
                    t.recalcBigTicks();
                }
                if (entry.value > t.max) {
                    // Aufrunden auf ganze Zehner
                    t.max = Math.ceil(entry.value / 10) * 10;
                    t.recalcBigTicks();
                }
            });
            this.updateSelectedDataset();
        }
    }

    /**
     * only show selected datatype
     */
    updateSelectedDataset() {
        //console.log("Update type to " + this.selectedType);
        this.selectedDataSet = [];

        this.textValue = this.selectedType;

        /*console.log(this.dataSet);
        console.log("ALL")
        console.log(this.selectedType)
        console.log(this.selectedType == "ALL")
        console.log(this.dataSet.length)*/

        let t = this;
        this.dataSet.forEach(function (entry: any) {
            if (t.selectedType == "ALL" || entry.type == t.selectedType) {
                t.selectedDataSet.push(Object.assign({}, entry));
            }
        });

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
