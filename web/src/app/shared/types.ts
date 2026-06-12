export interface User {
  id: string;
  name: string;
  handle: string;
  avatar: string;
  reputation: number;
  role: 'frontend' | 'backend' | 'fullstack' | 'devops' | '';
  level?: string;
}

export interface Solution {
  id: string;
  author: {
    name: string;
    avatar: string;
    reputation: string;
    isAuthor?: boolean;
  };
  votes: number;
  content: string;
  accepted: boolean;
  code?: string;
  createdAt: string;
  voted?: 'up' | 'down' | null;
}

export interface Snippet {
  id: string;
  title: string;
  description: string;
  code: string;
  language: string;
  tags: string[];
  author: {
    name: string;
    handle: string;
    avatar: string;
    reputation: string;
    isAuthor?: boolean;
  };
  likes: number;
  solutionsCount: number;
  createdAt: string;
  type: 'bug' | 'snippet';
  solutions?: Solution[];
  isLikedByMe?: boolean;
  isSavedByMe?: boolean;
}


export interface Tag {
  name: string;
  count: string;
}
