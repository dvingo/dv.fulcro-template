(ns {{namespace}}.server.task-resolvers
  (:require
    [clojure.core.async :refer [go <! <!! >! >!! chan]]
    [clojure.pprint :refer [pprint]]
    [clojure.spec.alpha :as s]
    [com.fulcrologic.guardrails.core :refer [>defn >def | => ?]]
    [com.wsscode.pathom.connect :as pc]
    [{{namespace}}.auth.user :as user]
    [{{namespace}}.data-model.task :as dm]
    [{{namespace}}.server.db-layer :as dl]
    [dv.fulcro-util :as fu]
    [dv.crux-util :as cu]
    [taoensso.timbre :as log]))

(defmacro validity-check
  [& args]
  `(when-let
     [msg# (cond ~@args)]
     (fu/server-error msg#)))

(pc/defmutation create-task-mutation
  [{:keys [current-user crux-node]}
   {:task/keys [id description] :as props}]
  {::pc/sym '{{namespace}}.client.ui.task-item/create-task}
  (let [user-tasks (:user/tasks current-user)]
    (log/info "props: ") (pprint props)
    (or
      (validity-check
        (not current-user) "You must be logged in to create a task."
        (not (s/valid? ::dm/task props)) "Task is invalid")

      (let [task (dm/make-db-task props)
            new-user (dl/read-merge-user (fu/conj-vec current-user :user/tasks id))]
        (log/info "Submitting tx for creating task")
        (pprint [task new-user])
        (cu/put-all crux-node [task new-user])))))

(pc/defresolver get-task [{:keys [crux-node]} {:task/keys [id]}]
  {::pc/input #{:task/id}
   ::pc/output [:task/description]}
  (log/info "get-task resolver id: " id)
  (dl/get-task crux-node id))

(pc/defresolver all-tasks [{:keys [crux-node]} {:task/keys [id]}]
  {::pc/output [{:all-tasks dm/db-task-keys}]}
  (log/info "all-tasks resolver")
  {:all-tasks (dl/get-all-tasks crux-node)})

(def resolvers [create-task-mutation get-task all-tasks])
