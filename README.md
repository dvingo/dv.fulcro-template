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
- pathom 2.2.31
- pedestal web server using jetty
- crux database,
  - uses single rocks-db node in dev
  - uses in memory node during testing
  - uses rocksdb node + postrgres tx+docs store in production.
  - stores ring sessions in crux.

# Use it

Add a `new` alias to your user deps.edn file. For instructions see:

https://github.com/seancorfield/clj-new

Then construct a new project as specified below.

After it is generated you should run:

```bash
yarn
yarn outdated
```

and

```bash
clojure -Sdeps '{:deps {com.github.liquidz/antq {:mvn/version "RELEASE"}}}' -m antq.core
```

and update any out of date dependencies.

Using Clojure CLI version 1.10.1.727 or later
```bash
# Frontend only app
clj -X:new :template dv.fulcro-template :name my-username/my-project-name

# Pass one or more options
clj -X:new :template dv.fulcro-template :name my-username/my-project-name :args '["+devcards" "+workspaces" "+test" "+node-server" "+server"]'

# Or include them all:
clj -X:new :template dv.fulcro-template :name my-username/my-project-name :args '["+all"]'

# output to another directory name, and overwrite if it already exists:
clj -X:new :template dv.fulcro-template :name my-username/my-project-name :output '"my-preferred-project-name"' :force true
```

Using Clojure CLI versions before 1.10.1.727:

```bash
# Frontend only app
clj -A:new dv.fulcro-template my-username/my-project-name

# Pass one or more options
clj -A:new dv.fulcro-template my-username/my-project-name +devcards +workspaces +test +node-server +server

# Or include them all:
clj -A:new dv.fulcro-template my-username/my-project-name +all

# output to another directory name:
clj -A:new dv.fulcro-template my-username/my-project-name -o my-preferred-project-name
```

If you're working on the template itself, you can generate a project from the filesystem:

```bash
clj -X:new :template '"/home/my-user/dv.fulcro-template::dv.fulcro-template"' :name my-group/my-project
```

## Options

```
+devcards
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
to setup a pathom parser and a crux standalone rocksdb node.

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
│   │               │   ├── crux_node.clj
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
