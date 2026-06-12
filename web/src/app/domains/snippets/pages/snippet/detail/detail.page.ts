import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Catalog } from '../../../../../core/state/catalog.service';
import { Session } from '../../../../../core/state/session.service';
import { INITIAL_USER } from '../../../../../shared/seed-data';
import { Solution } from '../../../../../shared/types';

@Component({
  imports: [FormsModule, RouterLink],
  templateUrl: './detail.page.html',
})
export class DetailPage {
  readonly text = signal('');
  readonly code = signal('');
  readonly snippet = computed(() => this.catalog.find(this.route.snapshot.paramMap.get('id')));

  constructor(
    readonly catalog: Catalog,
    private readonly session: Session,
    private readonly route: ActivatedRoute,
  ) {}

  addSolution(): void {
    const snippet = this.snippet();
    if (!snippet || !this.text().trim()) return;

    const solution: Omit<Solution, 'id' | 'createdAt'> = {
      author: {
        name: this.session.active().name || INITIAL_USER.name,
        avatar: this.session.active().avatar,
        reputation: `${Math.round(this.session.active().reputation / 100) / 10}k`,
      },
      votes: 1,
      content: this.text(),
      code: this.code() || undefined,
      accepted: false,
    };

    this.catalog.solve(snippet.id, solution);
    this.text.set('');
    this.code.set('');
  }
}
