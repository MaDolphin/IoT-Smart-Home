import { TypedJSON } from '@upe/typedjson';
import { BooleanWrapper } from '@shared/architecture/services/primitive-wrappers/boolean-wrapper';

describe('Architecture', () => {
  describe('Services', () => {
    describe('Primitive Wrappers', () => {

      describe('String Wrapper', () => {

        it('deserialize / serialize', () => {

          const json = {value: true};

          const bw = TypedJSON.parse(JSON.stringify(json), BooleanWrapper);

          expect(bw).toBeDefined('after deserialize the object schould be defined');
          expect(bw instanceof BooleanWrapper)
            .toBeTruthy('after deserialize the new instance thould be instance of StringWrapper');

          expect(bw.value).toEqual(json.value);

          const newValue = false;

          bw.value = newValue;

          expect(JSON.parse(TypedJSON.stringify(bw))).toEqual(
            {value: newValue},
            'after serialize the new value should be present',
          );

        });

      it('member check', () => {
          expect(Object.getOwnPropertyNames(new BooleanWrapper())).toEqual([
              '_value'
          ]);
        });

      });
    });
  });

});
