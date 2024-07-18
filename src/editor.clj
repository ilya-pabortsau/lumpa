(ns editor
  (:require [buffer :as b]
            [screen :as s]
            [tab :as t]
            [window :as w])
  (:import (com.googlecode.lanterna.screen TerminalScreen)))


(def editor-state
  (atom
    {:mode        :normal
     :running     false
     :screen      nil
     :current-tab nil
     :tabs        {}
     :windows     {}
     :buffers     {}}))

(def buffer-id-counter (atom 2))
(defn next-buf-id [] (swap! buffer-id-counter inc))
(def window-id-counter (atom 0))
(defn next-win-id [] (swap! window-id-counter inc))
(def tab-id-counter (atom 0))
(defn next-tab-id [] (swap! tab-id-counter inc))

(def status-buffer-id 0)

(def command-buffer-id 1)

(defn init-state []
  (let [buf (b/buffer-from-file
              "/Users/ilyapabortsau/projects/lumpa/test_text.txt"
              :id (next-buf-id))
        win (-> (next-win-id)
                w/window
                (w/assoc-buf buf)
                (w/switch-to-buffer (:id buf)))
        tab (-> (next-tab-id)
                t/tab
                (t/assoc-win win)
                (t/switch-to-window (:id win)))]
    (swap! editor-state #(-> %
                             (assoc-in [:tabs (:id tab)] tab)
                             (assoc-in [:windows (:id win)] win)
                             (assoc-in [:buffers (:id buf)] buf)
                             (assoc :current-tab (:id tab))))))

(defn render-to
  [tab-render ^TerminalScreen screen]
  (let [lines (:lines tab-render)
        cursor (:cursor tab-render)]
    (mapv #(s/put-string screen %1 0 %2)
          lines
          (range 0 (count lines)))
    (s/set-cursor screen cursor)))

(defn tab-render [state]
  (let [cur-tab-id (:current-tab state)
        tab (get (:tabs state) cur-tab-id)]
    (t/render tab state)))

(defn render! []
  (let [state @editor-state
        screen (:screen state)
        tab-render (tab-render state)]
    (.clear screen)
    (render-to tab-render screen)
    (s/redraw screen)))


;;; Lifecycle

(defn start []
  (let [scr (s/create-screen)]
    (swap! editor-state assoc :screen scr)
    (.startScreen scr)
    (render!)
    (swap! editor-state assoc :running true)))

(defn stop []
  (let [scr (:screen @editor-state)]
    (.stopScreen scr)
    (swap! editor-state assoc :running false)))


(defn print-in-status-buffer [^String s]
  (swap! editor-state update-in [:buffers 0] b/replace-line s 0))


;;; Handle keys


(defn read-key []
  (s/read-key (:screen @editor-state)))


;;; State management

(defn cur-tab [state]
  (get (:tabs state) (:current-tab state)))

(defn cur-win-id [state]
  (:current-window (cur-tab state)))

(defn cur-win [state]
  (get (:windows state) (cur-win-id state)))

(defn cur-buf-id [state]
  (:current-buffer (cur-win state)))

(defn cur-buff [state]
  (get (:buffers state) (cur-buf-id state)))

(defn switch-to-tab [tab-id]
  (when (contains? (:tabs @editor-state) tab-id)
    (swap! editor-state assoc :current-tab tab-id)))


(defn swap-buffer [state buf]
  (assoc-in state [:buffers (:id buf)] buf))

(defn swap-win [state win]
  (assoc-in state [:windows (:id win)] win))


(defn update-current-buffer [state update-fn]
  (update-in state [:buffers (cur-buf-id state)] update-fn))

(defn update-current-window [state update-fn]
  (let [win-id (cur-win-id state)
        win (get (:windows state) win-id)
        updated-win (update-fn win)]
    (assoc-in state [:windows win-id] updated-win)))

(defn update-current-tab [state update-fn & params]
  (update-in state [:tabs (:current-tab state)] update-fn params))

