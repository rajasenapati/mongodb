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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
        //ensure all files end with ttal extension
        // resetFiles();
        // insert document from the ttal files into database
        // insertDocumentFromFiles();

        System.out.println("Query 1: *****************");
//       db.ttal.find( { "requestReference": "dubtext:dubtext_script_authoring:9d8b60ff-f359-4289-bd55-b03615c807d5"} );
         List<Document> docs = ttalImporter.fetchDocumentsForRequestReference("dubtext:dubtext_script_authoring:9d8b60ff-f359-4289-bd55-b03615c807d5", 0, 0);
         docs.forEach( doc -> System.out.println(doc.toJson()));

        System.out.println("Query 2: *****************");
        //db.ttal.find( { "movieId": 81316185, "type": "DUB_SCRIPT", "lang": "es"} );
        List<Document> docs2 = ttalImporter.fetchDocumentsForMovieLanguageCodeAndType(81316185, "DUB_SCRIPT", "es");
        docs2.forEach( doc -> System.out.println(doc.toJson()));

        System.out.println("Query 3: *****************");
        //db.ttal.find( { $text: { $search: "camelids" }, "movieId": 81498468 } );
        List<Document> docs3 = ttalImporter.doTextSearch(81498468, "camelids");
        docs3.forEach( doc -> System.out.println(doc.toJson()));
    }


    private Collection<Document> insertDocumentFromFiles() throws IOException {
        Collection<Document> inserts = new ArrayList<>();
        Collection<String> fileProgress = new ArrayList<>();
        String[] extensions = new String[1];
        extensions[0] = "ttal";
        Collection<File> files = FileUtils.listFiles(new File("/Users/rranjan/Downloads/from_db/"), extensions, false);
        int count = 0;
        if (!files.isEmpty()) {
            System.out.println("Files in directory: ");
            for (File file : files) {
                String origFile = file.getAbsolutePath();
                System.out.println("processing file with count " +  ++count + ": " + origFile);
                Document doc = Document.parse(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                Document meta = (Document) doc.get("meta");
                if(meta != null) {
                    String requestReference = meta.getString("requestReference");
                    if(requestReference == null) {
                        List<String> requestRefs = meta.getList("linkParentRequestReference", String.class);
                        if(!requestRefs.isEmpty()) {
                            requestReference = requestRefs.get(0);
                        }
                    }
                    if(requestReference != null) {
                        Document newDocument = new Document();
                        newDocument.append("movieId", meta.get("movieId"));
                        newDocument.append("packageId", meta.get("packageId"));
                        newDocument.append("type", meta.get("type"));
                        newDocument.append("requestReference", requestReference);
                        newDocument.append("lang", meta.get("lang"));
                        newDocument.append("recipe", "default");
                        newDocument.append("document", doc);
                        inserts.add(newDocument);
                        fileProgress.add(origFile);
                        if(inserts.size() == 1000) {
                            saveToDBAndCleanup(inserts, fileProgress);
                        }
                    } else {
                        System.out.println("null requestref detected and hence ignoring processing of " + origFile);
                        Files.move(Paths.get(origFile), Paths.get(origFile+".nullrequestref"));
                    }

                } else {
                    System.out.println("null meta detected and hence ignoring processing of " + file.getAbsolutePath());
                    Files.move(Paths.get(origFile), Paths.get(origFile+".nullmeta"));
                }

            }
            //insert the last batch
            if(!inserts.isEmpty()) {
                saveToDBAndCleanup(inserts, fileProgress);
            }
        }
        return inserts;
    }

    private void saveToDBAndCleanup(Collection<Document> inserts, Collection<String> fileProgress) {
        long x = System.currentTimeMillis();
        ttalImporter.insertDocument(inserts);
        long y = System.currentTimeMillis();
        System.out.println("time taken in secs: " + ((y-x)/1000));
        inserts.clear();
        fileProgress.forEach(f -> {
            try {
                System.out.println("renaming " + f);
                Files.move(Paths.get(f), Paths.get(f+".processed"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fileProgress.clear();
    }

    private void resetFiles() throws IOException {
        String[] extensions = new String[1];
        extensions[0] = "processed";
        Collection<File> files = FileUtils.listFiles(new File("/Users/rranjan/Downloads/from_db/"), extensions, false);
        int count = 0;
        if (!files.isEmpty()) {
            System.out.println("Files in directory: ");
            for (File file : files) {
                String origName = file.getAbsolutePath();
                String replacedName = origName.replace(".processed", "");
                System.out.println(origName + " -> " + replacedName);
                Files.move(Paths.get(origName), Paths.get(replacedName));
            }
        }
    }


    public static void main(String ... args) {
        SpringApplication.run(Application.class, args);
    }
}
