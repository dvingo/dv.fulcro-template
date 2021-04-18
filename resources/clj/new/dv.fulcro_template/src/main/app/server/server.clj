(ns {{namespace}}.server.server
  (:require
    [clojure.pprint :refer [pprint]]
    [io.pedestal.http :as http]
    [mount.core :refer [defstate]]
    [{{namespace}}.server.config :refer [config]]
    [{{namespace}}.server.service :as service]
    [{{namespace}}.server.crux-node]
    [taoensso.timbre :as log]))

;(defstate http-server
;  :start
;  (let [pedestal-config       (::http/config config)
;        service (merge (service/make-service-map) pedestal-config)
;        port (::http/port pedestal-config)]
;    (when (nil? port)
;      (throw (Exception. "You must set a port as the environment variable PORT.")))
;    (log/info "Final service config: ")
;    (pprint service)
;    (log/info "Starting server on port" port)
;    (-> service http/create-server http/start))
;
;  :stop
;  (http/stop http-server))

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
            r    (try
                   (log/info "Starting server on port" port)
                   (-> service-map http/create-server http/start)
                   (catch IOException _
                     (println "EXCeption 1")
                     (log/info "Port " port " already in use trying" (inc port) "."))
                   (catch Exception e
                     (println "EXCeption 2" e " type: " (type e))))]
        (log/info "started server val: " r)
        (when-not r
          (when (< 10000 port))
          (recur
            (merge (service/make-service-map)
              (update pedestal-config ::http/port inc)))))))
  :stop
  (http/stop http-server))
