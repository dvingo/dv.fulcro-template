SHELL := bash
.ONESHELL:
.SHELLFLAGS := -eu -o pipefail -c
.DELETE_ON_ERROR:
MAKEFLAGS += --warn-undefined-variables
MAKEFLAGS += --no-builtin-rules

server-jar-file := server.jar
fe-module := main

ifeq ($(origin .RECIPEPREFIX), undefined)
  $(error This Make does not support .RECIPEPREFIX. Please use GNU Make 4.0 or later)
endif
.RECIPEPREFIX = >

shadow-server:
> yarn
> yarn shadow-cljs server

fe:
> bash ./scripts/start_dev.sh

prod-build: fe-release {{#server?}}be-release{{/server?}}

shadow-report:
> yarn shadow-cljs run shadow.cljs.build-report $(fe-module) fe-bundle-report.html

watch-$(fe-module):
> yarn shadow-cljs watch :$(fe-module)

watch: watch-$(fe-module)

{{#workspaces?}}
watch-workspaces:
> yarn shadow-cljs watch :workspaces
{{/workspaces?}}

{{#devcards?}}
watch-devcards:
> yarn shadow-cljs watch :devcards
{{/devcards?}}

{{#test?}}
watch-client-test:
> yarn shadow-cljs watch :test

fe-test:
> yarn shadow-cljs compile ci-tests
> yarn karma start --single-run
> clj -A:dev:clj-tests
{{/test?}}

watch-all: watch-$(fe-module){{#workspaces?}}watch-workspaces{{/workspaces?}}{{#test?}}watch-client-test{{/test?}}

fe-release:
> yarn shadow-cljs release $(fe-module)

{{#server?}}
deploy/$(server-jar-file):
> clojure -A:depstar -m hf.depstar.uberjar deploy/$(server-jar-file)

be-release: deploy/$(server-jar-file)

clean:
> rm deploy/$(server-jar-file)

start-dev-server:
> clojure -A:dev

start-prod-server:
> pushd deploy
> java -cp app.jar clojure.main -m {{namespace}}.server.server-entry
{{/server?}}

.PHONY: fe fe-release prod-build shadow-report watch-$(fe-module) watch shadow-server
{{#workspaces?}}.PHONY: watch-workspaces{{/workspaces?}}
{{#test?}}.PHONY: fe-test watch-client-test{{/test?}}
{{#server?}}.PHONY: be-release clean start-dev-server start-prod-server{{/server?}}
