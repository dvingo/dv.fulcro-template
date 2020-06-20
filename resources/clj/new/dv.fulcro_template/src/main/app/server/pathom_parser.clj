(ns {{namespace}}.server.pathom-parser
  (:require
    [com.wsscode.pathom.connect :as pc]
    [dv.crux-node :refer [crux-node]]
    [dv.pathom :refer [build-parser]]
    [{{namespace}}.server.task-resolvers :as task]
    [{{namespace}}.auth.session :as session]
    [{{namespace}}.auth.user :as user]
    [{{namespace}}.server.config :refer [config]]))

(def all-resolvers
  [session/resolvers
   user/resolvers
   task/resolvers])

(let [{:keys [trace? index-explorer? log-responses? connect-viz?]} (::config config)]
  (def parser
    (build-parser
      {:resolvers          all-resolvers
       :trace?             trace?
       :index-explorer?    index-explorer?
       :log-responses?     log-responses?
       :enable-pathom-viz? connect-viz?
       :env-additions      (fn [env] {:crux-node    crux-node
                                      :config       config
                                      :current-user (user/get-current-user env)})})))
