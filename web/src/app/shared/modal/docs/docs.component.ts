import { Component } from '@angular/core';
import { Workspace } from '../../../core/state/workspace.service';

@Component({
  selector: 'app-docs-modal',
  templateUrl: './docs.component.html',
})
export class DocsModalComponent {
  constructor(readonly workspace: Workspace) {}
}

