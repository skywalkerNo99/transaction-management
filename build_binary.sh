#!/bin/bash
mvn clean
mvn -Pnative package -DskipTests
