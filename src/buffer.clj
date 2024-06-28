(ns buffer
  (:require [file :as f]
            [utils :as u]))

(defn buffer
  [lines & {:keys [id name filename filepath] :as options}]
  {:id       id
   :name     (or name filename "")
   :filename (or filename "")
   :filepath filepath
   :lines    lines})

(defn length [buf]
  (count (:lines buf)))

(defn insert-char
  ([buf ch x y]
   (update-in buf [:lines y] u/str-insert ch x))

  ([buf ch [x y]]
   (insert-char buf ch x y)))

(defn remove-char
  ([buf x y]
   (update-in buf [:lines y] u/str-delete x))

  ([buf [x y]]
   (remove-char buf x y)))

(defn get-line
  ([buf y]
   (nth (:lines buf) y)))

(defn insert-line
  ([buf s y]
   (update buf :lines u/vec-insert y s))

  ([buf y]
   (insert-line buf "" y)))

(defn replace-line [buf line y]
  (assoc-in buf [:lines y] line))

(defn remove-line [buf y]
  (update buf :lines u/vec-remove y))

(defn append-to-line [buf text y]
  (update-in buf [:lines y] str text))

(defn split-line [buf x y]
  (let [line (get-in buf [:lines y])
        [a b] (u/str-split line x)]
    (-> buf
        (replace-line a y)
        (insert-line b (inc y)))))

(defn concat-lines [buf first second]
  (let [second-txt (get-line buf second)]
    (-> buf
        (remove-line second)
        (append-to-line second-txt first))))

(defn line-size [buf y]
  (count (get-line buf y)))

(defn slice [buf [x y] [cols rows]]
  (let [lines (:lines buf)
        slice-lines (u/vec-sub lines y (+ y rows))]
    (vec (map #(u/str-subs % x (+ x cols)) slice-lines))))


;; File IO

(defn load-f [buf path]
  (merge buf {:lines    (f/read-lines path)
              :filename (f/file-name path)
              :filepath path}))

(defn save-f [buf]
  (when-some [path (:filepath buf)]
    (f/write-lines path (:lines buf))))

(defn buffer-from-file [path & {:keys [id name]}]
  (buffer (f/read-lines path)
          :id id
          :name name
          :filename (f/file-name path)
          :filepath path))
