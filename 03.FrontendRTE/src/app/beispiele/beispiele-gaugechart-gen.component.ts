import {Component} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {BeispieleGaugechartGenComponentTOP} from '@targetgui/beispiele-gaugechart-gen.component/beispiele-gaugechart-gen.component-top';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {LineGraphDTO} from "@targetdtos/linegraph.dto";
import {TypedJSON} from "@upe/typedjson";
import {LineDataGroup} from "@components/charts/line-chart/line-chart.component";

@Component({
    templateUrl: '../../../target/generated-sources/gui/beispiele-gaugechart-gen.component/beispiele-gaugechart-gen.component.html',
})
export class BeispieleGaugechartGenComponent extends BeispieleGaugechartGenComponentTOP {
    public constructor(
        protected _commandRestService: CommandRestService,
        protected _route: ActivatedRoute,
        protected _router: Router,
        protected _webSocketService: WebSocketService
    ) {
        super(_commandRestService, _route, _router, _webSocketService);
    }

    public transformedRealtimeData: LineDataGroup[] = [];

    public subscribedataSocket(): void {
        if (this.dataSocket) {
            this.subscriptions.push(this.dataSocket.subscribe(message => {
                    let receivedData: LineGraphDTO = TypedJSON.parse(message.data, LineGraphDTO);
                    this.data = this.transformSocketData(receivedData);
                    this.transformedRealtimeData = this.data;
                }, err =>
                    console.error(err)
            ));
        } else {
            console.error('Socket is not initialized. Initialize socket in the component constructor');
        }
    }
}
