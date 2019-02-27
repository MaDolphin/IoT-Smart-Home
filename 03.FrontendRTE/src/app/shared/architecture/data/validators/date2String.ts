/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { toDateString } from '@shared/generic-form/validator/asserts/date';
import { date } from '@shared/architecture/data/validators/asserts/date';

export function date2String(dt: Date | string): string {
    return toDateString(date(dt))
}