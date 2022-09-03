(ns clj.new.dv.fulcro-template.options.server
  (:require [clj.new.dv.fulcro-template.options.helpers :as helpers]))

(def option "+server")

(defn files [data]
  [;;auth
   ["src/main/{{nested-dirs}}/auth/session.clj" (helpers/render "src/main/app/auth/session.clj" data)]
   ["src/main/{{nested-dirs}}/auth/session.cljs" (helpers/render "src/main/app/auth/session.cljs" data)]
   ["src/main/{{nested-dirs}}/auth/login.cljs" (helpers/render "src/main/app/auth/login.cljs" data)]
   ["src/main/{{nested-dirs}}/auth/signup.cljs" (helpers/render "src/main/app/auth/signup.cljs" data)]

   ;data models

   ;; User
   ["src/main/{{nested-dirs}}/user/db.clj" (helpers/render "src/main/app/user/db.clj" data)]
   ["src/main/{{nested-dirs}}/user/resolvers.clj" (helpers/render "src/main/app/user/resolvers.clj" data)]

   ;; Task
   ["src/main/{{nested-dirs}}/task/db.clj" (helpers/render "src/main/app/task/db.clj" data)]
   ["src/main/{{nested-dirs}}/task/resolvers.clj" (helpers/render "src/main/app/task/resolvers.clj" data)]
   ;["src/main/{{nested-dirs}}/task/mutations.clj" (helpers/render "src/main/app/task/mutations.clj" data)]

   ;; user ns
   ["src/dev/user.clj" (helpers/render "src/dev/user.clj" data)]

   ;; pedestal log config
   ["resources/logback.xml" (helpers/render "resources/logback.xml" data)]

   ;; config
   ["src/main/config/defaults.edn" (helpers/render "src/main/config/defaults.edn" data)]
   ["src/main/config/dev.edn" (helpers/render "src/main/config/dev.edn" data)]
   ["src/main/config/test.edn" (helpers/render "src/main/config/test.edn" data)]
   ["src/main/config/prod.edn" (helpers/render "src/main/config/prod.edn" data)]

   ;; server code
   ["src/main/{{nested-dirs}}/server/config.clj" (helpers/render "src/main/app/server/config.clj" data)]
   ["src/main/{{nested-dirs}}/server/xtdb_node.clj" (helpers/render "src/main/app/server/xtdb_node.clj" data)]
   ["src/main/{{nested-dirs}}/server/queries.clj" (helpers/render "src/main/app/server/queries.clj" data)]
   ["src/main/{{nested-dirs}}/server/pathom_parser.clj" (helpers/render "src/main/app/server/pathom_parser.clj" data)]
   ["src/main/{{nested-dirs}}/server/pathom_playground.clj" (helpers/render "src/main/app/server/pathom_playground.clj" data)]
   ["src/main/{{nested-dirs}}/server/server.clj" (helpers/render "src/main/app/server/server.clj" data)]
   ["src/main/{{nested-dirs}}/server/server_entry.clj" (helpers/render "src/main/app/server/server_entry.clj" data)]
   ["src/main/{{nested-dirs}}/server/service.clj" (helpers/render "src/main/app/server/service.clj" data)]])
