(ns {{namespace}}.task.model
  (:require
    [clojure.string :as str]
    [com.fulcrologic.fulcro.raw.components :as rc]
    [dv.fulcro-util-common :as fu]
    [malli.core :as m]
    [space.matterandvoid.malli.transform.fulcro :as malli.xf.fulcro :refer [schema->fulcro-query]]
    [space.matterandvoid.malli.transform.pathom :as malli.xf.pathom :refer [schema->pathom-output]]
    [{{namespace}}.malli-registry :as reg]
    #?(:clj [dv.xtdb-util :as xu])
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Sample Task Model
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def required-task-keys [::id ::description])
(def optional-task-keys [])
(def all-task-keys (into required-task-keys optional-task-keys))
(def global-keys [:db/created-at :db/updated-at :xt/id])
(def db-task-keys (into all-task-keys global-keys))

(def Task
  [:map
   ::id
   ::description
   [:xt/id {:optional true}]])

(reg/register! {::id          :uuid
                ::description :string
                ::task        Task})

(def pathom-output (schema->pathom-output Task))
(def fulcro-query (schema->fulcro-query Task))

(def task-component (rc/nc fulcro-query {:componentName ::TaskComponent}))

(defn make-task
  {:malli/schema [:=> [:cat :map] ::task]}
  [{::keys [id description]
    :or    {id (fu/uuid)}}]
  {::id          id
   ::description description})

(defn make-db-task [args]
  (let [{::keys [id] :as task} (make-task args)]
    (-> task
      (assoc :xt/id id))))

(comment (make-task {::description "TEST"}))

(defn fresh-task [props]
  (make-task (dissoc props ::id)))

(def valid? (m/validator Task))
(def explain-task (m/explainer Task))

(defn validate! [t] (valid? t))
(defn explain-invalid [t] (-> t (m/explainer Task)))

(comment
  (make-task {::description "hello"})
  (make-task {::id          :TESTING
              ::description "desc"}))
