(ns app.reframe.db)

(def default-db
  {:todos (sorted-map-by >)})
