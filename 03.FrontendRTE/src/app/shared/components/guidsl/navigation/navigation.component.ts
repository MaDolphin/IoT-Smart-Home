import { Component, Input, OnInit } from '@angular/core';
import { Logger } from '@upe/logger';
import { copyByValue } from '@shared/utils/util';

interface NavigationLink {
  link: string,
  title: string,
  disabled?: boolean,
}

@Component({
  selector: 'macoco-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {

  private _links: NavigationLink[] = [];

  @Input()
  public set links(linkData: NavigationLink[]) {
    this._links = copyByValue(linkData);
  };

  public get links(): NavigationLink[] {
    return this._links;
  }

  @Input()
  public set currentPath(path: string) {
    if (path === './') {
      return;
    }
    if (!this.links.map(l => l.link).includes(path)) {
      console.error(`Path ${path} does not exist`);
      return;
    }
    // not true in general
    let root = '../'.repeat(path.split('/').length - 1);
    this.links.forEach(linkData => {
      let link = linkData.link;
      if (link === path) {
        linkData.link = './';
      } else {
        linkData.link = (root + linkData.link).replace(/([^.])\.\//g, '$1');
      }
    });
    this.links = [...this.links];
  };

  private logger: Logger = new Logger({ name: 'NavigationComponent' });

  constructor() { }

  public ngOnInit(): void {
  }

}
