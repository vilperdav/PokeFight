#!/bin/bash

# Compilar
javac -cp lib/json-simple-1.1.1.jar src/*.java

# Ejecutar
java -cp lib/json-simple-1.1.1.jar:src pokeFight