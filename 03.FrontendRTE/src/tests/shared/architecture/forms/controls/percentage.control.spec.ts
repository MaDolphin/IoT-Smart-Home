import { PercentageControl } from '@shared/architecture/forms/controls/percentage.control';
import { FormControlHelper } from '@testutils/form-control';
import { loadArchitectureModule } from '@testutils/utilities';

describe('Architecture', () => {
  describe('Forms', () => {
    describe('Controls', () => {
      describe('Percentage Control', () => {

        beforeEach(loadArchitectureModule);

        it(
          'onChange',
          () => FormControlHelper.ChangeTest(new PercentageControl(null), '10', '110'),
        );

        it('handelChange', () => {
          let control = new PercentageControl(null);
          control.onValidChange.subscribe(value => expect(value).toEqual(12.12));
          control.setValue('12,12');
        });

        it(
          'required without additional validators',
          () => FormControlHelper.RequiredTest(new PercentageControl(null), '10'),
        );

        it(
          'required with additional validators',
          () => FormControlHelper.RequiredTest(new PercentageControl(null, () => null), '10'),
        );

        it(
          'default Validator checks',
          () => FormControlHelper.ValidatorTest(
            new PercentageControl(null),
            ['10', '001,0101', '100.', '.', '0', '.101'],
            ['1.1.', 'a', '100.00001', '-1', '0x10', '  ']
          )
        );
      });

    });
  });

});