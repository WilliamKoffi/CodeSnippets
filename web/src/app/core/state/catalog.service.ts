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
      const currentUser = this.storage.read<any>('cs_user', null);
      const viewerId = currentUser ? currentUser.id : '';
      const snippetsRes = await fetch(`http://localhost:8080/api/snippets?viewer=${viewerId}`);
      if (snippetsRes.ok) {
        const backendSnippetsRaw = await snippetsRes.json();

        const backendSnippets: Snippet[] = backendSnippetsRaw.map((bItem: any) => {
          return {
            id: bItem.id,
            title: bItem.title,
            description: bItem.description,
            code: bItem.code,
            language: bItem.language,
            tags: bItem.tags,
            author: {
              name: bItem.author.name,
              handle: bItem.author.handle,
              avatar: bItem.author.avatar,
              reputation: bItem.author.reputation,
              isAuthor: bItem.author.owner || (currentUser && bItem.author.handle === currentUser.handle),
            },
            likes: bItem.likes,
            solutionsCount: bItem.tally !== undefined ? bItem.tally : bItem.solutionsCount,
            createdAt: bItem.age !== undefined ? bItem.age : bItem.createdAt,
            type: bItem.type,
            solutions: (bItem.solutions || []).map((sol: any) => ({
              id: sol.id,
              author: {
                name: sol.author.name,
                avatar: sol.author.avatar,
                reputation: sol.author.reputation,
                isAuthor: sol.author.isAuthor,
              },
              votes: sol.votes,
              content: sol.content,
              accepted: sol.accepted,
              code: sol.code || undefined,
              createdAt: sol.createdAt,
              voted: sol.voted,
            })),
            isLikedByMe: bItem.liked !== undefined ? bItem.liked : bItem.isLikedByMe,
            isSavedByMe: bItem.saved !== undefined ? bItem.saved : bItem.isSavedByMe,
          };
        });
        
        // Preserve local state for likes, saves, solutions, and votes
        const localSnippets = this.storage.read<Snippet[]>('cs_snippets', []);
        const merged = backendSnippets.map((bItem) => {
          const localItem = localSnippets.find((l) => l.id === bItem.id);
          if (localItem) {
            return {
              ...bItem,
              likes: localItem.likes !== undefined ? localItem.likes : bItem.likes,
              isLikedByMe: localItem.isLikedByMe !== undefined ? localItem.isLikedByMe : bItem.isLikedByMe,
              isSavedByMe: localItem.isSavedByMe !== undefined ? localItem.isSavedByMe : bItem.isSavedByMe,
              solutionsCount: localItem.solutionsCount !== undefined ? localItem.solutionsCount : bItem.solutionsCount,
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
    const currentUser = this.storage.read<any>('cs_user', null);
    const userId = currentUser ? currentUser.id : 'current_user';

    const item = this.items().find((x) => x.id === id);
    if (!item) return;

    const liked = Boolean(item.isLikedByMe);

    // Optimistic update
    this.store(
      this.items().map((item) => {
        if (item.id !== id) return item;
        return {
          ...item,
          isLikedByMe: !liked,
          likes: liked ? item.likes - 1 : item.likes + 1,
        };
      }),
    );

    // Fire-and-forget backend call with revert
    fetch(`http://localhost:8080/api/snippets/${id}/likes?userId=${userId}`, {
      method: liked ? 'DELETE' : 'POST',
    }).catch((e) => {
      console.error('Error updating like on backend:', e);
      // Revert optimistic state
      this.store(
        this.items().map((item) => {
          if (item.id !== id) return item;
          return {
            ...item,
            isLikedByMe: liked,
            likes: liked ? item.likes + 1 : item.likes - 1,
          };
        }),
      );
    });
  }

  save(id: string): void {
    const currentUser = this.storage.read<any>('cs_user', null);
    const userId = currentUser ? currentUser.id : 'current_user';

    const item = this.items().find((x) => x.id === id);
    if (!item) return;

    const saved = Boolean(item.isSavedByMe);

    // Optimistic update
    this.store(
      this.items().map((item) =>
        item.id === id ? { ...item, isSavedByMe: !saved } : item,
      ),
    );

    // Fire-and-forget backend call with revert
    fetch(`http://localhost:8080/api/snippets/${id}/bookmarks?userId=${userId}`, {
      method: saved ? 'DELETE' : 'POST',
    }).catch((e) => {
      console.error('Error updating bookmark on backend:', e);
      // Revert optimistic state
      this.store(
        this.items().map((item) =>
          item.id === id ? { ...item, isSavedByMe: saved } : item,
        ),
      );
    });
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

  async solve(id: string, draft: Omit<Solution, 'id' | 'createdAt'>): Promise<void> {
    try {
      const currentUser = this.storage.read<any>('cs_user', null);
      const authorId = currentUser ? currentUser.id : 'current_user';

      const response = await fetch(`http://localhost:8080/api/snippets/${id}/solutions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          author: authorId,
          content: draft.content,
          code: draft.code || null
        })
      });

      if (!response.ok) {
        throw new Error('Failed to submit solution to backend');
      }

      const backendSol = await response.json();
      const newSolution: Solution = {
        id: backendSol.id,
        author: {
          name: backendSol.author.name,
          avatar: backendSol.author.avatar,
          reputation: backendSol.author.reputation,
          isAuthor: backendSol.author.isAuthor,
        },
        votes: backendSol.votes,
        content: backendSol.content,
        accepted: backendSol.accepted,
        code: backendSol.code || undefined,
        createdAt: backendSol.createdAt,
      };

      this.store(
        this.items().map((item) => {
          if (item.id !== id) return item;
          return {
            ...item,
            solutionsCount: item.solutionsCount + 1,
            solutions: [...(item.solutions ?? []), newSolution],
          };
        }),
      );
    } catch (e) {
      console.error('Error submitting solution:', e);
      // Fallback local creation
      const localSolution: Solution = {
        ...draft,
        id: `solution_${Date.now()}`,
        createdAt: "À l'instant",
      };

      this.store(
        this.items().map((item) => {
          if (item.id !== id) return item;
          return {
            ...item,
            solutionsCount: item.solutionsCount + 1,
            solutions: [...(item.solutions ?? []), localSolution],
          };
        }),
      );
    }
  }

  async acceptSolution(snippetId: string, solutionId: string): Promise<void> {
    try {
      const currentUser = this.storage.read<any>('cs_user', null);
      const viewerId = currentUser ? currentUser.id : 'current_user';

      const response = await fetch(`http://localhost:8080/api/solutions/${solutionId}/accept?viewer=${viewerId}`, {
        method: 'PUT'
      });

      if (!response.ok) {
        throw new Error('Failed to accept solution on backend');
      }

      const backendSol = await response.json();

      this.store(
        this.items().map((item) => {
          if (item.id !== snippetId) return item;
          const updatedSolutions = (item.solutions ?? []).map((sol) => {
            if (sol.id !== solutionId) return sol;
            return {
              ...sol,
              accepted: backendSol.accepted,
            };
          });
          return {
            ...item,
            solutions: updatedSolutions,
          };
        })
      );
    } catch (e) {
      console.error('Error accepting solution:', e);
      // Fallback local accept behavior
      this.store(
        this.items().map((item) => {
          if (item.id !== snippetId) return item;
          const updatedSolutions = (item.solutions ?? []).map((sol) => {
            if (sol.id !== solutionId) return sol;
            return {
              ...sol,
              accepted: true,
            };
          });
          return {
            ...item,
            solutions: updatedSolutions,
          };
        })
      );
    }
  }

  voteSolution(snippetId: string, solutionId: string, direction: 'up' | 'down'): void {
    const currentUser = this.storage.read<any>('cs_user', null);
    const userId = currentUser ? currentUser.id : 'current_user';

    const item = this.items().find((x) => x.id === snippetId);
    if (!item) return;

    const sol = (item.solutions ?? []).find((s) => s.id === solutionId);
    if (!sol) return;

    const prevVoted = sol.voted;
    const prevVotes = sol.votes;

    let newVoted: 'up' | 'down' | null = direction;
    let voteDiff = 0;

    if (sol.voted === direction) {
      newVoted = null;
      voteDiff = direction === 'up' ? -1 : 1;
    } else {
      const prevWeight = sol.voted === 'up' ? 1 : sol.voted === 'down' ? -1 : 0;
      const newWeight = direction === 'up' ? 1 : -1;
      voteDiff = newWeight - prevWeight;
    }

    // Optimistic update
    this.store(
      this.items().map((it) => {
        if (it.id !== snippetId) return it;
        const updatedSolutions = (it.solutions ?? []).map((s) => {
          if (s.id !== solutionId) return s;
          return {
            ...s,
            voted: newVoted,
            votes: s.votes + voteDiff,
          };
        });
        return {
          ...it,
          solutions: updatedSolutions,
        };
      }),
    );

    // Fire-and-forget backend call
    const isCancel = sol.voted === direction;
    const url = `http://localhost:8080/api/solutions/${solutionId}/votes?userId=${userId}` + (isCancel ? '' : `&direction=${direction}`);

    fetch(url, {
      method: isCancel ? 'DELETE' : 'POST',
    }).catch((e) => {
      console.error('Error updating vote on backend:', e);
      // Revert optimistic state
      this.store(
        this.items().map((it) => {
          if (it.id !== snippetId) return it;
          const updatedSolutions = (it.solutions ?? []).map((s) => {
            if (s.id !== solutionId) return s;
            return {
              ...s,
              voted: prevVoted,
              votes: prevVotes,
            };
          });
          return {
            ...it,
            solutions: updatedSolutions,
          };
        }),
      );
    });
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
