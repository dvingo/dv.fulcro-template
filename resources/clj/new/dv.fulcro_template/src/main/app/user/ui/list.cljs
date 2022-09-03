(ns {{namespace}}.user.ui.list
  (:require
    [{{namespace}}.client.ui :refer [defsc]]
    [com.fulcrologic.fulcro.components :as c]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [com.fulcrologic.fulcro.dom :as dom]
    [{{namespace}}.user.ui.detail :as user-detail]))

(defsc UserList [this {:keys [all-users]}]
  {:query             [{:all-users (c/get-query user-detail/User)}]
   :ident             (fn [_] [:component/id :users-list])
   :componentDidMount (fn [this] (df/load! this :all-users user-detail/User
                                   {:target [:component/id :users-list :all-users] :parallel true}))}
  (dom/div "Users List"
    (map user-detail/ui-user all-users)))

(def ui-user-list (c/factory UserList))


