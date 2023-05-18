(ns grierson.system
  (:require [donut.system :as ds]
            [grierson.router :as router]
            [aero.core :as aero]
            [ring.adapter.jetty :as rj]))

(defn env-config [& [profile]]
  (aero/read-config "config.edn" (when profile {:profile profile})))

(def base-system
  {::ds/defs
   {:env {}

    :components
    {}

    :http
    {:handler
     #::ds{:start  (fn [{{:keys []} ::ds/config}]
                     (router/app {}))
           :config {}}

     :server
     #::ds{:start (fn [{{:keys [handler options]} ::ds/config}]
                    (rj/run-jetty handler options))
           :stop  (fn [{:keys [::ds/instance]}]
                    (.stop instance))
           :config  {:handler (ds/local-ref [:handler])
                     :options {:port (ds/ref [:env :webserver :port])
                               :join? false}}}}}})

(defmethod ds/named-system ::base
  [_]
  base-system)

(defmethod ds/named-system ::production
  [_]
  (ds/system ::base {[:env] (env-config :production)}))

(defmethod ds/named-system ::test
  [_]
  (ds/system ::base {[:env] (env-config :test)
                     [:http :server] ::disabled}))

(comment
  (def system (ds/start ::production))
  (println (get-in system [::ds/instances :env]))
  (ds/stop system))
