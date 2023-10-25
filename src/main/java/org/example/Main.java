package org.example;

import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
public class Main {
    public static void main( String[] args ) {
        selectQuery();
    }

    private static void selectQuery() {
        // Replace the placeholder with your MongoDB deployment's connection string
        String uri = "mongodb://someuser:somepass@192.168.1.129:27017/?authMechanism=SCRAM-SHA-1";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = database.getCollection("sample");
            Document doc = collection.find(eq("name", "Java Hut 1")).first();
            if (doc != null) {
                System.out.println(doc.toJson());
            } else {
                System.out.println("No matching documents found.");
            }
        }
    }
}