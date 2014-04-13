(ns bombarder.map
  (:require [bombarder.ai :as ai]))

(def test-map
{:type "status update", 
 :players [{:id 0, :x 1, :y 1}], 
 :bombs [], 
 :x 6, 
 :y 5, 
 :height 8, 
 :width 8, 
 :map ["++++++++" 
       "+..####+" 
       "+.#####+" 
       "+######+" 
       "+######+" 
       "+#####.+" 
       "+#.##..+" 
       "++++++++"]})




(defrecord BombMap [])


(defprotocol MapHandler
  (player-pos [_] "Returns the player position")
  (on-map? [_ cords] "Gives back the item on the map")
  (get-best-path [_ cord] "Gets the best path to the given cords")
  (bombs? [_] "Do we need to get the fuck away?")
  (blocked? [_ cord] "Checks if the given cord is blocked"))


(extend-protocol MapHandler
  BombMap
  (player-pos [this]
    [(:x this) (:y this)])

  (on-map? [this cords]
    (str (get (get (:map this) (second cords)) (first cords))))

  (get-best-path [this cords]
    (ai/pathfinding cords))

  (bombs? [this]
    (if (empty? (:bombs this))
      true
      false))

  (will-i-blow? [this]
     (let [bomb-paths (remove-unmoveables this (ai/bomb-path this))]
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
              :when (nil? (some #{\+ \#} obj))]
    i)))

