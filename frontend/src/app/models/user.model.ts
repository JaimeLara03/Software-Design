export interface User {
  id?: number;
  nombre: string;
  apellido: string;
  email: string;
  password?: string;
  credito?: number;
  verificado?: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface JwtResponse {
  token: string;
  type: string;
  id: number;
  email: string;
  nombre: string;
  apellido: string;
  credito: number;
}
