(ns key
  (:import (com.googlecode.lanterna.input KeyStroke KeyType)))

(def key-codes
  {KeyType/Character  :char
   KeyType/Escape     :escape
   KeyType/Backspace  :backspace
   KeyType/ArrowLeft  :left
   KeyType/ArrowRight :right
   KeyType/ArrowUp    :up
   KeyType/ArrowDown  :down
   KeyType/Insert     :insert
   KeyType/Delete     :delete
   KeyType/Home       :home
   KeyType/End        :end
   KeyType/PageUp     :page-up
   KeyType/PageDown   :page-down
   KeyType/Tab        :tab
   KeyType/ReverseTab :reverse-tab
   KeyType/Enter      :enter})

(defn to-keyword [^KeyStroke ks]
  (let [type (get key-codes (.getKeyType ks))]
    (cond
      (= type :char) (.getCharacter ks)
      :else type)))
