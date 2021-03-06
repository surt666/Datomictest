(defproject datomictest "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.slf4j/slf4j-simple "1.6.1"]
                 [log4j "1.2.16" :exclusions [javax.mail/mail
                                             javax.jms/jms
                                             com.sun.jdmk/jmxtools
                                             com.sun.jmx/jmxri]]
                 [com.datomic/datomic-pro "0.8.3435" :exclusions [org.slf4j/slf4j-nop
                                                                   org.slf4j/slf4j-log4j12]]]
  :plugins [[lein-swank "1.4.4"]])