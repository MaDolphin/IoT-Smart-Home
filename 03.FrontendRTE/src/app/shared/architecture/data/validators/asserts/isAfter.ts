import { date } from '@shared/architecture/data/validators/asserts/date';

export function isAfter(start: string | Date, end: string | Date): boolean {
    start = date(start);
    end = date(end);

    return start.getTime() < end.getTime();
}