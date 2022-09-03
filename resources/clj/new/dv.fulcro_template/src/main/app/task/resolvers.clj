(ns {{namespace}}.task.resolvers
  (:require
    [clojure.core.async :refer [go <! <!! >! >!! chan]]
    [clojure.pprint :refer [pprint]]
    [com.wsscode.pathom.connect :as pc]
    [{{namespace}}.user.model :as user]
    [{{namespace}}.user.db :as user.db]
    [{{namespace}}.task.model :as task]
    [{{namespace}}.task.db :as task.db]
    [dv.fulcro-util :as fu]
    [dv.xtdb-util :as xu]
    [taoensso.timbre :as log]))

(defmacro validity-check
  [& args]
  `(when-let
     [msg# (cond ~@args)]
     (fu/server-error msg#)))

(pc/defmutation create-task-mutation
  [{:keys [current-user xtdb-node]}
   {::task/keys [id description] :as props}]
  {::pc/sym '{{namespace}}.task/create-task}
  (let [user-tasks (::user/tasks current-user)]
    (log/info "props: ") (pprint props)
    (or
      (validity-check
        (not current-user) "You must be logged in to create a task."
        (not (task/valid? props)) "Task is invalid")

      (let [task (task/make-db-task props)
            new-user (fu/conj-vec current-user ::user/tasks id)]
        (log/info "Submitting tx for creating task")
        (pprint [task new-user])
        (xu/match-update-entities xtdb-node {id task (::user/id new-user) new-user})))))

(pc/defresolver get-task [{:keys [xtdb-node]} {::task/keys [id]}]
  {::pc/input #{::task/id}
   ::pc/output task/pathom-output}
  (log/info "get-task resolver id: " id)
  (task.db/get-task xtdb-node id))

(pc/defresolver all-tasks [{:keys [xtdb-node]} {::task/keys [id]}]
  {::pc/output [{:all-tasks task/db-task-keys}]}
  (log/info "all-tasks resolver")
  {:all-tasks (task.db/get-all-tasks xtdb-node)})

(def resolvers [create-task-mutation get-task all-tasks])
