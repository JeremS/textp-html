(ns fr.jeremyschoffen.textp.alpha.html.compiler-test
  (:require
    #?(:clj [clojure.test :refer [deftest is]]
       :cljs [cljs.test :refer-macros [deftest is]])
    [fr.jeremyschoffen.textp.alpha.lib.compilation :as compilation]
    [fr.jeremyschoffen.textp.alpha.html.tags :as tags]
    [fr.jeremyschoffen.textp.alpha.html.compiler :as compiler :include-macros true]))


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
  (is (= (compiler/doc->str example)
         "<div class=\"blue\">Some text <a href=\"www.home.com\">home</a></div><br /><div class=\"blue\">Some text <a href=\"www.home.com\">home</a></div>")))



(defmethod compiler/emit-tag! [::md :h2] [node]
  (compilation/emit! (str "## " (apply str (:content node)))))

(compiler/with-implementation ::md
  (println (compiler/doc->str {:tag      :div
                               :content [{:tag :h2
                                          :content ["An important tile"]}]})))


(def example2 (compiler/with-implementation ::md
                (compiler/doc->str {:tag      :div
                                    :content [{:tag :h2
                                               :content ["An important tile"]}]})))

(deftest alternate-implementation
  (is (= example2 "<div>## An important tile</div>")))
