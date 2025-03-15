import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { User, LoginRequest, JwtResponse } from '../models/user.model';

const AUTH_API = 'http://localhost:8080/api/auth/';
const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) { }

  login(credentials: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(AUTH_API + 'signin', {
      email: credentials.email,
      password: credentials.password
    }, httpOptions).pipe(
      tap(response => {
        this.saveToken(response.token);
        this.saveUser(response);
      })
    );
  }

  register(user: User): Observable<any> {
    return this.http.post(AUTH_API + 'signup', {
      nombre: user.nombre,
      apellido: user.apellido,
      email: user.email,
      password: user.password
    }, httpOptions);
  }

  logout(): void {
    window.sessionStorage.clear();
  }

  saveToken(token: string): void {
    window.sessionStorage.removeItem(TOKEN_KEY);
    window.sessionStorage.setItem(TOKEN_KEY, token);
  }

  getToken(): string | null {
    return window.sessionStorage.getItem(TOKEN_KEY);
  }

  saveUser(user: any): void {
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  getUser(): any {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }
    return null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
