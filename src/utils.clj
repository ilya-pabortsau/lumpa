(ns utils
  (:require [clojure.core.rrb-vector :as fv]))

(defn vec-sub
  ([v x y]
   (let [len (count v)]
     (fv/subvec v (min x len) (min y len))))
  ([v x]
   (let [len (count v)]
     (fv/subvec v (min x len) len))))

(defn vec-insert [v i e]
  (fv/catvec (conj (vec-sub v 0 i) e) (vec-sub v i)))

(defn vec-remove [v i]
  (fv/catvec (vec-sub v 0 i) (vec-sub v (inc i))))

(defn str-insert [^String s ch x]
  (-> (new StringBuilder s)
      (.insert x ch)
      .toString))

(defn str-replace [^String s ch x]
  (-> (new StringBuilder s)
      (.setCharAt x ch)
      .toString))

(defn str-delete [^String s x]
  (-> (new StringBuilder s)
      (.deleteCharAt x)
      .toString))

(defn str-subs
  ([^String s x y]
   (let [len (count s)]
     (subs s (min x len) (min y len))))
  ([^String s x]
   (let [len (count s)]
     (subs s (min x len) len))))

(defn str-split [^String s x]
  (let [a (str-subs s 0 x)
        b (str-subs s x)]
    [a b]))

