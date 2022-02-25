(ns clj.new.dv.fulcro-template
  (:require
    [clojure.pprint :refer [pprint]]
    [clj.new.templates :refer [renderer project-data name-to-path sanitize-ns ->files]]
    [clj.new.dv.fulcro-template.options.base :as base]
    [clj.new.dv.fulcro-template.options.kondo :as kondo]
    [clj.new.dv.fulcro-template.options.devcards :as devcards]
    [clj.new.dv.fulcro-template.options.workspaces :as workspaces]
    [clj.new.dv.fulcro-template.options.storybook :as storybook]
    [clj.new.dv.fulcro-template.options.test :as test]
    [clj.new.dv.fulcro-template.options.node-server :as node-server]
    [clj.new.dv.fulcro-template.options.server :as server]
    [clj.new.dv.fulcro-template.options.helpers :as helpers]
    [clojure.set :as set]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Files & Data for Template
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn app-files [data options]
  (concat
    (base/files data)
    (kondo/files data)
    ;(when (helpers/option? kondo/option options) (kondo/files data))
    (when (helpers/option? node-server/option options) (node-server/files data))
    (when (helpers/option? server/option options) (server/files data))
    (when (helpers/option? test/option options) (test/files data))
    (when (helpers/option? devcards/option options) (devcards/files data))
    (when (helpers/option? storybook/option options) (storybook/files data))
    (when (helpers/option? workspaces/option options) (workspaces/files data))))

(defn template-data [name options]
  (let [data
        (merge
          {:devcards?    (helpers/option? devcards/option options)
           :node-server? (helpers/option? node-server/option options)
           :server?      (helpers/option? server/option options)
           :test?        (helpers/option? test/option options)
           :workspaces?  (helpers/option? workspaces/option options)}
          (project-data name))]
    data))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Check Options
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def available-set
  #{ ;kondo/option
    helpers/all-option
    devcards/option
    storybook/option
    node-server/option
    server/option
    test/option
    workspaces/option})

(defn check-available [options]
  (let [options-set (into #{} options)
        abort?      (not (set/superset? available-set
                           options-set))]
    (when abort?
      (println "\nError: invalid profile(s)\n")
      (System/exit 1))))

(defn check-options
  "Check the user-provided options"
  [options]
  (doto options check-available))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn fulcro-template
  "Generate Scaffold for fulcro using tools.deps"
  [name & options]
  (let [data (template-data name options)]
    ;(println "DATA : ") (pprint data)
    (check-options options)
    (println "Generating fresh 'clj new' dv.fulcro-template project.")
    (apply ->files data (app-files data options))))
