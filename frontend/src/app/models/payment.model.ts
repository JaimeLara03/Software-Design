export interface Payment {
  id: string;
  stripePaymentId: string;
  userId: string;
  amount: number;
  currency: string;
  paymentDate: Date;
  status: string;
}

export interface PaymentRequest {
  usuarioId: number;
  monto: number;
  metodoPago: string;
  numeroTarjeta?: string;
  fechaExpiracion?: string;
  cvv?: string;
}
