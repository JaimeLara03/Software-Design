import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CircuitService } from '../../../services/circuit.service';
import { Circuit } from '../../../models/circuit.model';

@Component({
  selector: 'app-retrieve-circuit',
  templateUrl: './retrieve-circuit.component.html',
  styleUrls: ['./retrieve-circuit.component.css']
})
export class RetrieveCircuitComponent implements OnInit {
  circuitId: number = 0;
  circuit: Circuit | null = null;
  loading: boolean = true;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private circuitService: CircuitService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.circuitId = +params['id'];
      this.loadCircuit();
    });
  }

  loadCircuit(): void {
    this.loading = true;
    this.errorMessage = '';

    this.circuitService.retrieveCircuit(this.circuitId).subscribe({
      next: (data) => {
        this.circuit = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al recuperar el circuito: ' + (err.error?.message || err.message);
        this.loading = false;
      }
    });
  }
}
