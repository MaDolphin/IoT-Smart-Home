import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GaugeChartComponent} from '@components/charts/gauge-chart/gauge-chart.component';
import {FormsModule} from "@angular/forms";
import {NgxChartsModule} from "@swimlane/ngx-charts";

describe('Components', () => {
    describe('GuiDSL', () => {
        describe('Charts', () => {
            describe('GaugeChartComponent', () => {
                let component: GaugeChartComponent;
                let fixture: ComponentFixture<GaugeChartComponent>;

                beforeEach(async(() => {
                    TestBed.configureTestingModule({
                        imports: [FormsModule, NgxChartsModule],
                        declarations: [GaugeChartComponent]
                    })
                        .compileComponents();
                }));

                beforeEach(() => {
                    fixture = TestBed.createComponent(GaugeChartComponent);
                    component = fixture.componentInstance;
                    fixture.detectChanges();
                });

                it('should create', () => {
                    expect(component).toBeTruthy();
                });


                it('recalcBigTicks',
                    () => {
                        component.min = 0;
                        component.max = 30;
                        component.recalcBigTicks();
                        expect(3).toEqual(component.bigTicks);

                        component.min = -40;
                        component.max = 50;
                        component.recalcBigTicks();
                        expect(9).toEqual(component.bigTicks);

                        component.min = -20;
                        component.max = 100;
                        component.recalcBigTicks();
                        expect(12).toEqual(component.bigTicks);

                        component.min = 0;
                        component.max = 160;
                        component.recalcBigTicks();
                        expect(2).toEqual(component.bigTicks);

                        component.min = 0;
                        component.max = 500;
                        component.recalcBigTicks();
                        expect(5).toEqual(component.bigTicks);
                    }
                );

                it('smallTickResizer',
                    () => {
                        component.smallTickResizer = 0;
                        expect(0).toEqual(component.smallTicks);

                        component.smallTickResizer = 1;
                        expect(1).toEqual(component.smallTicks);
                    }
                );

            });
        });
    });
});
