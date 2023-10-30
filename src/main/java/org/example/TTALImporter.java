package org.example;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class TTALImporter {

    @Autowired
    private MongoTemplate mongo;

    public int insertDocument(Collection<Document> documents) {
        Collection<Document> inserts = mongo.insert(documents, "ttal");
        return inserts.size();
    }

    public List<Document> fetchDocumentsForRequestReference(String requestReference, int limit, long skip) {
        if(requestReference == null) {
            return List.of();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("requestReference").is(requestReference));
        //use query projection to return only the TTAL document and exclude the _id field
        query.fields().include("document").exclude("_id");

        //set the result limit
        query.limit(limit);

        //set the offset
        query.skip(skip);
        List<Document> documents = mongo.find(query, Document.class, "ttal");
        return documents;
    }

    public List<Document> fetchDocumentsForMovieLanguageCodeAndType(long movieId, String elementType, String languageCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("movieId").is(movieId));
        query.addCriteria(Criteria.where("type").is(elementType));
        query.addCriteria(Criteria.where("lang").is(languageCode));
        List<Document> documents = mongo.find(query, Document.class, "ttal");
        return documents;
    }

    public List<Document> doTextSearch(long movieId, String term) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage()
                .matchingAny(term);

        Query query = TextQuery.queryText(criteria).sortByScore().addCriteria(Criteria.where("movieId").is(movieId));
        List<Document> documents = mongo.find(query, Document.class, "ttal");
        return documents;
    }
}
