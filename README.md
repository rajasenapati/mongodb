//get mongo db connection
db.getMongo()

// use test database
use test;

//create a collection called ttal from scratch
db.getCollection("ttal").drop();
db.createCollection("ttal")

//create indexes
db.ttal.dropIndexes()
db.ttal.createIndex({"movieId": 1, "lang": 2, "type": 3, "recipe": 4  })
db.ttal.createIndex({"packageId": 1, "lang": 2, "type":3, "recipe": 4  })
db.ttal.createIndex({"requestReference": 1 })
db.ttal.createIndex({ "$**": "text" , "movieId": 1, "lang": 1, "packageId": 1, "recipe": 1 }, { language_override: 'dummyVal' } )
db.ttal.getIndexes()

//get a count of entries in the collection (accurate and approxmiate)
db.ttal.find({}).count();
db.ttal.estimatedDocumentCount();

//standard queries
//find any 10 entries
db.ttal.find({}).limit(10);

//find any 10 entries after skipping 20 entries
db.ttal.find({}).limit(10).skip(20);

//find an exact match for a given request reference. Since it is indexed, it is very fast.
db.ttal.find( { "requestReference": "dubtext:dubtext_script_authoring:b88dc990-0543-441c-9b31-54bf80f7ab9c"} );

//also search by meta header inside the document. It is slow (12 sec) since we did not create an index on it
db.ttal.find( { "document.meta.requestReference": "dubtext:dubtext_script_authoring:b88dc990-0543-441c-9b31-54bf80f7ab9c"} );

//demonstrate column projection
db.ttal.find( { "movieId": 81021127, "type": "DUB_SCRIPT", "lang": "vi", "packageId": 1761663}, { "document" : 1, "_id": 0 } );

//text based queries
//exact query match across all collection (10 sec)
db.ttal.find( { $text: { $search: "\"बोलते क्यों नहीं\"" } } );
//exact query match across a specific movieId (millisecs)
db.ttal.find( { $text: { $search: "\"बोलते क्यों नहीं\"" }, "movieId": 81229408 } );
//any phrase match
db.ttal.find( { $text: { $search: "बोलते क्यों नहीं" } } );

//text match within a specific movieId
db.ttal.find( { $text: { $search: "tham" }, "movieId": 81021127 } );
