import { ReflectiveInjector } from '@angular/core';
import { AbstractControl, FormArray } from '@angular/forms';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subscription } from 'rxjs/Subscription';
import { SelectFormControl } from '@shared/generic-form/controls/select.form-control';
import { validate } from '@shared/generic-form/validator';
import {
  ADD_IF,
  AUTOCOMPLETE,
  CONTEXT_TEST,
  CONTROL_NAME,
  DEFAULT,
  DEFAULT_IF,
  DERIVED,
  DESIGN_TYPE,
  DISABLED,
  DISABLED_IF,
  FORCE_TYPE,
  LABEL,
  LABEL_IF,
  MAX,
  MIN,
  MULTI_SUB_GROUP,
  MULTI_SUB_GROUP_NAME_MAPPING,
  MULTI_SUB_GROUPS_PROPERTY,
  ON_CHANGE,
  ON_VALID_CHANGE,
  OPTIONS,
  OPTIONS_BEHAVIOR_SUBJECT,
  OPTIONS_IF,
  PLACEHOLDER,
  PLACEHOLDER_IF,
  READONLY,
  READONLY_IF,
  REMOVE_IF,
  REQUIRED,
  REQUIRED_IF,
  SUB_GROUP,
  SUB_GROUPS_PROPERTY,
  VALIDATOR_CONTROL_FUNCTIONS,
} from '../../config';
import { GenericFormControl, GenericFormGroup, ValidatorFn } from '@shared/generic-form/generic-form';
import { ContextDeps } from '../context-test';
import { IAddIf } from '../if';
import { ISelectOptions } from '../options';
import { RequiredValidator } from '../required';
import { extractPrototype } from './utils';
import { AutoCompleteFormControl } from '@shared/generic-form/controls/auto-complete.form-control';
import { RadioFormControl } from '@shared/generic-form/controls/radio.form-control';

if (!Object['entries'])
  Object['entries'] = function (obj) {
    let ownProps = Object.keys( obj ),
        i = ownProps.length,
        resArray = new Array(i); // preallocate the Array
    while (i--)
      resArray[i] = [ownProps[i], obj[ownProps[i]]];

    return resArray;
  };

