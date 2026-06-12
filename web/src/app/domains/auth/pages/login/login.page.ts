import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Session } from '../../../../core/state/session.service';
import { LogoComponent } from '../../../../shared/logo/logo.component';

@Component({
  imports: [FormsModule, RouterLink, LogoComponent],
  templateUrl: './login.page.html',
})
export class LoginPage {
  readonly email = signal('');
  readonly password = signal('');
  readonly rememberMe = signal(true);
  readonly error = signal<string | null>(null);

  constructor(
    private readonly session: Session,
    private readonly router: Router,
  ) {}

  async submit(): Promise<void> {
    this.error.set(null);
    if (this.email().trim() && this.password().trim()) {
      try {
        await this.session.login(this.email().trim(), this.password().trim());
      } catch (err: any) {
        this.error.set(err.message || 'Une erreur est survenue lors de la connexion.');
      }
    }
  }

  enterDirectly(): void {
    void this.router.navigateByUrl('/snippets/feed');
  }
}
