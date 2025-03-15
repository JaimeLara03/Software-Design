import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  isSubmitted = false;
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';

  constructor(
    private formBuilder: FormBuilder, 
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      apellido: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  // Validador personalizado para comprobar que las contraseñas coinciden
  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;

    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  // Getter para facilitar el acceso a los campos del formulario
  get f() { return this.registerForm.controls; }

  onSubmit(): void {
    this.isSubmitted = true;
    this.isSuccessful = false;
    this.isSignUpFailed = false;
    this.errorMessage = '';

    // Detener aquí si el formulario es inválido
    if (this.registerForm.invalid) {
      return;
    }

    this.authService.register({
      nombre: this.f['nombre'].value,
      apellido: this.f['apellido'].value,
      email: this.f['email'].value,
      password: this.f['password'].value
    }).subscribe({
      next: data => {
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: err => {
        this.errorMessage = err.error.message || 'Error al registrarse';
        this.isSignUpFailed = true;
      }
    });
  }
}
