(ns grierson.router
  (:require
   [grierson.recipe.routes :as recipe]
   [muuntaja.core :as m]
   [reitit.coercion.spec :as coercion-spec]
   [reitit.dev.pretty :as pretty]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.spec :as rs]
   [reitit.swagger :as swagger]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.swagger-ui :as swagger-ui]))

(def swagger-docs
  ["/swagger.json"
   {:get
    {:no-doc true
     :swagger {:basePath "/"
               :info {:title "Cheffy API Reference"
                      :description "The Cheffy API is organized around REST. Returns JSON, Transit (msgpack, json), or EDN  encoded responses."
                      :version "1.0.0"}}
     :handler (swagger/create-swagger-handler)}}])

(def router-config
  {:validate rs/validate
   :exception pretty/exception
   :data {:coercion coercion-spec/coercion
          :muuntaja m/instance
          :middleware [swagger/swagger-feature
                       parameters/parameters-middleware
                       muuntaja/format-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-response-middleware]}})

(defn app
  [_]
  (ring/ring-handler
   (ring/router
    [swagger-docs
     ["/v1"
      recipe/routes]]
    router-config)
   (ring/routes
    (swagger-ui/create-swagger-ui-handler {:path "/"}))))

