/* (c) https://github.com/MontiCore/monticore */
import {
  A,
  cleanup,
  clickConfirmOnDialog,
  clickOnLogout,
  getActiveToken,
  isTokenValid
} from '../../utilits/helper.e2e-util';
import {login, setup} from '../../utilits/login.e2e-util';
import {browser, protractor} from 'protractor';
import {navigateToDashboard, navigateToLogin} from '../../utilits/navigation.e2e-util';
import {LoginPo} from '../../pageobjects/login/login.po';

describe('Login site', function () {

  let EC = protractor.ExpectedConditions;

  beforeEach(setup);
  afterEach(cleanup);

  it('login', A(async () => {
    cleanup().then(() => {
      return navigateToLogin()
    }).then(() => {
      return LoginPo.setUserName('admin');
    }).then(() => {
      return LoginPo.setPassword('passwort');
    }).then(() => {
      return LoginPo.loginButtonClick();
    }).then(() => {
      return browser.wait(EC.urlContains('dashboard'), 4000, 'Failed to navigate to dashbord')
    }).then(() => {
      return getActiveToken()
    }).then((token) => {
      expect(isTokenValid(token)).toBe(true)
    }).catch(fail)
  }));

  it('logout', A(async () => {
    navigateToDashboard().then(() => {
      return clickOnLogout()
    }).then(() => {
      return clickConfirmOnDialog()
    }).then(() => {
      return browser.wait(EC.urlContains('login'), 4000, 'Failed to navigate to login')
    }).then(() => {
      return getActiveToken()
    }).then((token) => {
      expect(isTokenValid(token)).toBe(false)
    }).catch(fail)
  }));
});
