(ns {{namespace}}.client.prn-debug-noop)

;;
;; This file is used to remove the ns require on cljs.core.pprint.
;; At least for this application.
;;
;; See shadow-cljs.edn :release config for how this file is used.
;;

(defn pprint-str [_])
(defn pprint [_])
