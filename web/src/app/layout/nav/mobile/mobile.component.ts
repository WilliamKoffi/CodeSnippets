import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { LucideAngularModule, Home, Settings, Plus, User } from 'lucide-angular';

@Component({
  selector: 'app-mobile-nav',
  imports: [
    RouterLink,
    RouterLinkActive,
    LucideAngularModule,
  ],
  templateUrl: './mobile.component.html',
})
export class MobileNavComponent {}
