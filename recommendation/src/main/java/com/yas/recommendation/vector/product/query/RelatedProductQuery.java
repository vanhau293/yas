package com.yas.recommendation.vector.product.query;

import com.yas.recommendation.vector.common.query.VectorQuery;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * Service for performing related product searches using vector similarity.
 * Extends {@link VectorQuery} for {@link RelatedProductVm} results.
 */
@Service
public class RelatedProductQuery extends VectorQuery<ProductDocument, RelatedProductVm> {

    protected RelatedProductQuery(VectorStore vectorStore) {
        super(ProductDocument.class, RelatedProductVm.class);
    }
}
