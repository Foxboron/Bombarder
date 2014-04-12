(ns bombarder.ai)


(def test-map {:type "status update", :players [{:y 1, :id 0, :x 1}], :bombs [{:x 0 :y 0}], :x 6, :y 5, :height 8, :width 8, :map ["++++++++" "+..####+" "+.#####+" "+######+" "+######+" "+#####.+" "+#.##..+" "++++++++"]})


(defn pathfinding [cords]
  "Wat algo?"
  ["bomb" "bomb"])


(defn get-direction [cord pos]
  (let [c-x (first pos)
        c-y (second pos)]
    (take 2
        (drop 1
              (iterate
                (fn [[x y]] [(+' c-x x) (+' c-y y)]) cord)))))



;; Typing it out as i am a moron
;;
;;          0, 1
;; -1, 0    0, 0    1, 0
;;          0,-1
;;

(def pos [[0 1] ;; Up
          [0 -1] ;; Down
          [-1 0] ;; Left
          [1 0] ;; Right
          ])



(defn bomb-path [player]
  "Will we get killed?"
  (let [bombs (map #(vector (:x %) (:y %)) (:bombs player))
        field (:map player)]
    (mapcat #(mapcat (fn [i] (get-direction % i)) pos) bombs)))


