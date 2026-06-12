import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Catalog } from '../../core/state/catalog.service';
import { Session } from '../../core/state/session.service';
import { LogoComponent } from '../../shared/logo/logo.component';

@Component({
  selector: 'app-header',
  imports: [FormsModule, RouterLink, LogoComponent],
  templateUrl: './header.component.html',
})
export class HeaderComponent {
  constructor(
    readonly catalog: Catalog,
    readonly session: Session,
  ) {}
}
