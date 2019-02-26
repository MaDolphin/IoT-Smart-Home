import { async, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { Location } from '@angular/common';

import { HeaderService } from '@shared/layout/header/header.service';
import { BreadcrumbComponent, BreadcrumbEntry } from '@shared/layout/breadcrumb/breadcrumb.component';
import { TitleTranslationPipe } from '@shared/pipes/title-translation.pipe';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Component } from '@angular/core';
import { By } from '@angular/platform-browser';

// Blank Component because a simple route target was needed.
@Component({ selector: 'blank', template: '' })
class BlankComponent { }

// Global definition of variables for configuration etc.
const pathWithId = 'finanzen/mitarbeiter/:personalId/detail';
const path = 'finanzen/mitarbeiter/overview';
const id = '5';
const routeWithId = '/' + pathWithId.replace(':personalId', id);
const route = '/' + path;
const name = 'TestName';

describe('BreadcrumbComponent', () => {
  // Basic Stuff
  let fixture, component, element, debugElement;

  // Services
  let headerService, router, location;

  // Mocks
  let resolverSpy;

  const breadcrumb = [
    { title: 'finanzen', route: '/finanzen' },
    { title: 'mitarbeiter', route: '/finanzen/mitarbeiter' },
    { title: 'overview', route: '/finanzen/mitarbeiter/overview' }
  ];

  const breadcrumbWithResolvedId: BreadcrumbEntry[] = [
    { title: 'finanzen', route: '/finanzen' },
    { title: 'mitarbeiter', route: '/finanzen/mitarbeiter' },
    { title: name, route: `/finanzen/mitarbeiter/${id}` },
    { title: 'detail', route: `/finanzen/mitarbeiter/${id}/detail` }
  ];

  beforeEach(async(() => {
    // Mock Personal Resolver to control the id resolving
    const personalResolver = jasmine.createSpyObj('PersonalResolver', ['resolve']);

    // Configuration of Testing Module. Don't import to much!
    TestBed.configureTestingModule({
      declarations: [
        BreadcrumbComponent,
        TitleTranslationPipe,
        BlankComponent
      ],
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'finanzen/mitarbeiter/overview', component: BlankComponent },
          { path: 'finanzen/mitarbeiter', redirectTo: 'finanzen/mitarbeiter/overview', pathMatch: 'full'}
        ])
      ],
      providers: [
        HeaderService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(BreadcrumbComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
    debugElement = fixture.debugElement;

    // Get services from Injector
    headerService = TestBed.get(HeaderService);
    router = TestBed.get(Router);
    location = TestBed.get(Location);

    // Resolver Spy definition here because headerService is needed
    resolverSpy = personalResolver.resolve.and.callFake(() => {
      headerService.idResolve.next({ major: name });
    })
  }));

  it('should change the id in the breadcrumb if there is information about it', fakeAsync(() => {
    // No automatic change detection
    fixture.detectChanges();
    expect(component.breadcrumb).toEqual([], 'The breadcrumb should be empty initially');

    // Router navigate is asynchronous. FakeAsync makes tick call possible
    router.navigate([routeWithId]);
    tick(10);
    fixture.detectChanges();

    expect(resolverSpy.calls.any()).toBe(true);
    expect(component.titleOfId).toBeNull();

    expect(component.breadcrumb).toEqual(breadcrumbWithResolvedId);
  }));

  it('should route to a proper route after click and show correct breadcrumb', fakeAsync(() => {
      fixture.detectChanges();

      router.navigate([routeWithId]);
      tick(2);
      fixture.detectChanges();

      expect(component.breadcrumb).toEqual(breadcrumbWithResolvedId);

      const elements = debugElement.queryAll(By.css('span'));

      if (elements.length > 0) {
        elements[1].nativeElement.click()
      } else {
        fail('Not enough elements on the breadcrumb!');
      }

      tick(2);
      fixture.detectChanges();

      expect(router.url).toEqual(route);

      expect(component.breadcrumb).toEqual(breadcrumb);
    }));

  it('should route to previous route after location back and show correct breadcrumb', fakeAsync(() => {
    router.navigate([route]);
    tick(10);
    fixture.detectChanges();

    expect(router.url).toEqual(route);
    expect(component.breadcrumb).toEqual(breadcrumb);

    // Navigate to route with id and name resolving
    router.navigate([routeWithId]);
    tick(10);
    fixture.detectChanges();

    expect(router.url).toEqual(routeWithId);
    expect(component.breadcrumb).toEqual(breadcrumbWithResolvedId);

    location.back();
    tick(10);
    fixture.detectChanges();

    expect(router.url).toEqual(route);
    expect(location.path()).toEqual(route);
    expect(component.breadcrumb).toEqual(breadcrumb);
  }));

  it('should route to previous route after location back and show correct breadcrumb with id resolve', fakeAsync(() => {
    router.navigate([route]);
    tick(10);
    fixture.detectChanges();

    router.navigate([routeWithId]);
    tick(10);
    fixture.detectChanges();

    expect(router.url).toEqual(routeWithId);
    expect(component.breadcrumb).toEqual(breadcrumbWithResolvedId, 'Initial route should have valid breadcrumb with Name!');

    // Navigate to route with id and name resolving
    router.navigate([route]);
    tick(10);
    fixture.detectChanges();

    expect(router.url).toEqual(route);
    expect(component.breadcrumb).toEqual(breadcrumb, 'Route back should have a proper breadcrumb!');

    location.back();
    tick(10);
    fixture.detectChanges();

    expect(router.url).toEqual(routeWithId);
    expect(location.path()).toEqual(routeWithId, 'Route should be the initial one!');
    expect(component.breadcrumb).toEqual(breadcrumbWithResolvedId, 'Breadcrumb should be the same as before including a propper id resolve!');
  }));

});
