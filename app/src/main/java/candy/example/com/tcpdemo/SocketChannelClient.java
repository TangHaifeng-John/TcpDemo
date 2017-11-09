package candy.example.com.tcpdemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

public class SocketChannelClient extends Thread{
    public static final String SERVER_ADDRESS = "localhost";
    public static final int SERVER_PORT = 3467;
    private final String address;
    private final int port ;
    SocketChannel socketChannel;
    Selector selector;
    public SocketChannelClient(String remoteAdress , int remotePort){
        address = remoteAdress;
        port = remotePort;
    }


    public static void main(String[] args) {

        SocketChannelClient client = new SocketChannelClient(SocketChannelClient.SERVER_ADDRESS,
                SocketChannelClient.SERVER_PORT);
        client.start();
    }
    @Override
    public void run() {
        try{
            socketChannel = SocketChannel.open(new InetSocketAddress(address,port));
            socketChannel.configureBlocking(false);
            selector = SelectorProvider.provider().openSelector();
            socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
            Iterator<SelectionKey> keyIterator;
            SelectionKey oneKey = null;
            while(true){
                int n = selector.select();
                if(n == 0){
                    continue;
                }
                keyIterator = selector.selectedKeys().iterator();
                while(keyIterator.hasNext()){
                    try{
                        oneKey = keyIterator.next();
                        keyIterator.remove();
                        handleSelectorKey(oneKey);
                    }finally{
                        try{
                            if(oneKey != null && oneKey.isReadable()){
                                oneKey.channel().close();
                                oneKey = null;
                            }
                        }catch(IOException e){
                            e.printStackTrace();
                        }

                    }
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }

    }
    private synchronized void handleSelectorKey(SelectionKey key) {
        if(key == null){
            return;
        }
        if (key.isAcceptable()) {
            System.out.println("selection acceptable --> " + key);
            handleSelectionAcceptable(key);
        }

        if (key.isConnectable()) {
            System.out.println("selection connectable --> " + key);
            handleSelectionAcceptable(key);
        }

        if (key.isReadable()) {
            System.out.println("selection readable --> " + key);
            handleSelectionReadable(key);
        }

        if (key.isWritable()) {
            System.out.println("selection writeable --> " + key);
            handleSelectionWriteable(key);
        }
    }

    public  void handleSelectionAcceptable(SelectionKey key) {
        if(key == null) return;
        try{
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel sc = (SocketChannel) ssc.accept();

            System.out.println("receive connection request " +
                    sc.socket().getInetAddress()+":"+sc.socket().getPort());

            sc.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            sc.register(selector, SelectionKey.OP_READ,buffer);
        }catch(IOException e){

        }
    }

    public  void handleSelectionCollectable(SelectionKey key) {

    }

    public  void handleSelectionReadable(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        try {
            sc.read(readBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        readBuffer.flip();
        System.out.println("receive data from server --->: "
                + getString(readBuffer));
    }
    public static String getString(ByteBuffer buffer)
    {
        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;
        try
        {
            charset = Charset.forName("UTF-8");
            decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
            return charBuffer.toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "";
        }
    }
    static int talkValue = 0;
    ByteBuffer sendBuffer;
    public  void handleSelectionWriteable(SelectionKey key) {
        sendBuffer = ByteBuffer.wrap(("talk to server "+(++talkValue)).getBytes(Charset.forName("utf-8")));
        key.attach(sendBuffer);
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            sc.write(sendBuffer);
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        }
    }

}