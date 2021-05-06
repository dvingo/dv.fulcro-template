#!/bin/bash -

set -euo pipefail

main() {
  echo yarn install
  yarn install

  echo '
  Greetings. I trust you will have an excellent day.


  Starting shadow-cljs watches for you via the following command:

  yarn run shadow-cljs watch main{{#node-server?}} node-server{{/node-server?}}{{#test?}} test{{/test?}}{{#devcards?}} devcards{{/devcards?}}{{#workspaces?}} workspaces{{/workspaces?}}

  # shadow-cljs builds:
  http://localhost:9630

  # Frontend app
  start nrepl to shadow-cljs port (see shadow-cljs.edn) then connect with:
  (shadow/repl :main)

{{#server?}}
  # Backend
  1. Start clj repl for backend in your editor and then execute:
  (user/start)
{{/server?}}
{{#test?}}

  # Tests
 http://localhost:8022
{{/test?}}
{{#workspaces?}}

 # Workspaces
 http://localhost:8023
{{/workspaces?}}
'

  yarn run shadow-cljs watch main{{#node-server?}} node-server{{/node-server?}}{{#test?}} test{{/test?}}{{#devcards?}} devcards{{/devcards?}}{{#workspaces?}} workspaces{{/workspaces?}}
}

main "$@"
