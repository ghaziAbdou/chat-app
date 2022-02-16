import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LogsComponent } from './logs.component';
import {LogsRoutingModule} from "./logs-routing.module";



@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    LogsRoutingModule
  ],
  declarations: [
  LogsComponent]
})
export class LogsModule { }
