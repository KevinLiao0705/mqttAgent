/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base3;

import java.io.IOException;
import java.io.InputStream;

public class CommPortReceiver extends Thread {

    private boolean isContinue = true;
    InputStream in;
    Protocol protocol = new ProtocolImpl();
    byte[] buffer = new byte[1000];
    int len;

    public CommPortReceiver(InputStream in) {
        this.in = in;
    }

    public void terminate() {
        isContinue = false;
    }

    public void run() {
        try {
            while (isContinue) {
                len = in.available();
                if (len > 0) {
                    if (len >= 1000) {
                        len = 1000;
                    }
                    len = in.read(buffer, 0, len);
                    UartTest.scla.onUartReveive(buffer, len);
                    continue;
                } else {
                    UartTest.scla.onUartStreamClosed();
                }
                sleep(10);
            }

            /*
            int b;    
            while(isContinue) {    
                // if stream is not bound in.read() method returns -1    
                b = in.read();
                while((b = in.read()) != -1) {    
                    //protocol.onReceive((byte) b);   
                    UartTest.scla.onUartReveive((byte)b);
                }    
                UartTest.scla.onUartStreamClosed();    
                // wait 10ms when stream is broken and check again    
                
                sleep(10);    
            } 
             */
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
