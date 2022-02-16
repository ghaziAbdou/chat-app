import {Component, OnInit} from '@angular/core';
import {first} from "rxjs/operators";
import {AccountService, ChatService} from "../_services";
import {Event} from "../_models";

@Component({
  selector: 'app-logs',
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.less']
})
export class LogsComponent implements OnInit {

  users = [];
  events:Event[] = [];
  loading = false;
  constructor(private accountService:AccountService, private chatService:ChatService) { }

  ngOnInit(): void {
    this.accountService.getAll()
      .pipe(first())
      .subscribe((result: any) => {
        this.users = result.content;
        this.getEvents();
      });
  }

  getEvents(userId = undefined)
  {
    let cursor = Math.min(...this.events.map(m => m.id), Number.MAX_VALUE)
    if (cursor === 0 || cursor === Number.MAX_VALUE) {
      cursor = undefined;
    }
    if (!this.loading) {
      this.loading = true;
      this.chatService.getEvents(cursor, userId).pipe(first())
        .subscribe((result: any) =>
        {
          console.log(result);
          this.pushEvents(result);
          this.loading = false;
        }, (error => {
          console.log(error);
          this.loading = false;
        }))
    }
  }

  map(m: any): Event {
    const ev: Event = new Event();
    ev.id = m.id;
    ev.user = this.findUser(m.userId);
    ev.type = m.type;
    ev.createdAt = m.createdAt;
    return ev;
  }

  pushEvents(events: any[]) {
    events = events.map(m => this.map(m))
    this.events = this.events.concat(events)
      .sort((a, b) => b.id - a.id)
      .filter(function (item, pos, self) {
        return self.indexOf(item) == pos;
      });
  }

  findUser(userId) {
    return this.users.find(u => u.id === userId);
  }

  onScroll(event) {
    if (event.target.scrollTop + event.target.offsetHeight >= event.target.scrollHeight) {
      this.getEvents();
    }
  }
}
