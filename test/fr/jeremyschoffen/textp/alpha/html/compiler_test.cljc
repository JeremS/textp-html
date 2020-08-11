(ns fr.jeremyschoffen.textp.alpha.html.compiler-test
  (:require
    #?(:clj [clojure.test :refer [deftest is]]
       :cljs [cljs.test :refer-macros [deftest is]])
    [fr.jeremyschoffen.textp.alpha.html.tags :as tags]
    [fr.jeremyschoffen.textp.alpha.html.compiler :as compiler]))


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

