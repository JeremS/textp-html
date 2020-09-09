(ns fr.jeremyschoffen.textp.alpha.html.docs.core
  (:require
    [fr.jeremyschoffen.textp.alpha.doc.core :as doc]
    [fr.jeremyschoffen.mbt.alpha.utils :as u]))

(u/pseudo-nss
  project)


(def readme-src "fr/jeremyschoffen/textp/alpha/html/docs/readme/README.md.tp")

(defn make-readme! [{wd ::project/working-dir
                     maven-coords ::project/maven-coords
                     git-coords ::project/git-coords}]
  (spit (u/safer-path wd "README.md")
        (doc/make-document readme-src
                           {:project/maven-coords maven-coords
                            :project/git-coords git-coords})))


(comment
  (-> readme-src
      doc/slurp-resource
      doc/read-document))
