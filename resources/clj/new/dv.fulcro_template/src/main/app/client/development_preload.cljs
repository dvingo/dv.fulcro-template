(ns {{namespace}}.client.development-preload
  (:require
    [com.fulcrologic.fulcro.algorithms.timbre-support :as ts]
    [{{namespace}}.client.malli-registry :as reg]
    [malli.registry :as mr]
    [taoensso.timbre :as log]))

(js/console.log "Turning logging to :debug (in app.development-preload)")
(log/set-level! :debug)
(log/merge-config! {:output-fn ts/prefix-output-fn
                    :appenders {:console (ts/console-appender)}})
(mr/set-default-registry! (mr/mutable-registry reg/registry*))
