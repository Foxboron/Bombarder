(ns bombarder.core
  (:gen-class)
  (:require [bombarder.network :as n]
            [clojure.data.json :as json]))



(def moves ["LEFT" "RIGHT" "DOWN" "UP" "BOMB" "SAY LOL"])

(defn -main []
  (let [client (n/connect "localhost" 54321)
        disconnect? true]
    (n/write client "JSON")
    (n/write client "NAME Kjeppjagaren")
    (while (true? disconnect?) 
      (let [msg (n/read-from client)
            data (json/read-str msg :key-fn keyword)]
        (println data)
        (println (:type data))
        (n/write client (rand-nth moves))))))
