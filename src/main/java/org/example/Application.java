package org.example;

import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class Application implements ApplicationRunner {

    @Autowired
    TTALImporter ttalImporter;

    @Autowired
    Environment environment;
    @Override
    public void run(ApplicationArguments args) throws Exception {
//        insertDocumentFromFiles();

//        db.ttal.find( { "meta.requestReference": "dubtext:dubtext_script_authoring:9d8b60ff-f359-4289-bd55-b03615c807d5"} );
//       List<Document> docs = ttalImporter.fetchDocumentsForRequestReference("dubtext:dubtext_script_authoring:9d8b60ff-f359-4289-bd55-b03615c807d5");
//       docs.forEach( doc -> System.out.println(doc.toJson()));

        //db.ttal.find( { "meta.movieId": 81316185, "meta.type": "DUB_SCRIPT", "meta.lang": "es"} );
//        List<Document> docs2 = ttalImporter.fetchDocumentsForMovieLanguageCodeAndType(81316185, "DUB_SCRIPT", "es");
//        docs2.forEach( doc -> System.out.println(doc.toJson()));

        //db.ttal.find( { $text: { $search: "camelids" }, "meta.movieId": 81498468 } );
        List<Document> docs3 = ttalImporter.doTextSearch(81498468, "camelids");
        docs3.forEach( doc -> System.out.println(doc.toJson()));
    }

    private void insertDocumentFromFiles() throws IOException {
        Collection<Document>  documents = fetchDocumentsFromFiles();
        ttalImporter.insertDocument(documents);
    }

    private static Collection<Document> fetchDocumentsFromFiles() throws IOException {
        Collection<Document> inserts = new ArrayList<>();
        for(int i=0; i< 41; i++) {
            String name = "/home/raja/Downloads/clqtt/clqtt/" + String.format("%03d.clqtt", i+1);
            Document doc = Document.parse(FileUtils.readFileToString(new File(name), StandardCharsets.UTF_8));
            inserts.add(doc);
        }
        return inserts;
    }

    public static void main(String ... args) {
        SpringApplication.run(Application.class, args);
    }
}
