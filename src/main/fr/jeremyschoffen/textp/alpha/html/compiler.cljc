(ns fr.jeremyschoffen.textp.alpha.html.compiler
  (:refer-clojure :exclude [compile])
  (:require
    [net.cgrand.macrovich :as macro :include-macros true]
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

;;----------------------------------------------------------------------------------------------------------------------
;; low level api
;;----------------------------------------------------------------------------------------------------------------------
(declare compile!)
(declare compile-seq!)


(def self-closing-tags
  "Html tag names of tags that are self closing."
  #{:area :base :basefont :br :hr :input :img :link :meta})


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


;;----------------------------------------------------------------------------------------------------------------------
;; Normal tags
;;----------------------------------------------------------------------------------------------------------------------
(def ^:dynamic *implementation* ::html)


(defmulti emit-tag! (fn [node]
                      [*implementation* (:tag node)]))


(defmethod emit-tag! :default [t]
  (emit-tag*! t))


(defmethod emit-tag! [::html ::tags/un-escaped] [node]
  (emit-unescaped! node))


;;----------------------------------------------------------------------------------------------------------------------
;; Special tags
;;----------------------------------------------------------------------------------------------------------------------
(defmulti emit-special! (fn [node]
                          [*implementation* (:type node)]))


(defmethod emit-special! [::html :dtd] [x]
  (emit-dtd! x))


(defmethod emit-special! [::html :comment] [x]
  (emit-comment! x))


(defn special? [x]
  (and (map? x)
       (contains? x :type)))


(def tag? map?)


(defn compile-seq! [ss]
  (doseq [s ss]
    (compile! s)))

;;----------------------------------------------------------------------------------------------------------------------
;; High level
;;----------------------------------------------------------------------------------------------------------------------
(defn compile! [node]
  "Compile a document to a html string. Needs the dynamic variable
    [[fr.jeremyschoffen.textp.alpha.lib.compilation/*compilation-out*]] to be bound."
  (cond
    (special? node) (emit-special! node)
    (tag? node) (emit-tag! node)
    (sequential? node) (compile-seq! node)
    :else (emit! (xml-str node))))


(defn doc->str
  "Compile a document to s string."
  [x]
  (compile/text-environment
    (compile! x)))


(macro/deftime
  (defmacro with-implementation
    "Binds the dynamic var [[fr.jeremyschoffen.textp.alpha.html.compiler/*implementation*]]
    to `i`."
    [i & body]
    `(binding [*implementation* ~i]
       ~@body)))


(comment
  (println
    (doc->str [{:type  :dtd
                :data ["html" nil nil]}
               {:tag :img
                :attrs {:href "www.toto.com"}}
               {:tag ::tags/un-escaped
                :content ["&copy;"]}]))
  (println
    (doc->str [{:type  :dtd
                :data ["html" nil nil]}
               {:tag :img
                :attrs {:href "www.toto.com"}
                :content ["toto""titi"]}])))
