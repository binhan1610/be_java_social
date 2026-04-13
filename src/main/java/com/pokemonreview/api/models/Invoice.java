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
    private Long invoiceId;

    private Long workspaceId;
    private Long roomId;
    private int month;
    private int year;
    private double totalAmount;
    private double paidAmount;
    private String status;
    private long paymentTime;
    private long createTime;
    private long updateTime;
}