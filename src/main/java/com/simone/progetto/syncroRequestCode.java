package com.simone.progetto;

import com.simone.progetto.syncro.SyncroCodeRequestMessage;
import com.simone.progetto.syncro.SyncroCodeResponseMessage;
import com.simone.progetto.syncro.SyncronizationCodeResponseQueue;
import com.simone.progetto.utils.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Stack;

@RabbitListener(id="multi",queues = "#{SyncroRequestCode.name}")
@Component
@Slf4j
public class syncroRequestCode {
    @Autowired private Chain chain;
    @Autowired private SyncronizationCodeResponseQueue communicator;

    public Stack<Node> insertElementToStack(Stack<Node> stack,Stack<Node> toInsert){
        if(stack == null){
            stack = new Stack<>();
        }
        toInsert.addAll(stack);
        return toInsert;
    }

    public Stack<Node> getBLockToReturn(String researchHash){
        Stack<Node> toSend = null;
        Stack<Node> temp;
        for (Node node: chain.getTopList()) {
            //CERCO IL BLOCCO IN OGNI CATENA E RITORNO TUTTI QUELLI CHE LO SUCCEDONO
            temp = chain.searchListOfBlock(node,researchHash);
            if(temp != null){
                toSend = this.insertElementToStack(toSend,temp);
            }
        }
        return  toSend;
    }

    public void tryToSendSyncroMessage(SyncroCodeResponseMessage msg,Stack<Node> toSend){
        if(toSend.size() > 0){
            msg.setRequest_node(toSend);
            //DEVO INVIARLO NELLA CODA
            communicator.sendResponse(msg);
        }
        else{
            log.info("{"+Configuration.UUID + "} Consumer already syncronized");
        }
    }

    @RabbitHandler
    public void receive(SyncroCodeRequestMessage syncroCodeRequestMessage){
        Stack<Node> toSend;
        if(!syncroCodeRequestMessage.getId_applicant().equals(Configuration.UUID)){
            SyncroCodeResponseMessage msg = new SyncroCodeResponseMessage(Configuration.UUID,
                    syncroCodeRequestMessage.getId_applicant());
            toSend = this.getBLockToReturn(syncroCodeRequestMessage.getRequest_block());
            if(toSend != null){
                this.tryToSendSyncroMessage(msg,toSend);
            }
            else{
                log.info("{"+Configuration.UUID + "} Block not found, i can't answer !");
            }
        }
    }

    public void trySendSynchronizeRequest(Node parent){
        if (parent != null) {
            log.info("{"+Configuration.UUID + "} precedent block is missing, i can't syncronize queue!");
            SyncroCodeRequestMessage msg = new SyncroCodeRequestMessage(Configuration.UUID,parent.getData().getHash());
            communicator.sendRequest(msg);
        }
    }

    public boolean synchronizeChain(Stack<Node> syncStack){
        boolean syncro = true;
        Node toInsert;
        while (syncStack.size() > 0 && syncro) { //INserisco finchè lo stack è pieno
            toInsert = syncStack.pop();
            if(chain.checkHashBlock(toInsert.getData())){
                if (!chain.insertToChain(toInsert)) {
                    syncro = false;
                    this.trySendSynchronizeRequest(toInsert.getParent());
                }
            }
        }
        return syncro;
    }

    @RabbitHandler
    public void receive(SyncroCodeResponseMessage syncroCodeResponseMessage){
        boolean syncro;
        if(!syncroCodeResponseMessage.getId_publisher().equals(Configuration.UUID) &&
        syncroCodeResponseMessage.getId_consumer().equals(Configuration.UUID)) {
            //Controllo che già non sia stata effettuata la syncronizzazione
            if (chain.isToUpdate()){
                syncro = this.synchronizeChain(syncroCodeResponseMessage.getRequest_node());
                if (syncro) {
                    log.info("{"+Configuration.UUID + "} syncronization queue correctly done !");
                    chain.setToUpdate(false);
                    chain.printChain();
                }
            }
        }
    }

}

