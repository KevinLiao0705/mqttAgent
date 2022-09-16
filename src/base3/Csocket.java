/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base3;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Csocket {

    public String address = "127.0.0.1";// 連線的ip
    public int port = 2468;// 連線的port
    public int connectTimeOut = 4000;//ms 
    public int txDataTimeOut = 2000;//ms
    public int readTimeOut = 100;//10ms
    public byte[] txbuf;
    Socket client;
    InetSocketAddress isa;
    BufferedOutputStream outtx;
    int error_f = 0;
    String errStr = "";
    int td1_run_f = 0;
    CsocketTd1 td1 = null;
    int td1_destroy_f = 0;
    byte[] txBytesBuffer;
    int finished_f = 0;
    byte[] inbuf = new byte[4096];
    int inbuf_inx0 = 0;
    int inbuf_len = 0;

    public Csocket() {
        //client = new Socket();

    }

    public static void callServer(String host, int port, String outStr) throws IOException {
//        打开通道
        final SocketChannel sChannel = SocketChannel.open();
//        通道设置为非阻塞
        sChannel.configureBlocking(false);
//        连接
        sChannel.connect(new InetSocketAddress(host, port));
//        选择器
        final Selector selector = Selector.open();
//        把通道注册到选择器，监听读事件
        sChannel.register(selector, SelectionKey.OP_READ);

        while (!sChannel.finishConnect());

//        开启一个线程读数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //            定义缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                try {
//                    轮询
                    while (selector.select() > 0) {
                        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                        while (it.hasNext()) {
                            SelectionKey sk = it.next();
//                            读事件
                            if (sk.isReadable()) {
                                SocketChannel channel = (SocketChannel) sk.channel();
                                int len = 0;
                                while ((len = sChannel.read(buffer)) > 0) {
                                    sChannel.read(buffer);
                                    buffer.flip();
                                    String str = new String(buffer.array(), 0, len);
                                    System.out.println(str);
                                    buffer.clear();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

//        主线程写数据
        //Scanner sc = new Scanner(System.in);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //buffer.put(str.getBytes());
        buffer.put(outStr.getBytes());
        buffer.flip();
        sChannel.write(buffer);
        buffer.clear();
//        关闭通道
        sChannel.close();
    }

    public void connect() {
        try {
            this.error_f = 0;
            client = new Socket();
            isa = new InetSocketAddress(this.address, this.port);
            client.connect(isa, this.connectTimeOut);
            //client.setSoTimeout(txDataTimeOut);

        } catch (java.io.IOException e) {
            client = null;
            this.error_f = 1;
            this.errStr = "Connect To " + this.address + " port=" + this.port + " error !!!\n";
            System.out.println(errStr);
        }
    }

    public void disconnect() {
        try {
            client.close();
            client = null;

        } catch (java.io.IOException e) {
            client = null;
            this.error_f = 1;
            this.errStr = "Close Client error !!!\n";
            System.out.println(errStr);
        }
    }

    public void txData() {
        try {
            this.error_f = 0;
            outtx = new BufferedOutputStream(client.getOutputStream());
            // 送出字串
            //outtx.write("Send From Client ".getBytes());
            outtx.write(txBytesBuffer);
            outtx.flush();
            outtx.close();
            outtx = null;
        } catch (java.io.IOException e) {
            client = null;
            this.error_f = 1;
            this.errStr = "Client Socket Tx Data Error !!!\n";
            System.out.println(errStr);
            System.out.println("IOException :" + e.toString());
        }
    }

    public int recvDataBlock() {
        InputStream instr;
        int length;
        byte[] bbuf = new byte[1024];
        int i, j;
        try {
            instr = client.getInputStream();
            client.setSoTimeout(readTimeOut * 10);
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
            instr.close();

        } catch (IOException e) {
            this.error_f = 1;
            this.errStr = "Client Socket Tx Data Error !!!\n";
            System.out.println(errStr);
            System.out.println("IOException :" + e.toString());
        }
        //==============================================================
        return 0;
    }

    public int sendDataBlock(byte[] txBytes, int len) {
        this.error_f = 0;
        if (td1_run_f == 1) {
            this.error_f = 1;
            this.errStr = "Client Socket Tx Data Error !!!\n";
            System.out.println(errStr);
            return -1;
        }
        txBytesBuffer = new byte[len];
        System.arraycopy(txBytes, 0, txBytesBuffer, 0, len);
        this.connect();
        if (this.error_f == 1) {
            return -1;
        }
        this.txData();
        if (this.error_f == 1) {
            return -1;
        }
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(Csocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int sendDataRetBlock(byte[] txBytes, int len) {
        this.error_f = 0;
        if (td1_run_f == 1) {
            this.error_f = 1;
            this.errStr = "Client Socket Tx Data Error !!!\n";
            System.out.println(errStr);
            return -1;
        }
        txBytesBuffer = new byte[len];
        System.arraycopy(txBytes, 0, txBytesBuffer, 0, len);
        this.connect();
        if (this.error_f == 1) {
            return -1;
        }
        this.txData();
        if (this.error_f == 1) {
            return -1;
        }
        this.recvDataBlock();
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(Csocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int sendDataUnbock(byte[] txBytes, int len) {
        this.error_f = 0;
        if (td1_run_f == 1) {
            this.error_f = 1;
            this.errStr = "Client Socket Tx Data Error !!!\n";
            System.out.println(errStr);
            return -1;
        }
        txBytesBuffer = new byte[len];
        System.arraycopy(txBytes, 0, txBytesBuffer, 0, len);
        if (td1 == null) {
            td1 = new CsocketTd1(this);
        }
        td1_run_f = 1;
        td1_destroy_f = 0;
        td1.start();
        return 0;
    }

    public static void main(String args[]) {
        new Csocket();
    }
}

class CsocketTd1 extends Thread {

    Csocket cla;

    CsocketTd1(Csocket owner) {
        cla = owner;
    }

    @Override
    public void run() {
        cla.td1_run_f = 1;
        cla.connect();
        if (cla.error_f == 1) {
            cla.td1_run_f = 0;
            cla.finished_f = 1;
            return;
        }
        cla.txData();
        if (cla.error_f == 1) {
            cla.td1_run_f = 0;
            cla.finished_f = 1;
            return;
        }
        cla.td1_run_f = 0;
        cla.finished_f = 1;
    }
}
