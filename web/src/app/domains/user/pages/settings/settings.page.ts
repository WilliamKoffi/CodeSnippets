import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Catalog } from '../../../../core/state/catalog.service';
import { Session } from '../../../../core/state/session.service';
import { User } from '../../../../shared/types';

@Component({
  imports: [FormsModule],
  templateUrl: './settings.page.html',
})
export class SettingsPage {
  readonly name = signal('');
  readonly handle = signal('');
  readonly role = signal<User['role']>('');
  readonly level = signal('');
  readonly success = signal(false);
  readonly error = signal<string | null>(null);

  constructor(
    private readonly session: Session,
    private readonly catalog: Catalog,
  ) {
    const user = this.session.active();
    this.name.set(user.name);
    this.handle.set(user.handle);
    this.role.set(user.role);
    this.level.set(user.level ?? '');
  }

  async save(): Promise<void> {
    this.success.set(false);
    this.error.set(null);
    try {
      await this.session.update({
        ...this.session.active(),
        name: this.name().trim() || this.session.active().name,
        handle: this.handle().replace('@', '').trim() || this.session.active().handle,
        role: this.role(),
        level: this.level().trim(),
      });
      this.success.set(true);
    } catch (err: any) {
      this.error.set(err.message || 'Une erreur est survenue lors de la sauvegarde.');
    }
  }

  resetData(): void {
    this.catalog.purge();
    this.session.logout();
  }
}
