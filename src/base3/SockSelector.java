/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base3;

/**
 *
 * @author Kevin
 */
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SockSelector implements Runnable {

    public static int DEFAULT_PORT = 1234;
    SockSelectorRx rxClass;
            
    @Override
    public void run() {
        ServerSocketChannel serverChannel;
        Selector selector;
        SelectionKey clientKey=null;

        try {
            serverChannel = ServerSocketChannel.open();
            selector = Selector.open();

            if (serverChannel.isOpen() && selector.isOpen()) {
                // 設定為 false 即表示要用 non-blocking 模式
                serverChannel.configureBlocking(false);

                serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 256 * 1024);
                serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                serverChannel.bind(new InetSocketAddress(DEFAULT_PORT));
                // 註冊 OP_ACCEPT 給 ServerSocketChannel，這樣 ServerSocketChannel 接到 client 端連線時，就會有一個值為 OP_ACCEPT 的 SelectionKey。
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);

                while (true) {
                    // 程式會在這裡停下，等待關心的"selection operation"出現。這個 method 有另一個版本，可以傳入 timeout 值，以免程式一直停在這裡。
                    selector.select();
                    //===============================
                    Set<SelectionKey> readyKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = readyKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        // 移除將處理的 selection operation 很重要!
                        // 避免之後又重複接收到。
                        iterator.remove();

                        try {
                            if (key.isAcceptable()) {
                                // ServerSocketChannel 的用處就只是接受 client 連線，當收到 OP_ACCEPT 時，表示有 client 要連線，所以用 ServerSocketChannel 接收。
                                ServerSocketChannel server = (ServerSocketChannel) key.channel();
                                // 等待 client 連線
                                SocketChannel client = server.accept();
                                // 連線後設定這個連線為 non-blocking
                                client.configureBlocking(false);
                                // 這是一個 echo server，連線後緊接著要收 client 送來的訊息，所以註冊這個 socket channel 要關注 OP_READ。
                                clientKey = client.register(selector, SelectionKey.OP_READ);
                                                        
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                // non-blocking 模式有點像事件模式，每次都是待 selection operation 出現，程式才會接手做相關的事，在這不同的 selection operation 之間，要如何保留讀入的資料? 使用 attach() 及 attachment()，讓這些資料保存在 SelectionKey 中。 
                                clientKey.attach(buffer);
                            } else if (key.isReadable()) {
                                SocketChannel client = (SocketChannel) key.channel();
                                //在 OP_ACCEPT 階段，程式有預先產生一個 ByteBuffer，這時將它取出，用來儲存讀入的資料。
                                ByteBuffer output = (ByteBuffer) key.attachment();
                                int len=client.read(output);
                                rxClass.rxprg(output);
                                System.out.println("recv: " + new String(Arrays.copyOfRange(output.array(), 0, output.limit())));
                                // echo server 讀到資料後，當然接著要寫回給 client，所以註冊 OP_WRITE。
                                //clientKey.cancel();
                                clientKey = client.register(selector, SelectionKey.OP_WRITE);
                            } else if (key.isWritable()) {
                                SocketChannel client = (SocketChannel) key.channel();
                                // 在 OP_READ 階段讀取到的資料，現在將它取出。
                                ByteBuffer output = (ByteBuffer) key.attachment();
                                if (output != null) {
                                    output.flip();
                                    if (output.hasRemaining()) {
                                        // 把讀取到的資料寫回給 client。
                                        client.write(output);
                                        output.compact();
                                    } else {
                                        System.out.println("output has not remaining");
                                    }
                                }
                                //clientKey.cancel();
                            }
                        } catch (IOException e) {
                            key.cancel();
                            try {
                                key.channel().close();
                            } catch (IOException ex) {
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExecutorService svc = Executors.newSingleThreadExecutor();
        SockSelector echo = new SockSelector();
        svc.execute(echo);
        svc.shutdown();
    }
}


abstract class SockSelectorRx {
    public abstract void rxprg(ByteBuffer bb);
}
