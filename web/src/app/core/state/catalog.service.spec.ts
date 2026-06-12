import { TestBed } from '@angular/core/testing';
import { Catalog } from './catalog.service';
import { StorageService } from '../storage.service';
import { Router } from '@angular/router';

describe('Catalog Service', () => {
  let service: Catalog;
  let storageMock: any;
  let routerMock: any;

  beforeEach(() => {
    storageMock = {
      read: (key: string, fallback: any) => fallback,
      write: () => {},
      remove: () => {},
      clearAppData: () => {},
    };
    routerMock = {
      navigateByUrl: () => Promise.resolve(true),
    };

    TestBed.configureTestingModule({
      providers: [
        Catalog,
        { provide: StorageService, useValue: storageMock },
        { provide: Router, useValue: routerMock },
      ],
    });

    service = TestBed.inject(Catalog);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should like and unlike a snippet', () => {
    const items = service.items();
    expect(items.length).toBeGreaterThan(0);

    const first = items[0];
    const initialLikes = first.likes;
    const initialLikedState = Boolean(first.isLikedByMe);

    // Like
    service.like(first.id);
    let updated = service.items().find((item) => item.id === first.id)!;
    expect(updated.isLikedByMe).toBe(!initialLikedState);
    expect(updated.likes).toBe(initialLikes + (initialLikedState ? -1 : 1));

    // Unlike
    service.like(first.id);
    updated = service.items().find((item) => item.id === first.id)!;
    expect(updated.isLikedByMe).toBe(initialLikedState);
    expect(updated.likes).toBe(initialLikes);
  });

  it('should upvote, downvote, toggle and cancel solution votes', () => {
    const items = service.items();
    // find a snippet with solutions
    const snippet = items.find((item) => item.solutions && item.solutions.length > 0);
    expect(snippet).toBeDefined();

    const snippetId = snippet!.id;
    const solution = snippet!.solutions![0];
    const solutionId = solution.id;
    const initialVotes = solution.votes;

    // Upvote
    service.voteSolution(snippetId, solutionId, 'up');
    let updatedSol = service.items()
      .find((item) => item.id === snippetId)!
      .solutions!.find((sol) => sol.id === solutionId)!;
    expect(updatedSol.voted).toBe('up');
    expect(updatedSol.votes).toBe(initialVotes + 1);

    // Cancel Upvote (click Up again)
    service.voteSolution(snippetId, solutionId, 'up');
    updatedSol = service.items()
      .find((item) => item.id === snippetId)!
      .solutions!.find((sol) => sol.id === solutionId)!;
    expect(updatedSol.voted).toBeNull();
    expect(updatedSol.votes).toBe(initialVotes);

    // Downvote
    service.voteSolution(snippetId, solutionId, 'down');
    updatedSol = service.items()
      .find((item) => item.id === snippetId)!
      .solutions!.find((sol) => sol.id === solutionId)!;
    expect(updatedSol.voted).toBe('down');
    expect(updatedSol.votes).toBe(initialVotes - 1);

    // Toggle Downvote to Upvote
    service.voteSolution(snippetId, solutionId, 'up');
    updatedSol = service.items()
      .find((item) => item.id === snippetId)!
      .solutions!.find((sol) => sol.id === solutionId)!;
    expect(updatedSol.voted).toBe('up');
    expect(updatedSol.votes).toBe(initialVotes + 1);
  });

  it('should fetch snippets and tags from backend on load', async () => {
    const mockSnippets = [
      { id: '1', title: 'Snippet 1', description: 'Desc 1', code: '', language: 'js', tags: ['react'], author: { name: 'Ada', handle: 'ada', avatar: '', reputation: '100' }, likes: 5, solutionsCount: 0, type: 'snippet' }
    ];
    const mockTags = [{ name: 'react', count: '1' }];

    const originalFetch = globalThis.fetch;
    globalThis.fetch = ((url: string) => {
      if (url.includes('/snippets')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockSnippets)
        } as any);
      }
      if (url.includes('/tags')) {
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockTags)
        } as any);
      }
      return Promise.reject(new Error('Unknown URL'));
    }) as any;

    try {
      await service.load();

      expect(service.items().length).toBe(1);
      expect(service.items()[0].title).toBe('Snippet 1');
      expect(service.tags().length).toBe(1);
      expect(service.tags()[0].name).toBe('react');
    } finally {
      globalThis.fetch = originalFetch;
    }
  });
});
