package com.pokemonreview.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invoice")
@Data
public class Invoice {
    @Id
    private long invoiceId;

    private long workspaceId;
    private long roomId;
    private int month;
    private int year;
    private long totalAmount;
    private long paidAmount;
    private String status;
    private long paymentTime;
    private long createTime;
    private long updateTime;
}