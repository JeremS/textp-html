(ns textp.html.alpha.tags
  (:require
    [net.cgrand.macrovich :as macro]
    [textp.lib.alpha.core :as lib])
  #?(:cljs (:require-macros [textp.html.alpha.tags])))


;; list of tags inspired by
;; https://github.com/omcljs/om/blob/master/src/main/om/dom.cljc#L9
;; renames :
;; - var -> html-var
;; - map -> html-map
;; - meta -> html-meta
;; - time -> html-time

(def tags
  '[a abbr address area article aside audio
    b base bdi bdo big blockquote body br button
    canvas caption cite code col colgroup
    data datalist dd del details dfn dialog div dl dt
    em embed
    fieldset figcaption figure footer form
    h1 h2 h3 h4 h5 h6
    head header hr html
    i iframe img ins
    kbd keygen
    label legend li link
    main mark menu menuitem meter
    nav noscript

    object ol optgroup output
    p param picture pre progress
    q
    rp rt ruby
    s samp script section small source span strong style sub summary sup
    table tbody td tfoot th thead title tr track
    u ul
    video wbr

    ;; svg
    circle clipPath
    ellipse
    g
    line
    mask
    path pattern polyline
    rect
    svg
    text
    defs
    linearGradient
    polygon
    radialGradient
    stop
    tspan

    input textarea option select])

(macro/deftime
  (defmacro define-tags []
    `(do ~@(for [t tags]
             `(lib/def-xml-tag ~t)))))

(textp.html.alpha.tags/define-tags)

;; tag renames to avoid override of clojure functions:
;; - var -> html-var
;; - map -> html-map
;; - meta -> html-meta
;; - time -> html-time
(lib/def-xml-tag html-var :var)
(lib/def-xml-tag html-map :map)
(lib/def-xml-tag html-meta :meta)
(lib/def-xml-tag html-time :time)


(defn html5-dtd [& args]
  {:type :dtd, :data ["html" nil nil]})