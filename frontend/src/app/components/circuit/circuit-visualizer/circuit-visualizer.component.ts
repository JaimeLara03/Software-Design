import { Component, Input, ViewChild, ElementRef, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import { Circuit } from '../../../models/circuit.model';
import { Puerta } from '../../../models/puerta.model';

@Component({
  selector: 'app-circuit-visualizer',
  templateUrl: './circuit-visualizer.component.html',
  styleUrls: ['./circuit-visualizer.component.css']
})
export class CircuitVisualizerComponent implements AfterViewInit {
  @ViewChild('circuitContainer') container!: ElementRef;
  legendItems: { name: string, type: string, gateName?: string }[] = [];

  _circuit: Circuit | null = null;

  @Input()
  set circuit(value: Circuit | null) {
    this._circuit = value;
    if (this._circuit) {
      this.buildLegend();
      if (this.container) {
        this.drawCircuit();
      }
    }
  }

  get circuit(): Circuit | null {
    return this._circuit;
  }

  ngAfterViewInit(): void {
    if (this.circuit) {
      this.drawCircuit();
    }
  }

  private drawCircuit(): void {
    if (!this.circuit || !this.circuit.qubits || !this.container) {
      if (this.container) {
          this.container.nativeElement.innerHTML = '<p class="text-muted p-3">No hay datos de circuito para visualizar.</p>';
      }
      return;
    }

    const numQubits = this.circuit.qubits;
    const gates = this.circuit.puertas || [];

    const svgWidth = (gates.length + 2) * 80;
    const svgHeight = numQubits * 50 + 40;
    const qubitSpacing = 50;
    const gateSpacing = 80;
    const startX = 60;
    const startY = 40;

    let svg = `<svg width="${svgWidth}" height="${svgHeight}" xmlns="http://www.w3.org/2000/svg" style="background-color: white; border-radius: 5px; padding: 10px;">`;

    // Draw qubit lines and labels
    for (let i = 0; i < numQubits; i++) {
      const y = startY + i * qubitSpacing;
      svg += `<line x1="${startX - 30}" y1="${y}" x2="${svgWidth - 20}" y2="${y}" stroke="#6c757d" />`;
      svg += `<text x="${startX - 45}" y="${y + 5}" font-size="14" fill="#212529" font-weight="bold">q${i}</text>`;
    }

    // Draw gates
    svg += this.drawGates(gates, startX, startY, qubitSpacing, gateSpacing);

    svg += `</svg>`;
    this.container.nativeElement.innerHTML = svg;
  }

    private buildLegend(): void {
    if (!this.circuit || !this.circuit.puertas) {
      this.legendItems = [];
      return;
    }

    const uniqueGateNames = [...new Set(this.circuit.puertas.map(p => p.nombre?.toUpperCase()))];
    const legend = [];

    const otherGates = uniqueGateNames.filter(name => name && name !== 'CNOT');
    otherGates.forEach(name => {
        legend.push({ name: `Puerta ${name}`, type: 'GATE', gateName: name });
    });

    if (uniqueGateNames.includes('CNOT')) {
        legend.push({ name: 'Punto de Control (CNOT)', type: 'CNOT_CONTROL' });
        legend.push({ name: 'Punto Objetivo (CNOT)', type: 'CNOT_TARGET' });
    }

    this.legendItems = legend;
  }

  private drawGates(gates: Puerta[], startX: number, startY: number, qubitSpacing: number, gateSpacing: number): string {
      let gateSvg = '';
      gates.forEach((gate, index) => {
          const x = startX + index * gateSpacing;
          if (gate.nombre?.toUpperCase() === 'CNOT' && gate.qubitControl !== undefined && gate.qubitObjetivo !== undefined) {
              const controlY = startY + gate.qubitControl * qubitSpacing;
              const targetY = startY + gate.qubitObjetivo * qubitSpacing;
              gateSvg += `<line x1="${x}" y1="${controlY}" x2="${x}" y2="${targetY}" stroke="#0d6efd" stroke-width="2"/>`;
              gateSvg += `<circle cx="${x}" cy="${controlY}" r="5" fill="#0d6efd"/>`;
              gateSvg += `<circle cx="${x}" cy="${targetY}" r="12" fill="none" stroke="#0d6efd" stroke-width="2"/>`;
              gateSvg += `<line x1="${x - 8.5}" y1="${targetY - 8.5}" x2="${x + 8.5}" y2="${targetY + 8.5}" stroke="#0d6efd" stroke-width="2"/>`;
              gateSvg += `<line x1="${x - 8.5}" y1="${targetY + 8.5}" x2="${x + 8.5}" y2="${targetY - 8.5}" stroke="#0d6efd" stroke-width="2"/>`;
          } else if (gate.qubitObjetivo !== undefined) {
              const y = startY + gate.qubitObjetivo * qubitSpacing;
              gateSvg += `<rect x="${x - 18}" y="${y - 18}" width="36" height="36" rx="4" fill="#e9ecef" stroke="#343a40" stroke-width="2"/>`;
              gateSvg += `<text x="${x}" y="${y + 6}" font-size="16" font-weight="bold" text-anchor="middle" fill="#343a40">${gate.nombre}</text>`;
          }
      });
      return gateSvg;
  }
}
