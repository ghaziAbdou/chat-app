import {User} from "@app/_models/user";

export class MessageResponse {
  id: number;
  senderId: string;
  recipientId: string;
  content: string;
  sentAt: Date;
}

export class Message {
  id: number;
  sender: User;
  recipient: User;
  content: string;
  sentAt: Date;
}
