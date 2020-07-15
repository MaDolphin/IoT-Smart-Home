import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HeatmapChartComponent} from '@components/charts/heatmap-chart/heatmap-chart.component';
import {FormsModule} from "@angular/forms";
import {NgxChartsModule} from "@swimlane/ngx-charts";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";


describe('Components', () => {
    describe('GuiDSL', () => {
        describe('Charts', () => {
            describe('HeatmapChartComponent', () => {
                let component: HeatmapChartComponent;
                let fixture: ComponentFixture<HeatmapChartComponent>;

                beforeEach(async(() => {
                    TestBed.configureTestingModule({
                        imports: [FormsModule, NgxChartsModule, BrowserAnimationsModule],
                        declarations: [HeatmapChartComponent]
                    })
                        .compileComponents();
                }));

                beforeEach(() => {
                    fixture = TestBed.createComponent(HeatmapChartComponent);
                    component = fixture.componentInstance;
                    fixture.detectChanges();
                });

                it('should create', () => {
                    expect(component).toBeTruthy();
                });


                it('color_to_html_hex',
                    () => {
                        //Check if 0-Padding works
                        expect('#000000').toEqual(component.color_to_html_hex(0,0,0).toLowerCase());
                        //Check if Out of Bounds values behave as expected
                        expect('#2c2c2c').toEqual(component.color_to_html_hex(300,300,300).toLowerCase());
                    }
                );

                it('enter_color',
                    () => {
                        component.color_domain_mid_steps = 1;
                        component.enter_color('#FAFAFA');
                        expect('#4b4b4b').toEqual((component.colorScheme.domain[0]).toLowerCase());
                        expect('#fafafa').toEqual((component.colorScheme.domain[1]).toLowerCase());
                    }
                );

                it('enter_num_seperations',
                    () => {
                        component.enter_num_seperations(0);
                        expect(1).toEqual(component.num_seperations);
                        component.enter_num_seperations(99);
                        expect(32).toEqual(component.num_seperations);
                    }
                );

                it('enter_num_seperations',
                    () => {
                        component.enter_num_loop_factor(0);
                        expect(2).toEqual(component.loop_factor);
                    }
                );

                it('timeToString',
                    () => {
                        expect('0:00').toEqual(component.timeToString(component.getUTCOffset(),86400));
                    }
                );

                it('rearrange_array',
                    () => {
                        component.num_seperations=1;
                        var test = component.rearrange_array([{"name":"a", "value":1}]);
                        expect("1-2").toEqual(test[0].name);
                        expect("a").toEqual(test[0].series[0].name);
                        expect(1).toEqual(test[0].series[0].value);
                    }
                );

            });
        });
    });
});
