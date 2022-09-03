(ns {{namespace}}.user.db
  (:require
    [cryptohash-clj.impl.argon2 :refer [chash verify]]
    [dv.xtdb-util :as xu]
    [{{namespace}}.task.model :as task]
    [{{namespace}}.user.model :as user]
    [taoensso.timbre :as log]
    [xtdb.api :as xt]))

(defn hash-password [password]
  (chash password))

(defn verify-password [input hashed]
  (verify input hashed))

(defn insert-user [xtdb-node user]
  (xu/insert-entity xtdb-node ::user/id user))

(defn fetch-user [xtdb-node id]
  (log/info "Fetching user: " id)
  (update (xt/entity (xt/db xtdb-node) id) ::user/tasks
    (fn [ts] (mapv #(do {::task/id %}) ts))))

(defn get-user-by-email [xtdb-node username]
  (xu/entity-with-prop xtdb-node [::user/email username]))

(defn get-user-by-id [xtdb-node id]
  (xu/entity xtdb-node id))

(defn get-all-users [xtdb-node]
  (->>
    (xu/select xtdb-node [::user/id ::user/email])
    (map #(xu/domain-entity xtdb-node (::user/id %)))
    (group-by ::user/email)))

(defn get-current-user-from-session
  "Reads username (email) from the ring session"
  [xtdb-node {:keys [:ring/request]}]
  (log/info "session: " (:session request))
  (when-let [session (:session request)]
    (when (:session/valid? session)
      (if-let [email (::user/email (:user session))]
        (do (log/info "Have a user: " email)
            (get-user-by-email xtdb-node email))
        (do (log/info "No user")
            nil)))))

(defn assoc-auth-fields [user {:keys [password]}]
  (assert (some? password) (str "Must provide a password for user: " (pr-str user)))
  (assoc user ::user/password (hash-password password)))

(defn merge-user!
  [xtdb-node user]
  (xu/match-update-entity xtdb-node (::user/id user) user))
