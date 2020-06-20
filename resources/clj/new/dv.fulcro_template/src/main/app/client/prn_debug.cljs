(ns {{namespace}}.client.prn-debug
  (:require [clojure.pprint :as pprint]))
;; Hello.
;;
;;
;; I'm generated in the scripts/start_dev.sh script.
;;
;;
;;

(defn ^:export pprint-str [v] (with-out-str (pprint/pprint v)))
(defn ^:export pprint [v] (pprint/pprint v))

;; To use from within the browser dev tools console
(set! js/pprint_str pprint-str)
(set! js/pprint pprint)

