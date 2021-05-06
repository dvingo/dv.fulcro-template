(ns {{namespace}}.node-server
  (:require
    ["http" :as http]
    ["react" :as react]
    [cognitect.transit :as ct]
    [clojure.pprint :refer [pprint]]
    [clojure.string :as str]
    [com.fulcrologic.fulcro.networking.http-remote :as fnet]
    [com.fulcrologic.fulcro.algorithms.transit :as t]
    [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]
    [com.fulcrologic.fulcro.algorithms.indexing :as indexing]
    [com.fulcrologic.fulcro.algorithms.server-render :as ssr]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [*app* defsc]]
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.ui-state-machines :as uism]
    [edn-query-language.core :as eql]
    [httpurr.client.node :as node]
    [promesa.core :as p]
    [reitit.core :as r]
    [dv.fulcro-reitit :as fr]
    [reagent.core :as re]
    [reagent.dom.server :as rdom]
    [{{namespace}}.client.ui.root :as root]
    [taoensso.timbre :as log]))

(enable-console-print!)

(def api-endpoint "http://127.0.0.1:8085/api")

(defn wrap-accept-transit
  "For servers that do content negotiation, tell them we want transit+json"
  [handler]
  (fn [req]
    (handler (assoc-in req [:headers "Accept"] "application/transit+json"))))

(defn my-logger
  ([] (my-logger identity))
  ([handler]
   (fn [req-or-resp]
     (log/info "Logging request:")
     (pprint req-or-resp)
     (handler req-or-resp))))

(defn resp-logger
  ([] (my-logger identity))
  ([handler]
   (fn [req-or-resp]
     (log/info "Logging response")
     (let [out (handler req-or-resp)]
      (pprint out)
       out))))

(defn read-transit
  ([] (read-transit identity))
  ([handler]
   (let [t-reader (t/reader)]
     (fn [resp]
       (let [new-body (ct/read t-reader (:body resp))
             resp     (assoc resp :body new-body)]
         (handler resp))))))

(def resp-middleware
  (->
    (read-transit)
    (resp-logger)))

(def req-middleware
  (->
    (my-logger)
    (fnet/wrap-fulcro-request)
    (wrap-accept-transit)))

(defn post! [url req]
  (p/then
    (node/post url (req-middleware req))
    resp-middleware))

(defn call-api
  [headers query]
  (let [start-inst (js/Date.)]
    (p/then (post! api-endpoint {:headers headers :body query})
      (fn [response]
        (log/info "got response:")
        (log/info (str "[" (- (js/Date.) start-inst) "ms]") "elapsed:")
        (:body response)))))

(defn init-app [root-component]
  (let [app (app/fulcro-app {:render-root! rdom/render-to-string
                             :render-middleware (fn [this render] (re/as-element (render)))})]
    (app/set-root! app root-component {:initialize-state? true})
    (dr/initialize! app)
    ;; add any user state machines begin! here.
    (app/mount! app root-component {})
    app))

(defn data-tree [app root]
  (let [state     (app/current-state app)
        query     (comp/get-query root state)
        data-tree (fdn/db->tree query state state)]
    data-tree))

(defn render-app-to-str [app cb]
  (js/setTimeout
    #(binding [*app* app]
       (cb (rdom/render-to-string
             ((comp/factory root/Root) (data-tree app root/Root)))))))

(defn req-handler [req res]
  (log/info "\n\n--------------------------------------------------------------------------------")
  (println "url: " (.-url req))
  (js/console.log "Req keys: " (clj->js (sort (js->clj (js-keys req)))))
  (js/console.log "headers: " (.-headers req))
  (js/console.log "cookie : " (.. req -headers -cookie))

  (let [url  (.-url req)
        cookie (.. req -headers -cookie)
        ;; todo put your start data query here something like .. (c/get-query root/Root {})
        query [{:all-people '[*]}]
        app (init-app root/Root)
        router (fr/router-state app :reitit-router)
        routes (fr/router-state app :routes-by-name)
        m    (r/match-by-path router url)]
    (p/then
      (call-api {:cookie cookie} query)
      (fn [resp]
        (log/info "MATCH: " m)
        (if (some? m)
          (let [route-segment (-> m :data :name routes :segment)]
            (log/info "got a route: " route-segment)
            (dr/change-route! app route-segment)
            (render-app-to-str app #(.end res %)))
          (do
            (log/info "No route matched, rendering main")
            (dr/change-route! app ["main"])
            (render-app-to-str app #(.end res %))))))))

(defonce server-ref (volatile! nil))

(defn main [& args]
  (log/info "starting server ")
  (let [server (http/createServer #(req-handler %1 %2))]
    (.listen server 3020
      (fn [err]
        (if err
          (log/info "server failed to start")
          (log/info "server running on port 3020"))))
    (vreset! server-ref server)))

(defn start []
  (log/info "start called")
  (main))

(defn stop
  [done]
  (log/info "stop called")
  (when-some [srv @server-ref]
    (.close srv
      (fn [err]
        (log/info "stop completed " err)
        (done)))))

(log/info "__filename: " js/__filename)
