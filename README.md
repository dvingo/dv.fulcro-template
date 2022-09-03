# fulcro app template

[![Clojars Project](https://img.shields.io/clojars/v/dv.fulcro-template/clj-template.svg)](https://clojars.org/dv.fulcro-template/clj-template)

A Clojure tools.deps template for quickly creating a fulcro application.

This is a work in progress and reflects one style of app layout.

Feel free to remove or change any parts of the generated code.

It was based on the fulcro3 template: https://github.com/fulcrologic/fulcro-template
but differs in backend tech and will continue to diverge as features are added to this template.

## Tech Stack

- Fulcro
  - Dead code elimination of guardrails (expound and pprint) during release builds.
  - reagent rendering middleware of fulcro components.
- styling with cljs-emotion with two example themes
- shadow-cljs clojurescript compiler    
  - frontend config via shadow.resource/inline
- malli with custom registry on the client
- reitit for URL routing, can be used with malli for parameter coercion
- pathom 2.4.0
- pedestal web server using jetty
- XTDB database
  - uses single rocks-db node in dev
  - uses in memory node during testing
  - uses rocksdb node + postrgres tx+docs store in production.
  - stores ring sessions in XTDB.
- Component driven development tooling, choice of:
  - workspaces (add link)
  - devcards
  - storybook

# Use it

## TLDR

Install `clj-new` as a tool if you don't have it installed (this only needs to be done once).

```bash
# Only needs to be done once, to install the tool:
clojure -Ttools install com.github.seancorfield/clj-new '{:git/tag "v1.2.381"}' :as clj-new
```

Add these aliases to your ~/.clojure/deps.edn

```clojure
  :fulcro-app {:extra-deps {com.github.seancorfield/clj-new {:mvn/version "1.2.381"}}
               :exec-fn clj-new/create
               :exec-args {:template "dv.fulcro-template" :args ["+test" "+node-server"]}}

  :full-fulcro-app {:extra-deps {com.github.seancorfield/clj-new {:mvn/version "1.2.381"}}
               :exec-fn clj-new/create
               :exec-args {:template "dv.fulcro-template" :args ["+all"]}}
```

Now you can run:

```bash
# for a js only app:
clj -X:fulcro-app :name pro.my-org/app-name

# with pedestal + pathom + xtdb server:
clj -X:full-fulcro-app :name pro.my-org/app-name
```

For even more convenience you can make shell function helpers (this is bash):

```bash
new_fulcro_app () {
  if [ -z $1 ]; then echo "You must supply a project name, optionally clj-new args."; return; fi
  clojure -X:fulcro-app :name "${@}"
}

new_full_fulcro_app () {
  if [ -z $1 ]; then echo "You must supply a project name, optionally clj-new args."; return; fi
  clojure -X:full-fulcro-app :name "${@}"
}
```
And invoke them like so:
```bash
new_fulcro_app co.my-org/my-app
# override the args:
new_fulcro_app co.my-org/my-app :args '["+test"]'

# overwrite existing directory:
new_fulcro_app co.my-org/my-app :force true
# et cetera
```

### Start development proceses:

```bash
cd app-name
# Requires node > 16
nvm use

# Start compiling ClojureScript

bb fe
# or pass any deps aliases:
bb fe :dev/my-alias

# Connect your editor of choice and start the server if you have one via src/dev/user.clj.
# You can also start an nREPL server via:

bb be-repl
# or pass any deps aliases:
bb be-repl :dev/my-alias
```

## Have time, will read

clj-new can be invoked as a clojure tool or via a deps.edn alias,

You can install clj-new as a tool with:
```bash
clojure -Ttools install com.github.seancorfield/clj-new '{:git/tag "v1.2.381"}' :as clj-new
```
Or add a `new` alias to your user deps.edn file. For instructions see:

https://github.com/seancorfield/clj-new

Then construct a new project as specified below

After your project is generated you should run:

```bash
yarn
yarn outdated
```

and

```bash
clojure -Sdeps '{:deps {com.github.liquidz/antq {:mvn/version "RELEASE"}}}' -m antq.core
```

which will list out of date dependencies for you to update.

----
Commands to make a new project directory from this template.

If you installed clj-new as a tool replace `clj -X:new` with `clj -Tclj-new create` in the commands below.
If you didn't install the alias above in your ~/.clojure/deps.edn , then use: `clj -X:new :template dv.fulcro-template`

Using Clojure CLI version 1.10.1.727 or later
```bash
# Frontend only app
clj -X:fulcro-app :name my-username/my-project-name

# Pass one or more options
clj -X:fulcro-app :name my-username/my-project-name :args '["+devcards" "+workspaces" "+test" "+node-server" "+server"]'

# Or include them all:
clj -X:fulcro-app :name my-username/my-project-name :args '["+all"]'

# output to another directory name, and overwrite if it already exists:
clj -X:fulcro-app :name my-username/my-project-name :output '"my-preferred-project-name"' :force true
```

If you're working on the template itself, you can generate a project from the filesystem:

```bash
clj -X:new :template '"/home/my-user/dv.fulcro-template::dv.fulcro-template"' :name my-group/my-project
```

You can also add this to your ~/.clojure/deps.edn file in an alias for convenience.

## Options

```
+devcards
+storybook
+workspaces
+test
+node-server
+server
```

You can include all the above options by just passing:
```
+all
```

With no options you get a client-only fulcro app with a dev webserver handled by shadow-cljs.

The node-server is used for server-side rendering, it is not fully baked.
It has code to render the root fulcro component to a string, but some more work is needed
to ask the clojure server for the start data and inject that into the resulting page and pick it up
on the client.

The `+server` option produces a pedestal+jetty server with reitit for routing and muuntaja
to handle content negotiation.

The template uses helpers from https://github.com/dvingo/my-clj-utils
to setup a pathom parser and a XTDB standalone rocksdb node.

The server has simple password auth using cryptohash-clj to hash passwords with argon2.

Here is sample output of the template with these arguments: 

`'["+server" "+workspaces" "+test" "+node-server"]'`
```
$ tree -a
.
├── .clj-kondo
│   └── config.edn
├── deps.edn
├── .gitignore
├── guardrails.edn
├── karma.conf.js
├── Makefile
├── package.json
├── README.md
├── resources
│   ├── logback.xml
│   └── public
│       └── workspaces
│           └── workspaces.html
├── scripts
│   ├── node_server_start.sh
│   └── start_dev.sh
├── shadow-cljs.edn
├── src
│   ├── dev
│   │   └── user.clj
│   ├── main
│   │   ├── config
│   │   │   ├── defaults.edn
│   │   │   ├── dev.edn
│   │   │   ├── fe-config.edn
│   │   │   ├── prod.edn
│   │   │   └── test.edn
│   │   └── space
│   │       └── matterandvoid
│   │           └── template_test
│   │               ├── auth
│   │               │   ├── login.cljs
│   │               │   ├── session.clj
│   │               │   ├── session.cljs
│   │               │   ├── signup.cljs
│   │               │   ├── user.clj
│   │               │   └── user.cljs
│   │               ├── client
│   │               │   ├── application.cljs
│   │               │   ├── client_entry.cljs
│   │               │   ├── development_preload.cljs
│   │               │   ├── malli_registry.cljs
│   │               │   ├── prn_debug.cljs
│   │               │   ├── prn_debug_noop.cljs
│   │               │   └── ui
│   │               │       ├── root.cljs
│   │               │       └── styles
│   │               │           ├── app_styles.cljs
│   │               │           ├── global_styles.cljs
│   │               │           └── style_themes.cljs
│   │               ├── node_server.cljs
│   │               ├── server
│   │               │   ├── config.clj
│   │               │   ├── xtdb_node.clj
│   │               │   ├── pathom_parser.clj
│   │               │   ├── pathom_playground.clj
│   │               │   ├── server.clj
│   │               │   ├── server_entry.clj
│   │               │   └── service.clj
│   │               └── task
│   │                   ├── data_model.cljc
│   │                   ├── db_layer.clj
│   │                   ├── task_resolvers.clj
│   │                   └── ui
│   │                       ├── task_item.cljs
│   │                       └── task_page.cljs
│   ├── test
│   │   └── space
│   │       └── matterandvoid
│   │           └── template_test
│   │               └── app_tests.cljc
│   └── workspaces
│       └── space
│           └── matterandvoid
│               └── template_test
│                   └── main_ws.cljs
└── yarn.lock

27 directories, 51 files

```

# Building the template repo itself

Build a deployable jar of this template:

    clojure -X:jar

Install it locally:

    clojure -M:install

Deploy it to Clojars -- needs `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment variables:

    clojure -M:deploy

Steps to deploy:

Increment pom.xml version
commit all changes, push to remote.
```bash
make
```
run deploy

References:

This was adapted from:

https://clojars.org/re-frame-template/clj-template
