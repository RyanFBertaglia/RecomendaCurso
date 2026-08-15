package com.recommend.server.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;

@Service
public class ImageStorageService {

    private static final String BUCKET_NAME = "images";
    private static final Pattern OBJECT_ID_PATTERN = Pattern.compile("^[a-fA-F0-9]{24}$");

    private final GridFSBucket gridFSBucket;

    public ImageStorageService(MongoClient mongoClient, @Value("${mongodb.atlas.database}") String databaseName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.gridFSBucket = GridFSBuckets.create(database, BUCKET_NAME);
    }

    public String saveImage(MultipartFile file) {
        try {
            String original = Objects.requireNonNull(file.getOriginalFilename());
            String ext = original.contains(".")
                    ? original.substring(original.lastIndexOf('.'))
                    : "";
            String filename = java.util.UUID.randomUUID().toString().substring(0, 8) + ext;

            GridFSUploadOptions options = new GridFSUploadOptions()
                    .metadata(new Document("contentType", file.getContentType()));

            ObjectId id = gridFSBucket.uploadFromStream(filename, file.getInputStream(), options);
            return id.toHexString();
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar imagem: " + e.getMessage(), e);
        }
    }

    public ImageData getImage(String id) {
        if (id == null || !OBJECT_ID_PATTERN.matcher(id).matches()) {
            return null;
        }
        ObjectId objectId = new ObjectId(id);
        GridFSFindIterable files = gridFSBucket.find(eq("_id", objectId));
        GridFSFile gridFSFile = files.first();

        if (gridFSFile == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        gridFSBucket.downloadToStream(objectId, out);

        String contentType = null;
        if (gridFSFile.getMetadata() != null) {
            contentType = gridFSFile.getMetadata().getString("contentType");
        }

        return new ImageData(out.toByteArray(), contentType);
    }

    public void deleteImage(String id) {
        if (id == null || !OBJECT_ID_PATTERN.matcher(id).matches()) {
            return;
        }
        gridFSBucket.delete(new ObjectId(id));
    }

    public void deleteAll() {
        gridFSBucket.find().forEach(file -> gridFSBucket.delete(file.getId()));
    }

    public record ImageData(byte[] bytes, String contentType) {
    }
}
