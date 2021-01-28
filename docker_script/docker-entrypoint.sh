#!/bin/sh

# Abort on any error (including if wait-for-it fails).
set -e

# Wait for the backend to be up, if we know where it is.
if [ -n "$HOST" ]; then
  /usr/app/wait-for-it.sh "$HOST:${CUSTOMERS_PORT:-15672}"
fi

# Run the main container command.
exec "$@"