(ns {{namespace}}.user.ui.detail
  (:require
    [com.fulcrologic.fulcro.components :as c]
    [com.fulcrologic.fulcro.dom :as dom]
    [{{namespace}}.client.ui :refer [defsc]]
    [{{namespace}}.task.model :as task]
    [{{namespace}}.user.model :as user]))

(defsc User [this {::user/keys [id email] :keys [db/created-at]}]
  {:query (fn [] user/fulcro-query)
   :ident ::user/id}
  (dom/div nil
    (dom/h2 "user")
    (dom/div :.ui.grid
      (dom/div :.ui.five.wide.column (str "id: " id))
      (dom/div :.ui.five.wide.column (str "email: " email))
      (dom/div :.ui.five.wide.column (str "created: " created-at)))))

(def ui-user (c/factory User {:keyfn ::user/id}))
