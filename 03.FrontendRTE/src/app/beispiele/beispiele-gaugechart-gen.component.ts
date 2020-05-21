import {Component} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {BeispieleGaugechartGenComponentTOP} from '@targetgui/beispiele-gaugechart-gen.component/beispiele-gaugechart-gen.component-top';

@Component({
    templateUrl: '../../../target/generated-sources/gui/beispiele-gaugechart-gen.component/beispiele-gaugechart-gen.component.html',
})
export class BeispieleGaugechartGenComponent extends BeispieleGaugechartGenComponentTOP {
    public constructor(
        protected _commandRestService: CommandRestService,
        protected _route: ActivatedRoute,
        protected _router: Router
    ) {
        super(_commandRestService, _route, _router);
    }
}
