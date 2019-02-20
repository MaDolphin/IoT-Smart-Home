import {browser} from 'protractor';
import {promise} from 'selenium-webdriver';
import {getButtonByName, setInputByName} from '../../utilits/helper.e2e-util';

export class LoginPo {
  static async setUserName(name: string): promise.Promise<void> {
    return setInputByName('username', name);
  }

  static async setPassword(pw: string): promise.Promise<void> {
    return setInputByName('password', pw);
  }

  static async loginButtonClick(): promise.Promise<void> {
    return getButtonByName('login-button').click();
  }
}