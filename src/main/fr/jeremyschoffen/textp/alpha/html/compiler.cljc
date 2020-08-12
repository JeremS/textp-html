(ns fr.jeremyschoffen.textp.alpha.html.compiler
  (:refer-clojure :exclude [compile])
  (:require
    [fr.jeremyschoffen.textp.alpha.lib.compilation :refer [emit!] :as compile]
    [fr.jeremyschoffen.textp.alpha.html.tags :as tags]))

;; Generaly inspired by https://github.com/cgrand/enlive/blob/master/src/net/cgrand/enlive_html.clj

;;----------------------------------------------------------------------------------------------------------------------
;; arround https://github.com/cgrand/enlive/blob/master/src/net/cgrand/enlive_html.clj#L122
(defn xml-str
  "Like clojure.core/str but escapes < > and &."
  [x]
  (-> x str (.replace "&" "&amp;") (.replace "<" "&lt;") (.replace ">" "&gt;")))

(defn attr-str
  "Like clojure.core/str but escapes < > & and \"."
  [x]
  (-> x str (.replace "&" "&amp;") (.replace "<" "&lt;") (.replace ">" "&gt;") (.replace "\"" "&quot;")))
;;----------------------------------------------------------------------------------------------------------------------


;;----------------------------------------------------------------------------------------------------------------------
;; inspired by https://github.com/cgrand/enlive/blob/master/src/net/cgrand/enlive_html.clj#L175
(defn emit-comment!
  "Emit an html comment passed in map form."
  [node]
  (emit! "<!--" (str (:data node)) "-->"))

(defn emit-dtd!
  "Emit a dtd passed in map form."
  [{[name public-id system-id] :data}]
  (emit!
    (cond
       public-id
       (str "<!DOCTYPE " name " PUBLIC \"" public-id "\"\n    \"" system-id "\">\n")
       system-id
       (str "<!DOCTYPE " name " SYSTEM \"" system-id "\">\n")
       :else
       (str "<!DOCTYPE " name ">\n"))))
;;----------------------------------------------------------------------------------------------------------------------

(declare compile!)


(def self-closing-tags
  "Html tag names of tags that are self closing."
  #{:area :base :basefont :br :hr :input :img :link :meta})


(defn compile-seq! [ss]
  (doseq [s ss]
    (compile! s)))


(defn- named? [x]
  (or (symbol x) (keyword? x)))


(def ^:private destructure-named (juxt namespace name))


(defn- named->name-str [n]
  (let [[ns name] (destructure-named n)]
    (if ns
      (str ns ":" name)
      name)))


(defn name-str
  "Return a string representation of a name (tag or attribute) in html."
  [n]
  (cond
    (string? n) n
    (named? n) (named->name-str n)
    :else (throw (ex-info (str "Can't make an html name from: " n) {}))))


(defn- emit-attrs! [attrs]
  (doseq [[k v] attrs]
    (emit! \space (name-str k) \= \" (attr-str v) \")))


(defn- emit-content&close-tag! [tag content]
  (let [tag-name-str (name-str tag)]
    (if (seq content)
      (do (emit! ">")
          (compile-seq! content)
          (emit! "</"tag-name-str ">"))
      (if (contains? self-closing-tags tag)
        (emit! " />")
        (emit! "></" tag-name-str ">")))))


(defn emit-tag*! [{:keys [tag attrs content]}]
  (let [tag-name (name-str tag)]
    (emit! "<" tag-name)
    (emit-attrs! attrs)
    (emit-content&close-tag! tag content)))


(defn emit-unescaped! [{c :content}]
  (emit! (first c)))


(defmulti emit-tag! :tag)


(defmethod emit-tag! :default [t]
  (emit-tag*! t))


(defmethod emit-tag! ::tags/un-escaped [node]
  (emit-unescaped! node))


(defmulti emit-special! :type)


(defmethod emit-special! :dtd [x]
  (emit-dtd! x))


(defmethod emit-special! :comment [x]
  (emit-comment! x))


(defn special? [x]
  (and (map? x)
       (contains? x :type)))


(def tag? map?)


(defn compile! [node]
  (cond
    (special? node) (emit-special! node)
    (tag? node) (emit-tag! node)
    :else (emit! (xml-str node))))


(defn doc->html [x]
  (compile/text-environment
    (if (sequential? x)
      (compile-seq! x)
      (compile! x))))


(comment
  (println
    (doc->html [{:type :dtd
                 :data ["html" nil nil]}
                {:tag :img
                 :attrs {:href "www.toto.com"}}
                {:tag ::tags/un-escaped
                 :content ["&copy;"]}]))
  (println
    (doc->html [{:type :dtd
                 :data ["html" nil nil]}
                {:tag :img
                 :attrs {:href "www.toto.com"}
                 :content ["toto""titi"]}])))
