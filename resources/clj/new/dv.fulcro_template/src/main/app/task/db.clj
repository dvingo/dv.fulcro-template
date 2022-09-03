(ns {{namespace}}.task.db
  (:require
    [{{namespace}}.task.model :as task]
    [dv.xtdb-util :as xu]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DB API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-task [xtdb-node id] (select-keys (xu/domain-entity xtdb-node id) task/db-task-keys))

(defn get-all-tasks [xtdb-node]
  (xu/q xtdb-node '[:find [(pull ?t [*])] :where [[?t ::task/id]]]))

(defn insert-task [xtdb-node t] (xu/insert-entity xtdb-node ::task/id t))

(defn read-merge-task
  [xtdb-node task]
  (xu/read-merge-entity xtdb-node ::task/id task))

(defn merge-task!
  [xtdb-node task]
  (xu/match-update-entity xtdb-node (::task/id task) task))
