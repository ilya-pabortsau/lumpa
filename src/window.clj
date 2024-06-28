(ns window
  (:require [buffer :as b]))

(defn window [id]
  {:id             id
   :size           [50 40]
   ;; buffer-view ; should be a ref or id
   :current-buffer nil
   ;; buffer-views
   :buffers        {}})

(defn buffer-view [buf]
  ;; buffer reference or id
  {:buffer     (:id buf)
   ;; first [column row] to display
   :scroll     [0 0]
   :cursor     [0 0]
   ;; to support moving by shorter lines remaining cursor position
   :col-memory nil})

(defn assoc-buf [win buf]
  (let [buf-id (:id buf)
        buf-view (buffer-view buf)]
    (assoc-in win [:buffers buf-id] buf-view)))

(defn resize
  ([window size]
   (assoc window :size size))
  ([window x y]
   (resize window [x y])))

(defn buf-view [win buf]
  (get-in win [:buffers (:id buf)]))


;;; Cursor logic

(defn- calc-coord-scroll-pos
  [x scroll-x win-size]
  (cond
    (>= x (+ scroll-x win-size)) (inc (- x win-size))
    (< x scroll-x) x
    :else scroll-x))

(defn- calc-scroll-pos
  [buf-view [x y] [cols rows]]
  (let [[scroll-x scroll-y] (:scroll buf-view)]
    [(calc-coord-scroll-pos x scroll-x cols)
     (calc-coord-scroll-pos y scroll-y rows)]))

(defn- set-buf-cursor
  [buf-view cursor win-size]
  (let [scroll (calc-scroll-pos buf-view cursor win-size)]
    (-> buf-view
        (assoc :cursor cursor)
        (assoc :scroll scroll))))

(defn get-cursor [win]
  (let [buf-id (:current-buffer win)]
    (-> (:buffers win)
        (get buf-id)
        :cursor)))

(defn set-cursor [win cursor]
  (let [buf-id (:current-buffer win)]
    (assoc-in win [:buffers buf-id :cursor] cursor)))

(defn- move-buf-cursor-left [buf-view win-size]
  (let [[x y] (:cursor buf-view)]
    (if (> x 0)
      (-> buf-view
          (set-buf-cursor [(dec x) y] win-size)
          (dissoc :col-memory))
      buf-view)))

(defn- move-buf-cursor-right [buf-view win-size buf]
  (let [[x y] (:cursor buf-view)]
    (if (< x (b/line-size buf y))
      (-> buf-view
          (set-buf-cursor [(inc x) y] win-size)
          (dissoc :col-memory))
      buf-view)))

(defn- move-buf-cursor-up [buf-view win-size buf]
  (let [[x y] (:cursor buf-view)]
    (if (> y 0)
      (let [col (or (:col-memory buf-view) x)
            prev-line-size (b/line-size buf (dec y))]
        (-> buf-view
            (assoc :col-memory col)
            (set-buf-cursor [(min col prev-line-size) (dec y)] win-size)))
      buf-view)))

(defn- move-buf-cursor-down [buf-view win-size buf]
  (let [[x y] (:cursor buf-view)]
    (if (< y (-> buf b/length dec))
      (let [col (or (:col-memory buf-view) x)
            next-line-size (b/line-size buf (inc y))]
        (-> buf-view
            (assoc :col-memory col)
            (set-buf-cursor [(min col next-line-size) (inc y)] win-size)))
      buf-view)))

(defn move-cursor-left [win buf]
  (let [buf-id (:id buf)
        buf-view (get (:buffers win) buf-id)
        updated-buf-view (move-buf-cursor-left buf-view (:size win))]
    (assoc-in win [:buffers buf-id] updated-buf-view)))

(defn move-cursor-right [win buf]
  (let [buf-id (:id buf)
        buf-view (get (:buffers win) buf-id)
        updated-buf-view (move-buf-cursor-right buf-view (:size win) buf)]
    (assoc-in win [:buffers buf-id] updated-buf-view)))

(defn move-cursor-up [win buf]
  (let [buf-id (:id buf)
        buf-view (get (:buffers win) buf-id)
        updated-buf-view (move-buf-cursor-up buf-view (:size win) buf)]
    (assoc-in win [:buffers buf-id] updated-buf-view)))

(defn move-cursor-down [win buf]
  (let [buf-id (:id buf)
        buf-view (get (:buffers win) buf-id)
        updated-buf-view (move-buf-cursor-down buf-view (:size win) buf)]
    (assoc-in win [:buffers buf-id] updated-buf-view)))


;;; Other

(defn switch-to-buffer [win buf-id]
  (prn (:buffers win))
  (when
    (contains? (:buffers win) buf-id)
    (assoc win :current-buffer buf-id)))

(defn cursor-render [buf-view]
  (let [[x y] (:cursor buf-view)
        [s-x s-y] (:scroll buf-view)]
    [(- x s-x) (- y s-y)]))

(defn render-buf [win buf-view buf]
  {:lines  (b/slice buf (:scroll buf-view) (:size win))
   :cursor (cursor-render buf-view)})

(defn render [win state]
  (let [cur-buf-id (:current-buffer win)
        buf-view (get (:buffers win) cur-buf-id)
        buf (get (:buffers state) cur-buf-id)]
    (render-buf win buf-view buf)))
