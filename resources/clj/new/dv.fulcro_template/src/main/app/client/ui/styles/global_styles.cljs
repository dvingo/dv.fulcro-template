(ns {{namespace}}.client.ui.styles.global-styles)

(defn page-styles [theme]
  {":root"                {:box-sizing "border-box"}
   "*, ::after, ::before" {:box-sizing "inherit"}
   :body
   ;; use important here to override semantic ui styles
                          {:background-color (str (:bg theme) "!important")
                           :background-image (str (:bg-image theme) "!important")
                           :color            (str (:fg theme) "!important")
                           :font-size        "1.2rem"
                           :line-height      1.3
                           :font-family      "helvetica, sans-serif"}})

(defn semantic-styles [{:keys [fg container-bg]}]
  {".ui.segment.ui.segment"
   {:color            fg
    :background-color container-bg}

   ".ui.header.ui.header,
    .ui.form.ui.form .field>label,
    .ui.secondary.menu .item"
   {:color fg}})

(defn global-styles
  [theme]
  (merge
    (page-styles theme)
    (semantic-styles theme)))
