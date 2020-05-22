export class GaugeDummyData {
    static getNewData(): any[] {
        return [{"name": "Kitchen", "value": 10 + Math.random() * 20},
            {"name": "Bathroom", "value": 15 + Math.random() * 20},
            {"name": "Bedroom", "value": 2 + Math.random() * 20},
            {"name": "Office", "value": 7 + Math.random() * 20}];
    }
}
