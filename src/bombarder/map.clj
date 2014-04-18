(ns bombarder.map
  (:require [bombarder.ai :as ai]))

(declare remove-unmoveables)

(def test-map
{:type "status update", 
 :players [{:id 0, :x 1, :y 1}], 
 :bombs [], 
 :x 1, 
 :y 2, 
 :height 8, 
 :width 8, 
 :map ["++++++++" 
       "+..####+" 
       "+.#####+" 
       "+######+" 
       "+######+" 
       "+#####.+" 
       "+####..+" 
       "++++++++"]})




(defrecord BombMap [])


(defprotocol MapHandler
  (player-pos [_] "Returns the player position")
  (on-map? [_ cords] "Gives back the item on the map")
  (get-best-path [_ cord] "Gets the best path to the given cords")
  (bombs? [_] "Do we need to get the fuck away?")
  (will-i-blow? [this cords])
  (blocked? [_ cord] "Checks if the given cord is blocked"))


(extend-protocol MapHandler
  BombMap
  (player-pos [this]
    [(:x this) (:y this)])

  (on-map? [this [x y]]
    (str (get (get (:map this) y) x)))

  (get-best-path [this cords]
    (ai/pathfinding cords))

  (bombs? [this]
    (if (empty? (:bombs this))
      true
      false))

  (will-i-blow? [this cords]
     (let [bomb-paths (remove-unmoveables this (ai/bomb-path this cords))]
       (if (nil? (some #{(player-pos this)} bomb-paths))
         false
         true))))


(defn defmap [aimap]
  (map->BombMap aimap))


(def testmap (defmap test-map))


(defn remove-unmoveables [m cords]
  "Removed objects we can't go through"
  (into [] 
        (for [i cords
              :let [obj (on-map? m i)]
              :when (= "." obj)]
          (do #_(println (= "." obj) "---" i)
              i))))


(defn safe-spots-old [this [bx by]]
  "assume we are placing a bomb" 
    (for [tx (range (- bx 3) (+ bx 3))
          ty (range (- by 3) (+ by 3))
          :when (and (true? (will-i-blow? this [tx ty]))
                     (<= ty (- (:height this) 1))
                     (<= tx (- (:width this) 1))
                     (>= tx 0)
                     (>= ty 0))]   
      [tx ty]))

(defn safe-spots [this [bx by]]
  (let [smap (for [tx (range (- bx 3) (+ bx 3))
                  ty (range (- by 3) (+ by 3))
                  :when (and
                             (<= ty (- (:height this) 1))
                             (<= tx (- (:width this) 1))
                             (>= tx 0)
                             (>= ty 0))] 
                 [tx ty])
        sclear (remove-unmoveables this smap)
        bpath (ai/bomb-path this [bx by])]
    (remove (set (conj bpath [bx by])) sclear)))

(def testmap (defmap test-map))

(println (safe-spots testmap [2 1]))

