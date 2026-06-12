import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Catalog } from '../../../../core/state/catalog.service';
import { Snippet } from '../../../../shared/types';

type FeedFilter = 'latest' | 'old' | 'discussed';

@Component({
  imports: [FormsModule, RouterLink],
  templateUrl: './feed.page.html',
})
export class FeedPage {
  readonly filter = signal<FeedFilter>('latest');
  readonly tabs: FeedFilter[] = ['latest', 'old', 'discussed'];
  readonly snippets = computed(() => {
    const items = [...this.catalog.filtered()];
    if (this.filter() === 'discussed') {
      return items.sort((a, b) => b.solutionsCount - a.solutionsCount);
    }
    if (this.filter() === 'old') {
      return items.reverse();
    }
    return items;
  });

  constructor(
    readonly catalog: Catalog,
    private readonly router: Router,
  ) {}

  open(snippet: Snippet): void {
    void this.router.navigate(['/snippets', snippet.id]);
  }
}
