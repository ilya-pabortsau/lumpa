(ns tab
  (:require [window :as w]))

(defn tab [id]
  {:id             id
   :size           [50 40]
   :current-window nil
   :windows        {}})

(defn window [id position]
  {:id       id
   :position position})

(defn render [tab state]
  (let [win-id (:current-window tab)
        win (get (:windows state) win-id)]
    (w/render win state)))

(defn assoc-win [tab win]
  (let [win-id (:id win)]
    (assoc-in tab [:windows win-id] (window win-id [0 0]))))

(defn switch-to-window [tab win-id]
  (when (contains? (:windows tab) win-id)
    (assoc tab :current-window win-id)))

(comment (defn vertical-split [editor-state current-window-id new-buffer-id new-x new-width]
           (let [current-tab (nth (:tab-pages editor-state) (dec (:current-tab)))
                 windows (:windows current-tab)
                 current-window (get-current-window editor-state)
                 adjacent-window (find-left-window windows current-window)]
             (if adjacent-window
               (let [new-id (inc (count windows))]
                 (assoc editor-state :windows
                                     (conj (update windows
                                                   (position current-window)
                                                   #(assoc % :width (/ new-x 2)))
                                           (create-window new-id new-buffer-id (/ new-x 2) (:y current-window) (/ new-width 2) (:height current-window) "vertical" current-window-id))
                                     :current-window new-id))
               (do
                 (println "No window to split")
                 editor-state)))))
