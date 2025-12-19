package com.example.shop_clothes.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stock_receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receipt_code", unique = true, nullable = false, length = 50)
    private String receiptCode;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "receipt_date")
    private LocalDateTime receiptDate;

    @Column(name = "total_amount")
    private Float totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReceiptStatus status = ReceiptStatus.pending;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockReceiptDetail> details;

    public enum ReceiptStatus {
        pending, confirmed, cancelled
    }
}