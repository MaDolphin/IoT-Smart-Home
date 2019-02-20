import { DropDownControl } from '@shared/architecture/forms/controls/drop-down.control';
import { FormControlHelper } from '@testutils/form-control';
import { loadArchitectureModule } from '@testutils/utilities';

describe('Architecture', () => {
  describe('Forms', () => {
    describe('Controls', () => {
      describe('Drop Down Control', () => {

        beforeEach(loadArchitectureModule);

        it(
          'onChange',
          () => FormControlHelper.ChangeTest(new DropDownControl(), 'DFG', 'DEF'),
        );

        it(
          'required without additional validators',
          () => FormControlHelper.RequiredTest(new DropDownControl(), 'DFG'),
        );

        it(
          'required with additional validators',
          () => FormControlHelper.RequiredTest(new DropDownControl('null', [], 'null', () => null), 'DFG'),
        );

      });
    });
  });

});
