package com.simone.progetto;

import org.springframework.stereotype.Component;

@Component
public class TransactionRules {

    public boolean canInsert(Transaction transaction){
        if(transaction.getId_client() == 0 || transaction.getId_client() == null){
            return false;
        }
        if(transaction.getQuantity() == 0 || transaction.getId_client() == null){
            return false;
        }
        if(transaction.getProduct().getName().isEmpty() || transaction.getProduct().getName() == null){
            return false;
        }
        if(transaction.getProduct().getPrice() < 0){
            return false;
        }
        return true;
    }
}
