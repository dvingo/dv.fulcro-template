# Prerequisites

This template utilizes babashka to execute tasks.

This template uses `yarn` to handle npm dependencies.

# Start dev

## Shadow cljs tasks

In one terminal:

```bash
# If you use nvm, if not see .nvmrc for the expected node.js version you should have installed.
nvm use

bb fe
# optionally pass any deps.edn aliases:
bb fe :dev/my-alias
```
this runs `yarn install` and starts the shadow-cljs watch processes.

Please see the `shadow-cljs.edn` file for ports used for development builds.

If any of those ports are used already shadow-cljs will try different ports so please see the console output 
by shadow-cljs.

A shadow-cljs http server is started on port 8021 that serves the frontend app from resources/public/index.html
{{#server?}}
This is useful if you want to hack on the client code without needing the backend server running. Of course there will 
be no API data though.

When the main build is complete, start the backend server either in an editor or at the command line.

The clj server reads the manifest file produced by shadow-cljs so the frontend build must complete before you start the server.
{{/server?}}

## Editor setup

In your editor:
add 2 repls:

### frontend repl:

nREPL remote:

  localhost:$port
  
The $port defaults to 9000 but may be different if 9000 is already in use.

Using this repl you connect to the various ClojureScript builds using `(shadow/repl :build-id)`

{{#server?}}
### backend repl:

nREPL local

enable these deps aliases: dev, test, guardrails

start backend repl, then:

```clojure
(start) ;; (user/start)
```
This uses mount to start the web server.

_note_ you do not need to specify any JVM parameters.

You can start this on the command line via:

```bash
bb be-repl

# pass any aliases from deps.edn

bb be-repl :dev/my-alias
```

## Clojure webserver.

The clojure webserver listens on port 8085 by default - this is specified in `src/main/config/defaults.edn`

http://localhost:8085

# Production server build

All builds are handled by tasks in the Makefile.


Both frontend and backend builds:
```bash
bb release
```

Server jar only:

```bash
bb be-release
```

Run the prod server:
```bash
bb run-jar
```
{{/server?}}

{{#workspaces?}}
## Workspaces
Workspaces are available at:

http://127.0.0.1:8023

Again, the port may be different if 8023 is already in use.
{{/workspaces?}}

{{#devcards?}}
## Devcards
Devcards are available at:

http://127.0.0.1:4001
{{/devcards?}}

{{#node-server?}}
# Node.js server

Start the server with:
```bash
bb start-node-server
```

When the server is started you can connect a cljs repl using:

```clojure
(shadow/repl :node-server)
```
{{/node-server?}}
