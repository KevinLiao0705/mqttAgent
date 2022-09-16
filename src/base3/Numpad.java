package base3;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Numpad extends javax.swing.JDialog {

    public static int ret_f;
    public static int ret_i;
    public static String ret_str;

    String title_str = "title_str";
    int fullScr_f = 0;
    int frameOn_f = 1;
    int winW = 400;
    int winH = 300;
    int float_f = 0;
    int plus_minus_f = 0;
    int vlen = 16;
    int max=9999;
    int min=0;
    String initv_str;

    Container cp;
    JLabel lb1;
    JTextField tf1;
    JButton[] bta1 = new JButton[16];
    int first_f = 1;

    //static MyLayout ly=new MyLayout();
    public Numpad(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        Numpad cla = this;
        cla.setBounds(-100, -100, 0, 0);
        cla.enableInputMethods(true);
    }

    public void create() {
        Numpad cla = this;
        cla.addWindowListener(new NumpadWinLis(cla));
        cla.setTitle("Numpad");
        Font myFont = new Font("Serif", Font.BOLD, 20);
        NumpadMsLis mslis = new NumpadMsLis(this);
        first_f = 1;
        //===============================================
        cp = cla.getContentPane();
        cp.setLayout(null);
        cp.setBackground(Color.CYAN);
        //===============================================
        lb1 = new JLabel();
        lb1.setFont(myFont);
        lb1.setHorizontalAlignment(JLabel.CENTER);
        cp.add(lb1);
        //===============================================
        tf1 = new JTextField();
        tf1.setName(Integer.toString(0 * 256 + 0));
        tf1.addMouseListener(mslis);
        tf1.setHorizontalAlignment(JTextField.CENTER);
        tf1.setOpaque(true);
        tf1.setForeground(Color.BLUE);
        //tf1.addKeyListener(keylis);
        tf1.setFont(myFont);
        cp.add(tf1);

        int i;
        for (i = 0; i < bta1.length; i++) {
            bta1[i] = new JButton();
            bta1[i].setFont(myFont);
            bta1[i].setName(Integer.toString(1 * 256 + i));
            bta1[i].addMouseListener(mslis);
            bta1[i].setVisible(false);
            cp.add(bta1[i]);
        }
        //=======================
        bta1[0].setText("7");
        bta1[1].setText("8");
        bta1[2].setText("9");
        bta1[3].setText("Back");
        //=======================
        bta1[4].setText("4");
        bta1[5].setText("5");
        bta1[6].setText("6");
        bta1[7].setText("Clear");
        //=======================
        bta1[8].setText("1");
        bta1[9].setText("2");
        bta1[10].setText("3");
        bta1[11].setText("Enter");
        //=======================
        bta1[12].setText("0");
        bta1[13].setText(".");
        bta1[14].setText("+/-");
        bta1[15].setText("Exit");

        //=======================================================
    }
}

class NumpadWinLis extends WindowAdapter {

    Numpad cla;

    NumpadWinLis(Numpad owner) {
        cla = owner;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        Rectangle r = new Rectangle();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.height -= 10;
        cla.lb1.setText(cla.title_str);
        Numpad.ret_f = 0;
        cla.tf1.setText(cla.initv_str);
        cla.first_f = 1;
        //======================================================
        if (cla.fullScr_f == 1) {
            if (cla.frameOn_f == 1) {
                r.width = screenSize.width;
                r.height = screenSize.height - GB.winDialog_bm2;
                r.x = 0;
                r.y = 0;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width - GB.winDialog_wm1, r.height - GB.winDialog_hm1);
            } else {
                r.width = screenSize.width;
                r.height = screenSize.height - GB.winFrame_bm1;
                r.x = 0;
                r.y = 0;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width - GB.winFrame_wm1, r.height - GB.winFrame_hm1);
            }
            //cla.setExtendedState(JFrame.MAXIMIZED_BOTH);
            //cla.setMaximumSize();
        } else {
            r.width = cla.winW;
            r.height = cla.winH;
            r.x = (screenSize.width - r.width) / 2;
            r.y = (screenSize.height - r.height - GB.winFrame_ch) / 2;
            cla.setBounds(r);
            cla.cp.setBounds(0, 0, r.width - GB.winFrame_wm2, r.height - GB.winFrame_hm2);
            if (cla.frameOn_f == 1) {
                cla.cp.setBounds(0, 0, r.width - GB.winFrame_wm2, r.height - GB.winFrame_hm2);
            } else {
                cla.cp.setBounds(0, 0, r.width - GB.winFrame_wm3, r.height - GB.winFrame_hm3);
            }
        }
        //=================================
        MyLayout.ctrA[0] = cla.lb1;
        MyLayout.rateH = 0.1;
        MyLayout.gridLy();
        //=================================
        MyLayout.yst = MyLayout.yend;
        MyLayout.ctrA[0] = cla.tf1;
        MyLayout.rateH = 0.14;
        MyLayout.lm = 20;
        MyLayout.rm = 20;
        MyLayout.gridLy();
        //=================================
        MyLayout.yst = MyLayout.yend;
        MyLayout.tm = 4;
        for (int i = 0; i < cla.bta1.length; i++) {
            MyLayout.ctrA[i] = cla.bta1[i];
            cla.bta1[i].setVisible(true);
        }
        MyLayout.eleAmt = 16;
        MyLayout.xc = 4;
        MyLayout.yc = 4;
        MyLayout.gridLy();

    }
}

