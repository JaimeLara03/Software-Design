export interface Circuit {
  id?: number;
  nombre?: string;
  descripcion?: string;
  ubicacion?: string;
  coste?: number;
  qubits?: number;
  gates?: any;
  needsCredit?: boolean;
}
