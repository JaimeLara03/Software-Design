import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Circuit } from '../models/circuit.model';

const API_URL = 'http://localhost:8080/circuits/';

@Injectable({
  providedIn: 'root'
})
export class CircuitService {
  constructor(private http: HttpClient) { }

  createCircuit(qubits: number): Observable<any> {
    return this.http.get<any>(`${API_URL}createCircuit/${qubits}`);
  }

  retrieveCircuit(id: number): Observable<any> {
    return this.http.get<any>(`${API_URL}retrieveCircuit/${id}`);
  }

  getUserCredit(userId: number): Observable<any> {
    return this.http.get<any>(`${API_URL}getUserCredit/${userId}`);
  }
}
