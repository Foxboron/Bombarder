(ns bombarder.network
  (:gen-class)
  (:require [clojure.java.io :as io])
  (:import [java.net Socket]))

(defrecord Client [host port in out])

(defn connect [host port]
  (let [s (Socket. host port)
        in (io/reader s)
        out (io/writer s)]
      (Client. host port in out))) 


(defprotocol ClientHandler
  (write [client msg] "writes to the server")
  (read-from [client] "reads from the server"))


(extend-protocol ClientHandler
  Client
  (write [client msg]
    (binding [*out* (:out client)]
      (println (str msg "\n"))))
  (read-from [client]
    (.readLine (:in client))))
