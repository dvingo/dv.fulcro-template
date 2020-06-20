#!/bin/bash -

set -euo pipefail

filename=node-server.js
dir=builds/node-server/
full_path="$dir$filename"

main() {
  node "$full_path"
}

start_server() {
  printf "\nRestarting server\n\n"
  node "$full_path"
}

trap start_server EXIT
main "$@"
