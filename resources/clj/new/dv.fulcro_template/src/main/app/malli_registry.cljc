(ns {{namespace}}.malli-registry
  (:require
    [malli.core :as m]
    #?(:clj [dv.xtdb-util :as xu])
    [malli.registry :as mr]))

(defonce registry*
  (atom
    (merge
      (m/base-schemas)
      (m/type-schemas)
      (select-keys (m/sequence-schemas) [:cat])
      {:xt/id (:uuid (m/type-schemas))}
      #?(:clj {:xtdb-node (m/-simple-schema {:type :xtdb-node :pred xu/xtdb-node?})})
      ; add more schemas here
      )))

(defn register!
  "With one argument takes a map of schemas to merge into the registry,
   or with two arguments: one key -> schema pair to assoc into the registry.
   Returns nil."
  ([m]
   (swap! registry* merge m) nil)

  ([type schema]
   (swap! registry* assoc type schema) nil))

(mr/set-default-registry! (mr/mutable-registry registry*))
