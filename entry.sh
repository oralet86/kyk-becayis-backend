#!/bin/bash
set -e
ls -al /app

if [ ! -f ".env" ]; then
  echo "Missing .env file. Aborting."
  exit 1
fi

if [ ! -f "dorms.json" ]; then
  echo "Missing dorms.json file. Aborting."
  exit 1
fi

exec java -jar app.jar