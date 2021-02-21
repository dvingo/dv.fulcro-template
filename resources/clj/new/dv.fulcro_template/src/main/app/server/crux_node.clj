(ns {{namespace}}.server.crux-node
  (:require
    [{{namespace}}.server.config :refer [config]]
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [crux.api :as crux]
    [crux.jdbc :as jdbc]
    [crux.rocksdb :as rocks]
    [dv.fulcro-util-common :as fu]
    [mount.core :refer [defstate]]
    [taoensso.timbre :as log]))

(defn crux-pg-config
  "Index store: rocksdb
  Doc store: Postgres
  Tx store: Postgres"
  [data-dir config]
  (let [config* (::config config)
        {:keys [db-spec]} config*]
    {::jdbc/connection-pool {:dialect {:crux/module 'crux.jdbc.psql/->dialect} :db-spec db-spec}
     :crux/tx-log           {:crux/module `jdbc/->tx-log, :connection-pool ::jdbc/connection-pool}
     :crux/document-store   {:crux/module `jdbc/->document-store, :connection-pool ::jdbc/connection-pool}

     :crux/index-store      {:kv-store {:crux/module `rocks/->kv-store, :db-dir (io/file data-dir "indexes")}}
     #_#_:crux.metrics.cloudwatch/reporter {:jvm-metrics? true}}))

(defn crux-rocks-config
  [data-dir config]
  {:crux/tx-log         {:kv-store
                         {:crux/module `crux.rocksdb/->kv-store
                          :sync?       true
                          :db-dir      (io/file data-dir "tx-log")}}
   :crux/document-store {:kv-store
                         {:crux/module `crux.rocksdb/->kv-store
                          :sync?       true
                          :db-dir      (io/file data-dir "doc-store")}}
   :crux/index-store    {:kv-store {:crux/module `crux.rocksdb/->kv-store
                                    :sync?       true
                                    :db-dir      (io/file data-dir "indexes")}}})

(defn crux-in-memory-config
  [config]
  {})

(def crux-data-dir "crux-store")

(defn crux-config* [config]
  (let [env (or (:env config) :dev)
        config
            (get
              {:dev  (crux-rocks-config crux-data-dir config)
               :prod (crux-pg-config crux-data-dir config)
               :test (crux-in-memory-config config)}
              env)]
    (when-not config (fu/throw "Unknown env in config: " (pr-str env)))
    config))

(defn crux-config [config]
  (let [c (crux-config* config)]
    (log/info "Using crux config: ")
    (log/info (with-out-str (pprint c)))
    c))

(defn start-node
  "Convenience to run from tests."
  [config]
  (crux/start-node (crux-config config)))

(defstate crux-node
  :start (start-node config)
  :stop (.close crux-node))
