package com.stockflow.purchase.service;
import java.util.UUID;
import com.stockflow.purchase.dto.request.GoodsReceiptRequest;
import com.stockflow.purchase.dto.response.GoodsReceiptResponse;
import org.springframework.data.domain.*;
public interface GoodsReceiptService {
    GoodsReceiptResponse receive(GoodsReceiptRequest request);
    GoodsReceiptResponse getById(UUID id);
    Page<GoodsReceiptResponse> getAll(Pageable pageable);
}
