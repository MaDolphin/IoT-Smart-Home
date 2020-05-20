 /*(c) https://github.com/MontiCore/monticore */
import {Component, OnInit} from '@angular/core';
import {WebSocketService} from '@shared/architecture/services/websocket.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CommandRestService} from '@shared/architecture/command/rte/command.rest.service';
import {BeispieleDensitychartGenComponentTop} from "@targetgui/beispiele-densitychart-gen.component/beispiele-densitychart-gen.component-top";
@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-densitychart-gen.component/beispiele-densitychart-gen.component.html',
})
export class BeispieleDensitychartsGenComponent extends BeispieleDensitychartGenComponentTop implements OnInit{

  constructor(
      _router: Router,
      _route: ActivatedRoute,
      _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _route, _router);
  }
  ngOnInit(): void {
    super.ngOnInit();
    /*this.setNavigationBarLinks();*/
  }
  title = 'Densitychart';

  public data =
    [
        {
          "type": "Temperature Aachen",
          "value": 16.1,
          "day": 1
        },
{
  "type": "Temperature Aachen",
  "value": 14.2,
  "day": 2
},
 {
   "type": "Temperature Aachen",
     "value": 11.1,
     "day": 3
 },
 {
   "type": "Temperature Aachen",
     "value": 14.1,
     "day": 4
 },
 {
   "type": "Temperature Aachen",
     "value": 15.3,
     "day": 5
 },
 {
   "type": "Temperature Rio",
     "value": 32,
     "day": 1
 },
 {
   "type": "Temperature Rio",
     "value": 28,
     "day": 2
 },
 {
   "type": "Temperature Rio",
     "value": 26,
     "day": 3
 },
 {
   "type": "Temperature Rio",
     "value": 23,
     "day": 4
 },
 {
   "type": "Temperature Rio",
     "value": 25,
     "day": 5
 },
 {
   "type": "Temperature Montreal",
     "value": 11,
     "day": 1
 },
 {
   "type": "Temperature Montreal",
     "value": 18,
     "day": 2
 },
 {
   "type": "Temperature Montreal",
     "value": 19,
     "day": 3
 },
 {
   "type": "Temperature Montreal",
     "value": 21,
     "day": 4
 },
 {
   "type": "Temperature Montreal",
     "value": -3,
     "day": 5
 }
 ]

 
}
