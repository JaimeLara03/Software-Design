import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payment, PaymentRequest } from '../models/payment.model';

const API_URL = 'http://localhost:8080/api/pagos/';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  constructor(private http: HttpClient) { }

  procesarPago(paymentRequest: PaymentRequest): Observable<any> {
    return this.http.post<any>(`${API_URL}procesar`, paymentRequest);
  }

  verificarPago(referenciaPago: string): Observable<any> {
    return this.http.get<any>(`${API_URL}verificar/${referenciaPago}`);
  }

  getHistorialPagos(usuarioId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${API_URL}historial/${usuarioId}`);
  }
}
