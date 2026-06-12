import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { Session } from '../../../core/state/session.service';
import { Workspace } from '../../../core/state/workspace.service';
import { LucideAngularModule, Home, User, Tag, Settings, BookOpen, HelpCircle, LogOut, Plus } from 'lucide-angular';

@Component({
  selector: 'app-sidebar',
  imports: [
    RouterLink,
    RouterLinkActive,
    LucideAngularModule,
  ],
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent {
  constructor(
    readonly session: Session,
    readonly workspace: Workspace,
  ) {}
}