class NumpadMsLis extends MouseAdapter {

    int enkey_f;
    Numpad cla;

    NumpadMsLis(Numpad owner) {
        cla = owner;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int index;
        if (enkey_f != 1) {
            return;
        }
        index = Integer.parseInt(e.getComponent().getName());
        switch (index) {
            //===================================
            case 1 * 256 + 0:
                editpad('7');
                break;
            case 1 * 256 + 1:
                editpad('8');
                break;
            case 1 * 256 + 2:
                editpad('9');
                break;
            case 1 * 256 + 3:
                if (cla.tf1.getText().length() == 0) {
                    break;
                }
                cla.tf1.setText(cla.tf1.getText().substring(0, cla.tf1.getText().length() - 1));
                cla.first_f = 0;
                break;
            //===================================
            case 1 * 256 + 4:
                editpad('4');
                break;
            case 1 * 256 + 5:
                editpad('5');
                break;
            case 1 * 256 + 6:
                editpad('6');
                break;
            case 1 * 256 + 7:
                cla.tf1.setText("");
                break;
            //===================================
            case 1 * 256 + 8:
                editpad('1');
                break;
            case 1 * 256 + 9:
                editpad('2');
                break;
            case 1 * 256 + 10:
                editpad('3');
                break;
            case 1 * 256 + 11: {
                try {
                    Numpad.ret_i=Lib.str2int(cla.tf1.getText(), cla.max, cla.min);
                    Numpad.ret_f = 1;
                    Numpad.ret_str = cla.tf1.getText();
                    cla.dispose();
                    break;
                } catch (Exception ex) {
                    Message mes1 = new Message(null, true);
                    mes1.mesType_i = 2;
                    mes1.title_str = ex.getMessage();
                    mes1.create();
                    mes1.setVisible(true);
                    break;

                }
            }

            //===================================
            case 1 * 256 + 12:
                editpad('0');
                break;
            case 1 * 256 + 13:
                if (cla.float_f == 0) {
                    break;
                }
                editpad('.');
                break;

            case 1 * 256 + 14:
                if (cla.plus_minus_f == 1) {
                    break;
                }
                if (cla.tf1.getText().charAt(0) == '-') {
                    cla.tf1.setText(cla.tf1.getText().substring(1));
                } else {
                    cla.tf1.setText('-' + cla.tf1.getText());
                }
                break;
            case 1 * 256 + 15:
                Numpad.ret_f = 0;
                cla.dispose();
                break;

        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        enkey_f = 0;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        enkey_f = 1;
    }
    //public void mouseClicked(MouseEvent e){} //在源组件上点击鼠标按钮
    //public void mousePressed(MouseEvent e){} //在源组件上按下鼠标按钮
    //public void mouseReleased(MouseEvent e){} //释放源组件上的鼠标按钮
    //public void mouseEntered(MouseEvent e){} //在鼠标进入源组件之后被调用
    //public void mouseExited(MouseEvent e){} //在鼠标退出源组件之后被调用
    //public void mouseDragged(MouseEvent e){} //按下按钮移动鼠标按钮之后被调用
    //public void mouseMoved(MouseEvent e){} //不按住按钮移动鼠标之后被调用

    void editpad(char ch) {
        if (cla.first_f == 1) {
            cla.first_f = 0;
            cla.tf1.setForeground(Color.BLACK);
            cla.tf1.setText("");
        }
        if (cla.tf1.getText().length() >= cla.vlen) {
            return;
        }
        if (ch <= '9' && ch >= '0') {
            cla.tf1.setText(cla.tf1.getText() + ch);
            return;
        }
        if (ch == '.') {
            cla.tf1.setText(cla.tf1.getText() + ch);
            return;
        }
    }

}
