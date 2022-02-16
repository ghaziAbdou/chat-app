import {Injectable} from '@angular/core';
import {Client} from "@stomp/stompjs";
import {AccountService} from "@app/_services/account.service";
import {environment} from "@environments/environment";

@Injectable({ providedIn: 'root' })
export class WsService {

  user
  client: Client = undefined;

  constructor(private accountService: AccountService) {

    this.accountService.user.subscribe((user) => {
      this.user = user;

    })
  }

  connectWs(): Client {
    console.log('connect ws');
    const client = new Client({
      brokerURL: `${environment.apiUrl.replace('http', 'ws')}/stomp`,
      connectHeaders: {
        Authorization: "Bearer " + this.user.token
      },
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    console.log(client.brokerURL);

    client.onStompError = function (frame) {
      // Will be invoked in case of error encountered at Broker
      // Bad login/passcode typically will cause an error
      // Complaint brokers will set `message` header with a brief message. Body may contain details.
      // Compliant brokers will terminate the connection after any error
      console.log('Broker reported error: ' + frame.headers['message']);
      console.log('Additional details: ' + frame.body);
    };

    this.client = client;
    return client;
  }

  computeClient() {
    if (this.client === undefined) {
      this.connectWs();
      this.client.activate();
    }
    return this.client;
  }
}
