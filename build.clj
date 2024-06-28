(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'my/lib1)
(def version "1.0.1")
(def class-dir "out/classes")
(def uber-file (format "out/%s-%s-standalone.jar" (name lib) version))

;; delay to defer side effects (artifact downloads)
(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_]
  (b/delete {:path "out"}))

(defn uber [_]
  (clean _)
  (b/copy-dir {:src-dirs   ["src" "resources"]
               :target-dir class-dir})
  (b/compile-clj {:basis     @basis
                  :src-dirs  ["src"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis     @basis
           :main      'user}))