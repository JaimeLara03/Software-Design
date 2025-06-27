import { Component, OnInit, AfterViewInit, NgZone } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PaymentService } from '../../../services/payment.service';
import { AuthService } from '../../../services/auth.service';
import { PaymentRequest } from '../../../models/payment.model';

// Declara Stripe (viene de la librería cargada en index.html)
declare var Stripe: any;

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent implements OnInit, AfterViewInit {
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

  stripe: any;
  cardElement: any;
  cardErrors: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private paymentService: PaymentService,
    private authService: AuthService,
    private router: Router,
    private ngZone: NgZone
  ) { }

  ngOnInit(): void {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/payment' } });
      return;
    }
    const user = this.authService.getUser();
    this.userId = user.id;

    const pendingCircuitData = sessionStorage.getItem('pendingCircuit');
    if (pendingCircuitData) {
      this.pendingCircuit = JSON.parse(pendingCircuitData);
    }

    this.paymentForm = this.formBuilder.group({
      monto: [this.pendingCircuit?.cost || 10, [Validators.required, Validators.min(1)]],
      metodoPago: ['tarjeta', Validators.required]
    });
  }

  ngAfterViewInit(): void {
    this.stripe = Stripe('pk_test_51RaIgO2LOA8q9cPGirYeXV058sFMJN9OFV67FgRfbs496IxJyJMRkB9nL3XaAD9xBI3xPWebTJ88U4jMyLnK2oqj00B6ovDuvz'); // <-- pon aquí tu clave pública Stripe
    const elements = this.stripe.elements();
    this.cardElement = elements.create('card');
    this.cardElement.mount('#card-element');

    this.cardElement.on('change', (event: any) => {
      this.ngZone.run(() => {
        this.cardErrors = event.error ? event.error.message : '';
      });
    });
  }

  get f() { return this.paymentForm.controls; }

  async onSubmit() {
    this.isSubmitted = true;
    this.isSuccess = false;
    this.isFailed = false;
    this.errorMessage = '';

    if (this.paymentForm.invalid) {
      return;
    }

    if (this.f['metodoPago'].value === 'tarjeta') {
      // 1. Pide al backend crear PaymentIntent y obtener client_secret
      const paymentRequest: PaymentRequest = {
        usuarioId: this.userId,
        circuitoId: this.pendingCircuit?.id || 0,
        monto: this.f['monto'].value,
        metodoPago: this.f['metodoPago'].value
      };

      try {
        const response: any = await this.paymentService.procesarPago(paymentRequest).toPromise();
        const clientSecret = response.client_secret;

        // 2. Confirma el pago con Stripe.js usando Elements
        const { error, paymentIntent } = await this.stripe.confirmCardPayment(clientSecret, {
          payment_method: {
            card: this.cardElement
          }
        });

        if (error) {
          // Error con la tarjeta
          this.isFailed = true;
          this.errorMessage = error.message;
        } else if (paymentIntent && paymentIntent.status === 'succeeded') {
          // Pago correcto
          this.isSuccess = true;
          sessionStorage.removeItem('pendingCircuit');
          setTimeout(() => this.router.navigate(['/profile']), 3000);
        } else {
          this.isFailed = true;
          this.errorMessage = 'Pago no completado. Inténtalo de nuevo.';
        }
      } catch (err) {
        this.isFailed = true;
        this.errorMessage = (err as any).error?.message || 'Error al procesar el pago';
      }
    } else {
      // Métodos paypal o transferencia siguen igual que antes, llamar al backend directamente
      const paymentRequest: PaymentRequest = {
        usuarioId: this.userId,
        circuitoId: this.pendingCircuit?.id || 0,
        monto: this.f['monto'].value,
        metodoPago: this.f['metodoPago'].value
      };

      this.paymentService.procesarPago(paymentRequest).subscribe({
        next: () => {
          this.isSuccess = true;
          sessionStorage.removeItem('pendingCircuit');
          setTimeout(() => this.router.navigate(['/profile']), 3000);
        },
        error: err => {
          this.isFailed = true;
          this.errorMessage = err.error?.message || 'Error al procesar el pago';
        }
      });
    }
  }
}
