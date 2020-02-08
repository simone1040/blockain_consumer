package com.simone.progetto.syncro;

import com.simone.progetto.Transaction;

public interface SyncroCommunicator {
    public boolean sendMessage(SyncroMessage message);
}
