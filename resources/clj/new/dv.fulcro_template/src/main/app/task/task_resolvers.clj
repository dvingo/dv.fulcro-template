(ns {{namespace}}.task.task-resolvers
  (:require
    [clojure.core.async :refer [go <! <!! >! >!! chan]]
    [clojure.pprint :refer [pprint]]
    [clojure.spec.alpha :as s]
    [com.fulcrologic.guardrails.core :refer [>defn >def | => ?]]
    [com.wsscode.pathom.connect :as pc]
    [{{namespace}}.auth.user :as user]
    [{{namespace}}.task.data-model :as dm]
    [{{namespace}}.task.db-layer :as dl]
    [dv.fulcro-util :as fu]
    [dv.xtdb-util :as cu]
    [taoensso.timbre :as log]))

(defmacro validity-check
  [& args]
  `(when-let
     [msg# (cond ~@args)]
     (fu/server-error msg#)))

(pc/defmutation create-task-mutation
  [{:keys [current-user xtdb-node]}
   {:task/keys [id description] :as props}]
  {::pc/sym '{{namespace}}.task/create-task}
  (let [user-tasks (:user/tasks current-user)]
    (log/info "props: ") (pprint props)
    (or
      (validity-check
        (not current-user) "You must be logged in to create a task."
        (not (s/valid? ::dm/task props)) "Task is invalid")

      (let [task (dm/make-db-task props)
            new-user (dl/read-merge-user xtdb-node (fu/conj-vec current-user :user/tasks id))]
        (log/info "Submitting tx for creating task")
        (pprint [task new-user])
        (cu/put-all xtdb-node [task new-user])))))

(pc/defresolver get-task [{:keys [xtdb-node]} {:task/keys [id]}]
  {::pc/input #{:task/id}
   ::pc/output [:task/description]}
  (log/info "get-task resolver id: " id)
  (dl/get-task xtdb-node id))

(pc/defresolver all-tasks [{:keys [xtdb-node]} {:task/keys [id]}]
  {::pc/output [{:all-tasks dm/db-task-keys}]}
  (log/info "all-tasks resolver")
  {:all-tasks (dl/get-all-tasks xtdb-node)})

(def resolvers [create-task-mutation get-task all-tasks])
