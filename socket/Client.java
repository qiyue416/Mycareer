import org.json.simple.JSONObject;
import sun.reflect.generics.scope.Scope;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    //通过socket连接
    private static Socket socket;
    //连接状态，失败为false，成功为true
    public static boolean connection_state = false;

    public static void main(String[] args) {
        //可以一直尝试服务端连接，防止先启动客户端
        while (!connection_state){
            //连接服务器
            connect();
            try {
                //3秒尝试一次连接
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //connect();

    }
    //连接服务器
    private static void connect(){
        try {
            //本机测试使用127.0.0.1回环地址，端口号使用服务器设置的9999
            socket = new Socket("127.0.0.1", 9999);
            System.out.println("连接成功...");
            connection_state = true;
            //传入同一个oos，保证传入服务端验证通过
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //对象输入流，从socket获取
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            new Thread(new Client_listen(socket,ois)).start();
            new Thread(new Client_send(socket, oos)).start();
            new Thread(new Client_heart(socket, oos)).start();
        } catch (IOException e) {
            e.printStackTrace();
            connection_state = false;
            System.out.println("尝试连接失败...");
        }
    }
    //重新连接服务器,采用客户端主动连接服务端
    public static void reconnect(){
        while (!connection_state){
            System.out.println("正在尝试重新连接...");
            connect();
            try {
                //每3秒尝试连接
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

//接受服务器传来的信息
class Client_listen implements Runnable{
    //在socket连接上操作
    private Socket socket;
    public static ObjectInputStream ois;

    Client_listen(Socket socket, ObjectInputStream ois) {
        this.socket = socket;
        this.ois = ois;
    }

    @Override
    public void run() {
        try {
            //ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            while (true){
                //打印到client上
                System.out.println(ois.readObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

//发送信息给服务器
class Client_send implements Runnable{
    private Socket socket;
    //使用传入的oos
    private ObjectOutputStream oos;

    Client_send(Socket socket, ObjectOutputStream oos) {
        this.socket = socket;
        this.oos = oos;
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (true){
                System.out.print("请输入您要发送的信息:");
                String string = scanner.nextLine();
                JSONObject object = new JSONObject();
                object.put("type","chat");
                object.put("msg", string);
                oos.writeObject(object);
                //刷新缓冲区
                oos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
                Client.connection_state = false;
                Client.reconnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}

//为了保持socket连接，监测连接状态
class Client_heart implements Runnable{
    private Socket socket;
    //使用传入的oos
    private ObjectOutputStream oos;

    Client_heart(Socket socket, ObjectOutputStream oos) {
        this.socket = socket;
        this.oos = oos;
    }

    @Override
    public void run() {
        try {
            System.out.println("心跳包线程已启动...");

            while (true){
                //5秒发送一次心跳包
                Thread.sleep(5000);
                JSONObject object = new JSONObject();
                object.put("type","heart");
                object.put("msg", "心跳包");
                oos.writeObject(object);
                oos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                socket.close();
                Client.connection_state = false;
                //断开后尝试重新连接
                Client.reconnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}