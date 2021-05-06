(ns {{namespace}}.task.ui.task-item
  (:require
    [clojure.spec.alpha :as s]
    [com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [com.fulcrologic.fulcro.components :as c :refer [defsc]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.ui-state-machines :as sm]
    [dv.cljs-emotion-reagent :refer [defstyled]]
    [dv.fulcro-util :as fu]
    [dv.fulcro-entity-state-machine :as fmachine]
    [{{namespace}}.task.data-model :as dm]
    [taoensso.timbre :as log]))

(defstyled flex :div
  {:display "flex"
   :align-items "center"
   "> * + *" {:margin-left "0.5em"}})

(defstyled bold :div
  {:font-weight "700"})

(defsc TaskItem
  [this {:task/keys [id description] :ui/keys [show-debug?]}]
  {:query [:task/id :task/description :ui/show-debug?]
   :ident :task/id}
  [:div.ui.segment
   [:h4.ui.header "Task Item"]
   [flex [bold "ID: "] [:span (pr-str id)]]
   [flex {:style {:margin-bottom "1em"}} [bold "Description: "] [:span (pr-str description)]]
   [:button.ui.button.mini {:on-click #(m/toggle!! this :ui/show-debug?)}
    (str (if show-debug? "Hide" "Show") " debug")]
   (fu/props-data-debug this show-debug?)])

(def ui-task-item (c/factory TaskItem {:keyfn :task/id}))

(defsc TaskList [this {:keys [all-tasks]}]
  {:initial-state {}
   :query         [{[:all-tasks '_] (c/get-query TaskItem)}]}
  [:div "This is the list of tasks"
   [:div.ui.divider]
   (map ui-task-item all-tasks)])

(def ui-task-list (c/factory TaskList))

(defn task-item-card
  [{:task/keys [id description]}]
  [:div.ui.card {:key id}
   [:div.content>div.ui.tiny.horizontal.list>div.item description]])

(defn empty-form [] (dm/make-task {:task/description ""}))

(defn task-valid [form field]
  (let [v (get form field)]
    (s/valid? field v)))

(def validator (fs/make-validator task-valid))

(defsc TaskItemReturn [this props]
  {:query [:server/message :server/error?
           :task/id
           :task/duration
           :task/scheduled-at
           :task/description]
   :ident (fn [_] [:component/id :task-item-return])})

(defsc TaskForm
  [this {:keys [server/message ui/machine-state ui/loading? ui/show-form-debug?] :as props}
   {:keys [cb-on-submit on-cancel]}]
  {:query             [:task/id :task/description fs/form-config-join
                       :ui/machine-state :ui/loading? :server/message
                       (sm/asm-ident ::form-machine)
                       :ui/show-form-debug?]
   :ident             :task/id
   :form-fields       #{:task/description}
   :initial-state     (fn [_] (fs/add-form-config
                                TaskForm (merge (empty-form)
                                           {:ui/show-form-debug? false})))
   :componentDidMount (fn [this] (fmachine/begin! this ::form-machine TaskItemReturn))}
  (let [{:keys [checked? disabled?]} (fu/validator-state this validator)]
    [:div
      (fu/notification {:ui/submit-state machine-state :ui/server-message message})
     (when goog.DEBUG
         (fu/ui-button #(m/toggle! this :ui/show-form-debug?) "Debug form"))
     (fu/form-debug validator this show-form-debug?)

     [:h3 nil "Enter a new task"]

     [:div.ui.form
        {:class (when checked? "error")
         :onChange  (fn [e] (sm/trigger! this ::form-machine :event/reset)
                            true)}
      [:div.field
       (fu/ui-textfield this "Task Description" :task/description props :tabIndex 0
         :autofocus? true)]

      [:div.ui.grid
       [:div.column.four.wide>button
        {:tabIndex 0
         :disabled disabled?
         :onClick
                   {{#server?}}
                   (fu/prevent-default
                     #(let [task (dm/fresh-task props)]
                        (fmachine/submit-entity! this
                          {:entity          task
                           :machine         ::form-machine
                           :creating?       true
                           :remote-mutation '{{namespace}}.task/create-task
                           :on-reset        cb-on-submit
                           :target          {:append [:all-tasks]}})))
                    {{/server?}}
                   {{^server?}}
                    #(log/info "Clicked save a task!")
                    {{/server?}}


         :class    (str "ui primary button" (when loading? " loading"))}
        "Enter"]

       (when on-cancel
         [:div.column.four.wide>button.ui.secondary.button.column
          {:on-click on-cancel} "Cancel"])]]]))

(def ui-task-form (c/factory TaskForm {:keyfn :task/id}))
