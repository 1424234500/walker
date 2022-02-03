package com.walker.socket.client.chat.frame;

import com.walker.socket.base.MsgBuilder;
import com.walker.util.FileUtil;
import com.walker.util.ThreadUtil;
import com.walker.util.TimeUtil;
import com.walker.util.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 简易图形化控制模拟客户端 连理连接 收发消息 断开连接
 *
 * @author walker
 */
public class ClientUI<SESSION, DATA> extends JFrame implements Out {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    static int clientCount = 0;
    static int clientNum = 4;
    public JButton jbshowusers = new JButton("好友");
    public JButton jbshowrooms = new JButton("会话");
    public JButton jbtest = new JButton("auto on");
    public JCheckBox jcbscroll = new JCheckBox("锁定");

    public JButton btStart;// 启动服务器
    public JButton btSend;// 发送信息按钮
    public JButton btLogin;// 发送信息按钮
    public JTextField jtfSend;// 需要发送的文本信息
    public JTextField jtfSend1;// 需要发送的文本信息
    public JTextField jtfSend2;// 需要发送的文本信息
    public JTextArea taShow;// 信息展示
    AtomicLong count = new AtomicLong(0L);
    ScheduledFuture<?> future;
    Client<SESSION, DATA> client;

    public ClientUI(Client<SESSION, DATA> cc, String name) throws Exception {
        super(name);
        init(cc, name);
        clientCount++;
    }

    public ClientUI(Client<SESSION, DATA> cc) throws Exception {
        this(cc, "模拟客户端");
    }

    public void init(Client<SESSION, DATA> cc, String name) throws Exception {
        btStart = new JButton("关闭");
        btSend = new JButton("发送");
        btLogin = new JButton("登录");

        jtfSend = new JTextField(30);
        jtfSend1 = new JTextField(6);
        jtfSend2 = new JTextField(6);
        jtfSend1.setText("server_1");
        String key = Tools.getRandomNum(10, 99, 2);
        jtfSend2.setText(key);
        jtfSend.setText(MsgBuilder.makeMessageAllSocket("hello from " + key).toString());

        taShow = new JTextArea();


        btStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (btStart.getText().equals("启动")) {
                    btStart.setText("关闭");
                    try {
                        client.start();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        out(e1.getMessage());
                    }
                } else {
                    try {
                        client.stop(3000);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        out(e1.getMessage());
                    }
                    btStart.setText("启动");
                }
            }


        });
        btSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String mmsg = ("" + jtfSend.getText());// 写入发送流到 客户端去

                String obj = MsgBuilder.makeMessageAllSocket(mmsg).toString();

                try {
                    client.sendAutoDecode(obj);
                } catch (Exception e1) {
                    out(e1.toString(), obj);
                    e1.printStackTrace();
                }
            }
        });
        btLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String key = jtfSend2.getText();    //发往用户连接
                try {
                    client.sendAutoDecode(MsgBuilder.makeLogin(key).toString());
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        jbshowrooms.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out(client);
                try {
                    client.sendAutoDecode(MsgBuilder.testMonitor().toString());
                    client.sendAutoDecode(MsgBuilder.makeSession().toString());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        jbtest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (jbtest.getText().equals("auto on")) {
                    jbtest.setText("auto off");
                    count.set(0L);
                    future = ThreadUtil.scheduleAtFixedRate(new Runnable() {
                        public void run() {
                            count.addAndGet(1L);
                            try {
                                client.sendAutoDecode(MsgBuilder.makeMessageAllUser("up" + count.get()).toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000, 10, TimeUnit.MILLISECONDS);
                } else {
                    jbtest.setText("auto on");
                    future.cancel(true);
                }
            }
        });
        jbshowusers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out(client);
            }
        });
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    client.stop(2000);
                } catch (Exception e1) {
                    out(e1.getMessage());
                    e1.printStackTrace();
                }
            }
        });
        JPanel top = new JPanel(new FlowLayout());
        top.add(jtfSend);
        top.add(btSend);

        top.add(jtfSend1);
        top.add(jtfSend2);
        top.add(btLogin);

        top.add(btStart);
        top.add(jbshowrooms);
        top.add(jbtest);
        top.add(jbshowusers);

        jcbscroll.setSelected(true);
        top.add(jcbscroll);
        this.add(top, BorderLayout.SOUTH);
        final JScrollPane sp = new JScrollPane();
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setViewportView(this.taShow);
        this.taShow.setEditable(true);
        this.add(sp, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(10, 120 + clientCount * 300 / clientNum, 998, 280);
        this.setVisible(true);
        //	test();

        this.client = cc;
//		设定handler输出重定向到ui
        this.client.setOut(this);
        this.client.start();
    }


    @Override
    public String out(Object... objects) {
        String s = TimeUtil.getTimeSequence() + "." + Tools.objects2string(objects);
        if (s != null) {// 输出当服务端的界面上去显示
            if (s.length() > 60000)
                s = Tools.tooLongCut(s); // 太长的数据
            if (this.taShow.getText().length() >= 200000) {
                this.taShow.setText("");
            }
            this.taShow.append(FileUtil.getFileType(this.client.getClass().getName()) + " " + s + "\n");

            if (this.jcbscroll.isSelected())
                this.taShow.setCaretPosition(this.taShow.getText().length()); // 锁定最底滚动

        }
        return s;
    }
}