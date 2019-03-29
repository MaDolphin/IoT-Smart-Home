export class DateToDaysConverter {

  private static ONE_DAY: number = 1000 * 60 * 60 * 24;

  public static getDaysToYearEnd(date1: Date): number {
    let endOfYear = new Date(date1.getFullYear(), 11, 32);
    let date: Date = new Date(date1.getFullYear(), date1.getMonth(), date1.getDate());

    return Math.ceil( (endOfYear.getTime() - date.getTime()) / this.ONE_DAY);
  }

  public static getDaysFromStartYear(date2: Date): number {
    let startOfYear = new Date(date2.getFullYear(), 0, 0);
    let date: Date = new Date(date2.getFullYear(), date2.getMonth(), date2.getDate());

    return Math.ceil( (date.getTime() - startOfYear.getTime()) / this.ONE_DAY);
  }

  public static getDaysInTimeFrame(date1: Date, date2: Date): number {
    let startDate = new Date(date1.getFullYear(), date1.getMonth(), date1.getDate());
    let endDate: Date = new Date(date2.getFullYear(), date2.getMonth(), date2.getDate());
    return Math.ceil( (endDate.getTime() - startDate.getTime()) / this.ONE_DAY) + 1;
  }

  public static getDaysInYear(date: Date): number {
    let firstDay: Date = new Date(date.getFullYear(), 0, 0);
    let lastDay: Date = new Date(date.getFullYear(), 11, 31);
    return Math.ceil( (lastDay.getTime() - firstDay.getTime()) / this.ONE_DAY);
  }

  public static getMonthsFromStartYear(date2: Date): number {
    let startOfYear = new Date(date2.getFullYear(), 0, 1);
    let date: Date = new Date(date2.getFullYear(), date2.getMonth(), date2.getDate());

    return this.getMonthsInTimeFrame(startOfYear, date);
  }

  public static getMonthsToYearEnd(date1: Date): number {
    let endOfYear = new Date(date1.getFullYear(), 11, 31);
    let date: Date = new Date(date1.getFullYear(), date1.getMonth(), date1.getDate());

    return this.getMonthsInTimeFrame(date, endOfYear);
  }

  public static getMonthsInTimeFrame(date1: Date, date2: Date): number {
    let year1 = date1.getFullYear();
    let year2 = date2.getFullYear();
    let month1 = date1.getMonth();
    let month2 = date2.getMonth();

    // Months are starting from 0 in JS
    if (month1 === 0) {
      month1++;
      month2++;
    }
    let numberOfMonths = (year2 - year1) * 12 + (month2 - month1);

    if (date2.getDate() >= 15)
      numberOfMonths++;


    return numberOfMonths
  }


}