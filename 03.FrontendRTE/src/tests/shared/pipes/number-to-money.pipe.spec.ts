import { NumberToMoneyPipe } from '@shared/pipes/number-to-money.pipe';
import { DecimalPipe } from '@angular/common';
import { MoneyToNumberPipe } from '@shared/pipes/money-to-number.pipe';

describe('Number to Money Pipe', () => {
    const numbers = [2, 20, 200, 2000];
    const moneys = ['0,02', '0,20', '2,00', '20,00'];

    const numberToMoneyPipe = new NumberToMoneyPipe(new DecimalPipe('de-DE'));

    it('should convert a number to a money', () => {
        for (let index in numbers) {
            let result = numberToMoneyPipe.transform(numbers[index]);
            expect(result).toBe(moneys[index]);
        }
    });

    it('should be inverse to money-to-number pipe', () => {
        const moneyToNumberPipe = new MoneyToNumberPipe();
        for (let num of numbers) {
            let result = moneyToNumberPipe.transform(numberToMoneyPipe.transform(num));
            expect(result).toBe(num)
        }
    })
});