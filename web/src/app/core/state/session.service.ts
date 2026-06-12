import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { INITIAL_USER } from '../../shared/seed-data';
import { User } from '../../shared/types';
import { StorageService } from '../storage.service';

type Registration = { name: string; handle: string; email: string; password?: string; role: string };

@Injectable({ providedIn: 'root' })
export class Session {
  private readonly storage = inject(StorageService);
  private readonly router = inject(Router);

  readonly active = signal<User>(this.storage.read('cs_user', INITIAL_USER));

  async login(email: string, password: string): Promise<void> {
    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        throw new Error(errData.error || 'Authentication failed');
      }

      const user: User = await response.json();
      this.persist(user);
      await this.router.navigateByUrl('/snippets/feed');
    } catch (error: any) {
      console.error('Login error:', error);
      throw error;
    }
  }

  async register(credentials: Registration): Promise<void> {
    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: credentials.name,
          handle: credentials.handle.replace('@', ''),
          email: credentials.email,
          password: credentials.password || '',
          role: credentials.role || 'fullstack',
        }),
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        throw new Error(errData.error || 'Registration failed');
      }

      const user: User = await response.json();
      this.persist(user);
      await this.router.navigateByUrl('/snippets/feed');
    } catch (error: any) {
      console.error('Registration error:', error);
      throw error;
    }
  }

  async update(user: User): Promise<void> {
    if (user.id === 'current_user') {
      this.persist(user);
      return;
    }
    try {
      const response = await fetch(`http://localhost:8080/api/auth/users/${user.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: user.name,
          handle: user.handle,
          avatar: user.avatar,
          role: user.role,
          level: user.level,
        }),
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        throw new Error(errData.error || 'Profile update failed');
      }

      const updatedUser: User = await response.json();
      this.persist(updatedUser);
    } catch (error: any) {
      console.error('Update profile error:', error);
      throw error;
    }
  }

  async refresh(): Promise<void> {
    const user = this.active();
    if (user.id === 'current_user') return;
    try {
      const response = await fetch(`http://localhost:8080/api/auth/users/${user.id}`);
      if (response.ok) {
        const updated: User = await response.json();
        this.persist(updated);
      }
    } catch (error) {
      console.warn('Failed to refresh session:', error);
    }
  }

  async resetPassword(email: string): Promise<void> {
    try {
      const response = await fetch('http://localhost:8080/api/auth/reset', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });

      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        throw new Error(errData.error || 'Password reset request failed');
      }
    } catch (error: any) {
      console.error('Password reset error:', error);
      throw error;
    }
  }

  logout(): void {
    this.persist(INITIAL_USER);
    this.storage.remove('cs_user');
    void this.router.navigateByUrl('/auth/login');
  }

  private persist(user: User): void {
    this.active.set(user);
    this.storage.write('cs_user', user);
  }
}
