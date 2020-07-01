import { Data, RidgelineChartComponent, Ridgeline_Config } from '@shared/components/guidsl/charts/ridgeline-chart/ridgeline-chart.component'
import { N } from '@angular/cdk/keycodes';


describe('Components', () => {
    describe('GuiDSL', () => {
        describe('Charts', () => {
            describe('Ridgeline-Chart Component', () => {
                beforeEach(() => {
                    // Create testing data
                    let raw_data = [[[1,5],[5,4],[2.3,0]],
                                [[-22.3,-5],[2,0],[-30,7],[1,4]],
                                [[100,3],[73,-4],[34.02,5],[-10.2,199]]
                                ];
                    
                    this.data = new Data();
                    this.data.set_raw_data(raw_data, 1000);

                    let raw_data2 = [[[-20,194.23],[-75,222.2]],
                                     [[-2993,4]]
                                    ];
                            
                    this.data2 = new Data();
                    this.data2.set_raw_data(raw_data2, 10000);

                    this.config = new Ridgeline_Config();
                    this.config.set_params(["1","2"], 2, 1000, 798.8, 1000, 14, 20, 2, 1000);
                });

// ----------------------------------------- Data-class tests -----------------------------------------

                it('sort_by_x',
                    () => {
                        let assumed_result = [[[1,5],[2.3,0],[5,4]],
                                              [[-30,7],[-22.3,-5],[1,4],[2,0]],
                                              [[-10.2,199],[34.02,5],[73,-4],[100,3]]
                                             ];
                        
                        expect(assumed_result).toEqual(this.data.values);
                    }
                );

                it('get_xy_min_max', // Implicitly tests get_min/max_x_y and set_raw_data
                    () => {
                        let assumed_xy_min_max = [[-30,-5],[100,199]];
                        let assumed_x_range = 130;
                        let assumed_y_range = 204;
                        let assumed_max_y_range_from_0 = 199;
                        let assumed_length = 3;
                        expect(assumed_xy_min_max).toEqual(this.data.xy_min_max);
                        expect(assumed_x_range).toEqual(this.data.x_range);
                        expect(assumed_y_range).toEqual(this.data.y_range);
                        expect(assumed_max_y_range_from_0).toEqual(this.data.max_y_range_from_0);
                        expect(assumed_length).toEqual(this.data.length);

                        // Test correct initialization (e.g. not with 0) when 0 is not part of the range
                        let assumed_xy_min_max2 = [[-2993,4],[-20,222.2]];
                        let assumed_x_range2 = 2973;
                        let assumed_y_range2 = 218.2;
                        let assumed_max_y_range_from_0_2 = 222.2;
                        let assumed_length2 = 2;
                        expect(assumed_xy_min_max2).toEqual(this.data2.xy_min_max);
                        expect(assumed_x_range2).toEqual(this.data2.x_range);
                        expect(assumed_y_range2).toEqual(this.data2.y_range);
                        expect(assumed_max_y_range_from_0_2).toEqual(this.data2.max_y_range_from_0);
                        expect(assumed_length2).toEqual(this.data2.length);

                        // Test further edgecase in which no data is existing in one ridge
                        this.data.set_raw_data([[],[[5,-10]]], 100);
                        let assumed_xy_min_max3 = [[5,-10],[5,-10]];
                        let assumed_x_range3 = 0;
                        let assumed_y_range3 = 0;
                        let assumed_max_y_range_from_0_3 = 10;
                        let assumed_length3 = 2;
                        expect(assumed_xy_min_max3).toEqual(this.data.xy_min_max);
                        expect(assumed_x_range3).toEqual(this.data.x_range);
                        expect(assumed_y_range3).toEqual(this.data.y_range);
                        expect(assumed_max_y_range_from_0_3).toEqual(this.data.max_y_range_from_0);
                        expect(assumed_length3).toEqual(this.data.length);

                        // Test further edgecase in which no data is existing in all ridges
                        this.data.set_raw_data([[],[],[]], 100);
                        let assumed_xy_min_max4 = [[Number.POSITIVE_INFINITY, Number.POSITIVE_INFINITY],[Number.NEGATIVE_INFINITY,Number.NEGATIVE_INFINITY]];
                        let assumed_x_range4 = Number.POSITIVE_INFINITY;
                        let assumed_y_range4 = Number.POSITIVE_INFINITY;
                        let assumed_max_y_range_from_0_4 = Number.POSITIVE_INFINITY;
                        let assumed_length4 = 3;
                        expect(assumed_xy_min_max4).toEqual(this.data.xy_min_max);
                        expect(assumed_x_range4).toEqual(this.data.x_range);
                        expect(assumed_y_range4).toEqual(this.data.y_range);
                        expect(assumed_max_y_range_from_0_4).toEqual(this.data.max_y_range_from_0);
                        expect(assumed_length4).toEqual(this.data.length);

                    }
                );

                it('update_raw_data',
                    () => {
                        let further_raw_data = [[[200,49.3],[5.1,-24]],
                                                [[-1,12]],
                                                [[-11,42.1],[0,0.3],[-1,33.987]],
                                                [[1,3]]];
                        let assumed_result = [[[1,5],[2.3,0],[5,4],[5.1,-24],[200,49.3]],
                                              [[-30,7],[-22.3,-5],[-1,12]],
                                              [[-11,42.1],[-1,33.987],[0,0.3]],
                                              [[1,3]]
                                             ];
                        this.data.update_raw_data(further_raw_data, 1000);
                        expect(assumed_result).toEqual(this.data.values);

                        // Edgecase 1: empty input
                        further_raw_data = [];
                        this.data.update_raw_data(further_raw_data, 1000);
                        expect(assumed_result).toEqual(this.data.values); // nothing should have changed

                        // Edgecase 2: empty ridge
                        further_raw_data = [[[202,-5.3]],
                                            []];
                        assumed_result[0].push([202, -5.3]);
                        this.data.update_raw_data(further_raw_data, 1000);
                        expect(assumed_result).toEqual(this.data.values); // nothing should have changed
                    }
                )

                it('transform_data',
                    () => {
                        // Can only work, if test on Config-class works
                        // Test by using this.data2

                    }
                )



// ----------------------------------------- Data-class tests -----------------------------------------
                it('set_params',
                    () => {
                        let config : Ridgeline_Config = this.config;
                        expect(config.ridges_offset).toEqual(213);
                        expect(config.ridges_height).toEqual(276.9);
                        expect(config.grid_line_start_x).toBeGreaterThan(10);
                        expect(config.grid_line_end_x).toEqual(1000);
                        expect(config.grid_line_start_y).toEqual(276.9);
                        expect(config.font_size).toEqual(14);
                        expect(config.x_axis_start).toEqual(config.grid_line_start_x);
                        expect(config.x_axis_width).toEqual(1000-config.x_axis_start);
                        expect(config.x_axis_value_offset).toEqual(1000);
                        expect(config.y_axis_start).toEqual(276.9);
                    }
                )





// -------------------------------------- RidgelineChartcomponent --------------------------------------

                // TODO Add test for x-window






                it('get_x_label_precision',
                    () => {
                        let chart = new RidgelineChartComponent();

                        chart.relative_x_precision = 2;
                        expect(chart.get_x_label_precision(1093750)).toEqual(0);
                        expect(chart.get_x_label_precision(2535.3)).toEqual(0);
                        expect(chart.get_x_label_precision(100.5)).toEqual(0);
                        expect(chart.get_x_label_precision(99.9)).toEqual(1);
                        expect(chart.get_x_label_precision(10)).toEqual(1);
                        expect(chart.get_x_label_precision(9.999)).toEqual(2);
                        expect(chart.get_x_label_precision(0.0015)).toEqual(5);

                        chart.relative_x_precision = 5;
                        expect(chart.get_x_label_precision(1093750)).toEqual(0);
                        expect(chart.get_x_label_precision(2535.3)).toEqual(2);
                        expect(chart.get_x_label_precision(100.5)).toEqual(3);
                        expect(chart.get_x_label_precision(99.9)).toEqual(4);
                        expect(chart.get_x_label_precision(10)).toEqual(4);
                        expect(chart.get_x_label_precision(9.999)).toEqual(5);
                        expect(chart.get_x_label_precision(0.0015)).toEqual(8);

                        chart.relative_x_precision = -2;
                        expect(chart.get_x_label_precision(1)).toEqual(0);
                        expect(chart.get_x_label_precision(0.0015)).toEqual(1);

                        chart.relative_x_precision = 0;
                        expect(chart.get_x_label_precision(1)).toEqual(0);
                        expect(chart.get_x_label_precision(0.1)).toEqual(1);
                        expect(chart.get_x_label_precision(0.09)).toEqual(2);
                    }
                )


            });
        });
    });
});