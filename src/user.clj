(ns user
  (:require [clojure.core.async :as a]
            [editor :as e]
            [mode.edit :as ed]
            [mode.normal :as n])
  (:gen-class))

(defn handle-key
  ([state key]
   (cond
     (= (:mode state) :normal) (n/handle-key key state)
     (= (:mode state) :edit) (ed/handle-key key state)))
  ([key]
   (prn (str key))
   (e/print-in-status-buffer (str key))
   (swap! e/editor-state handle-key key)))


(defn read-loop []
  (while (:running @e/editor-state)
    (try
      (let [key (e/read-key)]
        (handle-key key)
        (e/render!))
      (catch Exception e
        (e/print-in-status-buffer (str "caught exception: " (.getMessage e)))
        (e/render!)))))


(defn -main [& args]
  (e/init-state)
  (e/start)
  (read-loop))

(comment
  (a/thread (-main))
  )
