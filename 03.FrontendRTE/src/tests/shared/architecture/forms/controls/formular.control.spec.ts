/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import 'rxjs/add/observable/timer';
import 'rxjs/add/operator/toPromise';
import { FormularControl } from '@shared/architecture/forms/controls/formular.control';
import { FormControlHelper } from '@testutils/form-control';
import { loadArchitectureModule } from '@testutils/utilities';

describe('Architecture', () => {
  describe('Forms', () => {
    describe('Controls', () => {
      describe('Formular Control', () => {

        beforeEach(loadArchitectureModule);

        it(
          'onChange',
          () => FormControlHelper.ChangeTest(new FormularControl(), 'valid', 'invalid'),
        );

        it(
          'required without additional validators',
          () => FormControlHelper.RequiredTest(new FormularControl(), 'input'),
        );

        it(
          'required with additional validators',
          () => FormControlHelper.RequiredTest(new FormularControl(null, () => null), 'input'),
        );

      });

    });
  });
});
