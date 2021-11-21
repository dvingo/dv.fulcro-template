(ns {{namespace}}.server.pathom-playground
  (:require
    [clojure.core.async :as async :refer [go <! >! <!! chan]]
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.spec.alpha :as s]
    [com.wsscode.pathom.connect :as pc]
    [com.wsscode.pathom.core :as p]
    [dv.xtdb-util :as cutil]
    [edn-query-language.core :as eql]
    [mount.core :refer [defstate]]
    [taoensso.timbre :as log]
    [{{namespace}}.auth.user :as user]
    [{{namespace}}.server.pathom-parser :refer [parser]])
  (:import [java.io PushbackReader IOException]))

(defn with-user
  ([email query]
   (parser {:ring/request {:session {:user/name email :session/valid? true}}}
     query))
  ([query]
   (parser {:ring/request {:session {:user/name "abc@abc.com" :session/valid? true}}}
     query)))

(comment
  (with-user "my-user@test.com"
    [:task/description])
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Playground parser
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Recursive resolver example

(pc/defresolver things-res [_ {:keys [xt/id]}]
  {::pc/input  #{:xt/id}
   ::pc/output [:name :children :xt/id]}
  (let [ent (cutil/entity id)
        ent (assoc ent :children (mapv #(hash-map :xt/id %) (:children ent)))]
    (log/info "Found entity: " ent)
    (select-keys ent [:name :children :xt/id])))

(def resolvers [things-res])

(def myparser
  (p/parser {::p/env     {::p/reader               [p/map-reader pc/reader2 pc/open-ident-reader
                                                    p/env-placeholder-reader]
                          ::p/placeholder-prefixes #{">"}}
             ::p/mutate  pc/mutate
             ::p/plugins [(pc/connect-plugin {::pc/register resolvers})
                          p/error-handler-plugin
                          p/trace-plugin]}))
