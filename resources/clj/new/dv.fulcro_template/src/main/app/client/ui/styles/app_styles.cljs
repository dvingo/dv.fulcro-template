(ns {{namespace}}.client.ui.styles.app-styles
  (:require
    [{{namespace}}.client.prn-debug :refer [pprint-str]]
    [{{namespace}}.client.ui.styles.style-themes :as themes]
    [com.fulcrologic.fulcro.components :as c]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.mutations :as m]
    [dv.cljs-emotion-reagent :as em]
    [dv.fulcro-util-common :as fuc]
    [reagent.core :as re]
    [taoensso.timbre :as log]))

(defn style
  [path-or-kw theme]
  (let [v (get-in theme (cond-> path-or-kw (keyword? path-or-kw) vector))]
    (when-not v
      (throw
        (fuc/error "Unknown style property: " (pr-str path-or-kw)
          " - current theme is: " (pprint-str theme))))
    v))

(defn set-current-theme* [s theme] (assoc s :root/style-theme theme))

(m/defmutation set-style-theme
  [{:keys [theme]}]
  (action [{:keys [state]}]
    (swap! state
      (fn [s]
        (-> s (set-current-theme* theme))))))

(defn toggle-app-styles! [this theme]
  (let [theme-name (::themes/name theme)
        next-theme (if (= theme-name :dark-theme)
                     (:light-theme themes/themes)
                     (:dark-theme themes/themes))]
    (c/transact! this [(set-style-theme {:theme next-theme})])))
