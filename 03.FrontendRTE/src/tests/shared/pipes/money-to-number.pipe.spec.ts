import { NumberToMoneyPipe } from '@shared/pipes/number-to-money.pipe';
import { DecimalPipe } from '@angular/common';
import { MoneyToNumberPipe } from '@shared/pipes/money-to-number.pipe';

describe('Money to Number Pipe', () => {
    const numbers = [2, 20, 200, 2000];
    const moneys = ['0,02', '0,20', '2,00', '20,00'];

    const moneyToNumberPipe = new MoneyToNumberPipe();

    it('should convert a number to a money', () => {
        for (let index in moneys) {
            let result = moneyToNumberPipe.transform(moneys[index]);
            expect(result).toBe(numbers[index]);
        }
    });

    it('should be inverse to money-to-number pipe', () => {
        const numberToMoneyPipe = new NumberToMoneyPipe(new DecimalPipe('de-DE'));
        for (let money of moneys) {
            let result = numberToMoneyPipe.transform(moneyToNumberPipe.transform(money));
            expect(result).toBe(money)
        }
    })
});