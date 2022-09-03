(ns {{namespace}}.server.pathom-parser
  (:require
    [com.wsscode.pathom.connect :as pc]
    [dv.pathom :refer [build-parser]]
    [mount.core :refer [defstate]]
    [taoensso.timbre :as log]
    [{{namespace}}.auth.session :as session]
    [{{namespace}}.user.model :as user]
    [{{namespace}}.user.db :as user.db]
    [{{namespace}}.user.resolvers :as user.resolvers]
    [{{namespace}}.task.resolvers :as task.resolvers]
    [{{namespace}}.server.config :refer [config]]
    [{{namespace}}.server.xtdb-node :refer [xtdb-node]]))

(def all-resolvers
  [session/resolvers
   user.resolvers/resolvers
   task.resolvers/resolvers])

(defn make-parser
  [{:keys [resolvers connect-viz? log-responses? index-explorer? trace? xtdb-node]
    :or {log-responses? false  connect-viz? false index-explorer? false trace? false }}]
  (build-parser
    {:resolvers          all-resolvers
     :log-responses?     log-responses?
     :handle-errors?     true
     :trace?             trace?
     :index-explorer?    index-explorer?
     :enable-pathom-viz? connect-viz?
     :env-additions      (fn [env]
                           {:xtdb-node         xtdb-node
                            :assoc-auth-fields (fn [user auth-args] (user.db/assoc-auth-fields user auth-args))
                            :authenticate-user (fn [user {:keys [password]}]
                                                 (log/info "verifying user: " user)
                                                 (log/info "verifying password : " password)
                                                 (user.db/verify-password password (::user/password user)))
                            :config            config
                            :current-user      (user.db/get-current-user-from-session xtdb-node env)})}))

(defstate parser
  :start
  (let [{:keys [log-responses? trace? index-explorer? connect-viz?] :as args} (::config config)]
    (log/info "Constructing pathom parser with config: " (::config config))
    (make-parser (assoc args :resolvers all-resolvers
                             :xtdb-node xtdb-node))))
