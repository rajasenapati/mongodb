db.getMongo()
db.ttal.dropIndexes()
db.ttal.createIndex({"meta.movieId": 1, "meta.lang": 2, "meta.type":3  })
db.ttal.createIndex({"meta.packageId": 1, "meta.lang": 2, "meta.type":3  })
db.ttal.createIndex({"meta.requestReference": 1 })
db.ttal.createIndex({ "$**": "text" , "meta.movieId": 1 }, { language_override: 'dummyVal' } )
db.ttal.getIndexes()

db.ttal.find( { "meta.movieId": 81316185, "meta.type": "DUB_SCRIPT", "meta.lang": "es"} );
db.ttal.find( { "meta.requestReference": "dubtext:dubtext_script_authoring:9d8b60ff-f359-4289-bd55-b03615c807d5"} );
db.ttal.find( { $text: { $search: "camelids" }, "meta.movieId": 81498468 } );