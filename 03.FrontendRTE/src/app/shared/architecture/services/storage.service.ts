import { Injectable } from '@angular/core';

@Injectable()
export class StorageService {

  constructor() {

  }

  public store(key: string, data: any): void {
      this.storeSession(key, data);
  }

  public retrieve(key: string) {
      return this.retrieveSession(key);
  }

  public storeLocal(key: string, data: any): void {
    localStorage.setItem(key, data);
  }

  public retrieveLocal(key: string) {
    return localStorage.getItem(key);
  }

  public storeSession(key: string, data: any): void {
      sessionStorage.setItem(key, data);
  }

  public retrieveSession(key: string) {
      return sessionStorage.getItem(key);
  }

  public storeServer(key: string, data: any): void {

  }

  public retrieveServer(key: string) {
      return {};
  }

}
