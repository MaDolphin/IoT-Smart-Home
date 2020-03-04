import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommandRestService } from '@shared/architecture/command/rte/command.rest.service';
import { BeispieleImagesGenComponentTOP } from "@targetgui/beispiele-images-gen.component/beispiele-images-gen.component-top";

@Component({
  templateUrl: '../../../target/generated-sources/gui/beispiele-images-gen.component/beispiele-images-gen.component.html',
})
export class BeispieleImagesGenComponent extends BeispieleImagesGenComponentTOP implements OnInit {

  private imageSource: string = "assets/img/BGlogin.png";

  constructor(
    _router: Router,
    _route: ActivatedRoute,
    _commandRestService: CommandRestService,
  ) {
    super(_commandRestService, _route, _router);
  }

  getImageSource(): string {
    return this.imageSource;
  }

  showImage1(): void {
    this.imageSource = "assets/img/BGlogin.png";
  }

  showImage2(): void {
    this.imageSource = "assets/img/rwth_se_rgb.png";
  }

}
