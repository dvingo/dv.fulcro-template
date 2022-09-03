(ns {{namespace}}.user.model
  (:require
    [com.fulcrologic.fulcro.raw.components :as rc]
    [dv.fulcro-util-common :as fu]
    [malli.core :as m]
    [{{namespace}}.malli-registry :as reg]
    [{{namespace}}.task.model :as task]
    [space.matterandvoid.malli.transform.fulcro :as malli.xf.fulcro :refer [schema->fulcro-query]]
    [space.matterandvoid.malli.transform.pathom :as malli.xf.pathom :refer [schema->pathom-output]]
    [taoensso.timbre :as log]))

(def User
  [:map
   ::id
   ::password
   ::email
   ::tasks])

(reg/register! {::id       :uuid
                ::email    :string
                ::password [:string {::malli.xf.pathom/elide? true}]
                ::tasks    [:vector {::malli.xf.fulcro/component task/task-component} ::task/task]})

(comment (schema->pathom-output User))

(def pathom-output (schema->pathom-output User))
(def fulcro-query (schema->fulcro-query User))

(def validate (m/validator User))
(def explain (m/explainer User))

(defn validate! [u] (validate u))
(defn explain-invalid [u] (-> u (m/explainer User)))

(defn make-user [{::keys [id email password tasks]
                  :or    {id     (fu/uuid)
                          tasks []}}]
  {::id       id
   ::email    email
   ::password password
   ::tasks    tasks})

(defn fresh-user [args] (make-user (dissoc args ::id)))

(defn make-db-user [args]
  (let [{::keys [id] :as user} (make-user args)]
    (-> user
      (assoc :xt/id id)
      (update ::tasks (partial mapv ::task/id)))))

(def user-component (rc/nc fulcro-query {:componentName ::UserComponent}))

(defn add-task-id
  [user task]
  (update user ::tasks (fnil conj []) (::task/id task)))

(def current-user-join
  {[::current-user '_] (rc/get-query user-component)})

(defn ident
  ([] ::id)
  ([h] (fu/->ident ::id h))
  ([h prop] (conj (fu/->ident ::id h) prop)))

