(ns fr.jeremyschoffen.textp.alpha.html.tags-test
  (:require
    #?(:clj [clojure.test :refer [deftest is are]]
       :cljs [cljs.test :refer-macros [deftest is are]])
    [fr.jeremyschoffen.textp.alpha.html.tags :as tags]))

(deftest basic
  (are [x y] (= x y)
    (tags/div)
    {:tag :div, :attrs {}, :content []}

    (tags/div {:tag :tag-args-clj
               :content [:a 1]}
              {:tag :tag-args-txt
               :content ["text" "text"]})

    {:tag :div, :attrs {:a 1}, :content ["text" "text"]}))
