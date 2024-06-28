(ns mode.normal
  (:require [buffer :as b]
            [editor :as e]
            [window :as w]))

(def lead-key \ )
(def command-key \:)

;(defn start-command [state]
;  (let [min-buf ()]
;    (b/replace-line min-buf ":" 0)
;    (e/switch-to-buffer min-buf)))

(defn move-cursor [state move-cursor-fn]
  (let [buf (e/cur-buff state)]
    (e/update-current-window state #(move-cursor-fn % buf))))


(defn handle-key [key state]
  (cond
    (= key \q) (e/stop)
    (= key \s) (b/save-f (:current-buffer state))
    (= key \i) (assoc state :mode :edit)
    (= key :left) (move-cursor state w/move-cursor-left)
    (= key :right) (move-cursor state w/move-cursor-right)
    (= key :up) (move-cursor state w/move-cursor-up)
    (= key :down) (move-cursor state w/move-cursor-down)
    (= key \h) (move-cursor state w/move-cursor-left)
    (= key \l) (move-cursor state w/move-cursor-right)
    (= key \k) (move-cursor state w/move-cursor-up)
    (= key \j) (move-cursor state w/move-cursor-down)
    :else state
    ;(= key \:) (start-command state)
    ))
