import {A, cleanup, clickConfirmOnDialog} from '../../utilits/helper.e2e-util';
import {setup} from '../../utilits/login.e2e-util';
import {promise} from 'selenium-webdriver';
import {navigateToPersonal, navigateToPersonalExpert} from '../../utilits/navigation.e2e-util';
import {PersonalExpertPo} from '../../pageobjects/personal/personal.expert.po';
import {PersonalOverviewPo} from '../../pageobjects/personal/personal.overview.po';
import {browser} from 'protractor';
import {Commands} from '../../utilits/commands.e2e-util';

describe('Create Person', function () {

  beforeEach(setup);
  afterEach(cleanup);

  it('Create simple Person', A(async () => {
    return navigateToPersonalExpert().then(() => {
      return browser.sleep(500)
    }).then(() => {
      return browser.getCurrentUrl();
    }).then((url) => {
      console.log(url);
      return promise.all([
        PersonalExpertPo.setVorname('Foo'),
        PersonalExpertPo.setNachname('Bar'),
        PersonalExpertPo.setPersonalnummer('555333555'),
        PersonalExpertPo.setGeburtsdatum('01.03.1937'),
        PersonalExpertPo.setBeschaeftigungsbeginn('01.01.1990')
      ])
    }).then(() => {
      return PersonalExpertPo.clickOnSubmission();
    }).then(() => {
      return browser.sleep(500)
    }).then(() => {
      expect(PersonalOverviewPo.getPersonalnummerOfTableRow(0)).toBe('555333555');
      expect(PersonalOverviewPo.getNameOfTableRow(0)).toBe('Foo Bar');
      expect(PersonalOverviewPo.getBeschZeitRaumOfTableRow(0)).toBe('01.01.1990 -')
    }).catch(fail);
  }));

  it('Delete Person', A(async () => {
    return Commands.createPerson().then(() => {
        return navigateToPersonal()
      }).then(() => {
        return browser.sleep(500)
      }).then(() => {
        return PersonalOverviewPo.clickOnDeleteActionOfTableRow(0)
      }).then(() => {
        return clickConfirmOnDialog();
      }).then(() => {
        expect(PersonalOverviewPo.getTableRowCount()).toBe(0)
      }).catch(fail);
  }));

});
