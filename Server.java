import org.json.simple.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        try {
            //创建一个服务器套接字，绑定到指定的9999端口。
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("socket服务器开始运行.....");
            //接受多个客户的请求，每个客户都有自己的线程
            while(true){
                //socket连接，服务器接受客户请求产生的,会一直等待连接成功为止
                Socket socket = serverSocket.accept();
                System.out.println("连接成功...");
                //传入socket套接字连接，启动Server_listen线程
                new Thread(new Server_listen(socket)).start();
                //启动Server_send线程
                new Thread(new Server_send(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//监听类(接受客户端数据)
class Server_listen implements Runnable{
    //操作数据都要经过socket连接
    private Socket socket;

    //默认权限
    Server_listen(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //对象输入流,传入节点流
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //保持随时监听
            while (true){
                //打印监听数据
                System.out.println(ois.readObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                //手动断开socket连接
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

//服务器发送数据给客户端
class Server_send implements Runnable{
    private Socket socket;

    Server_send(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //对象输出流，从socket中获取节点流
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //获取屏幕上输入的信息
            Scanner scanner = new Scanner(System.in);
            while (true){
                System.out.print("请输入要发送的内容:");
                //读取一行输入的内容存到string
                String string = scanner.nextLine();
                //利用JSON传递内容，可以区分传递的内容类别
                JSONObject object = new JSONObject();
                //自定义类型是chat类型
                object.put("type","chat");
                object.put("msg", string);
                //将发送的内容传入客户端
                oos.writeObject(object);
                //刷新
                oos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
