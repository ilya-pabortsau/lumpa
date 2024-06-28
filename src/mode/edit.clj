(ns mode.edit
  (:require [buffer :as b]
            [editor :as e]
            [window :as w]))

(defn insert-char [state ch]
  (let [buf (e/cur-buff state)
        win (e/cur-win state)
        cursor (w/get-cursor win)
        updated-buf (b/insert-char buf ch cursor)
        updated-win (w/move-cursor-right win updated-buf)]
    (-> state
        (e/swap-buffer updated-buf)
        (e/swap-win updated-win))))

(defn move-cursor [state move-cursor-fn]
  (let [buf (e/cur-buff state)]
    (e/update-current-window state #(move-cursor-fn % buf))))

(defn handle-enter [state]
  (let [buf (e/cur-buff state)
        win (e/cur-win state)
        [x y] (w/get-cursor win)
        updated-buf (b/split-line buf x y)
        updated-win (w/set-cursor win [0 (inc y)])]
    (prn updated-buf)
    (-> state
        (e/swap-buffer updated-buf)
        (e/swap-win updated-win))))

(defn- backspace-0 [win buf y]
  (when (> y 0)
    (let [prev-line (b/get-line buf (dec y))]
      [(w/set-cursor win [(count prev-line) (dec y)])
       (b/concat-lines buf (dec y) y)])))

(defn- backspace-n [win buf x y]
  [(w/set-cursor win [(dec x) y])
   (b/remove-char buf (dec x) y)])

(defn backspace [state]
  (let [buf (e/cur-buff state)
        win (e/cur-win state)
        [x y] (w/get-cursor win)
        [updated-win updated-buf] (if (= x 0)
                                    (backspace-0 win buf y)
                                    (backspace-n win buf x y))]
    (-> state
        (e/swap-buffer updated-buf)
        (e/swap-win updated-win))))

(defn delete-last [buf y]
  (prn "delete last")
  (when (< y (-> buf b/length dec))
    (b/concat-lines buf y (inc y))))

(defn delete [state]
  (let [buf (e/cur-buff state)
        [x y] (w/get-cursor (e/cur-win state))
        line (b/get-line buf y)]
    (if (= x (count line))
      (e/update-current-buffer state #(delete-last % y))
      (e/update-current-buffer state #(b/remove-char % x y)))))


(defn handle-key [key state]
  (cond
    (= key :escape) (assoc state :mode :normal)
    (char? key) (insert-char state key)
    (= key :left) (move-cursor state w/move-cursor-left)
    (= key :right) (move-cursor state w/move-cursor-right)
    (= key :up) (move-cursor state w/move-cursor-up)
    (= key :down) (move-cursor state w/move-cursor-down)
    (= key :enter) (handle-enter state)
    (= key :backspace) (backspace state)
    (= key :delete) (delete state)
    :else state))
