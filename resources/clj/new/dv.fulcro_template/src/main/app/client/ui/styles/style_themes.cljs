(ns {{namespace}}.client.ui.styles.style-themes)

(def light-theme
  {::name :light-theme
   :bg "cornFlowerBlue"
   :bg-image "radial-gradient(hsl(200, 80%, 77%), hsl(210, 80%, 50%))"
   :fg "#1b1c1d"})

(def dark-theme
  {::name :dark-theme
   :bg "#1b1c1d"
   :bg-image "radial-gradient(hsl(200, 80%, 30%), hsl(210, 80%, 20%))"
   :fg "#eee"})

(def themes
  {:light-theme light-theme
   :dark-theme dark-theme})
