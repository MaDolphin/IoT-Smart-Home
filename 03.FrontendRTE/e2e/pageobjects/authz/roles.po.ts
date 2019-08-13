/* (c) https://github.com/MontiCore/monticore */
import {promise} from 'selenium-webdriver';
import {setInputByName} from '../../utilits/helper.e2e-util';

export class RolesPo {
  static async setUserName(user: string): promise.Promise<void> {
    await setInputByName('username', user);
  }

  static async setEmail(emailAddress: string): promise.Promise<void> {
    await setInputByName('email', emailAddress);
  }

  static async setInitials(userInitials: string): promise.Promise<void> {
    await setInputByName('initials', userInitials);
  }
}
