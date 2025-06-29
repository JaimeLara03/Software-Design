import { environment } from 'src/environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Circuit } from '../models/circuit.model';

const API_URL = environment.circuitsApiUrl + '/circuitos';

@Injectable({
  providedIn: 'root'
})
export class CircuitService {
  constructor(private http: HttpClient) { }

  // Método legacy (GET con qubits)
  createCircuitLegacy(qubits: number): Observable<any> {
    return this.http.get<any>(`${API_URL}?qubits=${qubits}`);
  }

  // Nuevo método: envía un objeto Circuit completo con tabla de verdad
  createCircuit(circuitData: any): Observable<any> {
    return this.http.post<any>(`${API_URL}`, circuitData);
  }

  retrieveCircuit(id: number): Observable<any> {
    return this.http.get<any>(`${API_URL}/${id}`);
  }

  getUserCredit(userId: number): Observable<number> {
    return this.http.get<number>(`${API_URL}/verificarcredito/${userId}`);
  }
}
