(ns buffer.schema)

{:mode        :normal
 :running     false
 ;; TerminalScreen
 :screen      (.TerminalScreen)
 :current-tab 2
 :tabs        {2 {:id             2
                  :size           [50 40]
                  :current-window 1
                  :windows        {1 {:position [0 0]}}}}
 :windows     {1 {:id               1
                  :size             [50 40]
                  :current-buffer   10
                  ;; buffer-views
                  :buffers          {10 {:buffer     10
                                         ;; first [column row] to display
                                         :scroll     [0 0]
                                         :cursor     [0 0]
                                         ;; to support moving by shorter lines remaining cursor position
                                         :col-memory nil}}
                  :line-numbers     false
                  :rel-line-numbers false}}
 :buffers     {10 {:id       10
                   :name     "test-file"
                   :filename "test-file.clj"
                   :filepath "/Users/ilya/work/test-file.clj"
                   :lines    ["first line"
                              "second line"
                              "third line"]}}}

