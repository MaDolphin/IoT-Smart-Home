import { Data, RidgelineChartComponent } from '@shared/components/guidsl/charts/ridgeline-chart/ridgeline-chart.component'


describe('Components', () => {
    describe('GuiDSL', () => {
        describe('Charts', () => {
            describe('Ridgeline-Chart Component', () => {
                beforeAll(() => {
                    // Create testing data
                    let raw_data = [[[1,5],[5,4],[2.3,0]],
                                [[-22.3,-5],[2,0],[-30,7],[1,4]],
                                [[100,3],[73,-4],[34.02,5],[-10.2,199]]
                                ];
                    
                    this.data = new Data();
                    this.data.setup(raw_data);
                });

                it('sort_by_x',
                    () => {
                        let assumed_result = [[[1,5],[2.3,0],[5,4]],
                                              [[-30,7],[-22.3,-5],[1,4],[2,0]],
                                              [[-10.2,199],[34.02,5],[73,-4],[100,3]]
                                             ];
                        
                        expect(assumed_result).toEqual(this.data.values);
                    }
                );

                it('get_xy_min_max',
                    () => {
                        let assumed_xy_min_max = [[-30,-5],[100,199]];
                        let assumed_x_range = 130;
                        let assumed_y_range = 204;
                        expect(assumed_xy_min_max).toEqual(this.data.xy_min_max);
                        expect(assumed_x_range).toEqual(this.data.x_range);
                        expect(assumed_y_range).toEqual(this.data.y_range);
                    }
                );

                it('transform_data',
                    () => {
                        console.log("TODO");
                    }
                )
            });
        });
    });
});