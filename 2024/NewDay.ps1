param (
    [string]$dayNumber
)

# Validate the day number
if (-not $dayNumber -or $dayNumber -notmatch '^\d+$') {
    Write-Host "Please provide a valid day number."
    exit 1
}

# Define the new day folder name
$dayFolder = "Day$dayNumber"

# Create the new day folder
New-Item -ItemType Directory -Path $dayFolder

# Navigate to the new day folder
Set-Location $dayFolder

# Use dotnet new with the adventofcode template
dotnet new adventofcode

# Create an empty input.txt file
New-Item -ItemType File -Name "input.txt"

# Navigate back to the solution directory
Set-Location ..

# Rerun slngen to update the solution file
slngen **/*.csproj -o 2024.sln 2024.sln --launch false

Write-Host "New day folder created and solution updated successfully."