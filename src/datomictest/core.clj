(ns datomictest.core
  (:use [datomic.api :only [q db] :as d])
  (:import [java.util Date]))

(def schema [{:db/id #db/id[:db.part/db]
              :db/ident :abonnement
              :db.install/_partition :db.part/db}

             {:db/id #db/id[:db.part/db]
              :db/ident :produkt
              :db.install/_partition :db.part/db}

             {:db/id #db/id[:db.part/db]
              :db/ident :adresse
              :db.install/_partition :db.part/db}
             
             {:db/id #db/id[:db.part/db]
              :db/ident :adresse/amsid
              :db/unique :db.unique/identity
              :db/valueType :db.type/long
              :db/noHistory true
              :db/cardinality :db.cardinality/one
              :db.install/_attribute :db.part/db}
             
             {:db/id #db/id[:db.part/db]
              :db/ident :adresse/vejnavn
              :db/valueType :db.type/string
              :db/fulltext true
              :db/noHistory true
              :db/cardinality :db.cardinality/one              
              :db.install/_attribute :db.part/db}            

             {:db/id #db/id[:db.part/db]
              :db/ident :abon/varenr
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one              
              :db.install/_attribute :db.part/db}            

             {:db/id #db/id[:db.part/db]
              :db/ident :abon/amsid
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one              
              :db.install/_attribute :db.part/db}

             {:db/id #db/id[:db.part/db]
              :db/ident :abon/status
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one              
              :db.install/_attribute :db.part/db}

             {:db/id #db/id[:db.part/db]
              :db/ident :abon/oprettelsesdato
              :db/valueType :db.type/instant
              :db/index true
              :db/cardinality :db.cardinality/one              
              :db.install/_attribute :db.part/db}

              {:db/id #db/id[:db.part/db]
              :db/ident :abon/varenrl
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one              
              :db.install/_attribute :db.part/db}            

             {:db/id #db/id[:db.part/db]
              :db/ident :abon/amsidl
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one              
              :db.install/_attribute :db.part/db}             
             
             ;; abon/status enum values
             [:db/add #db/id[:db.part/user] :db/ident :abon.status/afventer]
             [:db/add #db/id[:db.part/user] :db/ident :abon.status/kunsignal]
             [:db/add #db/id[:db.part/user] :db/ident :abon.status/kunplan]
             [:db/add #db/id[:db.part/user] :db/ident :abon.status/lukket]
             [:db/add #db/id[:db.part/user] :db/ident :abon.status/aktiv]

             {:db/id #db/id[:db.part/db]
              :db/ident :produkt/id
              :db/unique :db.unique/identity
              :db/valueType :db.type/long
              :db/noHistory true
              :db/cardinality :db.cardinality/one              
              :db.install/_attribute :db.part/db}

             {:db/id #db/id[:db.part/db]
              :db/ident :produkt/navn
              :db/valueType :db.type/string
              :db/noHistory true
              :db/cardinality :db.cardinality/one              
              :db.install/_attribute :db.part/db}

             {:db/id #db/id[:db.part/db]
              :db/ident :seq
              :db/unique :db.unique/identity
              :db/valueType :db.type/long
              :db/noHistory true
              :db/cardinality :db.cardinality/one              
              :db.install/_attribute :db.part/db}
             
             {:db/id #db/id [:db.part/db]
              :db/ident :inc
              :db/fn #db/fn {:lang "clojure"
                             :params [db id attr]
                             :code (let [e (d/entity db id)
                                         current (attr e 0) ]
                                     [[:db/add id attr (inc current)]])}}])

(def uri "datomic:free://localhost:4334/test")

(def uri-pro "datomic:ddb://datomic/test?aws_access_key_id=AKIAIWWUQM2X3I2Z3PIQ&aws_secret_key=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")

(d/create-database uri-pro)

(def conn (d/connect uri-pro))

(defn create-schema []  
  @(d/transact conn schema))

(defn insert [l]
  @(d/transact conn l))

(defn findq [query]
  (seq (d/q query (d/db conn))))

(defn get-entity [id]
  (:abon/juridisk (d/entity (d/db conn) id)))

(defn decorate 
  "Simple function to pull out all the attributes of an entity into a map"
  [id]
  (let [db (d/db conn)
        e (d/entity db id)]
    (assoc (select-keys e (keys e)) :id id)))

(defn decorate-results 
  "maps through a result set where each item is a single entity and decorates it"
  [r]
  (map #(decorate (first %)) r))

(defn insert-data []
  (insert [{:db/id (d/tempid :adresse -1) :adresse/amsid 123456 :adresse/vejnavn "Nørrebrogade"}
           {:db/id (d/tempid :adresse -2) :adresse/amsid 123457 :adresse/vejnavn "Birkegade"}
           {:db/id (d/tempid :adresse -3) :adresse/amsid 123458 :adresse/vejnavn "Brobergsgade"}
           {:db/id (d/tempid :produkt -1) :produkt/id 1101101 :produkt/navn "Mellempakke"}
           {:db/id (d/tempid :produkt -2) :produkt/id 1101201 :produkt/navn "Fuldpakke"}
           {:db/id (d/tempid :produkt -3) :produkt/id 1101001 :produkt/navn "Grundpakke"}
           {:db/id #db/id[:abonnement] :abon/amsid (d/tempid :adresse -1) :abon/varenr (d/tempid :produkt -1) :abon/status :abon.status/aktiv :abon/oprettelsesdato (Date.)}
           {:db/id #db/id[:abonnement] :abon/amsid (d/tempid :adresse -2) :abon/varenr (d/tempid :produkt -2) :abon/status :abon.status/aktiv :abon/oprettelsesdato (Date.)}
           {:db/id #db/id[:abonnement] :abon/amsid (d/tempid :adresse -3) :abon/varenr (d/tempid :produkt -3) :abon/status :abon.status/aktiv :abon/oprettelsesdato (Date.)}
           {:db/id #db/id[:abonnement] :abon/amsidl 123456 :abon/varenrl 1101101 :abon/status :abon.status/aktiv :abon/oprettelsesdato (Date.)}
           {:db/id #db/id[:abonnement] :abon/amsidl 123457 :abon/varenrl 1101201 :abon/status :abon.status/aktiv :abon/oprettelsesdato (Date.)}
           {:db/id #db/id[:abonnement] :abon/amsidl 123458 :abon/varenrl 1101001 :abon/status :abon.status/aktiv :abon/oprettelsesdato (Date.)}
           {:db/id (d/tempid :db.part/user) :seq 0}]))

;;(findq '[:find ?s ?p :where [?a :adresse/vejnavn "Birkegade"] [?a :adresse/amsid ?ams] [?s :abon/amsidl ?ams] [?s :abon/varenrl ?pid] [?pi :produkt/id ?pid] [?pi :produkt/navn ?p]]) uden refs

;;(findq '[:find ?s ?p :where [?a :adresse/vejnavn "Birkegade"] [?s :abon/amsid ?a] [?s :abon/varenr ?pid] [?pid :produkt/navn ?p]]) med refs

;;(insert [[:inc 17592186045437 :seq]]) kald inc trans func:

;;(decorate-results (findq '[:find ?s :where [?s :seq]]))


