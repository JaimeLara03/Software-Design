import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PaymentService } from '../../../services/payment.service';
import { AuthService } from '../../../services/auth.service';
import { PaymentRequest } from '../../../models/payment.model';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent implements OnInit {
  paymentForm!: FormGroup;
  isSubmitted = false;
  isSuccess = false;
  isFailed = false;
  errorMessage = '';
  userId: number = 0;
  pendingCircuit: any = null;
  paymentMethods = [
    { id: 'tarjeta', name: 'Tarjeta de Crédito/Débito' },
    { id: 'paypal', name: 'PayPal' },
    { id: 'transferencia', name: 'Transferencia Bancaria' }
  ];

  constructor(
    private formBuilder: FormBuilder,
    private paymentService: PaymentService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Verificar que el usuario está logueado
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/payment' } });
      return;
    }

    // Obtener datos del usuario
    const user = this.authService.getUser();
    this.userId = user.id;

    // Comprobar si hay un circuito pendiente de pago
    const pendingCircuitData = sessionStorage.getItem('pendingCircuit');
    if (pendingCircuitData) {
      this.pendingCircuit = JSON.parse(pendingCircuitData);
    }

    // Crear formulario de pago
    this.paymentForm = this.formBuilder.group({
      monto: [this.pendingCircuit?.cost || 10, [Validators.required, Validators.min(1)]],
      metodoPago: ['tarjeta', Validators.required],
      numeroTarjeta: ['', [Validators.pattern(/^[0-9]{16}$/)]],
      fechaExpiracion: ['', [Validators.pattern(/^(0[1-9]|1[0-2])\/[0-9]{2}$/)]],
      cvv: ['', [Validators.pattern(/^[0-9]{3,4}$/)]]
    });

    // Añadir validadores condicionales
    this.f['metodoPago'].valueChanges.subscribe(metodoPago => {
      if (metodoPago === 'tarjeta') {
        this.f['numeroTarjeta'].setValidators([Validators.required, Validators.pattern(/^[0-9]{16}$/)]);
        this.f['fechaExpiracion'].setValidators([Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/[0-9]{2}$/)]);
        this.f['cvv'].setValidators([Validators.required, Validators.pattern(/^[0-9]{3,4}$/)]);
      } else {
        this.f['numeroTarjeta'].clearValidators();
        this.f['fechaExpiracion'].clearValidators();
        this.f['cvv'].clearValidators();
      }
      this.f['numeroTarjeta'].updateValueAndValidity();
      this.f['fechaExpiracion'].updateValueAndValidity();
      this.f['cvv'].updateValueAndValidity();
    });
  }

  // Getter para facilitar el acceso a los campos del formulario
  get f() { return this.paymentForm.controls; }

  onSubmit(): void {
    this.isSubmitted = true;
    this.isSuccess = false;
    this.isFailed = false;
    this.errorMessage = '';

    // Detener aquí si el formulario es inválido
    if (this.paymentForm.invalid) {
      return;
    }

    const paymentRequest: PaymentRequest = {
      usuarioId: this.userId,
      circuitoId: this.pendingCircuit?.id || 0,
      monto: this.f['monto'].value,
      metodoPago: this.f['metodoPago'].value
    };

    // Añadir datos de tarjeta si el método de pago es tarjeta
    if (this.f['metodoPago'].value === 'tarjeta') {
      paymentRequest.numeroTarjeta = this.f['numeroTarjeta'].value;
      paymentRequest.fechaExpiracion = this.f['fechaExpiracion'].value;
      paymentRequest.cvv = this.f['cvv'].value;
    }

    this.paymentService.procesarPago(paymentRequest).subscribe({
      next: response => {
        this.isSuccess = true;
        
        // Limpiar circuito pendiente
        sessionStorage.removeItem('pendingCircuit');
        
        setTimeout(() => {
          this.router.navigate(['/profile']);
        }, 3000);
      },
      error: err => {
        this.isFailed = true;
        this.errorMessage = err.error?.message || 'Error al procesar el pago';
      }
    });
  }
}
