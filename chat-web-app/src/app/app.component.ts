import {Component} from '@angular/core';

import {AccountService, WsService} from './_services';
import {User} from './_models';

@Component({ selector: 'app', templateUrl: 'app.component.html' })
export class AppComponent {
    user: User;

    constructor(private accountService: AccountService, private wsService:WsService) {
        this.accountService.user.subscribe(x => {
          this.user = x;
        });
    }

    logout() {
        this.accountService.logout();
        if (this.wsService.client) {
          this.wsService.client.deactivate().then(() => console.log("dis-activate client"));
        }
    }
}
