import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class Window extends Frame {
    public static final int GAME_WIDTH = 1000;  //  窗口宽
    public static final int GAME_HEIGHT = 700;  //  窗口高

    public static int count;
    public static int gameTime = 1;

    //键盘键入的数据
    public String data;
    private Random rd = new Random();
    //字符串数组
    String[] letters = new String[]{"a","b","c","d","e","f","g",
            "h","i","j","k","l","m","n",
            "o","p","q","r","s","t","u","v","w","x","y","z"};

    //抽象类 Image是所有类的父类的图形图像表示。
    Image screenImage = null;

    public void launchFrame(){
        //设置窗口参数

        //Frame方法设置框的标题
        this.setTitle("打字练习");
        //Window方法通过坐标系数设置位置
        this.setLocation(100,10);
        //Window方法设置框的宽度和高度
        this.setSize(GAME_WIDTH,GAME_HEIGHT);
        //Frame设置窗口的背景颜色
        this.setBackground(Color.green);
        //Window方法addWindowListener添加指定的窗口侦听器从该窗口接收窗口事件。
        //传入WindowAdapter用于接收窗口事件的侦听器接口。
        this.addWindowListener(new WindowAdapter() {
            //重写windowClosing方法，当用户试图关闭窗口时调用方法退出程序
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //Window方法显示窗口可见
        this.setVisible(true);
        //Frame方法设置该框架是否由用户调整大小，false为不能
        this.setResizable(false);
        //Component方法addKeyListener添加指定的键侦听器从该组件接收关键事件。
        //传入KeyMonitor接收键盘事件监听器接口（按键）。
        this.addKeyListener(new KeyMonitor());

        //启动打印字母线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    //Component方法重画部分。
                    //刷新页面,该方法首先会选择调用paint方法，然后因为是重量级组件会一直选择调用update方法，通过update()调用paint()
                    repaint();
                    try {
                        //0.01秒重画一次
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        //启动记录游戏时间线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    //新线程记录游戏时间
                    gameTime++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //重写更新容器
    //传入抽象类Graphics，所有图形上下文，允许应用程序画上，在各种设备上实现组件的基类，以及到了屏幕图像。
    @Override
    public void update(Graphics g) {
        System.out.println("update");
        if (screenImage==null){
            //Component方法创建一个屏幕的图片被用于双缓冲。指定高度和宽度
            screenImage = this.createImage(GAME_WIDTH,GAME_HEIGHT);
        }
        Graphics gScreen = screenImage.getGraphics();
        Color c = gScreen.getColor();
        gScreen.fillRect(0,0,GAME_WIDTH,GAME_HEIGHT);

        //刷新屏幕
        gScreen.setColor(c);
        //调用重写的方法paint()
        paint(gScreen);
        g.drawImage(screenImage,0,0,null);
    }

    //重写paint方法，实现打印信息和字母在屏幕里
    @Override
    public void paint(Graphics g) {
        System.out.println("paint");
        //将此图形上下文的当前颜色设置为指定的颜色。
        g.setColor(Color.gray);
        //使用指定的字符串绘制的文本，使用此图形上下文的当前字体和颜色。在这个图形中（x， y）的坐标
        g.drawString("总计："+count,20,60);
        g.drawString("时间："+gameTime+"s",20,80);
        g.drawString("字数/分钟："+count*60/gameTime,20,100);
        if (null==data) {
            //随机的字母赋值给String类型data
            data = letters[rd.nextInt(26)];
        }
        g.setColor(Color.gray);
        //将此图形上下文的字体设置为指定的字体。
        //创建指定名称的一个新的 Font，样式和大小。
        g.setFont(new Font(null,3,400));
        //绘制字母
        g.drawString(""+ data,350,500);
    }

    private class KeyMonitor extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            //判断键入的数据和随机打印的数据是否一致
            if (String.valueOf(e.getKeyChar()).equals(data)){
                data=null;
                count++;
            }
        }
    }

    public static void main( String[] args )
    {
        Window win = new Window();
        win.launchFrame();
    }
}