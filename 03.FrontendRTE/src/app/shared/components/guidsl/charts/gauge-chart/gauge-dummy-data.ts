import {GaugeChartDataEntryDTO} from "@targetdtos/gaugechartdataentry.dto";

/**
 * class used to generate random data
 */
export class GaugeDummyData {
    /**
     * returns 4 random values for the gauge chart
     * @param min generated values are above this value
     * @param max generated values are under this value
     */
    static getNewData(min: number, max: number): any {
        let middle = (max - min) / 2 + min;
        let percent10 = (max - min) / 10;
        /*return [{"name": "Kitchen", "value": (middle + percent10) + Math.random() * 20},
            {"name": "Bathroom", "value": (middle + percent10*1.5) + Math.random() * 20},
            {"name": "Bedroom", "value": (middle + percent10*0.2) + Math.random() * 20},
            {"name": "Office", "value": (middle + percent10*0.8) + Math.random() * 20}];*/
        var gK = new GaugeChartDataEntryDTO()
        gK.name = "Kitchen";
        gK.value = (middle + percent10) + Math.random() * 20;
        var gBa = new GaugeChartDataEntryDTO()
        gBa.name = "Bathroom";
        gBa.value = (middle + percent10) * 1.5 + Math.random() * 20;
        var gBe = new GaugeChartDataEntryDTO()
        gBe.name = "Bedroom";
        gBe.value = (middle + percent10) * 1.1 + Math.random() * 20;
        var gO = new GaugeChartDataEntryDTO()
        gO.name = "Office";
        gO.value = (middle + percent10) * 0.8 + Math.random() * 20;
        var entryDTO: GaugeChartDataEntryDTO[] = [gK, gBa, gBe, gO];

        return {entries: entryDTO};
    }
}
