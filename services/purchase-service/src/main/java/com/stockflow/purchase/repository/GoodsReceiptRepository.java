package com.stockflow.purchase.repository;
import java.util.UUID;
import com.stockflow.purchase.entity.GoodsReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, UUID> {}
