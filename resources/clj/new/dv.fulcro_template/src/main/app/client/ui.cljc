(ns {{namespace}}.client.ui
  #?(:cljs (:require-macros [{{namespace}}.client.ui]))
   (:require [space.matterandvoid.subscriptions.fulcro :as component]))

#?(:clj
   (defmacro ^{:doc      "Define a stateful component. This macro emits a React UI class with a query,
   optional ident (if :ident is specified in options), optional initial state, optional css, lifecycle methods,
   and a render method. It can also cause the class to implement additional protocols that you specify. Destructuring is
   supported in the argument list.

   The template (data-only) versions do not have any arguments in scope
   The lambda versions have arguments in scope that make sense for those lambdas, as listed below:

   ```
   (defsc Component [this {:keys [db/id x] :as props} {:keys [onSelect] :as computed} extended-args]
     {
      ;; stateful component options
      ;; query template is literal. Use the lambda if you have ident-joins or unions.
      :query [:db/id :x] ; OR (fn [] [:db/id :x]) ; this in scope
      ;; ident template is table name and ID property name
      :ident [:table/by-id :id] ; OR (fn [] [:table/by-id id]) ; this and props in scope
      ;; initial-state template is magic..see dev guide. Lambda version is normal.
      :initial-state {:x :param/x} ; OR (fn [params] {:x (:x params)}) ; nothing is in scope
      ;; pre-merge, use a lamba to modify new merged data with component needs
      :pre-merge (fn [{:keys [data-tree current-normalized state-map query]}] (merge {:ui/default-value :start} data-tree))

      ; React Lifecycle Methods (for the default, class-based components)
      :initLocalState            (fn [this props] ...) ; CAN BE used to call things as you might in a constructor. Return value is initial state.
      :shouldComponentUpdate     (fn [this next-props next-state] ...)

      :componentDidUpdate        (fn [this prev-props prev-state snapshot] ...) ; snapshot is optional, and is 16+. Is context for 15
      :componentDidMount         (fn [this] ...)
      :componentWillUnmount      (fn [this] ...)

      ;; DEPRECATED IN REACT 16 (to be removed in 17):
      :componentWillReceiveProps        (fn [this next-props] ...)
      :componentWillUpdate              (fn [this next-props next-state] ...)
      :componentWillMount               (fn [this] ...)

      ;; Replacements for deprecated methods in React 16.3+
      :UNSAFE_componentWillReceiveProps (fn [this next-props] ...)
      :UNSAFE_componentWillUpdate       (fn [this next-props next-state] ...)
      :UNSAFE_componentWillMount        (fn [this] ...)

      ;; ADDED for React 16:
      :componentDidCatch         (fn [this error info] ...)
      :getSnapshotBeforeUpdate   (fn [this prevProps prevState] ...)

      ;; static.
      :getDerivedStateFromProps  (fn [props state] ...)

      ;; ADDED for React 16.6:
      ;; NOTE: The state returned from this function can either be:
      ;; a raw js map, where Fulcro's state is in a sub-key: `#js {\"fulcro$state\" {:fulcro :state}}`.
      ;; or a clj map. In either case this function will *overwrite* Fulcro's component-local state, which is
      ;; slighly different behavior than raw React (we have no `this`, so we cannot read Fulcro's state to merge it).
      :getDerivedStateFromError  (fn [error] ...)

      NOTE: shouldComponentUpdate should generally not be overridden other than to force it false so
      that other libraries can control the sub-dom. If you do want to implement it, then old props can
      be obtained from (prim/props this), and old state via (gobj/get (. this -state) \"fulcro$state\").

      ; React Hooks support
      ;; if true, creates a function-based instead of a class-based component, see the Developer's Guide for details
      :use-hooks? true

      ; BODY forms. May be omitted IFF there is an options map, in order to generate a component that is used only for queries/normalization.
      (dom/div #js {:onClick onSelect} x))
   ```

   NOTE: The options map is \"open\". That is: you can add whatever extra stuff you want to in order
   to co-locate data for component-related concerns. This is exactly what component-local css, the
   dynamic router, and form-state do.  The data that you add is available from `comp/component-options`
   on the component class and instances (i.e. `this`).

   See the Developer's Guide at book.fulcrologic.com for more details.
   "
               :arglists '([this dbprops computedprops]
                           [this dbprops computedprops extended-args])}
     defsc
     [& args]
     `(component/defsc ~@args)))
