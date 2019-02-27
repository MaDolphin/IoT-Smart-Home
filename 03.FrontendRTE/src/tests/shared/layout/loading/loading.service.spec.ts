/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { getTestBed, inject, TestBed } from '@angular/core/testing';
import { LoadingService } from '@shared/layout/loading/loading.service';

describe('AuthService', () => {
  let loadingService: LoadingService;
  beforeEach(() => {
    TestBed.configureTestingModule({
                                     declarations: [],
                                     providers:    [
                                       LoadingService,
                                     ],
                                   });
    loadingService = getTestBed().get(LoadingService);
  });

  afterAll(() => {
    localStorage.removeItem('jwt');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('expirationDate');
  });

  it('should create an injected instance test 1', inject([LoadingService], (injectedService: LoadingService) => {
    expect(injectedService).toBeDefined();
  }));

  it('should create an instance instance test 2', () => {
    expect(loadingService).toBeDefined();
  });

  it('after initialization get active() should return false', () => {
    expect(loadingService.active).toBe(false);
  });

  it('after set active to true it should return true', () => {
    loadingService.active = true;
    expect(loadingService.active).toBe(true);
  });

  it('after running stop function active should be false', () => {
    loadingService.stop();
    expect(loadingService.active).toBe(false);
  });

  it('after running start function active should be true', () => {
    loadingService.start();
    expect(loadingService.active).toBe(true);
  });
});