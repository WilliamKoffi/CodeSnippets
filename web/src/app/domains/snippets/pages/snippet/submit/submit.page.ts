import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Session } from '../../../../../core/state/session.service';
import { Catalog } from '../../../../../core/state/catalog.service';
import { Snippet } from '../../../../../shared/types';

@Component({
  imports: [FormsModule, RouterLink],
  templateUrl: './submit.page.html',
})
export class SubmitPage {
  readonly title = signal('');
  readonly description = signal('');
  readonly code = signal('');
  readonly language = signal('typescript');
  readonly tags = signal('');
  readonly type = signal<'bug' | 'snippet'>('bug');

  constructor(
    private readonly session: Session,
    private readonly catalog: Catalog,
  ) {}

  submit(): void {
    if (!this.title().trim() || !this.description().trim() || !this.code().trim()) return;
    const user = this.session.active();
    const snippet: Omit<Snippet, 'id' | 'createdAt' | 'likes' | 'solutionsCount' | 'solutions'> = {
      title: this.title(),
      description: this.description(),
      code: this.code(),
      language: this.language(),
      tags: this.tags().split(',').map((tag) => tag.trim()).filter(Boolean),
      type: this.type(),
      author: {
        name: user.name,
        handle: user.handle,
        avatar: user.avatar,
        reputation: String(user.reputation),
        isAuthor: true,
      },
    };
    this.catalog.create(snippet);
  }
}
