import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';

import {Message, MessageResponse, User} from '@app/_models';
import {AccountService, AlertService, ChatService, WsService} from '@app/_services';
import {first} from "rxjs/operators";
import {ToastService} from "@app/toasts-container/toast-service";

@Component({templateUrl: 'home.component.html'})
export class HomeComponent implements OnInit {
  @ViewChild('messagesHistory') private messagesHistoryScroll: ElementRef;

  user: User;
  colors = ["#001f3f", "#0074D9", "#7FDBFF", "#39CCCC", "#B10DC9", "#F012BE", "#FFDC00", "#FF851B", "#FF4136", "#3D9970", "#2ECC40", "#01FF70"]
  recipient = undefined;


  loading = false;
  messages: Message[] = []
  users: User[] = []

  constructor(
    private accountService: AccountService,
    private chatService: ChatService,
    private alertService: AlertService,
    private toastService: ToastService,
    private wsService: WsService) {
    this.user = this.accountService.userValue;
  }

  ngOnInit() {
    this.accountService.getAll()
      .pipe(first())
      .subscribe((result: any) => {
        this.users = result.content;


        const user = this.user;
        const that = this;

        this.wsService.computeClient().onConnect = function (frame) {
          console.log('onConnect');
          that.wsService.client.subscribe('/queue/' + user.id + '/messages', (message => {
            const m = JSON.parse(message.body);
            console.log(m);
            const ms = that.map(m);
            that.pushMessages([m]);
            that.toastService.show(ms.content, { delay: 10000 , classname: 'bg-info text-light', header : 'new message from ' + ms.sender.name});
            that.scrollToEndMessages();
          }));
          that.wsService.client.subscribe('/queue/all/events', (m  => {
            const message = JSON.parse(m.body);
            console.log(message);
            if (message.userId !== that.user.id) {
              const user = that.findUser(message.userId);
              const type = message.type.replace('USER_' ,'').toLowerCase();
              let c;
              switch (type) {
                case 'connected':
                  c = 'bg-success text-light';
                  break;
                case 'disconnected':
                  c = 'bg-danger text-light';
                  break;
                default:
                case 'registred':
                  c = 'bg-primary text-light';
                  break;
              }

              that.toastService.show(user?.name + ' ' + type, { delay: 10000 , classname: c});

            }
          }));
        };
      });
  }

  selectRecipient(user) {
    this.messages = [];
    this.recipient = user;
    this.getMessages();
  }


  getMessages() {

    let cursor = Math.min(...this.messages.map(m => m.id), Number.MAX_VALUE)
    if (cursor === 0 || cursor === Number.MAX_VALUE) {
      cursor = undefined;
    }
    if (this.recipient && !this.loading) {
      this.loading = true;
      this.chatService.getMessages(this.recipient.id, cursor)
        .pipe(first())
        .subscribe({
          next: (result: []) => {
            this.loading = false;
            this.pushMessages(result);
          },
          error: error => {
            this.loading = false;
            this.alertService.error(error);
          }
        });
    }
  }

  sendMessage(message) {
    console.log('send message ' + message);
    if (this.recipient) {
      this.chatService.send(this.recipient.id, message)
        .pipe(first())
        .subscribe({
          next: (m: MessageResponse) => {
            this.pushMessages([m]);
            this.scrollToEndMessages();
            return m;
          },
          error: error => {
            this.alertService.error(error);
          }
        });
    }
  }

  findUser(userId) {
    return this.users.find(u => u.id === userId);
  }

  colorFor(letter) {
    return letter ? this.colors[letter.charCodeAt(0) % this.colors.length] : this.colors[0];
  }

  getVisibleUsers() {
    return this.users.filter(u => u.id !== this.user.id);
  }

  map(m: any): Message {
    const ms: Message = new Message();
    ms.id = m.id;
    ms.sender = this.findUser(m.senderId);
    ms.recipient = this.findUser(m.recipientId);
    ms.content = m.content;
    ms.sentAt = m.sentAt;
    return ms;
  }

  pushMessages(messages: any[]) {
    messages = messages.map(m => this.map(m))
    this.messages = this.messages.concat(messages)
      .sort((a, b) => a.id - b.id)
      .filter(function (item, pos, self) {
        return self.indexOf(item) == pos;
      });
  }

  onScroll(event) {
    if (event.target.scrollTop <= 0) {
      this.getMessages();
    }
  }
  scrollToEndMessages(){
    try {
      this.messagesHistoryScroll.nativeElement.scrollTop = this.messagesHistoryScroll.nativeElement.scrollHeight;
    } catch(err) { }
    console.log(this.messagesHistoryScroll.nativeElement.scrollTop);
  }
}
