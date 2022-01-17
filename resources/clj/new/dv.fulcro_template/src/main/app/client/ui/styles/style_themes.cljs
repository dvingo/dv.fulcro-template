(ns {{namespace}}.client.ui.styles.style-themes
  (:require
    ["polished/lib/color/darken" :as darken]
    ["polished/lib/color/lighten" :as lighten]))

(def light-theme
  {::name        :light-theme
   :bg           "cornFlowerBlue"
   :bg-image     "radial-gradient(hsl(200, 80%, 77%), hsl(210, 80%, 50%))"
   :fg           "#1b1c1d"
   :container-bg "white"})

(def dark-theme
  (let [bg "#1b1c1d"]
    {::name        :dark-theme
     :bg           bg
     :bg-image     "radial-gradient(hsl(200, 80%, 30%), hsl(210, 80%, 20%))"
     :fg           "#eee"
     :container-bg (darken 0.1 "hsl(200, 80%, 30%)")}))

(def themes
  {:light-theme light-theme
   :dark-theme  dark-theme})
