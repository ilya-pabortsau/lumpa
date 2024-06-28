(ns screen
  (:require [key :as k])
  (:import (com.googlecode.lanterna TerminalPosition TextCharacter)
           (com.googlecode.lanterna.screen Screen$RefreshType TerminalScreen)
           (com.googlecode.lanterna.terminal DefaultTerminalFactory))
  (:gen-class))

(defn create-screen ^TerminalScreen []
  (.createScreen (new DefaultTerminalFactory)))

(defn get-size
  [^TerminalScreen screen]
  (let [size (.getTerminalSize screen)
        cols (.getColumns size)
        rows (.getRows size)]
    [cols rows]))

(defn set-cursor
  ([^TerminalScreen screen [x y]]
   (set-cursor screen x y))
  ([^TerminalScreen screen x y]
   (.setCursorPosition screen (new TerminalPosition x y))))

(defn put-char
  ([^TerminalScreen scr ^Character c [x y]]
   (put-char scr c x y)
   (scr))
  ([^TerminalScreen scr ^Character c x y]
   (mapv #(.setCharacter scr %1 y %2)
         (iterate inc x)
         (TextCharacter/fromCharacter c))))

(defn put-string
  ([^TerminalScreen scr ^String s [x y]]
   (put-string scr s x y))
  ([^TerminalScreen scr ^String s x y]
   (mapv #(.setCharacter scr %1 y %2)
         (iterate inc x)
         (TextCharacter/fromString s))))

(defn read-key
  ([^TerminalScreen scr]
   (-> scr
       .readInput
       k/to-keyword)))

(defn redraw [^TerminalScreen scr]
  (.refresh scr Screen$RefreshType/DELTA))

