/* (c) https://github.com/MontiCore/monticore */
import { by, element } from 'protractor';
import { promise } from 'selenium-webdriver';
import { getElement, setInputByName } from '../../utilits/helper.e2e-util';

export class PersonalExpertPo {
  static async setVorname(name: string): promise.Promise<void> {
    return setInputByName('vorname', name);
  }

  static async setNachname(name: string): promise.Promise<void> {
    return setInputByName('nachname', name);
  }

  static async setPersonalnummer(persnr: string): promise.Promise<void> {
    return setInputByName('personalnummer', persnr);
  }

  static async setGeburtsdatum(gebDate: string): promise.Promise<void> {
    return setInputByName('gebDatum', gebDate);
  }

  static async setBeschaeftigungsbeginn(beschdate: string): promise.Promise<void> {
    return setInputByName('beschBeginn', beschdate);
  }

  static async isMDOptionPresent(option: string): promise.Promise<boolean> {
    return element(by.css('mat-option[ng-reflect-value=\'' + option + '\']')).isPresent();
  }

  static async setBeschaeftigungsende(beschdate: string): promise.Promise<void> {
    return setInputByName('beschEnde', beschdate);
  }

  static async hasError(): promise.Promise<boolean> {
    return element(by.css('macoco-input[class="has-error"')).isPresent();
  }

  static async clickOnSubmission(): promise.Promise<void> {
    return getElement(by.css('button[type="submit"]')).click();
  }
}
