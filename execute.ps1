# Output file for logs
$LogFile = "execution.log"

# Clear previous log
$null | Out-File -FilePath $LogFile -Encoding utf8

function Log-Message {
    param (
        [string]$Message
    )
    Write-Host $Message
    $Message | Out-File -FilePath $LogFile -Append -Encoding utf8
}

Log-Message "Starting execution..."

# 1. Run .NET program
Log-Message "Running .NET program..."

# Execute dotnet run and capture output and error streams
# utilizing cmd /c to ensure redirection works as expected for external processes if needed, 
# but standard piping usually works. 
# However, checking exit code directly after pipe in PS can be tricky if not careful.
# We will use Start-Process -NoNewWindow -Wait to ensure we catch the exit code correctly if simpler piping fails,
# but simpler piping:
dotnet run --project . 2>&1 | Tee-Object -FilePath $LogFile -Append

if ($LASTEXITCODE -ne 0) {
    Log-Message "Error: .NET program failed."
    exit 1
}

Log-Message ".NET program finished successfully."

# 2. Run Java program (Maven)
Log-Message "Running Java program..."

if (-not (Test-Path "parser")) {
    Log-Message "Error: parser directory not found."
    exit 1
}

Push-Location parser

try {
    # PowerShell wrapper for mvn might be needed if it's a .cmd file, but typically 'mvn' resolves to mvn.cmd in PS.
    # We use 'cmd /c mvn ...' to be safe if environment variables are tricky, but usually direct call is fine.
    # Let's try direct call.
    mvn compile exec:java -Dexec.mainClass="org.example.Main" 2>&1 | Tee-Object -FilePath "..\$LogFile" -Append
    
    if ($LASTEXITCODE -ne 0) {
        # Tee-Object might mask the exit code of the left side in some PS versions/configurations 
        # (though usually $LASTEXITCODE is preserved for native commands).
        # If checking $LASTEXITCODE is unreliable after pipe, we might need a different approach, 
        # but for a simple helper script this is usually fine.
        Log-Message "Error: Java program failed."
        exit 1
    }
}
catch {
    Log-Message "An unexpected error occurred: $_"
    exit 1
}
finally {
    Pop-Location
}

Log-Message "Java program finished successfully."
Log-Message "All done. Check $LogFile for full output."

