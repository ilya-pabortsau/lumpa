(ns file
  (:require [clojure.java.io :as io])
  (:import (java.io Writer)))


(defn write-file [path content]
  (io/make-parents path)
  (spit path content))

(defn read-lines [path]
  (if (.exists (io/file path))
    (with-open [r (io/reader path)]
      (vec (line-seq r)))
    []))

(defn write-lines [path lines]
  (io/make-parents path)
  (with-open [^Writer w (io/writer path)]
    (doseq [^String line lines]
      (.write w line)
      (.append w "\n"))))

(defn file-name [path]
  (.getName (io/file path)))
