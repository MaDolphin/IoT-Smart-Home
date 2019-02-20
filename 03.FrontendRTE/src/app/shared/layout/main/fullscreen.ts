import { Subject } from 'rxjs/Subject';

export interface IFullscreen {
  requestFullscreen: string;
  exitFullscreen: string;
  fullscreenElement: string;
  fullscreenEnabled: string;
  fullscreenchange: string;
  fullscreenerror: string;
}

const fnMap = [
  [
    'requestFullscreen',
    'exitFullscreen',
    'fullscreenElement',
    'fullscreenEnabled',
    'fullscreenchange',
    'fullscreenerror',
  ],
  // New WebKit
  [
    'webkitRequestFullscreen',
    'webkitExitFullscreen',
    'webkitFullscreenElement',
    'webkitFullscreenEnabled',
    'webkitfullscreenchange',
    'webkitfullscreenerror',

  ],
  // Old WebKit (Safari 5.1)
  [
    'webkitRequestFullScreen',
    'webkitCancelFullScreen',
    'webkitCurrentFullScreenElement',
    'webkitCancelFullScreen',
    'webkitfullscreenchange',
    'webkitfullscreenerror',

  ],
  [
    'mozRequestFullScreen',
    'mozCancelFullScreen',
    'mozFullScreenElement',
    'mozFullScreenEnabled',
    'mozfullscreenchange',
    'mozfullscreenerror',
  ],
  [
    'msRequestFullscreen',
    'msExitFullscreen',
    'msFullscreenElement',
    'msFullscreenEnabled',
    'MSFullscreenChange',
    'MSFullscreenError',
  ],
];

export class Fullscreen {

  private static interaction: IFullscreen;

  public get isFullscreen(): boolean {
    return !!this.element;
  }

  public get element(): HTMLElement {
    return document[Fullscreen.interaction.fullscreenElement];
  }

  public get enabled(): boolean {
    return !!document[Fullscreen.interaction.fullscreenEnabled];
  }

  public get keyboardAllowed(): boolean {
    return typeof Element !== 'undefined' && 'ALLOW_KEYBOARD_INPUT' in Element && Element['ALLOW_KEYBOARD_INPUT'];
  }

  public onChange: Subject<any> = new Subject();
  public onError: Subject<any>  = new Subject();

  constructor() {
    if (!Fullscreen.interaction) {
      Fullscreen.interaction = {} as any;
      for (const methodArray of fnMap) {
        if (methodArray[1] in document) {
          for (let i = 0; i < methodArray.length; i++) {
            Fullscreen.interaction[fnMap[0][i]] = methodArray[i];
          }
          break;
        }
      }
    }
    document.addEventListener(
      Fullscreen.interaction.fullscreenchange,
      (event) => this.onChange.next(event),
      false,
    );
    document.addEventListener(
      Fullscreen.interaction.fullscreenerror,
      (event) => this.onError.next(event),
      false,
    );
  }

  public request(elem: HTMLElement = document.documentElement) {
    // Work around Safari 5.1 bug: reports support for
    // keyboard in fullscreen even though it doesn't.
    // Browser sniffing, since the alternative with
    // setTimeout is even worse.
    if (/5\.1[.\d]* Safari/.test(navigator.userAgent)) {
      elem[Fullscreen.interaction.requestFullscreen]();
    } else {
      elem[Fullscreen.interaction.requestFullscreen](this.keyboardAllowed);
    }
  }

  public exit() {
    document[Fullscreen.interaction.exitFullscreen]();
  }

  public toggle(elem?: HTMLElement) {
    if (this.isFullscreen) {
      this.exit();
    } else {
      this.request(elem);
    }
  }

}
