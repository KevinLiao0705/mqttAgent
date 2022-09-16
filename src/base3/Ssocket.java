/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base3;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ssocket extends java.lang.Thread {

    int status_f = 0;
    String status_str;
    int stop_f;
    int connect_f = 0;
    String clientIp = null;
    int datain_f = 0;
    byte[] inbuf = new byte[4096];
    int inbuf_len;

    int inbuf_inx0 = 0;
    int inbuf_inx1 = 0;
    int port = 1234;
    int format = 1;   //0:encode formate 
    int readTimeOut = 200;//unit 10ms

    SsktxTd txTd;
    int txTd_run_f = 0;
    int txTd_destroy_f = 0;
    int tx_start_f = 0;
    String tx_str;
    String tx_ip;
    int tx_port;
    byte[] tx_bytes;

    BufferedOutputStream outstr;
    InputStream instr;

    private ServerSocket serverSocket;
    MyStm stm;
    SskRx sskRx;

    public Ssocket() {
        Ssocket cla = this;
        stm = new MyStm();
        if (cla.txTd == null) {
            cla.txTd = new SsktxTd(cla);
            cla.txTd.start();
            cla.txTd_run_f = 1;
            cla.txTd_destroy_f = 0;
        }

    }

    public void rxproc(int format) {
        sskRx.sskRx(format);
    }

    public void create(int pt) {
        try {
            port = pt;
            serverSocket = new ServerSocket(port);
        } catch (java.io.IOException e) {
            status_str = "\n Socket啟動有問題 ! ";
            status_str += "\n IOException : " + e.toString();
            status_f = 1;
        }
    }

    
    
    
    public void txout() {

        int stx_index = 0;
        int i;
        stm.tbuf[stx_index++] = (byte) 0xA2;
        stm.tbuf[stx_index++] = (byte) 0x12;
        stm.tbuf[stx_index++] = (byte) 0x34;
        stm.tbuf[stx_index++] = (byte) 0x56;
        stm.tbuf[stx_index++] = (byte) 0x78;
        stm.tbuf_byte = stx_index;
        stm.enc_mystm();
        for (i = 0; i < stm.txlen; i++) {
            try {
                outstr.write(stm.tdata[i]);
            } catch (IOException ex) {
                Logger.getLogger(Ssocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    
    public void txretStr(String str) {
            try {
                outstr.write(str.getBytes());
                outstr.flush();
            } catch (IOException ex) {
                Logger.getLogger(Ssocket.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
    public void run() {
        int i, j;
        Socket socket;
        status_f = 1;
        status_str = "\n 伺服器已啟動 !";
        //
        int length;
        byte[] bbuf = new byte[1024];
        //
        int rxdata;
        int rxcon_tim;

        while (stop_f != 1) {
            try {
                if (serverSocket == null) {
                    continue;
                }
                synchronized (serverSocket) {
                    socket = serverSocket.accept();
                }
                //========================================================
                clientIp = socket.getInetAddress().toString();
                status_str = "\n 取得連線 : InetAddress = " + socket.getInetAddress();
                status_f = 1;
                instr = socket.getInputStream();
                outstr = new BufferedOutputStream(socket.getOutputStream());
                //=========================================================
                //outstr = socket.getOutputStream();
                inbuf_inx0 = 0;
                //==============================================================
                if (format == 0) {
                    /*
                    socket.setSoTimeout(readTimeOut*10);
                    for(;;){
                        length = instr.read(bbuf);
                        inbuf_len = length;
                        
                    }
                    */
                    /*
                    while ((length = instr.read(bbuf)) > 0)// <=0的話就是結束了
                    {
                        if (inbuf_inx0 > 4096) {
                            break;
                        }
                        for (i = 0; i < length; i++) {
                            inbuf[(inbuf_inx0 + i) & 0xfff] = bbuf[i];
                        }
                        inbuf_inx0 += length;
                        inbuf_len = inbuf_inx0;
                    }
                    */
                    /*
                    instr.close();
                    instr = null;
                    datain_f = 1;
                    //rxproc(0);
                    outstr.close();
                    socket.close();
                    continue;
                    */
                }
                //==============================================================
                if (format == 1) {
                    socket.setSoTimeout(readTimeOut*10);
                    rxcon_tim = 0;
                    while (true) {
                        rxdata = instr.read();
                        if (rxdata == -1) {
                            if (++rxcon_tim >= readTimeOut) {
                                instr.close();
                                instr = null;
                                socket.close();
                                outstr.close();
                                status_str = "\n 連線中斷 : InetAddress = " + clientIp;
                                status_f = 1;
                                break;
                            }
                            continue;
                        }
                        rxdata &= 0xff;
                        rxcon_tim = 0;
                        stm.dec_mystm((byte) rxdata);
                        if (stm.rxin_f == 1) {
                            stm.rxin_f = 0;
                            for (i = 0; i < stm.rxlen; i++) {
                                inbuf[i] = (byte) (stm.rdata[i + 1]);
                            }
                            inbuf_len = stm.rxlen;
                            datain_f = 1;
                            rxproc(1);
                            //================================
                            //txout();
                            //=================================
                        }

                    }

                }
                //==============================================================

            } catch (java.io.IOException e) {
                status_str = "\n Socket連線有問題 ! ";
                status_str += "\n IOException : " + e.toString();
                status_f = 1;
            }
        }
    }
    
    
    
    

    public void txret(String txstr, int txport) {
        String addr;
        if (clientIp == null) {
            return;
        }
        addr = clientIp.substring(1);
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(addr, txport);
        try {
            client.connect(isa, 100);
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            // 送出字串
            out.write(txstr.getBytes());
            out.flush();
            out.close();
            client.close();

        } catch (java.io.IOException e) {
            String str;
            str = "Socket連線有問題 !" + addr + " port: " + txport;
            System.out.println(str);
            System.out.println("IOException :" + e.toString());
        }
    }

    public void txret(byte[] bytes, int txport) {
        String addr;
        if (clientIp == null) {
            return;
        }
        addr = clientIp.substring(1);
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(addr, txport);
        try {
            client.connect(isa, 100);
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            // 送出字串
            out.write(bytes);
            out.flush();
            out.close();
            client.close();

        } catch (java.io.IOException e) {
            String str;
            str = "Socket連線有問題 !" + addr + " port: " + txport;
            System.out.println(str);
            System.out.println("IOException :" + e.toString());
        }
    }

    public void txip(String ipaddr, String txstr, int txport) {
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(ipaddr, txport);
        try {
            client.connect(isa, 1000);
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            // 送出字串
            out.write(txstr.getBytes());
            out.flush();
            out.close();
            client.close();

        } catch (java.io.IOException e) {
            String str;
            str = "Socket連線有問題 !" + ipaddr + " port: " + txport;
            System.out.println(str);
            System.out.println("IOException :" + e.toString());
        }
    }

    public void txip(String ipaddr, byte[] bytes, int txport) {
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(ipaddr, txport);
        try {
            client.connect(isa, 1000);
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            // 送出字串
            out.write(bytes);
            out.flush();
            out.close();
            client.close();

        } catch (java.io.IOException e) {
            String str;
            str = "Socket連線有問題 !" + ipaddr + " port: " + txport;
            System.out.println(str);
            System.out.println("IOException :" + e.toString());
        }
    }

    public static void main(String args[]) {
        (new Ssocket()).start();
    }
}

abstract class SskRx {

    public abstract void sskRx(int format);
}

class SsktxTd extends Thread {

    Ssocket cla;

    SsktxTd(Ssocket owner) {
        cla = owner;
    }

    @Override
    public void run() { // override Thread's run()
        //Test cla=Test.thisCla;
        for (;;) {
            if (cla.txTd_run_f == 1) {
                if (cla.tx_start_f != 0) {
                    switch (cla.tx_start_f) {
                        case 1:
                            cla.txret(cla.tx_str, cla.tx_port);
                            break;
                        case 2:
                            cla.txip(cla.tx_ip, cla.tx_str, cla.tx_port);
                            break;
                        case 3:
                            cla.txret(cla.tx_bytes, cla.tx_port);
                            break;
                        case 4:
                            cla.txip(cla.tx_ip, cla.tx_bytes, cla.tx_port);
                            break;

                    }
                }
                cla.tx_start_f = 0;
            }
            Lib.thSleep(10);
        }
    }
}
