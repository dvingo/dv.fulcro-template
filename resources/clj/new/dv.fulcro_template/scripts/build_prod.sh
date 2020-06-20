#!/bin/bash -

set -eo pipefail

write_prn_file() {
  echo "(ns {{namespace}}.client.prn-debug)(defn pprint-str [v])(defn pprint [v])" \
    > src/main/{{nested-dirs}}/client/prn_debug.cljs
}

main() {
  write_prn_file

  echo yarn run client/release
  yarn run client/release
  {{#node-server?}}
  echo yarn shadow-cljs release node-server
  yarn shadow-cljs release node-server
  {{/node-server?}}
  {{#server?}}
  echo yarn uberjar
  yarn uberjar
  if [[ "$1" = "run" ]]; then
    echo "running"
    yarn run start-prod-server
  fi
  {{/server?}}
  echo "done"
}

main "$@"
