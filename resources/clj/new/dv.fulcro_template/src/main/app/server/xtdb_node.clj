(ns {{namespace}}.server.xtdb-node
  (:require
    [{{namespace}}.server.config :refer [config]]
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [xtdb.api :as xt]
    [xtdb.jdbc :as jdbc]
    [dv.fulcro-util-common :as fu]
    [mount.core :refer [defstate]]
    [taoensso.timbre :as log]))

(defn xtdb-postgres-config
  "Index store: rocksdb
   Doc store: Postgres
   Tx store: Postgres"
  [data-dir config]
  (let [config* (::config config)
        {:keys [db-spec ]} config*]
    {::jdbc/connection-pool {:dialect {:xtdb/module 'xtdb.jdbc.psql/->dialect} :db-spec db-spec}
     :xtdb/tx-log           {:xtdb/module `jdbc/->tx-log, :connection-pool ::jdbc/connection-pool}
     :xtdb/document-store   {:xtdb/module `jdbc/->document-store, :connection-pool ::jdbc/connection-pool}
     :xtdb/index-store      {:kv-store {:xtdb/module `rocks/->kv-store, :db-dir (io/file data-dir "indexes")}}
     :xtdb.metrics.csv/reporter {:output-file "xtdb-metrics.csv"}}))

(defn xtdb-rocks-config
  [data-dir _config]
  {:xtdb/tx-log         {:kv-store
                         {:xtdb/module 'xtdb.rocksdb/->kv-store
                          :sync?       true
                          :db-dir      (io/file data-dir "rocksdb-tx-log")}}
   :xtdb/document-store {:kv-store
                         {:xtdb/module 'xtdb.rocksdb/->kv-store
                          :sync?       true
                          :db-dir      (io/file data-dir "rocksdb-doc-store")}}
   :xtdb/index-store    {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                                    :sync?       true
                                    :db-dir      (io/file data-dir "rocksdb-indexes")}}})

(defn xtdb-lmdb-config
  [data-dir _config]
  {:xtdb/tx-log         {:kv-store
                         {:xtdb/module 'xtdb.lmdb/->kv-store
                          :sync?       true
                          :db-dir      (io/file data-dir "lmdb-tx-log")}}
   :xtdb/document-store {:kv-store
                         {:xtdb/module 'xtdb.lmdb/->kv-store
                          :sync?       true
                          :db-dir      (io/file data-dir "lmdb-doc-store")}}
   :xtdb/index-store    {:kv-store {:xtdb/module 'xtdb.lmdb/->kv-store
                                    :sync?       true
                                    :db-dir      (io/file data-dir "lmdb-indexes")}}})

(defn xtdb-in-memory-config [_config] {})

(def xtdb-data-dir "xtdb-store")

(defn xtdb-config* [{:keys [node-type] :as config}]
  (let [node-config
        (get
          {:rocks     (xtdb-rocks-config xtdb-data-dir config)
           :postgres  (xtdb-postgres-config xtdb-data-dir config)
           :lmdb      (xtdb-lmdb-config xtdb-data-dir config)
           :in-memory (xtdb-in-memory-config config)}
          node-type)]
    (when-not node-config (fu/throw "Unknown XTDB node type in config: " (pr-str node-type)))
    node-config))

(defn xtdb-config [node-config]
  (let [c (xtdb-config* node-config)]
    (log/info "Using XTDB config: ")
    (log/info (with-out-str (pprint c)))
    c))

(defn start-node
  "Convenience to run from tests."
  [config]
  (log/info "XTDB CONFIG: " config)
  (xt/start-node (xtdb-config config)))

(defstate xtdb-node
          :start (start-node (::config config))
          :stop (.close xtdb-node))
