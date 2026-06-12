import { Component, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Catalog } from '../../../../core/state/catalog.service';
import { Session } from '../../../../core/state/session.service';

@Component({
  imports: [RouterLink],
  templateUrl: './profile.page.html',
})
export class ProfilePage {
  readonly authored = computed(() =>
    this.catalog.items().filter((snippet) => snippet.author.handle === this.session.active().handle || snippet.author.isAuthor),
  );
  readonly saved = computed(() => this.catalog.items().filter((snippet) => snippet.isSavedByMe));

  constructor(
    readonly session: Session,
    readonly catalog: Catalog,
  ) {
    void this.session.refresh();
  }
}
