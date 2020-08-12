(ns docs.core
  (:require
    [clojure.java.io :as io]
    [fr.jeremyschoffen.textp.alpha.doc.core :as doc]))



(def readme-src "docs/readme/README.md.tp")
(def readme-dest "README.md")



(defn make-readme! [maven-coords]
  (spit readme-dest (doc/make-document readme-src {:project/maven-coords maven-coords})))



(comment
  (-> readme-src
      doc/slurp-resource
      doc/read-document)



  (make-readme '{fr.jeremyschoffen/textp-doc {:mvn/version "0"}}))
