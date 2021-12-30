(ns {{namespace}}.client.malli-registry
  (:require
    [malli.core :as m]))

(defonce registry*
  (atom
    (merge
      (m/base-schemas)
      (m/type-schemas)
      (select-keys (m/sequence-schemas) [:cat]))))

(defn register! [type schema]
  (swap! registry* assoc type schema))
