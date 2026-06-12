import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Catalog } from '../../../core/state/catalog.service';
import { Workspace } from '../../../core/state/workspace.service';

@Component({
  selector: 'app-trending-tags-modal',
  templateUrl: './trending-tags.component.html',
})
export class TrendingTagsModalComponent {
  constructor(
    readonly catalog: Catalog,
    readonly workspace: Workspace,
    private readonly router: Router,
  ) {}

  selectTag(tag: string | null): void {
    this.catalog.active.set(tag);
    this.workspace.drawer.set(false);
    void this.router.navigateByUrl('/snippets/feed');
  }
}
