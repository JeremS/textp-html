(ns fr.jeremyschoffen.textp.html.alpha.compiler-test
  (:require
    [clojure.test :refer [deftest is]]
    [fr.jeremyschoffen.textp.html.alpha.tags :as tags]
    [fr.jeremyschoffen.textp.html.alpha.compiler :as compiler]))


(def example [(tags/div
                {:tag :tag-args-clj
                 :content [:class "blue"]}
                {:tag :tag-args-txt
                 :content ["Some text " (tags/a {:tag :tag-args-clj :content [:href "www.home.com"]}
                                                {:tag :tag-args-txt :content ["home"]})]})
              (tags/br)
              (tags/div
                {:tag :tag-args-clj
                 :content [:class "blue"]}
                {:tag :tag-args-txt
                 :content ["Some text " (tags/a {:tag :tag-args-clj :content [:href "www.home.com"]}
                                                {:tag :tag-args-txt :content ["home"]})]})])

(deftest simple
  (is (= (compiler/doc->html example)
         "<div class=\"blue\">Some text <a href=\"www.home.com\">home</a></div><br /><div class=\"blue\">Some text <a href=\"www.home.com\">home</a></div>")))