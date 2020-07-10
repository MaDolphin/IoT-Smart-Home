import { Data, RidgelineChartComponent, Ridgeline_Config } from '@shared/components/guidsl/charts/ridgeline-chart/ridgeline-chart.component'
import * as paper from 'paper';
import { N } from '@angular/cdk/keycodes';
import { TestBed, async } from '@angular/core/testing';


describe('Components', () => {
    describe('GuiDSL', () => {
        describe('Charts', () => {
            describe('Ridgeline-Chart Component', () => {
                let fixture, chart, element, de;


                beforeEach(async(() => {
                    // // Create module
                    // TestBed.configureTestingModule({
                    //     declarations: [RidgelineChartComponent]
                    // }).compileComponents();
                
                }));
                beforeEach(() => {
                    // Create module
                    TestBed.configureTestingModule({
                        declarations: [RidgelineChartComponent]
                    }).compileComponents();
                    fixture = TestBed.createComponent(RidgelineChartComponent);
                    fixture.detectChanges();

                    chart = fixture.componentInstance;
                    // element = fixture.nativeElement;
                    // de = fixture.debugElement;
                });

                beforeEach(() => {
                    // Create testing data
                    let raw_data = [[[1,5],[5,4],[2.3,0]],
                                [[-22.3,-5],[2,0],[-30,7],[1,4]],
                                [[100,3],[73,-4],[34.02,5],[-10.2,199]]
                                ];
                    
                    this.data = new Data();
                    this.data.set_raw_data(raw_data, 1000);
                    // console.log(this.data);

                    let raw_data2 = [[[-20,194.23],[-75,222.2]],
                                     [[-2993,4]]
                                    ];
                            
                    this.data2 = new Data();
                    this.data2.set_raw_data(raw_data2, 10000);

                    this.data3 = new Data(); //Setup clean data object


                    // Setup for transformation tests
                    raw_data = [[[1,-27],[3,-15.2]],
                                [[-22.3,-5],[-11,4]],
                               ];
                    let color_gradients = [["#e91e63", -27], ["#2196f3", 27], ["#2196f3", 22.5]];
                    // With default overshoot=30% the last value should be exactly on the line of the subsequent ridge
                
                    this.dataT = new Data();
                    this.dataT.set_raw_data(raw_data, 1000);
                    this.dataT.set_color_gradients(color_gradients);

                    let config = new Ridgeline_Config();
                    config.set_params(["1","2"], 2, 1000, 797, 14, 20, 2, 100);

                    this.dataT.transform_data(config);
                    this.dataT.transform_color_gradients(config, 1.0, 797);
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

                it('get_xy_min_max', // Implicitly tests get_min/max_x_y and set_raw_data (and thus compute_ranges, too).
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

                it('restrict_to_x_range',
                    () => {
                        // Firstly test, that it is called correctly in set_raw_data
                        let raw_data = [[[3,5],[-1,4],[11,0],[2.6,18]],
                                        [[11.5,-5],[-22.6,0],[-30,7],[15.6,4]]
                                        ];

                        let assumed_restricted_data = [[[2.6,18],[3,5],[11,0]],
                                                       [[11.5,-5],[15.6,4]]
                                                       ];
                    
                        this.data3.set_raw_data(raw_data, 13);
                        expect(assumed_restricted_data).toEqual(this.data3.values);

                        // Secondly test, that it is called correctly in update_raw_data
                        let further_raw_data = [[[10.8,17.35],[17.9,-5]],
                                                [[16.6,0]]];
                        assumed_restricted_data = [[[10.8,17.35],[17.9,-5]],
                                                   [[11.5,-5],[15.6,4],[16.6,0]]
                                                  ];
                        this.data3.update_raw_data(further_raw_data, 13);
                        expect(assumed_restricted_data).toEqual(this.data3.values);
                    }
                )

                it('transform_data',
                    () => {
                        // Can only work, if test on Config-class works
                        let data = this.dataT;

                        let assumed_transformed_data = [[[922.4,540],[1000,422]],
                                                        [[18.03,545],[456.6,455]]
                                                       ];

                        for(let ridge=0; ridge<assumed_transformed_data.length; ridge++){
                            expect(data.get_transformed_data_row(ridge).length).toEqual(assumed_transformed_data[ridge].length);
                            for(let entry=0; entry<assumed_transformed_data[ridge].length; entry++){
                                expect(data.get_transformed_data_row(ridge)[entry][0]).toBeCloseTo(assumed_transformed_data[ridge][entry][0],0.5);
                                expect(data.get_transformed_data_row(ridge)[entry][1]).toBeCloseTo(assumed_transformed_data[ridge][entry][1],0.1);
                            }
                        }
                    }
                )

                it('transform_color_gradients',
                    () => {
                        // Can only work, if test on Config-class works
                        let data = this.dataT;
                        console.log("Transform_color_gradients");

                        let color1 = new paper.Color("#e91e63");
                        let color2 = new paper.Color("#2196f3");
                        let color3 = new paper.Color("#2196f3");
                        color1.alpha = 1.0;
                        color2.alpha = 1.0;
                        color3.alpha = 1.0;

                        let assumed_transformed_color_gradients = [[[color1, 540], [color2, 45],  [color3, 0]],
                                                                   [[color1, 765], [color2, 270], [color3, 225]]];

                        for(let ridge=0; ridge<assumed_transformed_color_gradients.length; ridge++){
                            expect(data.get_transformed_color_gradients(ridge).length).toEqual(assumed_transformed_color_gradients[ridge].length);
                            for(let entry=0; entry<assumed_transformed_color_gradients[ridge].length; entry++){
                                console.log(data.get_transformed_color_gradients(ridge)[entry][0]);
                                expect(data.get_transformed_color_gradients(ridge)[entry][0]).toEqual(assumed_transformed_color_gradients[ridge][entry][0]);
                                expect(data.get_transformed_color_gradients(ridge)[entry][1]).toEqual(assumed_transformed_color_gradients[ridge][entry][1]);
                            }
                        }
                    }
                )



// ----------------------------------------- Data-class tests -----------------------------------------
                it('Config: set_params',
                    () => {
                        // Create Config
                        this.config = new Ridgeline_Config();
                        this.config.set_params(["1","2"], 2, 1000, 797, 14, 20, 2, 100);

                        let config : Ridgeline_Config = this.config;
                        expect(config.ridges_offset).toEqual(225);
                        expect(config.ridges_height).toEqual(270);
                        expect(config.grid_line_start_x).toBeCloseTo(18,0.5);
                        console.log(config.grid_line_start_x);
                        expect(config.grid_line_end_x).toEqual(1000);
                        expect(config.grid_line_start_y).toEqual(270);
                        expect(config.font_size).toEqual(14);
                        expect(config.x_axis_start).toEqual(config.grid_line_start_x);
                        expect(config.x_axis_width).toEqual(1000-config.x_axis_start);
                        expect(config.x_axis_value_offset).toEqual(100);
                        expect(config.y_axis_start).toEqual(270);
                    }
                )





// -------------------------------------- RidgelineChartcomponent --------------------------------------


                it('get_x_label_precision',
                    () => {
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

                it('x_to_text',
                    () => {
                        chart.show_date = true;
                        expect(chart.x_to_text(1594291372000, 1000)).toEqual("09.07.2020\n12:42:52");

                        chart.show_date = false;
                        expect(chart.x_to_text(1486739568462, 1)).toEqual("16:12:48");

                        chart.x_is_time = false;
                        expect(chart.x_to_text(2785.38694, 2)).toEqual("2785.39");
                    }
                )

            });
        });
    });
});