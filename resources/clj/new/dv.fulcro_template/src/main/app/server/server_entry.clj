(ns {{namespace}}.server.server-entry
  (:require
    [mount.core :as mount]
    {{namespace}}.server.server)
  (:gen-class))

(defn -main [& args]
  (println "args: " args)
  (mount/start-with-args {:config "config/prod.edn"}))
