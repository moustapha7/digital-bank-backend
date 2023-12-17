package com.sid.digitalbankbackend.dtos;

import com.sid.digitalbankbackend.entities.AccountOperation;
import com.sid.digitalbankbackend.entities.Customer;
import com.sid.digitalbankbackend.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
public  class BankAccountDTO {


    private String type;


}
