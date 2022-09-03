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
    [taoensso.timbre :as log]
    [{{namespace}}.task.model :as task]
    [{{namespace}}.user.model :as user]
    [{{namespace}}.server.pathom-parser :refer [parser]])
  (:import [java.io PushbackReader IOException]))

(defn with-user
  ([email query]
   (parser {:ring/request {:session {::user/name email :session/valid? true}}}
     query))
  ([query]
   (parser {:ring/request {:session {::user/name "abc@abc.com" :session/valid? true}}}
     query)))

(comment
  (with-user "my-user@test.com"
    [::task/description]))
