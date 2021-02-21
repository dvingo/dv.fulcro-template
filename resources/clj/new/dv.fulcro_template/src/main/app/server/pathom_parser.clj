(ns {{namespace}}.server.pathom-parser
  (:require
    [com.wsscode.pathom.connect :as pc]
    [dv.pathom :refer [build-parser]]
    [{{namespace}}.server.task-resolvers :as task]
    [{{namespace}}.auth.session :as session]
    [{{namespace}}.auth.user :as user]
    [{{namespace}}.server.config :refer [config]]
    [{{namespace}}.server.crux-node :refer [crux-node]]))

(def all-resolvers
  [session/resolvers
   user/resolvers
   task/resolvers])

(defstate parser
  :start
  (let [{:keys [log-responses? trace? index-explorer? connect-viz?]} (::config config)]
    (log/info "Constructing pathom parser with config: " (::config config))
    (build-parser
      {:resolvers          all-resolvers
       :log-responses?     log-responses?
       :handle-errors?     true
       :trace?             trace?
       :index-explorer?    index-explorer?
       :enable-pathom-viz? connect-viz?
       :env-additions      (fn [env]
                             ;(log/info "in pathom current user: " (some-> (users-dl/get-current-user env) :user/id))
                             {:crux-node    crux-node
                              :config       config
                              ;:auth         auth
                              #_#_:current-user (users-dl/get-current-user env)})})))
