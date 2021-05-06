(ns {{namespace}}.client.malli-registry
  (:require
    [malli.core :as m]
    [malli.registry :as mr]))

(defonce registry*
  (atom {:string (m/-string-schema)
         :uuid   (m/-uuid-schema)
         :maybe  (m/-maybe-schema)
         :map    (m/-map-schema)}))

(defn register! [type schema]
  (swap! registry* assoc type schema))
