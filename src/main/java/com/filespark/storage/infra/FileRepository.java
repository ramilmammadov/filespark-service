package com.filespark.storage.infra;

import com.filespark.storage.domain.FileDocument;
import com.filespark.storage.domain.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends MongoRepository<FileDocument, String> {
    boolean existsByUserIdAndFilename(String userId, String filename);

    boolean existsByUserIdAndContentHash(String userId, String contentHash);

    Optional<FileDocument> findByLink(String link);

    Page<FileDocument> findByVisibility(Visibility visibility, Pageable pageable);

    Page<FileDocument> findByUserIdAndTagsContaining(String userId, String tag, Pageable pageable);

    Page<FileDocument> findByUserId(String userId, Pageable pageable);

    @Query("{'visibility': ?0, 'tags': {$in: [{$regex: ?1, $options: 'i'}]}}")
    Page<FileDocument> searchByVisibilityAndTagIgnoreCase(Visibility visibility, String tag, Pageable pageable);

}
