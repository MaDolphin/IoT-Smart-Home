/* tslint:disable:no-unused-variable */

import { TypedJSON } from '@upe/typedjson';
import 'rxjs/add/operator/toPromise';
import { StringWrapper } from '@shared/architecture/services/primitive-wrappers/string-wrapper';

describe('Architecture', () => {
  describe('Services', () => {
    describe('Primitive Wrappers', () => {

      describe('String Wrapper', () => {

        it('deserialize / serialize', () => {

          const json = {value: 'mystring'};

          const sw = TypedJSON.parse(JSON.stringify(json), StringWrapper);

          expect(sw).toBeDefined('after deserialize the object schould be defined');
          expect(sw instanceof StringWrapper)
            .toBeTruthy('after deserialize the new instance thould be instance of StringWrapper');

          expect(sw.value).toEqual(json.value);

          const newValue = 'newVal';

          sw.value = newValue;

          expect(JSON.parse(TypedJSON.stringify(sw))).toEqual(
            {value: newValue},
            'after serialize the new value should be present',
          );

        });

        it('member check', () => {
              expect(Object.getOwnPropertyNames(new StringWrapper())).toEqual([
                  '_value'
              ]);
          });

      });
    });
  });

});
