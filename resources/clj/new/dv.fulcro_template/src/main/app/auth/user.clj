(ns {{namespace}}.auth.user
  (:require
    [clojure.spec.alpha :as s]
    [cryptohash-clj.impl.argon2 :refer [chash verify]]
    [com.fulcrologic.guardrails.core :refer [>defn => | ?]]
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
    [{{namespace}}.server.crux-node :refer [crux-node]]
    [dv.crux-util :as cu]
    [dv.fulcro-util :as fu]
    [taoensso.timbre :as log]))

(defn hash-password [password]
  (chash password))

(comment
  (chash "My OPW")
  (verify "My OPW" "100$12$argon2id$v13$pYRqzeaijuNw70HfHSvJgg$Ke0LQgABxQi6tH3yVTJsgtGoTZvQ4Rq+FTVwSbqB2Ok$$$"))

(defn verify-password [input hashed]
  (verify input hashed))

(defn make-user [id email password]
  {:user/id       id
   :user/email    email
   :user/password (hash-password password)
   :user/tasks    []})

(defn insert-user [id email password]
  (cu/insert-entity crux-node :user/id (make-user id email password)))

(defn insert-user-map [m]
  (cu/insert-entity crux-node :user/id m))

(defn fresh-user
  [email password]
  (make-user (fu/uuid) email password))

(defn get-user-by-email [username]
  (cu/entity-with-prop crux-node [:user/email username]))

(defn get-user-by-id [crux-node id]
  (cu/entity crux-node id))

(defn get-all-users []
  (->>
    (cu/crux-select crux-node [:user/id :user/email])
    (map #(cu/domain-entity crux-node (:user/id %)))
    (group-by :user/email)))

(defn get-current-user
  "Reads username (email) from the ring session"
  [{:keys [:ring/request]}]
  (log/info "session: " (:session request))
  (when-let [session (:session request)]
    (when (:session/valid? session)
      (if-let [email (:user/name session)]
        (do (log/info "Have a user: " email)
            (get-user-by-email email))
        (do (log/info "No user")
            nil)))))

(def user-keys [:user/email :user/id])
(def db-user-keys (into user-keys [:db/created-at :db/updated-at]))

(defresolver user-resolver [{:keys [crux-node]} {:user/keys [id]}]
  {::pc/input  #{:user/id}
   ::pc/output [:user/email]}
  (select-keys (get-user-by-id crux-node id) [:user/email]))

(pc/defresolver current-user-res [env _]
  {::pc/output [:app/current-user]}
  {:app/current-user (get-current-user env)})

(pc/defresolver all-users-resolver [env _]
  {::pc/output [{:all-users [:user/id]}]}
  {:all-users (map (comp #(cu/domain-entity crux-node %) :user/id)
                (cu/crux-select crux-node [:user/id]))})

(def resolvers [user-resolver current-user-res all-users-resolver])
