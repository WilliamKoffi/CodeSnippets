import { TestBed } from '@angular/core/testing';
import { Session } from './session.service';
import { StorageService } from '../storage.service';
import { Router } from '@angular/router';

describe('Session Service', () => {
  let service: Session;
  let storageMock: any;
  let routerMock: any;

  beforeEach(() => {
    storageMock = {
      read: vi.fn().mockReturnValue({ id: 'current_user', name: 'Ada Lovelace' }),
      write: vi.fn(),
      remove: vi.fn(),
    };
    routerMock = {
      navigateByUrl: vi.fn().mockResolvedValue(true),
    };

    TestBed.configureTestingModule({
      providers: [
        Session,
        { provide: StorageService, useValue: storageMock },
        { provide: Router, useValue: routerMock },
      ],
    });

    service = TestBed.inject(Session);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should log out', () => {
    service.logout();
    expect(storageMock.remove).toHaveBeenCalledWith('cs_user');
    expect(routerMock.navigateByUrl).toHaveBeenCalledWith('/auth/login');
  });

  it('should log in successfully', async () => {
    const mockUser = { id: 'user_123', name: 'John Doe', handle: 'john', email: 'john@example.com' };
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockUser),
    } as Response);

    await service.login('john@example.com', 'password123');

    expect(fetchSpy).toHaveBeenCalledWith('http://localhost:8080/api/auth/login', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ email: 'john@example.com', password: 'password123' })
    }));
    expect(storageMock.write).toHaveBeenCalledWith('cs_user', mockUser);
    expect(service.active()).toEqual(mockUser as any);
    expect(routerMock.navigateByUrl).toHaveBeenCalledWith('/snippets/feed');
  });

  it('should register successfully', async () => {
    const mockUser = { id: 'user_123', name: 'John Doe', handle: 'john', email: 'john@example.com' };
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockUser),
    } as Response);

    await service.register({
      name: 'John Doe',
      handle: '@john',
      email: 'john@example.com',
      password: 'password123',
      role: 'fullstack',
    });

    expect(fetchSpy).toHaveBeenCalledWith('http://localhost:8080/api/auth/register', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({
        name: 'John Doe',
        handle: 'john',
        email: 'john@example.com',
        password: 'password123',
        role: 'fullstack',
      })
    }));
    expect(storageMock.write).toHaveBeenCalledWith('cs_user', mockUser);
    expect(service.active()).toEqual(mockUser as any);
  });

  it('should update profile successfully', async () => {
    const updatedUser = { id: 'user_123', name: 'John Updated', handle: 'john', email: 'john@example.com' };
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(updatedUser),
    } as Response);

    await service.update({
      id: 'user_123',
      name: 'John Updated',
      handle: 'john',
      avatar: 'avatar_url',
      role: 'frontend',
      level: 'Aspirant',
    } as any);

    expect(fetchSpy).toHaveBeenCalledWith('http://localhost:8080/api/auth/users/user_123', expect.objectContaining({
      method: 'PUT',
      body: JSON.stringify({
        name: 'John Updated',
        handle: 'john',
        avatar: 'avatar_url',
        role: 'frontend',
        level: 'Aspirant',
      })
    }));
    expect(storageMock.write).toHaveBeenCalledWith('cs_user', updatedUser);
  });

  it('should bypass API update for current_user', async () => {
    const fetchSpy = vi.spyOn(globalThis, 'fetch');
    const mockUser = { id: 'current_user', name: 'Ada Lovelace' };

    await service.update(mockUser as any);

    expect(fetchSpy).not.toHaveBeenCalled();
    expect(storageMock.write).toHaveBeenCalledWith('cs_user', mockUser);
  });

  it('should refresh successfully', async () => {
    const updatedUser = { id: 'user_123', name: 'John Synced', handle: 'john', email: 'john@example.com' };
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(updatedUser),
    } as Response);

    // Set the user in the signal manually
    service.active.set({ id: 'user_123', name: 'John Old' } as any);

    await service.refresh();

    expect(fetchSpy).toHaveBeenCalledWith('http://localhost:8080/api/auth/users/user_123');
    expect(storageMock.write).toHaveBeenCalledWith('cs_user', updatedUser);
  });

  it('should reset password successfully', async () => {
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
    } as Response);

    await service.resetPassword('john@example.com');

    expect(fetchSpy).toHaveBeenCalledWith('http://localhost:8080/api/auth/reset', expect.objectContaining({
      method: 'POST',
      body: JSON.stringify({ email: 'john@example.com' })
    }));
  });
});
