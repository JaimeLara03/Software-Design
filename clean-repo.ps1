# clean-repo.ps1

Write-Host "Limpiando repositorio..."

# Guardar tamaño inicial
$initialSize = (Get-ChildItem -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB
$initialSize = "{0:N2} MB" -f $initialSize

# Limpiar backend
Write-Host "Limpiando archivos compilados del backend..."
mvn clean

# Limpiar frontend
Write-Host "Limpiando archivos del frontend..."
Set-Location frontend

# Función robusta para eliminar carpetas grandes
function Remove-Directory($path) {
    if (Test-Path $path) {
        Write-Host "Eliminando $path ..."
        try {
            # Intenta con rimraf si existe
            if (Get-Command rimraf -ErrorAction SilentlyContinue) {
                rimraf $path
            }
            else {
                # Fallback con cmd.exe para Windows
                cmd /c "rmdir /s /q `"$path`""
            }
        } catch {
            Write-Host "Error eliminando $path : $_"
        }
    }
}

Remove-Directory "node_modules"
Remove-Directory ".angular"
Remove-Directory "dist"

Write-Host "Limpiando caché de npm..."
npm cache clean --force

Set-Location ..

# Eliminar logs
Write-Host "Eliminando archivos de log..."
Get-ChildItem -Recurse -Filter *.log | Remove-Item -Force -ErrorAction SilentlyContinue

# Mostrar tamaño final
$finalSize = (Get-ChildItem -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB
$finalSize = "{0:N2} MB" -f $finalSize

Write-Host "¡Limpieza completada!"
Write-Host "Tamaño antes: $initialSize"
Write-Host "Tamaño después: $finalSize"
Write-Host ""
Write-Host "El repositorio está listo para ser subido a GitHub."
