import { Routes } from '@angular/router';

export const SNIPPETS_ROUTES: Routes = [
  {
    path: 'feed',
    loadComponent: () => import('./pages/feed/feed.page').then((m) => m.FeedPage),
  },
  {
    path: 'new',
    loadComponent: () => import('./pages/snippet/submit/submit.page').then((m) => m.SubmitPage),
  },
  {
    path: ':id',
    loadComponent: () => import('./pages/snippet/detail/detail.page').then((m) => m.DetailPage),
  },
  {
    path: '',
    redirectTo: 'feed',
    pathMatch: 'full',
  },
];
