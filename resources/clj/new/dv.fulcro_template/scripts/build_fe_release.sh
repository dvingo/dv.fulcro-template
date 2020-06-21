#!/bin/bash -

set -eo pipefail

write_prn_file() {
  echo "(ns {{namespace}}.client.prn-debug)(defn pprint-str [v])(defn pprint [v])" \
    > src/main/{{nested-dirs}}/client/prn_debug.cljs
}

main() {
  write_prn_file

  if [ $# -ne 1 ]; then
    printf "Pass a shadow-cljs build id.\n"
    printf "\nUsage:
    %s <build_id>

" "$0"
    exit 1
  fi

  local build_id="$1"
  echo yarn; yarn
  echo yarn shadow-cljs release "$build_id"
  yarn shadow-cljs release "$build_id"

  echo "done"
}

main "$@"
