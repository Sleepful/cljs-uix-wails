(ns app.core
  (:require
   [cljs.spec.alpha :as s]
   [uix.core :as uix :refer [defui $]]
   [uix.dom]
   [uix.re-frame :as uix.rf]
   [app.subs]
   [app.reframe.handlers]
   [app.reframe.fx]
   [app.reframe.db]
   [app.routes :refer [route-provider use-route]]
   [re-frame.core :as rf]
   ["../wailsjs/go/main/App.js" :refer [Greet SetTitle]]))

(defui header []
  ($ :header.app-header
     ($ :img {:src "https://raw.githubusercontent.com/pitch-io/uix/master/logo.png"
              :width 32})))

(defui footer []
  ($ :footer.app-footer
     ($ :small "made with "
        ($ :a {:href "https://github.com/pitch-io/uix"}
           "UIx"))))

(defui text-field [{:keys [on-add-todo]}]
  (let [[value set-value!] (uix/use-state "")]
    ($ :input.text-input
       {:value value
        :placeholder "Add a new todo and hit Enter to save"
        :on-change (fn [^js e]
                     (set-value! (.. e -target -value)))
        :on-key-down (fn [^js e]
                       (when (= "Enter" (.-key e))
                         (set-value! "")
                         (on-add-todo {:text value :status :unresolved})))})))

(defui editable-text [{:keys [text text-style on-done-editing]}]
  (let [[editing? set-editing!] (uix/use-state false)
        [editing-value set-editing-value!] (uix/use-state "")]
    (if editing?
      ($ :input.todo-item-text-field
         {:value editing-value
          :auto-focus true
          :on-change (fn [^js e]
                       (set-editing-value! (.. e -target -value)))
          :on-key-down (fn [^js e]
                         (when (= "Enter" (.-key e))
                           (set-editing-value! "")
                           (set-editing! false)
                           (on-done-editing editing-value)))})
      ($ :span.todo-item-text
         {:style text-style
          :on-click (fn [_]
                      (set-editing! true)
                      (set-editing-value! text))}
         text))))

(s/def :todo/text string?)
(s/def :todo/status #{:unresolved :resolved})

(s/def :todo/item
  (s/keys :req-un [:todo/text :todo/status]))

(defui todo-item
  [{:keys [created-at text status on-remove-todo on-set-todo-text] :as props}]
  {:pre [(s/valid? :todo/item props)]}
  ($ :.todo-item
     {:key created-at}
     ($ :input.todo-item-control
        {:type :checkbox
         :checked (= status :resolved)
         :on-change #(rf/dispatch [:todo/toggle-status created-at])})
     ($ editable-text
        {:text text
         :text-style {:text-decoration (when (= :resolved status) :line-through)}
         :on-done-editing #(on-set-todo-text created-at %)})
     ($ :button.todo-item-delete-button
        {:on-click #(on-remove-todo created-at)}
        "×")))

(defui app []
  (let [todos (uix.rf/use-subscribe [:app/todos])
        [greet set-greet] (uix/use-state "Hello...")]
    (uix/use-effect (fn [] (.then (Greet "User") (fn [greet] (set-greet greet)))) [])
    ($ :div {:style {:display "flex" :class "space-x-2"}}
       ($ :div
          ($ :div greet)
          ($ :label
             {:for "test"}
             "This changes the window title with the Wails Go method:"
             ($ :input
                {:id "test",
                 :type "text",
                 :placeholder "type here",
                 :class "input"
                 :on-change (fn [^js e]
                             (SetTitle (.. e -target -value)))}))) 
          
       ($ :div {:style {:display "flex" :flex-direction "column"}}
          ($ :.app
             ($ header)
             ($ text-field {:on-add-todo #(rf/dispatch [:todo/add %])})
             (for [[created-at todo] todos]
               ($ todo-item
                  (assoc todo :created-at created-at
                         :key created-at
                         :on-remove-todo #(rf/dispatch [:todo/remove %])
                         :on-set-todo-text #(rf/dispatch [:todo/set-text %1 %2]))))
             ($ footer))
          ; NOTE: links need to use `#/` as their prefix for Wails
          ($ :a {:style {:padding "12px"} :href "#/about"} "About link")))))

(defui app-with-router []
  (let [{:keys [view]} (use-route)]
    ($ view)))

(defui about []
  ($ :div.prose
     {:style {:background "white"}}
     ($ :h1 "about page")
     ; NOTE: links need to use `#/` as their prefix for Wails
     ($ :a {:href "#/"} "Home page")))

(def routes
  [["/" {:view app}] ; empty page load
   ["/#/" {:view app}] ; for links that return to home
   ["/#/about" {:view about}]])

(def error-boundary
  (uix.core/create-error-boundary
   {:derive-error-state (fn [error]
                          {:error error})
    :did-catch          (fn [error info]
                          #_(js/console.log "Component did catch" error)
                          info)}
   (fn [[state set-state!] {:keys [children]}]
     (if-some [error (:error state)]
       ($ :<>
          ($ :p.warning "There was an error rendering!")
          ($ :pre (pr-str error)))
       children))))

(defui root-component []
  ($ error-boundary
     ($ route-provider {:routes routes}
        ($ app-with-router))))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn render []
  (rf/dispatch-sync [:app/init-db app.reframe.db/default-db])
  (uix.dom/render-root
   ($ uix/strict-mode
      ($ root-component))
   root))

(defn ^:export init []
  (render))
