import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  isSubmitted = false;
  isLoginFailed = false;
  errorMessage = '';
  returnUrl: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Crear el formulario de login
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    // Obtener URL a la que redirigir tras el login
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

    // Verificar si el usuario ya está logueado
    if (this.authService.isLoggedIn()) {
      this.router.navigate([this.returnUrl]);
    }
  }

  // Getter para facilitar el acceso a los campos del formulario
  get f() { return this.loginForm.controls; }

  onSubmit(): void {
    this.isSubmitted = true;
    this.isLoginFailed = false;
    this.errorMessage = '';

    // Detener aquí si el formulario es inválido
    if (this.loginForm.invalid) {
      return;
    }

    this.authService.login({
      email: this.f['email'].value,
      password: this.f['password'].value
    }).subscribe({
      next: data => {
        this.router.navigate([this.returnUrl]);
      },
      error: err => {
        this.errorMessage = err.error?.message || 'Error al iniciar sesión';
        this.isLoginFailed = true;
      }
    });
  }
}
