package com.yas.recommendation.vector.common.document;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.document.ContentFormatter;
import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.id.IdGenerator;
import org.springframework.util.Assert;

/**
 * Represent for base vector document structure.
 */
@Getter
@Setter
public abstract class BaseDocument {

    private Long entityId;
    private String content;
    private Map<String, Object> metadata;
    private ContentFormatter contentFormatter;

    public static final ContentFormatter DEFAULT_CONTENT_FORMATTER = DefaultContentFormatter.builder()
        .from(DefaultContentFormatter.defaultConfig())
        .withTextTemplate(DocumentMetadata.DEFAULT_CONTENT_FORMATTER)
        .build();

    /**
     * Convert {@link BaseDocument} base document type to {@link Document}
     * including content formatter and id generator (define metadata {@link DocumentMetadata}
     * in class extends {@link BaseDocument}).
     *
     * @param idGenerator id generator.
     * @return {@link Document} to save in db
     */
    public Document toDocument(IdGenerator idGenerator) {
        Assert.isTrue(
            this.getClass().isAnnotationPresent(DocumentMetadata.class),
            "Document must annotated by '@DocumentMetadata'"
        );
        Assert.notNull(content, "Document's content cannot be null");
        Assert.notNull(metadata, "Document's metadata cannot be null");
        Document document = new Document(content, metadata, idGenerator);
        document.setContentFormatter(contentFormatter == null ? DEFAULT_CONTENT_FORMATTER : contentFormatter);
        return document;
    }

}
