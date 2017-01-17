package org.dwoodard.kura.example.can;

import java.io.IOException;
import java.lang.Thread.State;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.protocol.can.CanConnectionService;
import org.eclipse.kura.protocol.can.CanMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CanExample {
    
    private static Logger s_logger = LoggerFactory.getLogger(CanExample.class);

    private CanConnectionService canConnectionService;
    private Thread pollThread = null;
    private boolean threadDone = false;
    
    protected void setCanConnectionService(CanConnectionService canConnectionService) {
        this.canConnectionService = canConnectionService;
    }
    
    protected void unsetCanConnectionService(CanConnectionService canConnectionService) {
        this.canConnectionService = null;
    }
    
    protected void activate() {
        this.pollThread = new Thread(new Runnable() {      
            @Override
            public void run() {
                if(canConnectionService != null){
                    doCanPoll();
                }
            }
        }, getClass().getSimpleName() + "-doCanPoll");
        this.pollThread.start();
    }
    
    protected void deactivate() {
        this.threadDone = true;
        while (this.pollThread.getState() != State.TERMINATED) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                s_logger.error(e.getLocalizedMessage());
            }
        }
    }
    
    private void doCanPoll() {
        try {
            this.canConnectionService.connectCanSocket();
        } catch (IOException e) {
            s_logger.error(e.getLocalizedMessage());
        }
        
        while (!this.threadDone) {
            CanMessage canMsg = null;
            try {
                canMsg = this.canConnectionService.receiveCanMessage(-1, 0);
                s_logger.info("Can ID: " + Integer.toHexString(canMsg.getCanId()));
            } catch (IOException e) {
                s_logger.error(e.getLocalizedMessage());
            }
        }
        
        try {
            this.canConnectionService.disconnectCanSocket();
        } catch (IOException e) {
            s_logger.error(e.getLocalizedMessage());
        }
    }
}
