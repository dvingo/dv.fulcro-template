(ns clj.new.dv.fulcro-template.options.base
  (:require [clj.new.dv.fulcro-template.options.helpers :as helpers]))

(defn files [data]
  (concat
    [;; Top level files
     [".gitignore" (helpers/render "gitignore" data)]
     ["deps.edn" (helpers/render "deps.edn" data)]
     ["package.json" (helpers/render "package.json" data)]
     ["bb.edn" (helpers/render "bb.edn" data)]
     ["README.md" (helpers/render "README.md" data)]
     ["shadow-cljs.edn" (helpers/render "shadow-cljs.edn" data)]
     [".nvmrc" (helpers/render ".nvmrc" data)]

     ;; Client
     ["src/main/{{nested-dirs}}/client/application.cljs" (helpers/render "src/main/app/client/application.cljs" data)]
     ["src/main/{{nested-dirs}}/client/client_entry.cljs" (helpers/render "src/main/app/client/client_entry.cljs" data)]
     ["src/main/{{nested-dirs}}/malli_registry.cljc" (helpers/render "src/main/app/malli_registry.cljc" data)]
     ["src/main/{{nested-dirs}}/client/prn_debug.cljs" (helpers/render "src/main/app/client/prn_debug.cljs" data)]
     ["src/main/{{nested-dirs}}/client/prn_debug_noop.cljs" (helpers/render "src/main/app/client/prn_debug_noop.cljs" data)]
     ["src/main/{{nested-dirs}}/client/development_preload.cljs" (helpers/render "src/main/app/client/development_preload.cljs" data)]
     ["src/main/{{nested-dirs}}/client/ui.cljc" (helpers/render "src/main/app/client/ui.cljc" data)]
     ["src/main/{{nested-dirs}}/client/ui/root.cljs" (helpers/render "src/main/app/client/ui/root.cljs" data)]
     ["src/main/{{nested-dirs}}/client/ui/styles/app_styles.cljs" (helpers/render "src/main/app/client/ui/styles/app_styles.cljs" data)]
     ["src/main/{{nested-dirs}}/client/ui/styles/global_styles.cljs" (helpers/render "src/main/app/client/ui/styles/global_styles.cljs" data)]
     ["src/main/{{nested-dirs}}/client/ui/styles/style_themes.cljs" (helpers/render "src/main/app/client/ui/styles/style_themes.cljs" data)]
     ["src/main/config/fe-config.edn" (helpers/render "src/main/config/fe-config.edn" data)]

     ;; User
     ["src/main/{{nested-dirs}}/user/model.cljc" (helpers/render "src/main/app/user/model.cljc" data)]
     ["src/main/{{nested-dirs}}/user/ui/detail.cljs" (helpers/render "src/main/app/user/ui/detail.cljs" data)]
     ["src/main/{{nested-dirs}}/user/ui/list.cljs" (helpers/render "src/main/app/user/ui/list.cljs" data)]

     ;; Task
     ["src/main/{{nested-dirs}}/task/model.cljc" (helpers/render "src/main/app/task/model.cljc" data)]
     ;["src/main/{{nested-dirs}}/task/mutations.cljs" (helpers/render "src/main/app/task/mutations.cljs" data)]
     ["src/main/{{nested-dirs}}/task/ui/task_item.cljs" (helpers/render "src/main/app/task/ui/task_item.cljs" data)]
     ["src/main/{{nested-dirs}}/task/ui/task_page.cljs" (helpers/render "src/main/app/task/ui/task_page.cljs" data)]

     ;; Serves fe-only app
     ["resources/public/index.html" (helpers/render "resources/public/index.html" data)]
     ["resources/public/favicon.ico" (helpers/render "resources/public/favicon.ico" data)]]))
