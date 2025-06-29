import { Puerta } from './puerta.model';

export interface Circuit {
  id?: number;
  nombre?: string;
  descripcion?: string;
  ubicacion?: string;
  coste?: number;
  qubits?: number;
    puertas?: Puerta[];
  needsCredit?: boolean;
}
