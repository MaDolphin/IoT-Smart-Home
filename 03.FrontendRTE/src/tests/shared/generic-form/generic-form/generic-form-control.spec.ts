import { expect } from 'chai';
import { GenericFormControl } from '@shared/generic-form/generic-form';

describe('Generic Form', () => {

  describe('Form Control', () => {

    let control: GenericFormControl<any>;

    beforeEach(() => {
      control = new GenericFormControl();
    });

    it('isDisabled', () => {

      expect(control.isDisabled, 'default state').false;

      control.isDisabled = false;

      expect(control.isDisabled, 'false -> false').false;

      control.isDisabled = true;

      expect(control.isDisabled, 'false -> true').true;

      control.isDisabled = true;

      expect(control.isDisabled, 'true -> true').true;

      control.isDisabled = false;

      expect(control.isDisabled, 'true -> false').false;


    });

  });

});
