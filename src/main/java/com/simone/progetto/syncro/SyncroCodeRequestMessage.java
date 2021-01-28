package com.simone.progetto.syncro;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class SyncroCodeRequestMessage implements Serializable {
    private String id_applicant;
    private String request_block = null;

    public SyncroCodeRequestMessage(String id_applicant) {
        this.id_applicant = id_applicant;
    }
}
