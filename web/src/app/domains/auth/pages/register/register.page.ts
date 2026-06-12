import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Session } from '../../../../core/state/session.service';
import { LogoComponent } from '../../../../shared/logo/logo.component';

@Component({
  imports: [FormsModule, RouterLink, LogoComponent],
  templateUrl: './register.page.html',
})
export class RegisterPage {
  readonly fullName = signal('');
  readonly handle = signal('');
  readonly email = signal('');
  readonly role = signal('');
  readonly password = signal('');
  readonly roles = ['frontend', 'backend', 'fullstack', 'devops'];
  readonly strength = computed(() => {
    const password = this.password();
    if (!password) return 'None';
    if (password.length >= 10 && /[^A-Za-z0-9]/.test(password)) return 'High';
    if (password.length >= 8 && /[A-Z]/.test(password) && /[0-9]/.test(password)) return 'Medium';
    return 'Low';
  });

  readonly error = signal<string | null>(null);

  constructor(
    private readonly session: Session,
    private readonly router: Router,
  ) {}

  async submit(): Promise<void> {
    this.error.set(null);
    if (!this.fullName().trim() || !this.handle().trim() || !this.email().trim() || !this.password().trim()) return;
    try {
      await this.session.register({
        name: this.fullName().trim(),
        handle: this.handle().trim(),
        email: this.email().trim(),
        password: this.password().trim(),
        role: this.role() || 'fullstack',
      });
    } catch (err: any) {
      this.error.set(err.message || 'Une erreur est survenue lors de la création du compte.');
    }
  }

  enterDirectly(): void {
    void this.router.navigateByUrl('/snippets/feed');
  }
}
