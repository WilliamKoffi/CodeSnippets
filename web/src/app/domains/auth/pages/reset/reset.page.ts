import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Session } from '../../../../core/state/session.service';
import { LogoComponent } from '../../../../shared/logo/logo.component';

@Component({
  imports: [FormsModule, RouterLink, LogoComponent],
  templateUrl: './reset.page.html',
})
export class ResetPage {
  private readonly session = inject(Session);

  readonly email = signal('');
  readonly sent = signal(false);
  readonly error = signal<string | null>(null);

  async submit(): Promise<void> {
    this.error.set(null);
    this.sent.set(false);
    
    if (this.email().trim()) {
      try {
        await this.session.resetPassword(this.email().trim());
        this.sent.set(true);
      } catch (err: any) {
        this.error.set(err.message || 'Une erreur est survenue lors de la réinitialisation.');
      }
    }
  }
}
