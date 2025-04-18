import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  username: string = '';
  private authSubscription!: Subscription;

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    // Actualizar el estado inicial
    this.updateLoginStatus();
    
    // Suscribirse a los cambios en el estado de autenticación
    this.authSubscription = this.authService.authState$.subscribe(isAuthenticated => {
      if (isAuthenticated) {
        this.updateLoginStatus();
      } else {
        this.isLoggedIn = false;
        this.username = '';
      }
    });
  }
  
  ngOnDestroy(): void {
    // Limpiar la suscripción cuando se destruye el componente
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  updateLoginStatus(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    if (this.isLoggedIn) {
      const user = this.authService.getUser();
      this.username = user.nombre;
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
    // Ya no necesitamos actualizar manualmente isLoggedIn aquí porque 
    // estamos suscritos a los cambios en el servicio de autenticación
  }
}
