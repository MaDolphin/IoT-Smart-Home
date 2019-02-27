/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

/* tslint:disable:no-unused-variable */

import { ApiService } from '@shared/architecture/services/api.service';
import { BooleanWrapper } from '@shared/architecture/services/primitive-wrappers/boolean-wrapper';

describe('Architecture', () => {
    describe('Services', () => {
        describe('API Service', () => {
          xit('deserialize', () => {
                    const json = {value: true};

                    const bw = ApiService.deserialize(json, BooleanWrapper);

                    expect(bw).toBeDefined('after deserialize the object should be defined');
                    expect(bw instanceof BooleanWrapper)
                        .toBeTruthy('after deserialize the new instance should be instance of BooleanWrapper');

                    expect(bw.value).toEqual(json.value);

                    const newValue = false;

                    bw.value = newValue;

                  let result: BooleanWrapper = new BooleanWrapper();
                    result.value = newValue;

                    expect(ApiService.deserialize(JSON.stringify(bw), BooleanWrapper)).toEqual(
                        result,
                        'after serialize the new value should be present',
                    );

                });


        });
    });

});
