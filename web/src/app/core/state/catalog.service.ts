import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { INITIAL_SNIPPETS, INITIAL_TAGS } from '../../shared/seed-data';
import { Snippet, Solution } from '../../shared/types';
import { StorageService } from '../storage.service';

@Injectable({ providedIn: 'root' })
export class Catalog {
  private readonly storage = inject(StorageService);
  private readonly router = inject(Router);

  readonly items = signal<Snippet[]>(this.storage.read('cs_snippets', INITIAL_SNIPPETS));
  readonly tags = signal(this.storage.read('cs_tags', INITIAL_TAGS));

  readonly query = signal('');
  readonly active = signal<string | null>(null);

  readonly filtered = computed(() => {
    const search = this.query().toLowerCase().trim();
    const selection = this.active();

    return this.items().filter((item) => {
      const matched =
        !search ||
        item.title.toLowerCase().includes(search) ||
        item.description.toLowerCase().includes(search) ||
        item.tags.some((tag) => tag.toLowerCase().includes(search));
      
      const categorized = selection ? item.tags.includes(selection) : true;

      return matched && categorized;
    });
  });

  constructor() {
    void this.load();
  }

  async load(): Promise<void> {
    try {
      const snippetsRes = await fetch('http://localhost:8080/api/snippets');
      if (snippetsRes.ok) {
        const backendSnippets: Snippet[] = await snippetsRes.json();
        
        // Preserve local state for likes, saves, solutions, and votes
        const localSnippets = this.storage.read<Snippet[]>('cs_snippets', []);
        const merged = backendSnippets.map((bItem) => {
          const localItem = localSnippets.find((l) => l.id === bItem.id);
          if (localItem) {
            return {
              ...bItem,
              likes: localItem.likes,
              isLikedByMe: localItem.isLikedByMe,
              isSavedByMe: localItem.isSavedByMe,
              solutionsCount: localItem.solutionsCount,
              solutions: localItem.solutions || bItem.solutions,
            };
          }
          return bItem;
        });

        this.items.set(merged);
        this.storage.write('cs_snippets', merged);
      }
      await this.loadTags();
    } catch (error) {
      console.error('Failed to load catalog data from backend:', error);
    }
  }

  async loadTags(): Promise<void> {
    try {
      const tagsRes = await fetch('http://localhost:8080/api/tags');
      if (tagsRes.ok) {
        const backendTags = await tagsRes.json();
        this.tags.set(backendTags);
        this.storage.write('cs_tags', backendTags);
      }
    } catch (error) {
      console.error('Failed to load tags from backend:', error);
    }
  }

  find(id: string | null): Snippet | undefined {
    return this.items().find((item) => item.id === id) ?? this.items()[0];
  }

  like(id: string): void {
    this.store(
      this.items().map((item) => {
        if (item.id !== id) return item;
        const liked = Boolean(item.isLikedByMe);
        return {
          ...item,
          isLikedByMe: !liked,
          likes: liked ? item.likes - 1 : item.likes + 1,
        };
      }),
    );
  }

  save(id: string): void {
    this.store(
      this.items().map((item) =>
        item.id === id ? { ...item, isSavedByMe: !item.isSavedByMe } : item,
      ),
    );
  }

  async create(draft: Omit<Snippet, 'id' | 'createdAt' | 'likes' | 'solutionsCount' | 'solutions'>): Promise<void> {
    try {
      const currentUser = this.storage.read<any>('cs_user', null);
      const authorId = currentUser ? currentUser.id : 'current_user';

      const response = await fetch('http://localhost:8080/api/snippets', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          title: draft.title,
          description: draft.description,
          code: draft.code,
          language: draft.language,
          type: draft.type,
          tags: draft.tags,
          authorId: authorId
        })
      });

      if (!response.ok) {
        throw new Error('Failed to create snippet on backend');
      }

      const newSnippet: Snippet = await response.json();
      this.store([newSnippet, ...this.items()]);
      void this.loadTags();

    } catch (e) {
      console.error('Error creating snippet:', e);
      // Fallback local creation
      const item: Snippet = {
        ...draft,
        id: `snippet_${Date.now()}`,
        createdAt: "À l'instant",
        likes: 0,
        solutionsCount: 0,
        solutions: [],
      };
      this.store([item, ...this.items()]);
    } finally {
      void this.router.navigateByUrl('/snippets/feed');
    }
  }

  solve(id: string, draft: Omit<Solution, 'id' | 'createdAt'>): void {
    this.store(
      this.items().map((item) => {
        if (item.id !== id) return item;

        const solution: Solution = {
          ...draft,
          id: `solution_${Date.now()}`,
          createdAt: "À l'instant",
        };

        return {
          ...item,
          solutionsCount: item.solutionsCount + 1,
          solutions: [...(item.solutions ?? []), solution],
        };
      }),
    );
  }

  voteSolution(snippetId: string, solutionId: string, direction: 'up' | 'down'): void {
    this.store(
      this.items().map((item) => {
        if (item.id !== snippetId) return item;

        const updatedSolutions = (item.solutions ?? []).map((sol) => {
          if (sol.id !== solutionId) return sol;

          let newVoted: 'up' | 'down' | null = direction;
          let voteDiff = 0;

          if (sol.voted === direction) {
            // Cancel the vote
            newVoted = null;
            voteDiff = direction === 'up' ? -1 : 1;
          } else {
            // New vote or direction change
            const prevWeight = sol.voted === 'up' ? 1 : sol.voted === 'down' ? -1 : 0;
            const newWeight = direction === 'up' ? 1 : -1;
            voteDiff = newWeight - prevWeight;
          }

          return {
            ...sol,
            voted: newVoted,
            votes: sol.votes + voteDiff,
          };
        });

        return {
          ...item,
          solutions: updatedSolutions,
        };
      }),
    );
  }

  purge(): void {
    this.storage.clearAppData();
    this.items.set(INITIAL_SNIPPETS);
    this.tags.set(INITIAL_TAGS);
  }

  private store(items: Snippet[]): void {
    this.items.set(items);
    this.storage.write('cs_snippets', items);
  }
}
