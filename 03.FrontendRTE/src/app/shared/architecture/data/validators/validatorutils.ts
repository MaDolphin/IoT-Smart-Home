import { isBefore } from '@shared/architecture/data/validators/asserts/isBefore';

export class ValidatorUtils {

  public static isDateInRange(date: Date | string, minusYears: number, plusYears: number): boolean {
    let begin = new Date();
    begin.setDate(Date.now());
    begin.setFullYear(begin.getFullYear() - minusYears);
    let end = new Date();
    end.setDate(Date.now());
    end.setFullYear(end.getFullYear() + plusYears);
    return isBefore(begin, date) && isBefore(date, end);
  }

  public static isDateAfterRWTHFounding(date: Date | string): boolean {
    let rwthFounding = new Date();
    rwthFounding.setFullYear(1870, 10, 10);
    return isBefore(rwthFounding, date);
  }

}

