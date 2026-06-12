import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./domains/landing/pages/landing/landing.page').then((m) => m.LandingPage),
  },
  {
    path: 'auth',
    loadChildren: () => import('./domains/auth/auth.routes').then((m) => m.AUTH_ROUTES),
  },
  {
    path: 'snippets',
    loadComponent: () => import('./layout/shell/shell.component').then((m) => m.ShellComponent),
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./domains/snippets/snippets.routes').then((m) => m.SNIPPETS_ROUTES),
      },
    ],
  },
  {
    path: 'user',
    loadComponent: () => import('./layout/shell/shell.component').then((m) => m.ShellComponent),
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./domains/user/user.routes').then((m) => m.USER_ROUTES),
      },
    ],
  },
  {
    path: '**',
    redirectTo: '',
  },
];
