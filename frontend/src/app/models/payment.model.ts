export interface Payment {
  id?: number;
  usuarioId: number;
  circuitoId: number;
  monto: number;
  referenciaPago?: string;
  fechaPago?: Date;
  estado?: string;
  metodoPago: string;
}

export interface PaymentRequest {
  usuarioId: number;
  circuitoId: number;
  monto: number;
  metodoPago: string;
  numeroTarjeta?: string;
  fechaExpiracion?: string;
  cvv?: string;
}
