(ns fr.jeremyschoffen.textp.html.alpha.tags-test
  (:require
    [clojure.test :refer [deftest is are]]
    [fr.jeremyschoffen.textp.html.alpha.tags :as tags]))

(deftest basic
  (are [x y] (= x y)
    (tags/div)
    {:tag :div, :attrs {}, :content []}

    (tags/div {:tag :tag-args-clj
               :content [:a 1]}
              {:tag :tag-args-txt
               :content ["text" "text"]})

    {:tag :div, :attrs {:a 1}, :content ["text" "text"]}))


