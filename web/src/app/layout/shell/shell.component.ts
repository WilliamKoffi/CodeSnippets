import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { MobileNavComponent } from '../nav/mobile/mobile.component';
import { SidebarComponent } from '../nav/sidebar/sidebar.component';
import { DocsModalComponent } from '../../shared/modal/docs/docs.component';
import { SupportModalComponent } from '../../shared/modal/support/support.component';
import { TrendingTagsModalComponent } from '../../shared/modal/trending-tags/trending-tags.component';

@Component({
  selector: 'app-shell',
  imports: [
    RouterOutlet,
    HeaderComponent,
    SidebarComponent,
    MobileNavComponent,
    DocsModalComponent,
    SupportModalComponent,
    TrendingTagsModalComponent,
  ],
  templateUrl: './shell.component.html',
})
export class ShellComponent {}
