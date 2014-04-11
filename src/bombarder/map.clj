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
       "+####..+" 
       "++++++++"]})



(defrecord BombMap [])

(defprotocol MapHandler
  BombMap
  (player-pos [_] "Returns the player position")
  (get-best-path [_ cord] "Gets the best path to the given cords")
  (bombs? [_] "Do we need to get the fuck away?"))

(extend-protocol MapHandler
  BombMap
  (player-pos [this]
    [(:x this) (:y this)])

  (get-best-path [this]
    ["bomb" "bomb"])

  (bombs? [this]
    "THE FUCK IF I KNOW"))

(defn defmap [aimap]
  (map->BombMap aimap))

(def testmap (defmap test-map))

(get-best-path testmap)

