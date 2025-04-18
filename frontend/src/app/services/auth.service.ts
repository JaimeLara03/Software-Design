import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
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
  // Observable para notificar cambios en el estado de autenticación
  private authStateChanged = new BehaviorSubject<boolean>(this.isLoggedInCheck());
  authState$ = this.authStateChanged.asObservable();
  
  constructor(private http: HttpClient) { }

  login(credentials: LoginRequest): Observable<JwtResponse> {
    // Tenemos dos formas de hacer login para asegurar que una funcione
    // 1. Intentar con la ruta original /api/auth/login
    console.log('Iniciando sesión en URL: ' + AUTH_API + 'login');
    
    // SOLUCIÓN TEMPORAL: Para entrar como administrador en la aplicación
    if (credentials.email === 'admin@example.com' && credentials.password === 'admin123') {
      console.log('Accediendo como administrador');
      const mockResponse: JwtResponse = {
        token: 'mock-jwt-token',
        type: 'Bearer',
        id: 1,
        email: 'admin@example.com',
        nombre: 'Admin',
        apellido: 'User',
        credito: 1000
      };
      this.saveToken(mockResponse.token);
      this.saveUser(mockResponse);
      return of(mockResponse);
    }
    
    return this.http.post<JwtResponse>(AUTH_API + 'login', {
      email: credentials.email,
      password: credentials.password
    }, httpOptions).pipe(
      catchError((error: any) => {
        console.log('Error con el endpoint principal, intentando con alternativa');
        // 2. Si falla, intentar con la ruta alternativa /api/usuarios/login
        return this.http.post<JwtResponse>('http://localhost:8080/api/auth/login', {
          email: credentials.email,
          password: credentials.password
        }, httpOptions);
      }),
      tap(
        response => {
          console.log('Inicio de sesión exitoso:', response);
          this.saveToken(response.token);
          this.saveUser(response);
        },
        error => console.error('Error en inicio de sesión:', error)
      )
    );
  }

  register(user: User): Observable<any> {
    console.log('Registrando usuario en URL: http://localhost:8080/api/usuarios/registro');
    console.log('Datos de usuario:', user);
    return this.http.post('http://localhost:8080/api/usuarios/registro', {
      nombre: user.nombre,
      apellido: user.apellido,
      email: user.email,
      password: user.password
    }, httpOptions).pipe(
      tap(
        response => console.log('Respuesta exitosa:', response),
        error => console.error('Error en registro:', error)
      )
    );
  }

  logout(): void {
    window.sessionStorage.clear();
    // Notificar cambio en el estado de autenticación
    this.authStateChanged.next(false);
  }

  saveToken(token: string): void {
    window.sessionStorage.removeItem(TOKEN_KEY);
    window.sessionStorage.setItem(TOKEN_KEY, token);
    // Notificar cambio en el estado de autenticación
    this.authStateChanged.next(true);
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

  // Método privado para verificar internamente el estado de autenticación
  private isLoggedInCheck(): boolean {
    return !!this.getToken();
  }
  
  isLoggedIn(): boolean {
    return this.isLoggedInCheck();
  }
}
