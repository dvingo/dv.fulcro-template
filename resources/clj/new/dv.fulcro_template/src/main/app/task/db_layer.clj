(ns {{namespace}}.task.db-layer
  (:require
    [com.fulcrologic.guardrails.core :refer [>defn >def | => ?]]
    [{{namespace}}.task.data-model :as dm]
    [dv.xtdb-util :as cu]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DB API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-task [xtdb-node id] (select-keys (cu/domain-entity xtdb-node id) dm/db-task-keys))
(defn get-all-tasks [xtdb-node]
  (cu/crux-select xtdb-node dm/all-task-keys))

(defn insert-task [xtdb-node t] (cu/insert-entity xtdb-node :task/id t))
(defn read-merge-task
  [xtdb-node task-data]
  (cu/read-merge-entity xtdb-node :task/id task-data))

(defn read-merge-user
  [xtdb-node user-data]
  (cu/read-merge-entity xtdb-node :user/id user-data))

;; does db update
(defn merge-user!
  "(merge-user! {:new user data})"
  [xtdb-node user-data]
  (cu/merge-domain-entity xtdb-node :user/id user-data))
