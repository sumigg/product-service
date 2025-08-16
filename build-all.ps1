# PowerShell script to build all projects
Write-Host "Building all projects..." -ForegroundColor Green

try {
    # Store the original location
    $originalLocation = Get-Location
    $rootDir = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)

    # Build common modules first
    Write-Host "`nBuilding common/api..." -ForegroundColor Cyan
    Set-Location "$rootDir\common\api"
    ./mvnw clean install
    if ($LASTEXITCODE -ne 0) { throw "Failed to build common/api" }

    Write-Host "`nBuilding common/util..." -ForegroundColor Cyan
    Set-Location "$rootDir\common\util"
    ./mvnw clean install
    if ($LASTEXITCODE -ne 0) { throw "Failed to build common/util" }

    # Build microservices
    Write-Host "`nBuilding product service..." -ForegroundColor Cyan
    Set-Location "$rootDir\microservices\product"
    ./mvnw clean install
    if ($LASTEXITCODE -ne 0) { throw "Failed to build product service" }

    Write-Host "`nBuilding review service..." -ForegroundColor Cyan
    Set-Location "$rootDir\microservices\review-service"
    ./mvnw clean install
    if ($LASTEXITCODE -ne 0) { throw "Failed to build review service" }

    Write-Host "`nBuilding recommendation service..." -ForegroundColor Cyan
    Set-Location "$rootDir\microservices\recommendation"
    ./mvnw clean install
    if ($LASTEXITCODE -ne 0) { throw "Failed to build recommendation service" }

    Write-Host "`nBuilding composite service..." -ForegroundColor Cyan
    Set-Location "$rootDir\microservices\composite"
    ./mvnw clean install
    if ($LASTEXITCODE -ne 0) { throw "Failed to build composite service" }

    Write-Host "`nAll projects built successfully!" -ForegroundColor Green
}
catch {
    Write-Host "`nBuild failed: $_" -ForegroundColor Red
    exit 1
}
finally {
    # Return to the original location
    Set-Location $originalLocation
}
