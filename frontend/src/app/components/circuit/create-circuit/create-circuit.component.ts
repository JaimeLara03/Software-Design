import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CircuitService } from '../../../services/circuit.service';
import { AuthService } from '../../../services/auth.service';
import { Circuit } from '../../../models/circuit.model';

@Component({
  selector: 'app-create-circuit',
  templateUrl: './create-circuit.component.html',
  styleUrls: ['./create-circuit.component.css']
})
export class CreateCircuitComponent implements OnInit {
  circuitForm!: FormGroup;
  isSubmitted = false;
  errorMessage = '';
  isLoggedIn = false;
  createdCircuit: Circuit | null = null;
  insufficientCredit = false;
  userId: number | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private circuitService: CircuitService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.circuitForm = this.formBuilder.group({
      qubits: [6, [Validators.required, Validators.min(1), Validators.max(20)]]
    });

    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      const user = this.authService.getUser();
      this.userId = user.id;
    }
  }

  // Getter para facilitar el acceso a los campos del formulario
  get f() { return this.circuitForm.controls; }

  onSubmit(): void {
    this.isSubmitted = true;
    this.errorMessage = '';
    this.createdCircuit = null;
    this.insufficientCredit = false;

    // Detener aquí si el formulario es inválido
    if (this.circuitForm.invalid) {
      return;
    }

    const qubits = this.f['qubits'].value;
    
    this.circuitService.createCircuit(qubits).subscribe({
      next: (data) => {
        this.createdCircuit = data;
        
        // Si el circuito requiere crédito y el usuario no está logueado, redirigir a login
        if (data.needsCredit && !this.isLoggedIn) {
          sessionStorage.setItem('pendingCircuit', JSON.stringify(data));
          this.router.navigate(['/login'], { queryParams: { returnUrl: '/payment' } });
          return;
        }
        
        // Si el circuito requiere crédito y el usuario está logueado, verificar crédito
        if (data.needsCredit && this.isLoggedIn && this.userId) {
          this.circuitService.getUserCredit(this.userId).subscribe({
            next: (creditData) => {
              if (creditData < data.cost) {
                this.insufficientCredit = true;
                sessionStorage.setItem('pendingCircuit', JSON.stringify(data));
              }
            },
            error: (err) => {
              this.errorMessage = 'Error al verificar el crédito: ' + err.message;
            }
          });
        }
      },
      error: (err) => {
        this.errorMessage = 'Error al crear el circuito: ' + err.message;
      }
    });
  }

  goToPayment(): void {
    this.router.navigate(['/payment']);
  }
}
