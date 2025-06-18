import { environment } from 'src/environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Circuit } from '../models/circuit.model';

const API_URL = environment.circuitsApiUrl + '/circuits/';

@Injectable({
  providedIn: 'root'
})
export class CircuitService {
  constructor(private http: HttpClient) { }

  createCircuit(qubits: number): Observable<any> {
    return this.http.get<any>(`${API_URL}createCircuit?qubits=${qubits}`);
  }

  retrieveCircuit(id: number): Observable<any> {
    return this.http.get<any>(`${API_URL}retrieveCircuit/${id}`);
  }

  getUserCredit(userId: number): Observable<number> {
    return this.http.get<number>(`${API_URL}user/${userId}/credit`);
  }
}
