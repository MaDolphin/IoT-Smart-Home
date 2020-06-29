import { DensityChartComponent } from '@components/charts/density-chart/density-chart.component'
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {Input} from "@angular/core";
import * as d3 from 'd3';
import {FormsModule} from "@angular/forms";

interface Data2Model {
    type: string;
    value: number;
}

describe('Components', () => {
    describe('GuiDSL', () => {
        describe('Charts', () => {
            describe('Density-ChartComponent', () => {
                let c: DensityChartComponent;
                let fixture: ComponentFixture<DensityChartComponent>;
                let testData = [{type: 'A', value: 10}, {type: 'A', value: 14}, {type: 'A', value: 21}];
                beforeEach(async(() => {
                    TestBed.configureTestingModule({
                        imports: [FormsModule, d3],
                        declarations: [DensityChartComponent]
                    })
                        .compileComponents();

                    beforeEach(() => {
                        fixture = TestBed.createComponent(DensityChartComponent);
                        c = fixture.componentInstance;
                        fixture.detectChanges();

                        c.createDensityChart(testData);
                    afterEach(function () {
                        d3.selectAll('svg').remove();
                    });
                    });
                    describe('testing the data', function () {
                        it('if no data it should be null', function () {
                            expect(c.getData()).toBeNull();
                        });

                        it('Data update test', function () {
                            c.setData(testData);
                            expect(c.getData()).toBe(testData);
                        });
                    });
                    describe('create paths', function () {
                        it('should render the correct number of paths', function () {
                            waiting(function () {
                                expect(getPaths().length).toBe(1);
                            }, 4000);
                        });
                        it('should render the path with correct x', function () {
                            waiting(function () {
                                expect(d3.select(getPaths()[0]).attr('x')).toBeCloseTo(this.kernelDensityEstimator(this.kernelEpanechnikov(7), this.x.ticks(60)));
                            }, 4000);
                        });
                        it('should render the path with correct y', function () {
                            waiting( function () {
                                expect(d3.select(getPaths()[0]).attr('y')).toBeCloseTo(this.kernelEpanechnikov(7));
                            }, 4000);
                        });
                    });
                    describe('test axis', function () {
                        it('should be creating the x axis', function () {
                            let x = getXAxis();
                            expect(x.length).toBe(1);
                        })
                        it('should be creating the y axis', function () {
                            let y = getYAxis();
                            expect(y.length).toBe(1);
                        })
                    });
                    describe('test updater', function () {
                        it('should update if data has been changed', function () {
                            let newData = [{type: 'A', value: 12}, {type: 'B', value: 15}];
                            c.updateChart(newData);

                            waiting(function () {
                                expect(getPaths().length).toBe(2);
                            }, 4000);
                        });
                    });

                    /**
                     * Helper function to get the x Axis
                     */
                    function getXAxis() {
                        return d3.selectAll('g.x.axis')[0];
                    }

                    /**
                     * Helper function to get the y Axis
                     */
                    function getYAxis() {
                        return d3.selectAll('g.y.axis')[0];
                    }

                    /**
                     * Helper function to get the paths
                     */
                    function getPaths() {
                        return d3.selectAll('path')[0];
                    }

                    /**
                     * will wait certain amount of time before executing next run
                     * Necessary, otherwise errors because element is not there yet
                     * @param fn function
                     * @param time the time to wait
                     */
                    function waiting(fn, time) {
                        time = time || 2000;
                        waits(time);
                        runs(fn);
                    }
                //   it('updateChart() should call kernelDensityEstimator()', () => {
                //       let spy = jasmine.createSpyObj({updateChart: null});
                //       let densitymocker = {kernelDensityEstimator: spy};
                //       c.updateChart(densitymocker);
                //       expect(spy.kernelDensityEstimator).toHaveBeenCalled();
                //   });
                //   it('updateChart() should call kernelEpanechnikov()', () => {
                //       let spy = jasmine.createSpyObj({kernelEpanechnikov: null});
                //       let densitymocker = {kernelEpanechnikov: spy};
                //       c.updateChart(densitymocker);
                //       expect(spy.kernelEpanechnikov).toHaveBeenCalled();
                //   });
                    it('updateChart() should call kernelDensityEstimator()', () => {
                        spyOn(c, 'updateChart');
                        c.kernelDensityEstimator(7, 60);
                        expect(c.kernelDensityEstimator).toHaveBeenCalled();
                    });
                    it('updateChart() should call kernelEpanechnikov()', () => {
                        spyOn(c, 'updateChart');
                        c.kernelEpanechnikov(7);
                        expect(c.kernelEpanechnikov).toHaveBeenCalled();
                    });
                    it('should trigger onResize method', () => {
                        const spyResize = spyOn(c, 'ngOnChanges');
                        window.dispatchEvent(new Event('resize'));
                        expect(spyResize).toHaveBeenCalled();
                    });
                }));
            });
        });
    });
});