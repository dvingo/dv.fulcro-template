(ns {{namespace}}.server.server
  (:require
    [clojure.pprint :refer [pprint]]
    [io.pedestal.http :as http]
    [mount.core :refer [defstate]]
    [{{namespace}}.server.config :refer [config]]
    [{{namespace}}.server.service :as service]
    [{{namespace}}.server.xtdb-node]
    [taoensso.timbre :as log])
  (:import [java.net BindException]))

(defstate http-server
  :start
  (let [pedestal-config (::http/config config)
        service         (merge (service/make-service-map) pedestal-config)]
    (when (nil? (::http/port pedestal-config))
      (throw (Exception. "You must set a port as the environment variable PORT.")))
    (log/info "Final service config: ")
    (pprint service)

    (loop [service-map service]
      (let [port (::http/port service-map)
            server-val
                 (try
                   (log/info "Starting server on port" port)
                   (-> service-map http/create-server http/start)
                   (catch Exception e
                     (if (instance? BindException (.getCause e))
                       (if (< 10000 port) ::max-attempts-reached ::port-already-bound)
                       (log/error "Exception when trying to start the server: " e))))]
        (cond
          (= server-val ::port-already-bound)
          (do
            (log/info "Port " port " already in use trying" (inc port) ".")
            (recur (update service-map ::http/port inc)))
          (= server-val ::max-attempts-reached)
          (do (log/error "Max attempts trying to bind server to an open port. Failed to start server.") nil)

          server-val server-val))))
  :stop
  (http/stop http-server))
