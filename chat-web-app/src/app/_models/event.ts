import {User} from "@app/_models/user";

export class Event {
  id: number;
  user: User;
  type: string;
  createdAt: Date;
}
