package com.simone.progetto.utils;
/*Classe che mi permette di bloccare il calcolo della proof of work nel caso il blocco sia stato risolto da altro consumer*/
public class InsertChainSemaphore {
    private boolean toCompute = true;

    public boolean isToCompute() {
        return toCompute;
    }

    public void blockComputation(){
        toCompute = false;
    }

    public void restartSemaphore(){
        toCompute = true;
    }
}
