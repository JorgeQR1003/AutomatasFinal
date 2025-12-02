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

# Execute dotnet run, convert output to strings (handling error streams), and log to both console and file in UTF-8
dotnet run --project . 2>&1 | ForEach-Object { 
    $str = "$_" 
    Write-Host $str 
    $str | Out-File -FilePath $LogFile -Append -Encoding utf8 
}

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
    # Use cmd /c mvn to ensure we pick up mvn.cmd on Windows if not directly executable, 
    # though 'mvn' usually works if in PATH.
    if ($IsWindows) {
        $mvnCmd = "mvn.cmd"
    } else {
        $mvnCmd = "mvn"
    }
    
    # Check if we can find the command, fallback to just 'mvn' if check fails or simplistic
    if (-not (Get-Command $mvnCmd -ErrorAction SilentlyContinue)) {
        $mvnCmd = "mvn" 
    }

    & $mvnCmd 'exec:java' '-Dexec.mainClass=org.example.Main' 2>&1 | ForEach-Object { 
        $str = "$_" 
        Write-Host $str 
        $str | Out-File -FilePath "..\$LogFile" -Append -Encoding utf8 
    }
    
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

