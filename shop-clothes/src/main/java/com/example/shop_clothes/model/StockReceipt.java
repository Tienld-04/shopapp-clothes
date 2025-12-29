package com.example.shop_clothes.model;

import com.example.shop_clothes.enums.ReceiptStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "stock_receipts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReceipt extends BaseEntity {

    @Column(name = "receipt_code", unique = true, nullable = false, length = 50)
    private String receiptCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "total_amount")
    private Float totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReceiptStatus status = ReceiptStatus.PENDING;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

//    @Column(name = "receipt_date")
//    private LocalDateTime receiptDate;
//
//    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinColumn(name = "created_by")
//    private User createdBy;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockReceiptDetail> details;

}