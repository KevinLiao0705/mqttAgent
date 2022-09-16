package base3;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class DemoClass extends javax.swing.JDialog {

    static public int ret_i;
    static public int ret_f = 0;
    static public String retStr = "";

    String title_str = "title_str";
    int fullScr_f = 0;
    int frameOn_f = 0;
    int winW = 1920;//1934
    int winH = 1040;//1947

    Timer tm1 = null;
    int td1_run_f = 0;
    DemoClassTd1 td1 = null;
    int td1_destroy_f = 0;

    Container cp;
    JButton[] bta1 = new JButton[16];
    JPanel pnMain, pnBar;

    //static MyLayout ly=new MyLayout();
    public DemoClass(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        DemoClass cla = this;
        if (cla.frameOn_f == 0) {
            cla.setUndecorated(true);
        }
        cla.setBounds(-100, -100, 0, 0);
    }

    public void create() {
        DemoClass cla = this;
        cla.setTitle("DemoClass");
        Font myFont = new Font("Serif", Font.BOLD, 24);
        cla.addWindowListener(new DemoClassWinLis(cla));
        DemoClassMsLis mslis = new DemoClassMsLis(cla);
        //===============================================
        cp = cla.getContentPane();
        cp.setLayout(null);
        cp.setBackground(Color.CYAN);
        pnMain = new JPanel();
        pnBar = new JPanel();
        cp.add(pnMain);
        cp.add(pnBar);
        //===============================================
        int i;
        for (i = 0; i < cla.bta1.length; i++) {
            bta1[i] = new JButton();
            bta1[i].setFont(myFont);
            bta1[i].setName(Integer.toString(0 * 256 + i));
            bta1[i].addMouseListener(mslis);
            bta1[i].setFocusable(false);
            bta1[i].setVisible(true);
            pnBar.add(bta1[i]);
        }
        bta1[15].setText("ESC");
        /*
        //==
        if (cla.tm1 == null) {
            cla.tm1 = new Timer(20, new DemoClassTm1(cla));
            cla.tm1.start();
        }
        //==
        if (cla.td1 == null) {
            cla.td1 = new DemoClassTd1(cla);
            cla.td1.start();
            cla.td1_run_f = 1;
            cla.td1_destroy_f = 0;
        }
        */

    }
}

class DemoClassWinLis extends WindowAdapter {

    DemoClass cla;

    DemoClassWinLis(DemoClass owner) {
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
        DemoClass.ret_f = 0;
        //======================================================
        if (cla.fullScr_f == 1) {
            if (cla.frameOn_f == 1) {
                r.width = screenSize.width- GB.winDialog_wm1;
                r.height = screenSize.height - GB.winDialog_hm1;
                r.x = GB.winDialog_xm1;
                r.y = GB.winDialog_ym1;;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width-GB.winDialog_wc1, r.height-GB.winDialog_hc1);
            } else {
                r.width = screenSize.width- GB.winDialog_wm2;
                r.height = screenSize.height - GB.winDialog_hm2;
                r.x = GB.winDialog_xm2;
                r.y = GB.winDialog_ym2;;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width-GB.winDialog_wc2, r.height-GB.winDialog_hc2);
            }
        } else {
            if (cla.frameOn_f == 1) {
                r.width = cla.winW;
                r.height = cla.winH;
                r.x = (screenSize.width -GB.winDialog_wm3- r.width) / 2;
                r.y = (screenSize.height-GB.winDialog_hm3 - r.height) / 2;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width-GB.winDialog_wc3, r.height-GB.winDialog_hc3);
            } else {
                r.width = cla.winW;
                r.height = cla.winH;
                r.x = (screenSize.width -GB.winDialog_wm4- r.width) / 2;
                r.y = (screenSize.height-GB.winDialog_hm4 - r.height) / 2;
                //r.y = 0;
                //r.x = 0;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width-GB.winDialog_wc4, r.height-GB.winDialog_hc4);
            }
            
            
            
            
            
            
            /*
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
            */
        }
        //=================================
        cla.pnMain.setLayout(null);
        cla.pnBar.setLayout(null);
        MyLayout.ctrA[0] = cla.pnMain;
        MyLayout.rateH = 0.9;
        MyLayout.gridLy();
        MyLayout.yst = MyLayout.yend;
        MyLayout.ctrA[0] = cla.pnBar;
        MyLayout.rateH = 1;
        MyLayout.gridLy();

        for (int i = 0; i < cla.bta1.length; i++) {
            MyLayout.ctrA[i] = cla.bta1[i];
        }
        MyLayout.eleAmt = cla.bta1.length;
        MyLayout.xc = 8;
        MyLayout.yc = MyLayout.eleAmt / MyLayout.xc;
        if ((MyLayout.eleAmt % MyLayout.xc) != 0) {
            MyLayout.yc++;
        }
        MyLayout.gridLy();

    }
}

class DemoClassMsLis extends MouseAdapter {

    int enkey_f;
    DemoClass cla;

    DemoClassMsLis(DemoClass owner) {
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
            case 0 * 256 + 15:
                cla.dispose();
                DemoClass.ret_f = 0;
                break;
            default:
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
}

class DemoClassTm1 implements ActionListener {

    String str;
    DemoClass cla;

    DemoClassTm1(DemoClass owner) {
        cla = owner;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {

        /*
        if(cla.ssk.datain_f==1)
        {
            cla.ssk.datain_f=0;
            cla.ssk.txout();
            cla.bta1[28].setText(Integer.toString(cla.timer1_cnt++ % 100));
        }    
        if(cla.timer1_cnt==30)
        {    
        }    
         */
    }

}

class DemoClassTd1 extends Thread {

    DemoClass cla;

    DemoClassTd1(DemoClass owner) {
        cla = owner;
    }

    @Override
    public void run() { // override Thread's run()
        //Test cla=Test.thisCla;
        for (;;) {

            if (cla.td1_run_f == 1) {
                //cla.bta1[30].setText("Thread "+Integer.toString(cla.timer1_cnt++%100));

            }
        }
    }
}
