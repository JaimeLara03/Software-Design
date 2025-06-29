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
  truthRows: string[][] = []; // matriz de bits por fila
  outputs: string[] = [];   // valores 0/1 editados
  qubitIndices: number[] = [];
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
    // Formulario con campos básicos; truthTable se gestiona aparte

    this.circuitForm = this.formBuilder.group({
      nombre: [''],
      descripcion: [''],
      activo: [true],
      qubits: [3, [Validators.required, Validators.min(1), Validators.max(10)]]
    });

    // Generar tabla al cambiar qubits
    this.circuitForm.get('qubits')?.valueChanges.subscribe((q:number)=>{
      this.generateTruthTable(q);
    });

    // Generar tabla de verdad inicial
    this.generateTruthTable(this.f['qubits'].value);

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

    // Construir truth table a partir de outputs
    const truthTable = this.outputs.map(o => o === '1' ? '1' : '0');

    const timestamp = new Date().getTime();
    const nombre = `Circuito ${timestamp}`;
    const descripcion = `Circuito de ${qubits} qubits generado automáticamente.`;

    const payload = {
      nombre: nombre,
      descripcion: descripcion,
      activo: true,
      qubits: qubits,
      truthTable: truthTable
    };

    this.circuitService.createCircuit(payload).subscribe({
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

  private generateTruthTable(qubits:number){
    const rows = Math.pow(2,qubits);
    this.truthRows = [];
    this.outputs = [];
    this.qubitIndices = Array.from({length: qubits}, (_,i)=>i);
    for(let i=0;i<rows;i++){
      const bits = i.toString(2).padStart(qubits,'0').split('');
      this.truthRows.push(bits);
      this.outputs.push('0');
    }
  }

  goToPayment(): void {
    this.router.navigate(['/payment']);
  }
}
