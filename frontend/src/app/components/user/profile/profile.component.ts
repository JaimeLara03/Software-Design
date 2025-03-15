import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { PaymentService } from '../../../services/payment.service';
import { Payment } from '../../../models/payment.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  currentUser: any = null;
  paymentHistory: Payment[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private paymentService: PaymentService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Verificar que el usuario está logueado
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/profile' } });
      return;
    }

    this.currentUser = this.authService.getUser();
    this.loadPaymentHistory();
  }

  loadPaymentHistory(): void {
    this.loading = true;
    this.errorMessage = '';

    this.paymentService.getHistorialPagos(this.currentUser.id).subscribe({
      next: data => {
        this.paymentHistory = data;
        this.loading = false;
      },
      error: err => {
        this.errorMessage = 'Error al cargar el historial de pagos: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }

  goToPayment(): void {
    this.router.navigate(['/payment']);
  }
}
