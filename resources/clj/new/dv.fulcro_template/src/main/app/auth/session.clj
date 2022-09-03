(ns {{namespace}}.auth.session
  (:require [clojure.spec.alpha :as s]
            [com.fulcrologic.fulcro.server.api-middleware :as fmw]
            [com.fulcrologic.guardrails.core :refer [>defn => | ?]]
            [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
            [dv.fulcro-util :as fu]
            [taoensso.timbre :as log]
            [{{namespace}}.user.db :as user.db]
            [{{namespace}}.user.model :as user]))

(defn make-session [{:keys [valid? user error-msg] :or {error-msg nil}}]
  {:session/valid? valid? :session/server-error-msg error-msg
   :user (select-keys user [::user/id ::user/email])})

(defn valid-session [user]
  (make-session {:valid? true :user user}))

(defn invalid-session
  ([] (make-session {:valid? false}))
  ([msg] (make-session {:valid? false :error-msg msg})))

(defn augment-session-resp
  "Uses `mutation-response` as the actual return value for a mutation,
   but also stores the data into the session."
  ([mutation-env new-session-data]
   (augment-session-resp mutation-env new-session-data (or new-session-data {})))
  ([mutation-env new-session-data data]
   (let [existing-session (some-> mutation-env :ring/request :session)]
     (log/info "response-updating-session" new-session-data)
     (fmw/augment-response
       data
       (fn [resp]
         (let [new-session (cond->> new-session-data (some? new-session-data) (merge existing-session))]
           (log/info "Setting new session to " new-session)
           (assoc resp :session new-session)))))))

(pc/defmutation signup
  [{:keys [xtdb-node assoc-auth-fields] :as env} {:keys [email password]}]
  {::pc/sym '{{namespace}}.auth.signup/signup}
  (assert email "You must pass an email")
  (assert password "You must pass a password")
  (if (user.db/get-user-by-email xtdb-node email)
    (do
      (log/info "Signup attempt for user already in db: " email)
      (augment-session-resp env (invalid-session "Problem signing up.")))
    (let [new-user (assoc-auth-fields (user/make-db-user {::user/email email}) {:password password})]
      (user.db/insert-user xtdb-node new-user)
      (augment-session-resp env (valid-session new-user)))))

;; todo use a protocol to support pluggable auth
(defmutation login [{:keys [authenticate-user xtdb-node] :as env} {:keys [username password]}]
  {::pc/output [:session/valid? :user/name]}
    (log/info "Authenticating" username)
    (if-let [{hashed-pw :user/password :as user} (user.db/get-user-by-email xtdb-node username)]
      (do (log/info "User from db: " (dissoc user :user/password))
          (if (authenticate-user user {:password password})
            (augment-session-resp env (valid-session user))
            (do
              (log/error "Invalid credentials supplied for" username)
              (fu/server-error "Invalid credentials"))))
      (fu/server-error "Invalid credentials")))

(defmutation logout [env params]
  {::pc/output [:session/valid?]}
  (log/info "in logout")
  (augment-session-resp env (invalid-session)))

(defresolver current-session-resolver [env _]
  {::pc/output [{::current-session [:session/valid? :user/name]}]}
  (let [{:keys [user session/valid?] :as session} (get-in env [:ring/request :session])]
    (log/info " in current sesh resolver: " session)
    (if valid?
      (do
        (log/info (::user/email user) "already logged in!")
        {::current-session session})
      {::current-session (invalid-session)})))

(def resolvers [current-session-resolver signup login logout])
