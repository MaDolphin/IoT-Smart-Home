export class GaugeDummyData {
    static getNewData(min: number, max: number): any[] {
        let middle = (max - min) / 2 + min;
        let percent10 = (max -min) /10;
        return [{"name": "Kitchen", "value": (middle + percent10) + Math.random() * 20},
            {"name": "Bathroom", "value": (middle + percent10*1.5) + Math.random() * 20},
            {"name": "Bedroom", "value": (middle + percent10*0.2) + Math.random() * 20},
            {"name": "Office", "value": (middle + percent10*0.8) + Math.random() * 20}];
    }
}
