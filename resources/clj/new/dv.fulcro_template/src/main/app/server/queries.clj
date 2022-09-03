(ns {{namespace}}.server.queries
  (:require
    [xtdb.api :as xt]
    [{{namespace}}.task.model :as task]
    [{{namespace}}.user.model :as user]))

(defn user->tasks
  "Returns all tasks associated with the given user"
  [db user]
  (mapv first
    (xt/q db
      {:find  '[(pull ?h [::task/id ::task/description])]
       :where ['[?u ::user/email ?email]
               '[?u ::user/tasks ?h]]
       :in    '[?email]}
      (::user/email user))))
