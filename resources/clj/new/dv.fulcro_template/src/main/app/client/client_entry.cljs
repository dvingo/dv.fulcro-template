(ns {{namespace}}.client.client-entry
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.ui-state-machines :as uism]
    [com.fulcrologic.fulcro.components :as c]
    [clojure.edn :as edn]
    [{{namespace}}.client.ui.root :as root]
    [{{namespace}}.client.application :refer [SPA]]
    {{#server?}}
    [{{namespace}}.auth.login :refer [Login Session]]
    [{{namespace}}.auth.session :as session]
    {{/server?}}
    [dv.fulcro-reitit :as fr]
    [reitit.coercion.malli]
    [shadow.resource :as rc]
    [space.matterandvoid.subscriptions.fulcro :as sub]
    [taoensso.timbre :as log]))

;; set logging lvl using goog-define, see shadow-cljs.edn
(goog-define LOG_LEVEL "warn")

(def fe-config (edn/read-string (rc/inline "/config/fe-config.edn")))
(log/info "Log level is: " LOG_LEVEL)

(def log-config
  (merge
    (-> fe-config ::config :logging)
    {:level (keyword LOG_LEVEL)}))

(defn ^:export refresh []
  (log/info "Hot code remount")
  (log/merge-config! log-config)
  (when goog/DEBUG (malli.dev.cljs/start!))
  (sub/clear-subscription-cache! SPA)
  (c/refresh-dynamic-queries! SPA)
  (app/mount! SPA root/Root "app"))

(defn ^:export init []
  (log/merge-config! log-config)
  (log/info "Application starting.")
  (when goog/DEBUG (malli.dev.cljs/start!))
  (fr/register-fulcro-router! SPA root/TopRouter {:data {:coercion reitit.coercion.malli/coercion}})
  (app/set-root! SPA root/Root {:initialize-state? true})
  (fr/start-router! SPA)
  {{#server?}}
  (log/info "Starting session machine.")
  (uism/begin! SPA session/session-machine ::session/session
    {:actor/login-form      Login
     :actor/current-session Session})
  {{/server?}}
   (log/info "MOUNTING APP")

  (js/setTimeout #(app/mount! SPA root/Root "app" {:initialize-state? true})))
