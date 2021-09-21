(ns {{namespace}}.client.prn-debug
  (:require [clojure.pprint :as pprint]))
;;
;;
;; See shadow-cljs.edn configuration for ns-aliases used during release builds to not have cljs-pprint included in the bundle.
;;
;;

(defn ^:export pprint-str [v] (with-out-str (pprint/pprint v)))
(defn ^:export pprint [v] (pprint/pprint v))

;; To use from within the browser dev tools console
(set! js/pprint_str pprint-str)
(set! js/pprint pprint)

