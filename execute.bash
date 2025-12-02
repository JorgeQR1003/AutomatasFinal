#!/bin/bash

# Output file for logs
LOG_FILE="execution.log"

# Clear previous log
> "$LOG_FILE"

echo "Starting execution..." | tee -a "$LOG_FILE"

# 1. Run .NET program
echo "Running .NET program..." | tee -a "$LOG_FILE"
dotnet run --project . >> "$LOG_FILE" 2>&1

if [ $? -ne 0 ]; then
    echo "Error: .NET program failed." | tee -a "$LOG_FILE"
    exit 1
fi

echo ".NET program finished successfully." | tee -a "$LOG_FILE"

# 2. Run Java program (Maven)
echo "Running Java program..." | tee -a "$LOG_FILE"
cd parser || { echo "Error: parser directory not found."; exit 1; }
mvn compile exec:java -Dexec.mainClass="org.example.Main" >> "../$LOG_FILE" 2>&1

if [ $? -ne 0 ]; then
    echo "Error: Java program failed." | tee -a "../$LOG_FILE"
    exit 1
fi

echo "Java program finished successfully." | tee -a "../$LOG_FILE"
echo "All done. Check $LOG_FILE for full output."

