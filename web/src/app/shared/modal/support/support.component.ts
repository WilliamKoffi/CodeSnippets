import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Workspace } from '../../../core/state/workspace.service';

@Component({
  selector: 'app-support-modal',
  imports: [FormsModule],
  templateUrl: './support.component.html',
})
export class SupportModalComponent {
  readonly subject = signal('');
  readonly message = signal('');
  readonly sent = signal(false);

  constructor(readonly workspace: Workspace) {}

  submit(): void {
    if (!this.subject().trim() || !this.message().trim()) {
      return;
    }
    this.sent.set(true);
    this.subject.set('');
    this.message.set('');
  }
}
