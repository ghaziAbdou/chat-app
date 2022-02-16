import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

import { environment } from '@environments/environment';
import {MessageResponse, User} from '@app/_models';

@Injectable({ providedIn: 'root' })
export class ChatService {

    constructor(
        private router: Router,
        private http: HttpClient) {

    }

    getMessages(recipientId, cursor, search = undefined) {
      let url = `${environment.apiUrl}/users/me/messages?recipientId=${recipientId}`;
      if (cursor !== undefined) {
        url+=`&cursor=${cursor}`;
      }
      if (search !== undefined) {
        url+=`&search=${search}`;
      }
        return this.http.get<MessageResponse[]>(url);
    }

    getEvents(cursor, userId = undefined) {
      let url = `${environment.apiUrl}/events`;
      if (cursor !== undefined) {
        url+=`?cursor=${cursor}`;
      }
      if (userId !== undefined) {
        url+=`&userId=${userId}`;
      }
        return this.http.get<MessageResponse[]>(url);
    }

    send(recipientId, content) {
        return this.http.post(`${environment.apiUrl}/users/me/messages`, {recipientId, content});
    }
}
