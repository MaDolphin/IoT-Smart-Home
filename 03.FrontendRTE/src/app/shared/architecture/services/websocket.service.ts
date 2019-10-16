/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

import { Injectable } from "@angular/core";
import { Observable, Observer, Subject } from 'rxjs/Rx';
import { Token } from "../../auth/token";
import { ReplaySubject } from "rxjs";

@Injectable()
export class WebSocketService {
  public port: string = location.port ? ':' + location.port : '';
  public hostname: string = location.hostname;

  public open(method: string, usage: string[] = ['All']) {
    let protocol;
    if (location.protocol != 'https:') {
      protocol = 'ws';
    } else {
      protocol = 'wss';
    }

    let replay = new ReplaySubject();

    // bind the callbacks to the subscriber
    let observable = Observable.create(
      (obs) => {
        // connect to server endpoint
        let ws = new WebSocket(`${protocol}://${this.hostname}${this.port}/montigem-be/websocket/${Token.GetToken().jwt}/${method}/${usage.join(';')}`);

        let sub;
        ws.onopen = () => {
          // websocket is now connected

          // register callback to send message to the server
          // call ...open(...).next('msg')
          replay.subscribe(msg => {
            sub = ws.send(msg);
          });
        };
        ws.onmessage = obs.next.bind(obs);
        ws.onerror = obs.error.bind(obs);
        ws.onclose = obs.complete.bind(obs);

        // close session on unsubscribe
        return () => {
          ws && ws.readyState === WebSocket.OPEN && ws.close();
          sub && sub.dispose();
        }
      }
    );

    return Subject.create(replay, observable);
  }

}