(ns oneword.core
  (:require
    [cljs.core.match :refer-macros [match]]
    [mount.core :as mount :include-macros true]
    [oak.component :as oak]
    [oak.render :as render]
    [rococo.core :as rococo :include-macros true]
    [rococo.reset :as css-reset]
    [oak.dom :as d]
    [rococo.units :as u]
    [garden.types]
    [oak.schema :as os]
    [schema.core :as s]
    [garden.color :as color]))

; -----------------------------------------------------------------------------
; Styles

(def bg-color "#fafafa")
(def hilite-color "#3fc")
(def text-color "#333")
(def faded-text-color "#ddd")

(def CoreSS
  [[:html {:background-color bg-color}]])

(def MasterSS
  [css-reset/MeyerReset2-0
   CoreSS])

; -----------------------------------------------------------------------------
; Components

(declare BorderRiseKf)
(rococo/defkeyframes BorderRiseKf
  [:from
   {:border-bottom [[:thin :solid :gray]]}]
  [:to
   {:border-bottom [[(u/px 3) :solid hilite-color]]}])

(declare WordCollectorC)
(rococo/defclass WordCollectorC
  {:&
   {:width      (u/px 500)
    :height     (u/em 2)
    :color      text-color
    :border     {:top    :none
                 :left   :none
                 :right  :none
                 :bottom [[:thin :solid text-color]]}
    :box-sizing :content-box
    :text-align :center
    :font       {:size   (u/em 2)
                 :family "Lato"
                 :weight 200}}

   :&::-webkit-input-placeholder
   {:color faded-text-color}

   :&::-moz-placeholder
   {:color faded-text-color}

   :&:-ms-input-placeholder
   {:color faded-text-color}

   :&:focus
   {:outline       :none
    :border-bottom [[(u/px 3) :solid hilite-color]]
    :animation     {:name            BorderRiseKf
                    :duration        (u/ms 120)
                    :timing-function :ease-in}}})

(def WordCollector
  (oak/make
    :name "WordCollector"
    :action (os/cmd :new-word s/Str)
    :view
    (fn WordCollector-view [_ submit]
      (d/uinput {:className   WordCollectorC
                 :placeholder "One word at a time..."
                 :onKeyPress  (fn [ev]
                                (when (or
                                        (= " " (.-key ev))
                                        (= "Enter" (.-key ev)))
                                  (.preventDefault ev)
                                  (let [target (.-target ev)
                                        value (.-value target)]
                                    (set! (.-value target) "")
                                    (submit [:new-word value]))))
                 :autoFocus   true}))))

(declare WordGrafC)
(rococo/defclass WordGrafC
  {:&                {:color    (color/lighten text-color 60)
                      :margin   [[0 :auto]]
                      :padding  [[(u/px 5) 0]]
                      :overflow :hidden
                      :width    (u/px 740)
                      :height   (u/px 200)
                      :display  :block
                      :font     {:size   (u/em 1.5)
                                 :family "'Lato'"
                                 :weight 200}}
   :span             {:display :block
                      :margin  [[0 (u/px -2)]]
                      :padding (u/px 5)}
   :span:first-child {:font-weight 400
                      :color       (color/lighten text-color 30)
                      :display     :inline-block
                      :background  (color/lighten hilite-color 30)}})

(def WordsViewer
  (oak/make
    :name "WordsViewer"
    :model [s/Str]
    :view
    (fn [{words :model} _]
      (let [fst (first words)]
        (d/p {:className WordGrafC} (map #(d/span {} %) (reverse words)))))))

; -----------------------------------------------------------------------------
; App Component

(declare AppC)
(rococo/defclass AppC
  {:&          {:margin     [[(u/px 25) :auto]]
                :text-align :center
                :width      (u/px 960)}
   :.collector {:display         :flex
                :justify-content :center
                :padding         (u/px 20)
                :text-align      :center}})

(def App
  (oak/make
    :name "App"
    :action (oak/action WordCollector)
    :step (fn App-step [action model]
            (match action
              [:new-word new-word] (conj model new-word)))
    :model [s/Str]
    :view
    (fn App-view [{words :model} submit]
      (d/section {:className AppC}
        (d/div {:className :collector}
          (WordCollector nil submit))
        (d/div {:className :viewer}
          (WordsViewer words submit))))))

; -----------------------------------------------------------------------------
; States

(declare styles)
(mount/defstate styles
  :start (rococo/inject-styles
           (rococo/total-css
             :pretty-print? true
             :base-styles MasterSS))
  :stop (.removeChild (.-head js/document) @styles))

(declare app)
(mount/defstate app
  :start
  (render/render
    App
    :initial-model ["Your" "story" "begins" "..."]
    :on-action (fn on-action [target action] (println target "-->" action))
    :target (.getElementById js/document "app"))

  :stop
  ((:stop! @app)))

; -----------------------------------------------------------------------------
; RTS

(enable-console-print!)

(defn ^:export start [] (mount/start))
(defn ^:export stop [] (mount/stop))
(defn ^:export main [] (start))




