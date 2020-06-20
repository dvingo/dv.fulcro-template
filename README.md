# fulcro app template

A Clojure tools.deps template for quickly creating a fulcro application.

This is a work in progress and reflects one style of app layout.

Feel free to remove or change any parts of the generated code.

It was based on the fulcro3 template: https://github.com/fulcrologic/fulcro-template

# Use it

Add a `new` alias to your user deps.edn file see:

https://github.com/seancorfield/dot-clojure

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

# Building the template repo itself

Build a deployable jar of this template:

    clojure -A:jar

Install it locally:

    clojure -A:install

Deploy it to Clojars -- needs `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment variables:

    clojure -A:deploy

References:

This was adapted from:

https://clojars.org/re-frame-template/clj-template
