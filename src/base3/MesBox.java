package base3;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MesBox extends javax.swing.JDialog {

    static public int ret_i;
    static public int ret_f = 0;
    static public String ret_s = "";

    public int reti;
    public int retf = 0;
    public String rets = "";

    String title_str = "title_str";
    int fullScr_f = 0;
    int frameOn_f = 1;
    int winW = 800;
    int winH = 0;
    //String[] selstr = new String[1024];
    ArrayList<String> mesStr = new ArrayList();
    String[] mesA = new String[0];
    int marginH = 0;

    int butType;
    int[] bounds;
    int count = 0;
    int xc = 1;
    int yc = 0;
    int xm = 10;
    int ym = 0;
    int lm = 20;
    int rm = 20;
    int tm = 5;
    int bm = 12;
    int ym1 = 15;
    int ym2 = 20;
    int iw = 0;
    int ih = 40;
    int butWidth = 160;
    int focus_f=0;

    int align = 1;

    int mesPanelCnt;
    int page = 0;
    Color bkColor = Color.CYAN;

    Container cp;
    final static int MaxMesPanelAmt = 32;
    JLabel lbTitle;
    JLabel[] lba1 = new JLabel[MaxMesPanelAmt];
    JButton[] bta1 = new JButton[3];
    int exRowCnt=0;

    //static MyLayout ly=new MyLayout();
    public MesBox(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        MesBox cla = this;
        cla.setBounds(-100, -100, 0, 0);

    }

    void windowShow() {
        MesBox cla = this;
        cla.setBounds(cla.bounds[0], cla.bounds[1], cla.bounds[2], cla.bounds[3]);
        cla.cp.setBounds(cla.bounds[4], cla.bounds[5], cla.bounds[6], cla.bounds[7]);
        paint();
    }

    public static MesBox mBox(String _title, String mes,int _butType) {
        MesBox mb = new MesBox(null, true);
        mb.bkColor = Color.CYAN;
        if (_title == null) {
            _title = "MESSAGE";
        }
        mb.title_str = _title;
        mb.butType=_butType;
        mb.mesA = mes.split("~");
        mb.create();
        mb.setVisible(true);
        return mb;
    }

    public static MesBox eBox(String _title, String mes,int _butType) {
        MesBox mb = new MesBox(null, true);
        mb.bkColor = Color.MAGENTA;
        if (_title == null) {
            _title = "ERROR";
        }
        mb.title_str = _title;
        mb.butType=_butType;
        mb.mesA = mes.split("~");
        mb.create();
        mb.setVisible(true);
        return mb;
    }

    public static MesBox wBox(String _title, String mes,int _butType) {
        MesBox mb = new MesBox(null, true);
        mb.bkColor = Color.YELLOW;
        if (_title == null) {
            _title = "WARNNING";
        }
        mb.title_str = _title;
        mb.butType=_butType;
        mb.mesA = mes.split("~");
        mb.create();
        mb.setVisible(true);
        return mb;
    }

    public static MesBox oBox(String _title, String mes,int _butType) {
        MesBox mb = new MesBox(null, true);
        mb.bkColor = Color.GREEN;
        if (_title == null) {
            _title = "MESSAGE";
        }
        mb.title_str = _title;
        mb.butType=_butType;
        mb.mesA = mes.split("~");
        mb.create();
        mb.setVisible(true);
        return mb;
    }

    public void create() {
        retf = 0;
        reti = 0;
        final MesBox cla = this;
        cla.setTitle("MesBox");
        int fontSizw = ih * 2 / 3;
        Font myFont = new Font("Serif", Font.BOLD, fontSizw);
        cla.addWindowListener(new MesBoxWinLis(cla));
        MesBoxMsLis mslis = new MesBoxMsLis(cla);
        cla.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ev) {
                cla.windowShow();
            }
        });
        exRowCnt=2;
        if(title_str.length()==0){
            exRowCnt=1;
            tm=0;
        }
        //===============================================
        if (count == 0) {
            count = mesA.length;
        }
        //===========================================================================
        if (cla.yc == 0) {
            cla.yc = cla.count / cla.xc;
            if (cla.count % cla.xc != 0) {
                cla.yc++;
            }
        }
        while (cla.yc * cla.xc >= MaxMesPanelAmt) {
            cla.yc--;
        }
        int cph;

        if (winH == 0) {
            bounds = Lib.getBounds(fullScr_f, frameOn_f, winW, 1000);
            cph = ih * (yc + exRowCnt) + tm + bm + (yc - 1) * ym + ym1 + ym2;
            winH = cph + bounds[3] - bounds[7];
        }
        bounds = Lib.getBounds(fullScr_f, frameOn_f, winW, winH);
        //===========================================================================
        for (;;) {
            cph = ih * (yc + exRowCnt) + tm + bm + (yc - 1) * ym + ym1 + ym2;
            if (cph <= bounds[7]) {
                break;
            }
            yc--;
        }
        if (fullScr_f == 0) {
            cph = ih * (yc + exRowCnt) + tm + bm + (yc - 1) * ym + ym1 + ym2;
            bounds = Lib.getBounds(fullScr_f, frameOn_f, winW, winH);
            cla.winH -= bounds[7] - cph;
            bounds = Lib.getBounds(fullScr_f, frameOn_f, winW, winH);
            cla.marginH = 0;
        } else {
            cph = ih * (yc + exRowCnt) + tm + bm + (yc - 1) * ym + ym1 + ym2;
            bounds = Lib.getBounds(fullScr_f, frameOn_f, winW, winH);
            cla.marginH = bounds[7] - cph;
        }
        mesPanelCnt = cla.yc * cla.xc;

        cp = cla.getContentPane();
        cp.setLayout(null);
        cp.setBackground(bkColor);

        //===============================================
        lbTitle = new JLabel();
        lbTitle.setFont(myFont);
        lbTitle.setHorizontalAlignment(JLabel.CENTER);
        cp.add(lbTitle);
        //===============================================
        int i;

        for (i = 0; i < mesPanelCnt; i++) {

            lba1[i] = new JLabel();
            lba1[i].setFont(myFont);
            if (align == 0) {
                lba1[i].setHorizontalAlignment(JLabel.LEFT);
            }
            if (align == 1) {
                lba1[i].setHorizontalAlignment(JLabel.CENTER);
            }
            if (align == 2) {
                lba1[i].setHorizontalAlignment(JLabel.RIGHT);
            }
            cp.add(lba1[i]);
        }

        for (i = 0; i < bta1.length; i++) {
            bta1[i] = new JButton();
            bta1[i].setFont(myFont);
            bta1[i].setName(Integer.toString(0 * 256 + i));
            //bta1[i].addMouseListener(mslis);
            bta1[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton but = (JButton) e.getSource();
                    cla.butClick(but);
                }
            });
            if(focus_f==0)
                bta1[i].setFocusable(false);
            bta1[i].setVisible(false);
            cp.add(bta1[i]);
        }
        //=======================================================
    }

    public void paint() {
        MesBox cla = this;

        cla.lbTitle.setText(cla.title_str);
        //======================================================
        MyLayout.ctrA[0] = cla.lbTitle;
        MyLayout.rateH = cla.ih;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.gridLy();
        //==
        MyLayout.yst = MyLayout.yend;
        if(exRowCnt==1){
          cla.lbTitle.setVisible(false);
            MyLayout.yst = 0;
        }  
        
        

        int eInx = cla.page * cla.xc * cla.yc;
        for (int i = 0; i < cla.mesPanelCnt; i++) {
            MyLayout.ctrA[i] = cla.lba1[i];
            if ((eInx + i) >= cla.count) {
                cla.lba1[i].setVisible(false);
                continue;
            }
            cla.lba1[i].setBackground(GB.coBt);
            cla.lba1[i].setVisible(true);
            cla.lba1[i].setForeground(Color.BLACK);
            cla.lba1[i].setText(mesA[eInx + i]);
        }

        MyLayout.eleAmt = cla.mesPanelCnt;
        MyLayout.rateH = cla.ih * cla.yc + cla.ym * (cla.yc - 1);
        MyLayout.xc = cla.xc;
        MyLayout.yc = cla.yc;
        MyLayout.tm = ym1;
        MyLayout.bm = cla.bm;
        MyLayout.ym = cla.ym;
        MyLayout.lm = cla.lm;
        MyLayout.rm = cla.rm;
        MyLayout.xm = cla.xm;
        MyLayout.gridLy();

        MyLayout.yst = MyLayout.yend + ym2;
        if (butType == 0) {
            MyLayout.ctrA[0] = cla.bta1[0];
            MyLayout.iw = butWidth;
            MyLayout.xcen = 1;
            cla.bta1[0].setVisible(true);
            cla.bta1[0].setText("ESC");
        }
        if (butType == 1) {
            MyLayout.ctrA[0] = cla.bta1[0];
            MyLayout.ctrA[1] = cla.bta1[1];
            MyLayout.xc=2;
            MyLayout.iw = butWidth;
            MyLayout.xcen = 1;
            cla.bta1[0].setVisible(true);
            cla.bta1[0].setText("NO");
            cla.bta1[1].setVisible(true);
            cla.bta1[1].setText("YES");
        }
        if (butType == 2) {
            MyLayout.ctrA[0] = cla.bta1[0];
            MyLayout.ctrA[1] = cla.bta1[1];
            MyLayout.ctrA[2] = cla.bta1[2];
            MyLayout.xc=3;
            MyLayout.iw = butWidth;
            MyLayout.xcen = 1;
            cla.bta1[0].setVisible(true);
            cla.bta1[0].setText("NO");
            cla.bta1[1].setVisible(true);
            cla.bta1[1].setText("YES");
            cla.bta1[2].setVisible(true);
            cla.bta1[2].setText("RETRY");
        }
        MyLayout.yst = MyLayout.yend;
        MyLayout.tm = ym2;
        MyLayout.bm = cla.bm;
        MyLayout.xm = 50;
        MyLayout.gridLy();

        cla.bta1[0].requestFocus();
    }

    void butClick(JButton but) {
        //JButton but = (JButton) e.getComponent();
        int index;
        String str = but.getText();
        rets = str;
        index = Integer.parseInt(but.getName());
        reti = index;
        if (reti != 0) {
            retf = 1;
        }
        MesBox.ret_f = retf;
        MesBox.ret_i = reti;
        MesBox.ret_s = rets;
        dispose();
    }

}

class MesBoxWinLis extends WindowAdapter {

    MesBox cla;

    MesBoxWinLis(MesBox owner) {
        cla = owner;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }
}

class MesBoxMsLis extends MouseAdapter {

    int enkey_f;
    MesBox cla;

    MesBoxMsLis(MesBox owner) {
        cla = owner;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int index;
        if (enkey_f != 1) {
            return;
        }
        JButton but = (JButton) e.getComponent();
         cla.butClick(but);
    }

    @Override
    public void mouseExited(MouseEvent e
    ) {
        enkey_f = 0;
    }

    @Override
    public void mousePressed(MouseEvent e
    ) {
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
