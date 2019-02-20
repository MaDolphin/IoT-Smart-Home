import { HeaderService } from '@shared/layout/header/header.service';
import { AuthService } from '@shared/auth/auth.service';
import { NotificationService } from '@shared/notification/notification.service';
import { HeaderComponent } from '@shared/layout/header/header.component';
import { Router } from '@angular/router';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { TitleTranslationPipe } from '@shared/pipes/title-translation.pipe';
import { DateToStringPipe } from '@shared/pipes/date-to-string.pipe';

// Mocks

class AuthServiceMock {
  user = {};
  exp = '';
}

class NotificationServiceMock {
  notificationYesNo(...args) { }
}

@Component({selector: 'app-breadcrumb', template: ''})
class BreadcrumbStubComponent {}

// Tests

describe('HeaderComponent', () => {

  let headerComponent: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  let headerService: HeaderService;
  let router: Router;

  beforeEach(async(() => {

    TestBed.configureTestingModule({
      declarations: [
        HeaderComponent,
        BreadcrumbStubComponent,
        TitleTranslationPipe,
        DateToStringPipe
      ],
      imports: [RouterTestingModule],
      providers: [
        HeaderService,
        { provide: AuthService, useClass: AuthServiceMock },
        { provide: NotificationService, useClass: NotificationServiceMock }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    headerComponent = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.get(Router);
    headerService = TestBed.get(HeaderService);
  });

  it('should be date, title and logout ok', () => {
      // headerComponent = new HeaderComponent(headerService, authService, router, notificationService);
      expect(headerComponent.title).not.toBeNull();
      expect(headerComponent.curDate).not.toBeNull();
      expect(headerComponent.logout()).toBeTruthy();
  });
});