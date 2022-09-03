(ns {{namespace}}.user.resolvers
  (:require
    [com.wsscode.pathom.connect :as pc]
    [dv.xtdb-util :as xu]
    [xtdb.api :as xt]
    [space.matterandvoid.pathom-sieppari :as interceptors]
    [{{namespace}}.user.db :as user.db]
    [{{namespace}}.user.model :as user]
    [{{namespace}}.server.queries :as queries]
    [taoensso.timbre :as log]
    [sieppari.context :as c]))

(defn hydrate-tasks-for-user [xtdb-node current-user]
  (let [tasks (queries/user->tasks (xt/db xtdb-node) current-user)]
    {:current-user (assoc current-user ::user/tasks tasks)}))

(defn current-user-response-fn
  [{:keys [current-user xtdb-node]}]
  (hydrate-tasks-for-user xtdb-node current-user))

(def current-user-response-interceptor (interceptors/response-interceptor current-user-response-fn))

(def check-logged-in-interceptor
  (interceptors/terminate-if #(when (nil? (:current-user %))
                                {:server/message "You must be logged in" :server/error? true})))

;; todo - you're likely going to want to use transform
;; becuase you can then have transforms/wrappers that both add interceptors as well as update the output vector
;; for example to handle errors
(def current-user-resolver
  {::pc/sym     `current-user-resolver
   ::pc/output  [{:current-user user/pathom-output} :server/message :server/error?]
   ::pc/resolve (interceptors/interceptors->resolve-fn [check-logged-in-interceptor
                                                        current-user-response-interceptor])})

(pc/defresolver all-users-resolver [{:keys [xtdb-node]} _]
  {::pc/output [{:all-users [::user/id]}]}
  {:all-users
   (mapv
     (comp #(user.db/fetch-user xtdb-node %) ::user/id)
     (xu/select xtdb-node [::user/id]))})

(def resolvers [current-user-resolver all-users-resolver])
