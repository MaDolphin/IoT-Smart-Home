/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { toDate } from '@shared/generic-form/validator/asserts/date';


export function date(datum: string | Date) {
    if (typeof datum === 'string') {
        datum = toDate(datum);
    }

    if (!(datum instanceof Date)) {
        throw new Error(`passed date '${datum}' is not valid`);
    }

    return datum;
}