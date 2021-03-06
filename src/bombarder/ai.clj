(ns bombarder.ai
  (:require [clojure.data.priority-map :refer [priority-map-by]]))


(def test-map {:type "status update", :players [{:y 1, :id 0, :x 1}], :bombs [{:x 0 :y 0}], :x 6, :y 5, :height 8, :width 8, 
               :map ["++++++++" 
                     "+..####+"
                     "+.#####+" 
                     "+######+" 
                     "+######+" 
                     "+#####.+" 
                     "+####..+" 
                     "++++++++"]})

(defn get-direction [cord pos]
  (let [c-x (first pos)
        c-y (second pos)]
    (take 2
        (drop 1
              (iterate
                (fn [[x y]] [(+ c-x x) (+ c-y y)]) cord)))))



;; Typing it out as i am a moron
;;
;; -1,-1    0,-1    1,-1
;; -1, 0    0, 0    1, 0
;; -1,1     0, 1    1, 1
;;

(def pos [[0 1] ;; Up
          [0 -1] ;; Down
          [-1 0] ;; Left
          [1 0] ;; Right
          ])


(defn bomb-path [player cords]
  "Will we get killed?"
  (let [bombs  (vector cords)
        field (:map player)
        d? (mapcat #(mapcat (fn [i] (get-direction % i)) pos) bombs)]
     d?))


(defn invalid-positions [this]
  (let [bombs (map #(vector (:x %) (:y %)) (:bombs this))
        players (map #(vector (:x %) (:y %)) (:players this))]
    (concat bombs players)))


(defn manhattan-distance [[x1 y1] [x2 y2]]
  (+ (Math/abs ^Integer (- x2 x1)) (Math/abs ^Integer (- y2 y1))))

(defn cost [curr start end]
  (let [g (manhattan-distance start curr)
        h (manhattan-distance curr end)
        f (+ g h)] 
    [f g h]))

(defn edges [map invalid width height closed [x y]]
  (for [tx (range (- x 1) (+ x 2)) 
        ty (range (- y 1) (+ y 2))
        :let [no-go {[(+ x 1) (+ y 1)]
                     [(+ x 1) (- y 1)] 
                     [(- x 1) (+ y 1)] 
                     [(- x 1) (- y 1)]}]
        :when (and (>= tx 0)
                   (>= ty 0)
                   (<= tx width)
                   (<= ty height)
                   (not= [x y] [tx ty])
                   (nil? (some  #{\+} (str (nth (nth map ty) tx))))
                   (nil? (some (set invalid) [tx ty]))
                   (not (contains? no-go [tx ty]))
                   (not (contains? closed [tx ty])))]
    (do (println invalid)
      [tx ty])))




(defn path [end parent closed]
  (reverse
   (loop [path [end parent]
          node (closed parent)]
     (if (nil? node)
       path
       (recur (conj path node) (closed node))))))




(defn pathfinding 
  ([mapmap start end]
     (let [[sx sy] start
           [ex ey] end
           map (:map mapmap)
           invalid-pos (invalid-positions mapmap)
           open (priority-map-by
                 (fn [x y]
                   (if (= x y)
                     0
                     (let [[f1 _ h1] x
                           [f2 _ h2] y]
                       (if (= f1 f2)
                         (if (< h1 h2) -1 1)
                         (if (< f1 f2) -1 1)))))
                 start (cost start start end))
           closed {}
           width (-> map first count dec)
           height (-> map count dec)]
       (when (and (not= (nth (nth map sy) sx) 1)
                  (not= (nth (nth map ey) ex) 1))
         (pathfinding map invalid-pos width height open closed start end))))

  ([map invalid width height open closed start end]
     (if-let [[coord [_ _ _ parent]] (peek open)]
       (if-not (= coord end)
         (let [closed (assoc closed coord parent)
               edges (edges map invalid width height closed coord)
               open (reduce
                     (fn [open edge]
                       (if (not (contains? open edge))
                         (assoc open edge (conj (cost edge start end) coord))
                         (let [[_ pg] (open edge)
                               [nf ng nh] (cost edge start end)]
                           (if (< ng pg)
                             (assoc open edge (conj [nf ng nh] coord))
                             open))))
                     (pop open) edges)]
           (recur map invalid width height open closed start end))
         (path end parent closed)))))

;(println (pathfinding test-map [1 1] [6 5]))