export function init<T extends GenericFormGroup<any>>(this: T, _model?: any, readonly: boolean = false): T {

  this.logger.info('init');

  if (this.parent && _model === undefined) {
    throw new Error('model lost parent reference');
  }

  this.model = _model === undefined || readonly ? {} : _model;

  const propertyKeys = Object.getOwnPropertyNames(this);

  const prototype = extractPrototype(this);

  const controlNamePropertyKeyMapping = {};

  for (const pk of propertyKeys
    .filter((propertyKey: string) => Reflect.hasMetadata(CONTROL_NAME, prototype, propertyKey))) {
    controlNamePropertyKeyMapping[Reflect.getMetadata(CONTROL_NAME, prototype, pk)] = pk;
  }

  const controlNames = Object.keys(controlNamePropertyKeyMapping);

  // holds a control name to control instance mapping
  const controlsByName: { [key: string]: GenericFormControl<any> | GenericFormGroup<any> } = {};

  // init the model for each control name
  for (const controlName of controlNames.filter((cn) => !this.model.hasOwnProperty(cn))) {
    // with null
    if (readonly && _model[controlName] && !Reflect.hasMetadata(DERIVED, prototype, controlNamePropertyKeyMapping[controlName])) {
      this.model[controlName] = _model[controlName];
    } else {
      this.model[controlName] = null;
    }
  }

  this.logger.info('init model object');

  const addIfs: IAddIf[]    = [];
  const removeIfs: IAddIf[] = []; // TODO : rename IAddIf

  // region construct controls
  for (const propertyKey of propertyKeys.filter((pk) => Reflect.hasMetadata(CONTROL_NAME, prototype, pk))) {

    // get the control name for this property
    const controlName = Reflect.getMetadata(CONTROL_NAME, prototype, propertyKey);

    // if the init value is not set
    if (!this.model.hasOwnProperty(controlName)) {
      // init with null
      this.model[controlName] = Reflect.getMetadata(DEFAULT, prototype, propertyKey) || null;
    }

    // get the constructor for this control
    const controlConstructor = Reflect.getMetadata(DESIGN_TYPE, prototype, propertyKey);

    // region create control instance and set the class member for this control

    if (controlConstructor === undefined) {
      throw new Error('Could not create control. The constructor ist undefined');
    }

    let injector;

    try {
      injector = ReflectiveInjector.resolveAndCreate([controlConstructor], this.injector);
    } catch (e) {
      throw new Error(`could not resolve control '${controlName}': ` + e);
    }

    // create sub group instance
    this[propertyKey] = injector.get(controlConstructor);

    // endregion

    const currentControl: GenericFormControl<any> = this[propertyKey];

    if (!(currentControl instanceof AbstractControl)) {
      throw new Error(`Control instance '${propertyKey}' is not instance of AbstractControl`);
    }

    currentControl.setParent(this);

    // region add options form select or radio control
    if (Reflect.hasMetadata(OPTIONS, prototype, propertyKey)) {
      if (!(currentControl instanceof SelectFormControl || currentControl instanceof RadioFormControl)) {
        throw new Error(`Can not use the options decorator on non SelectFormControl or RadioFormControl controls, '${typeof currentControl}'`);
      }
      currentControl.addOptions(Reflect.getMetadata(OPTIONS, prototype, propertyKey));
    }
    // endregion

    // region add auto complete option form auto complete
    if (Reflect.hasMetadata(AUTOCOMPLETE, prototype, propertyKey)) {
      if (!(currentControl instanceof AutoCompleteFormControl)) {
        throw new Error(`Can not use the autocomplete decorator on non AutoCompleteFormControl controls, '${typeof currentControl}'`);
      }
      currentControl.setOptions(Reflect.getMetadata(AUTOCOMPLETE, prototype, propertyKey));
    }
    // endregion

    if (!Reflect.getMetadata(DERIVED, prototype, propertyKey)) {
      currentControl.setModelValue(this.model[controlName]);
      if (this.model[controlName]) {
        currentControl.markAsDirty({onlySelf: true});
        currentControl.markAsTouched({onlySelf: true});
      }
    }

    // region required validator
    // if this controller is marked as required
    if (Reflect.hasMetadata(REQUIRED, prototype, propertyKey)) {
      // add a required validator
      currentControl.addValidatorFunction(RequiredValidator);
      currentControl.isRequired = true;
    }
    // endregion

    // region max and min validators
    if (Reflect.hasMetadata(MAX, prototype, propertyKey)) {
      const max = Number(Reflect.getMetadata(MAX, prototype, propertyKey));
      const forceText = Reflect.getMetadata(MAX + FORCE_TYPE, prototype, propertyKey);
      if (isNaN(max)) {
        throw new Error(`@Max requires a number, '${max}'`);
      }
      // add a max validator
      currentControl.addValidatorFunction(function (this: GenericFormControl<any>) {
        validate(this).max(max, forceText);
      });
    }
    if (Reflect.hasMetadata(MIN, prototype, propertyKey)) {
      const min = Number(Reflect.getMetadata(MIN, prototype, propertyKey));
      const forceText = Reflect.getMetadata(MIN + FORCE_TYPE, prototype, propertyKey);
      if (isNaN(min)) {
        throw new Error(`@Min requires a number, '${min}'`);
      }
      // add a max validator
      currentControl.addValidatorFunction(function (this: GenericFormControl<any>) {
        validate(this).min(min, forceText);
      });
    }
    // endregion

    // set control name
    currentControl.name = controlName;
    currentControl.propertyKey = propertyKey;

    // map the form control name to the controlName (build performance)
    controlsByName[controlName] = currentControl;

    // region disabled
    // if this control is marked as disabled
    if (Reflect.hasMetadata(DISABLED, prototype, propertyKey)) {
      // disable this control
      currentControl.isDisabled = true;
    }
    // endregion

    // region readonly
    // if this control is marked as readonly
    if (Reflect.hasMetadata(READONLY, prototype, propertyKey)) {
      // set the isReadonly property
      // the view needs to listen on the property manuel
      currentControl.isReadonly = true;
    }
    // endregion

    if (Reflect.hasMetadata(LABEL, prototype, propertyKey)) {
      currentControl.label = Reflect.getMetadata(LABEL, prototype, propertyKey);
    } else {

    }

    if (Reflect.hasMetadata(PLACEHOLDER, prototype, propertyKey)) {
      currentControl.placeholder = Reflect.getMetadata(PLACEHOLDER, prototype, propertyKey);
    }

    if (!Reflect.getMetadata(DERIVED, prototype, propertyKey)) {
      // update the model object if control is valid
      currentControl.valueChanges
        .skipWhile(() => currentControl.invalid)
        .subscribe(() => this.model[controlName] = this[propertyKey].getModelValue());
      // update the model object initially as well
      this.model[controlName] = this[propertyKey].getModelValue();
    }
  }
  // endregion

  this.logger.info('construct controls');

  // region construct sub groups
  for (const propertyKey of propertyKeys.filter((pk) => Reflect.hasMetadata(SUB_GROUP, prototype, pk))) {

    if (!this[SUB_GROUPS_PROPERTY]) {
      this[SUB_GROUPS_PROPERTY] = [];
    }

    // store the sub group property key tobe available on submit
    this[SUB_GROUPS_PROPERTY].push(propertyKey);

    const subGroupName = Reflect.getMetadata(SUB_GROUP, prototype, propertyKey);

    // if the init value is not set
    if (!this.model.hasOwnProperty(subGroupName)) {
      // init with null
      this.model[subGroupName] = {};
    }

    // get the constructor for this control
    const subGroupConstructor = Reflect.getMetadata(DESIGN_TYPE, prototype, propertyKey);

    if (subGroupConstructor === undefined) {
      throw new Error('Could not create new sub group. The constructor ist undefined');
    }

    let injector;

    try {
      injector = ReflectiveInjector.resolveAndCreate([subGroupConstructor], this.injector);
    } catch (e) {
      throw new Error(`could not resolve multi sub form group '${name}': ` + e);
    }

    // create sub group instance
    const subGroup = injector.get(subGroupConstructor);

    if (!(subGroup instanceof GenericFormGroup)) {
      throw new Error(`SubGroup instance '${propertyKey}' is not instance of GenericFormGroup`);
    }

    subGroup.setParent(this);

    subGroup.onAddControl.subscribe((c) => this.onAddControl.next(c));
    subGroup.onRemoveControl.subscribe((c) => this.onRemoveControl.next(c));

    // create control instance and set the class member for this control
    this[propertyKey] = subGroup;

    const currentSubGroup: GenericFormGroup<any> = this[propertyKey];

    if (!(currentSubGroup instanceof GenericFormGroup)) {
      throw new Error(`SubGroup instance '${propertyKey}' is not instance of AbstractControl`);
    }

    // set control name
    currentSubGroup.name = subGroupName;

    // map the form control name to the controlName (build performance)
    controlsByName[subGroupName] = currentSubGroup;

    this.addControl(subGroupName, subGroup);

    // init form group
    currentSubGroup.init(this.model[subGroupName]);

  }
  // endregion

  this.logger.info('construct sub groups');

  // region load and construct mutli sub groups
  for (const propertyKey of propertyKeys.filter((pk) => Reflect.hasMetadata(MULTI_SUB_GROUP, prototype, pk))) {

    if (!this[MULTI_SUB_GROUPS_PROPERTY]) {
      this[MULTI_SUB_GROUPS_PROPERTY] = [];
    }

    // store the multi sub group property key tobe available on submit
    this[MULTI_SUB_GROUPS_PROPERTY].push(propertyKey);

    // get the mutli sub group name for this property
    const multiSubGroupName = Reflect.getMetadata(MULTI_SUB_GROUP, prototype, propertyKey);

    // TODO : dont allow custom multi sub group names
    if (multiSubGroupName !== propertyKey) {
      if (!this[MULTI_SUB_GROUP_NAME_MAPPING]) {
        this[MULTI_SUB_GROUP_NAME_MAPPING] = {};
      }
      this[MULTI_SUB_GROUP_NAME_MAPPING][multiSubGroupName] = propertyKey;
    }

    // if the init value is not set
    if (!this.model.hasOwnProperty(multiSubGroupName)) {
      // init with an empty array
      this.model[multiSubGroupName] = [];
    }

    this[propertyKey] = [];

    const formArray = new FormArray([]);
    formArray.setParent(this);

    // add form array to store the multi subGroups
    this.addControl(multiSubGroupName, formArray);

    // create foreach sub group that is passed by the model an instance
    for (const subGroupModel of this.model[multiSubGroupName]) {
      this.createMultiSubGroup(multiSubGroupName, subGroupModel);
    }

  }
  // endregion

  this.logger.info('load and construct mutli sub groups');

  for (const propertyKey of propertyKeys.filter((pk) => Reflect.hasMetadata(CONTROL_NAME, prototype, pk))) {

    // region load if-hooks for controls
    // get the control name for this property
    const controlName = Reflect.getMetadata(CONTROL_NAME, prototype, propertyKey);

    const controlValidators: ValidatorFn[] = [];

    // region label
    if (Reflect.hasMetadata(LABEL_IF, prototype, propertyKey)) {
      const hook                  = Reflect.getMetadata(LABEL_IF, prototype, propertyKey);
      const changeTriggerControls = Object.keys(controlsByName)
        .filter((name) => hook.changeTrigger.indexOf(name) !== -1)
        .map((name) => {
          if (!controlsByName[name]) {
            throw new Error(`Control with name '${name}' not found`);
          }
          return controlsByName[name];
        });
      const handelHook            = () => {
        const label = hook.fnc.apply(this[propertyKey], changeTriggerControls);
        if (typeof label !== 'string') {
          throw new Error(`new label '${label}' is not type of string`);
        }
        this[propertyKey].label = label;
      };
      for (const changeName of hook.changeTrigger.concat([controlName])) {
        if (!controlsByName[changeName]) {
          throw new Error(`Control with name '${changeName}' not found`)
        }
        controlsByName[changeName]
          .valueChanges
          .skipWhile((value) => value === null)
          .subscribe(() => handelHook());
      }
      handelHook();
    }
    // endregion

    // region placeholder
    if (Reflect.hasMetadata(PLACEHOLDER_IF, prototype, propertyKey)) {
      const hook                  = Reflect.getMetadata(PLACEHOLDER_IF, prototype, propertyKey);
      const changeTriggerControls = Object.keys(controlsByName)
        .filter((name) => hook.changeTrigger.indexOf(name) !== -1)
        .map((name) => {
          if (!controlsByName[name]) {
            throw new Error(`Control with name '${name}' not found`);
          }
          return controlsByName[name];
        });
      const handelHook            = () => {
        const placeholder = hook.fnc.apply(this[propertyKey], changeTriggerControls);
        if (typeof placeholder !== 'string') {
          throw new Error(`new placeholder '${placeholder}' is not type of string`);
        }
        this[propertyKey].placeholder = placeholder;
      };
      for (const changeName of hook.changeTrigger.concat([controlName])) {
        if (!controlsByName[changeName]) {
          throw new Error(`Control with name '${changeName}' not found`)
        }
        controlsByName[changeName]
          .valueChanges
          .skipWhile((value) => value === null)
          .subscribe(() => handelHook());
      }
      handelHook();
    }
    // endregion

    // region options
    if (Reflect.hasMetadata(OPTIONS_IF, prototype, propertyKey)) {
      if (!(this[propertyKey] instanceof SelectFormControl)) {
        throw new Error(`Can not use the options decorator on non SelectFormControl controls, '${typeof this[propertyKey]}'`);
      }
      const hook                  = Reflect.getMetadata(OPTIONS_IF, prototype, propertyKey);
      const changeTriggerControls = Object.keys(controlsByName)
        .filter((name) => hook.changeTrigger.indexOf(name) !== -1)
        .map((name) => {
          if (!controlsByName[name]) {
            throw new Error(`Control with name '${name}' not found`);
          }
          return controlsByName[name];
        });
      // store if the hook is once called
      let called = false;
      const handelHook            = () => {
        if (called && hook.options.triggerOnce) {
          return; // skip hook if already called and triggerOnce option is set
        }
        called = true;
        const options: string[] = hook.fnc.apply(this[propertyKey], changeTriggerControls);
        if (!Array.isArray(options) || options.some((option) => typeof option !== 'string' && (!option['value'] || !option['option']))) {
          throw new Error(`new options '${options}' is not type of string array`);
        }
        this[propertyKey].setOptions(options);
      };
      const changeNames = hook.changeTrigger;
      if (!hook.options.skipSelf) {
        changeNames.push(controlName);
      }
      for (const changeName of changeNames) {
        if (!controlsByName[changeName]) {
          throw new Error(`Control with name '${changeName}' not found`)
        }
        controlsByName[changeName]
          .valueChanges
          .skipWhile((value) => value === null)
          .subscribe(() => handelHook());
      }
      if (!hook.options.skipInitCall) {
        handelHook();
        called = false; // init call is not a trigger call
      }
    }
    // endregion

    // region default
    if (Reflect.hasMetadata(DEFAULT_IF, prototype, propertyKey)) {
      const hook                  = Reflect.getMetadata(DEFAULT_IF, prototype, propertyKey);
      const changeTriggerControls = Object.keys(controlsByName)
        .filter((name) => hook.changeTrigger.indexOf(name) !== -1)
        .map((name) => {
          if (!controlsByName[name]) {
            throw new Error(`Control with name '${name}' not found`);
          }
          return controlsByName[name];
        });
      if (!this[propertyKey].value) {
        this[propertyKey].setValue(hook.fnc.apply(this[propertyKey], changeTriggerControls));
      }
    }
    // endregion

    // region disabled
    if (Reflect.hasMetadata(DISABLED_IF, prototype, propertyKey)) {
      const hook                  = Reflect.getMetadata(DISABLED_IF, prototype, propertyKey);
      const changeTriggerControls = Object.keys(controlsByName)
        .filter((name) => hook.changeTrigger.indexOf(name) !== -1)
        .map((name) => {
          if (!controlsByName[name]) {
            throw new Error(`Control with name '${name}' not found`);
          }
          return controlsByName[name];
        });
      const handelHook            = () => {
        try {
          hook.fnc.apply(this[propertyKey], changeTriggerControls);
          this[propertyKey].isDisabled = true;
        } catch (e) {
          this[propertyKey].isDisabled = false;
        }
      };
      for (const changeName of hook.changeTrigger.concat([controlName])) {
        if (!controlsByName[changeName]) {
          throw new Error(`Control with name '${changeName}' not found`)
        }
        controlsByName[changeName]
          .valueChanges
          .skipWhile((value) => value === null)
          .subscribe(() => handelHook());
      }
      handelHook();
    }
    // endregion

    // region readonly
    if (Reflect.hasMetadata(READONLY_IF, prototype, propertyKey)) {
      const hook                  = Reflect.getMetadata(READONLY_IF, prototype, propertyKey);
      const changeTriggerControls = Object.keys(controlsByName)
        .filter((name) => hook.changeTrigger.indexOf(name) !== -1)
        .map((name) => {
          if (!controlsByName[name]) {
            throw new Error(`Control with name '${name}' not found`);
          }
          return controlsByName[name];
        });
      const handelHook            = () => {
        try {
          hook.fnc.apply(this[propertyKey], changeTriggerControls);
          this[propertyKey].isReadonly = true;
        } catch (e) {
          this[propertyKey].isReadonly = false;
        }
      };
      for (const changeName of hook.changeTrigger.concat([controlName])) {
        if (!controlsByName[changeName]) {
          throw new Error(`Control with name '${changeName}' not found`)
        }
        controlsByName[changeName]
          .valueChanges
          .skipWhile((value) => value === null)
          .subscribe(() => handelHook());
      }
      handelHook();
    }
    // endregion

    // region required
    if (Reflect.hasMetadata(REQUIRED_IF, prototype, propertyKey)) {
      const hook                  = Reflect.getMetadata(REQUIRED_IF, prototype, propertyKey);
      const changeTriggerControls = Object.keys(controlsByName)
        .filter((name) => hook.changeTrigger.indexOf(name) !== -1)
        .map((name) => {
          if (!controlsByName[name]) {
            throw new Error(`Control with name '${name}' not found`);
          }
          return controlsByName[name];
        });
      const handelHook            = () => {
        try {
          hook.fnc.apply(this[propertyKey], changeTriggerControls);
          this[propertyKey].isRequired = true;
          this[propertyKey].addValidatorFunction(RequiredValidator);
        } catch (e) {
          this[propertyKey].isRequired = false;
          this[propertyKey].removeValidatorFunction(RequiredValidator);
        }
      };
      for (const changeName of hook.changeTrigger.concat([controlName])) {
        if (!controlsByName[changeName]) {
          throw new Error(`Control with name '${changeName}' not found`)
        }
        controlsByName[changeName]
          .valueChanges
          .skipWhile((value) => value === null)
          .subscribe(() => handelHook());
      }
      handelHook();
    }
    // endregion

    // region add control
    // if this control should only be added if ...
    if (Reflect.hasMetadata(ADD_IF, prototype, propertyKey)) {
      const addIfObj: IAddIf = {
        reflect:     Reflect.getMetadata(ADD_IF, prototype, propertyKey),
        controlName: controlName,
        propertyKey: propertyKey,
      };
      // add to array to be used on addIfForAll
      // so that a control will be added after formGroup is inited
      addIfs.push(addIfObj);
      for (const changeName of addIfObj.reflect.changeTrigger.concat([controlName])) {
        if (!controlsByName[changeName]) {
          throw new Error(`Control with name '${changeName}' not found`)
        }
        controlsByName[changeName].valueChanges.skipWhile((value) => value === null)
        // skip if control is already added
          .skipWhile(() => this.contains(controlName))
          .subscribe(() => this.addIf(addIfObj));
      }
    } else {
      // check : remove if when all controls are added and init
      this.addControl(controlName, this[propertyKey]);
    }
    // endregion

    // region remove control
    // if this control should only be added if ...
    if (Reflect.hasMetadata(REMOVE_IF, prototype, propertyKey)) {
      const removeIfObj: IAddIf = {
        reflect:     Reflect.getMetadata(REMOVE_IF, prototype, propertyKey),
        controlName: controlName,
        propertyKey: propertyKey,
      };
      // add to array to be used on addIfForAll
      // so that a control will be added after formGroup is inited
      removeIfs.push(removeIfObj);
      for (const changeName of removeIfObj.reflect.changeTrigger.concat([controlName])) {
        if (!controlsByName[changeName]) {
          throw new Error(`Control with name '${changeName}' not found`)
        }
        controlsByName[changeName].valueChanges.skipWhile((value) => value === null)
        // skip if control is not added
          .skipWhile(() => this.contains(controlName) === false)
          .subscribe(() => this.removeIf(removeIfObj));
      }
    }
    // endregion

    // endregion

    // region load behavior subjects for controls

    // region options
    if (Reflect.hasMetadata(OPTIONS_BEHAVIOR_SUBJECT, prototype, propertyKey)) {
      const optionsConfig: BehaviorSubject<Array<(string | ISelectOptions)>> | string = Reflect.getMetadata(OPTIONS_BEHAVIOR_SUBJECT, prototype, propertyKey);
      if (!optionsConfig) {
        throw new Error('BehaviorSubject options object is not defined');
      }
      let optionsBehaviorSubject: BehaviorSubject<Array<(string | ISelectOptions)>>;
      if (optionsConfig instanceof BehaviorSubject) {
        optionsBehaviorSubject = optionsConfig;
      } else if (this[optionsConfig] instanceof BehaviorSubject) {
        optionsBehaviorSubject = this[optionsConfig];
      } else {
        throw new Error('only a BehaviorSubject instance or a propertyKey that points to a BehaviorSubject instance are allowed')
      }
      if (optionsBehaviorSubject.value && Array.isArray(optionsBehaviorSubject.value)) {
        this[propertyKey].setOptions(optionsBehaviorSubject.value);
      }
      optionsBehaviorSubject.subscribe((options) => {
        if (options && Array.isArray(options)) {
          this[propertyKey].setOptions(options);
        } else {
          throw new Error('BehaviorSubject options dont returns an array');
        }
      });
    }
    // endregion

    // endregion

    // region add Validators
    const validatorFunctions = Reflect.getMetadata(VALIDATOR_CONTROL_FUNCTIONS, prototype, propertyKey);
    if (validatorFunctions && validatorFunctions.length) {
      for (const validatorFunction of validatorFunctions) {
        if (validatorFunction.changeTrigger.length) {
          const changeTriggerControls = validatorFunction.changeTrigger
            .map((cn: string) => {
              if (!controlsByName[cn]) {
                throw new Error(`Control with name '${cn}' not found`);
              }
              return controlsByName[cn]
            });

          for (const changeName of validatorFunction.changeTrigger) {
            if (!controlsByName[changeName]) {
              throw new Error(`Control with name '${changeName}' not found`)
            }
            let preValue: any = undefined;
            controlsByName[changeName]
              .valueChanges
              .skipWhile((value) => value === preValue)
              .subscribe((value) => {
                preValue = value;
                this[propertyKey]['_runValidator']();
              });
          }
          controlValidators.push(function (this: GenericFormControl<any>) {
            validatorFunction.fnc.apply(this, changeTriggerControls);
          });
        } else {
          controlValidators.push(validatorFunction.fnc);
        }
      }
    }
    // endregion

    // region add context test
    const contextTests = Reflect.getMetadata(CONTEXT_TEST, prototype, propertyKey);
    if (contextTests && contextTests.length) {

      const root = this.root;
      if (!(root instanceof GenericFormGroup)) {
        throw new Error('Root Form Group is not defined');
      }

      for (const contextTest of contextTests) {
        if (contextTest.changeTrigger.length) {
          let changeTriggerControls: GenericFormControl<any>[] = null;
          let subs: Subscription[]                             = [];
          let pres: { [key: string]: any }                     = {};

          const loadChangeTriggerControls = () => {

            subs.forEach((sub: Subscription) => sub.unsubscribe());
            subs = [];

            changeTriggerControls = contextTest
              .changeTrigger
              .map((ct: ContextDeps) => ct.apply(this[propertyKey], [root]))
              .map((r: GenericFormControl<any> | Array<GenericFormControl<any>>) => {
                if (!Array.isArray(r)) {
                  return [r];
                }
                return r;
              })
              .reduce((a: Array<GenericFormGroup<any>>, b: Array<GenericFormGroup<any>>) => a.concat(b), []);

            if (changeTriggerControls.some((c) => !c)) {
              console.warn('loadChangeTriggerControls - Some controls are undefined or null');
              changeTriggerControls = changeTriggerControls.filter((c) => !!c);
            }

            changeTriggerControls.forEach((control: GenericFormControl<any>) => {
              if (pres[control.name] === undefined) {
                pres[control.name] = control.value;
              }
              subs.push(control
                .valueChanges
                .skipWhile(v => v === pres[control.name])
                .subscribe((v) => {
                  pres[control.name] = v;
                  this[propertyKey].validate();
                  loadChangeTriggerControls();
                }));
            });
          };

          loadChangeTriggerControls();

          root.onAddControl.subscribe(() => loadChangeTriggerControls());
          root.onRemoveControl.subscribe(() => loadChangeTriggerControls());

          // separation is required

          controlValidators.push(function (this: GenericFormControl<any>) {
            contextTest.fnc.apply(this, changeTriggerControls);
          })

        } else {
          throw new Error('a context test requires changeTriggers')
        }
      }
    }
    // endregion

    this[propertyKey].addValidatorFunction(...controlValidators);
  }

  this.logger.info('apply controls hooks');

  // region load if-hooks for sub groups
  for (const propertyKey of propertyKeys.filter((pk) => Reflect.hasMetadata(SUB_GROUP, prototype, pk))) {

    const subGroupName = Reflect.getMetadata(SUB_GROUP, prototype, propertyKey);

    // region add control
    // if this control should only be added if ...
    if (Reflect.hasMetadata(ADD_IF, prototype, propertyKey)) {
      const addIfObj: IAddIf = {
        reflect:     Reflect.getMetadata(ADD_IF, prototype, propertyKey),
        controlName: subGroupName,
        propertyKey: propertyKey,
      };
      // add to array to be used on addIfForAll
      // so that a control will be added after formGroup is inited
      addIfs.push(addIfObj);
      for (const changeName of addIfObj.reflect.changeTrigger.concat([subGroupName])) {
        if (!controlsByName[changeName]) {
          throw new Error(`Control with name '${changeName}' not found`)
        }
        controlsByName[changeName].valueChanges.skipWhile((value) => value === null)
        // skip if control is already added
          .skipWhile(() => this.contains(subGroupName))
          .subscribe(() => this.addIf(addIfObj));
      }
    } else {
      // check : remove if when all controls are added and init
      this.addControl(subGroupName, this[propertyKey]);
    }
    // endregion

    // region remove control
    // if this control should only be added if ...
    if (Reflect.hasMetadata(REMOVE_IF, prototype, propertyKey)) {
      const removeIfObj: IAddIf = {
        reflect:     Reflect.getMetadata(REMOVE_IF, prototype, propertyKey),
        controlName: subGroupName,
        propertyKey: propertyKey,
      };
      // add to array to be used on addIfForAll
      // so that a control will be added after formGroup is inited
      removeIfs.push(removeIfObj);
      for (const changeName of removeIfObj.reflect.changeTrigger.concat([subGroupName])) {
        if (!controlsByName[changeName]) {
          throw new Error(`Control with name '${changeName}' not found`)
        }
        controlsByName[changeName].valueChanges.skipWhile((value) => value === null)
        // skip if control is not added
          .skipWhile(() => this.contains(subGroupName) === false)
          .subscribe(() => this.removeIf(removeIfObj));
      }
    }
    // endregion

  }
  // endregion

  this.logger.info('load if-hooks for sub groups');

  // region add change hooks

  // region on change
  const onChangeFunctions = Reflect.getMetadata(ON_CHANGE, prototype);

  if (onChangeFunctions) {
    // for (const [controlName, onChangeFncs] of Object.entries(onChangeFunctions)) {
    for (const [controlName, onChangeObject] of (Object as any).entries(onChangeFunctions)) {
      if (!controlsByName[controlName]) {
        throw new Error(`Control with name '${controlName}' not found`)
      }
      controlsByName[controlName]
        .valueChanges
        .skipWhile(() => this['_initialized'] === false)
        .subscribe((value: any) => {
        for (const obj of onChangeObject) {
          obj.fnc.apply(this);
        }
      });
      // check if any on change hook should be called on init
      for (const obj of onChangeObject.filter(o => o.onInit)) {
        obj.fnc.apply(this);
      }
    }
  }
  // endregion

  // region on validchange
  const onValidChangeFunctions = Reflect.getMetadata(ON_VALID_CHANGE, prototype);

  if (onValidChangeFunctions) {
    // for (const [controlName, onChangeFncs] of Object.entries(onChangeFunctions)) {
    for (const [controlName, onValidChangeObject] of (Object as any).entries(onValidChangeFunctions)) {
      if (!controlsByName[controlName]) {
        throw new Error(`Control with name '${controlName}' not found`)
      }
      controlsByName[controlName].validValueChanges.subscribe((value: any) => {
        for (const obj of onValidChangeObject) {
          obj.fnc.apply(this);
        }
      });
      // check if any on change hook should be called on init
      for (const obj of onValidChangeObject.filter(o => o.onInit)) {
        obj.fnc.apply(this);
      }
    }
  }
  // endregion

  // endregion add change hooks

  // add if for all
  for (const addIfObj of addIfs) {
    this.addIf(addIfObj);
  }
  for (const removeIfObj of removeIfs) {
    this.removeIf(removeIfObj);
  }

  // TODO : add update system for models
  // create model object watcher
  // this.model = Watch(model);

  this['_initialized'] = true;

  return this;
}
