import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class Workspace {
  readonly docs = signal(false);
  readonly support = signal(false);
  readonly drawer = signal(false);

  read(): void {
    this.docs.set(true);
  }

  help(): void {
    this.support.set(true);
  }

  browse(): void {
    this.drawer.set(true);
  }
}
