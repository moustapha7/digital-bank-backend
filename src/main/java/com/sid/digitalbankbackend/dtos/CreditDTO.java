package com.sid.digitalbankbackend.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CreditDTO {
    private String accountId;
    private double amount;
    private  String description;

}
