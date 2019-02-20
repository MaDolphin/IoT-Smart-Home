import { isAfter } from '@shared/architecture/data/validators/asserts/isAfter';

export function isBefore(start: string | Date, end: string | Date): boolean {
    return !isAfter(end, start);
}