import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatSidenavModule} from '@angular/material/sidenav';
import {HttpClientModule} from '@angular/common/http';
import {MatCardModule} from '@angular/material/card';
import {MatListModule} from '@angular/material/list';
import { ProgressSpinnerDialogComponent } from './progress-spinner-dialog/progress-spinner-dialog.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatDialogModule} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';

@NgModule({
  declarations: [
    AppComponent,
    ProgressSpinnerDialogComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatSidenavModule,
    MatCardModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
