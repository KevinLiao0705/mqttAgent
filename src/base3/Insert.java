package base3;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Insert extends javax.swing.JDialog {

    public int reti;
    public int retf = 0;
    public String rets = "";

    int type = 0;  //0:-+,+-;1:-0,+0,

    String title_str = "title_str";
    int fullScr_f = 0;
    int frameOn_f = 1;
    int winW = 800;
    int winH = 0;

    ArrayList<String> selstrA = new ArrayList();
    ArrayList<String> selstrB = new ArrayList();
    ArrayList<String> selTmpA = new ArrayList();
    ArrayList<String> selTmpB = new ArrayList();
    int sort = 1;

    int xc = 1;
    int yc = 0;
    int xm = 0;
    int ym = 0;
    int lm = 0;
    int rm = 0;
    int tm = 0;
    int bm = 0;
    int iw = 0;
    int ih = 0;
    int escWidth = 40;
    int pageA = 0;
    int pageB = 0;
    int selPanel_cnt = 0;
    int ih2 = 0;
    int disSame_f = 1;
    int removeSelect_f = 0;

    Container cp;
    JLabel lb1;
    JButton[] bta1 = new JButton[256];
    JButton[] bta2 = new JButton[256];
    JButton btesc = new JButton();
    JButton btok = new JButton();
    JButton btupA = new JButton();
    JButton btdnA = new JButton();
    JLabel lbpgA = new JLabel();
    JButton btupB = new JButton();
    JButton btdnB = new JButton();
    JLabel lbpgB = new JLabel();
    JPanel pnA;
    JPanel pnB;

    //static MyLayout ly=new MyLayout();
    public Insert(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        Insert cla = this;
        cla.setBounds(-100, -100, 0, 0);
        cla.tm = 4;
        cla.bm = 10;
        cla.lm = 4;
        cla.rm = 4;
        cla.ym = 4;
        cla.xm = 4;
        cla.ih = 40;
        cla.iw = 200;
        cla.ih2 = cla.ih;

    }

    public void create() {
        Insert cla = this;
        cla.setTitle("Insert");
        Font myFont = new Font("Serif", Font.BOLD, 24);
        cla.addWindowListener(new InsertWinLis(cla));
        InsertMsLis mslis = new InsertMsLis(cla);
        //===============================================
        cp = cla.getContentPane();
        cp.setLayout(null);
        cp.setBackground(Color.CYAN);

        pnA = new JPanel();
        pnB = new JPanel();
        pnA.setBackground(Color.white);
        pnB.setBackground(Color.white);
        cp.add(pnA);
        cp.add(pnB);
        //===============================================
        lb1 = new JLabel();
        lb1.setFont(myFont);
        lb1.setHorizontalAlignment(JLabel.CENTER);
        cp.add(lb1);
        while (cla.yc * cla.xc >= 256) {
            cla.yc--;
        }
        selPanel_cnt = cla.yc * cla.xc;
        //===============================================
        int i;
        for (i = 0; i < selPanel_cnt; i++) {
            bta1[i] = new JButton();
            bta1[i].setFont(myFont);
            bta1[i].setName(Integer.toString(1 * 256 + i));
            bta1[i].addMouseListener(mslis);
            bta1[i].setFocusable(false);
            bta1[i].setVisible(true);
            pnA.add(bta1[i]);
        }

        //===============================================
        for (i = 0; i < selPanel_cnt; i++) {
            bta2[i] = new JButton();
            bta2[i].setFont(myFont);
            bta2[i].setName(Integer.toString(2 * 256 + i));
            bta2[i].addMouseListener(mslis);
            bta2[i].setFocusable(false);
            bta2[i].setVisible(true);
            pnB.add(bta2[i]);
        }

        btesc = new JButton();
        btesc.setFont(myFont);
        btesc.setName(Integer.toString(0 * 256 + 0));
        btesc.addMouseListener(mslis);
        btesc.setVisible(true);
        btesc.setFocusable(false);
        btesc.setText("ESC");
        cp.add(btesc);

        btok = new JButton();
        btok.setFont(myFont);
        btok.setName(Integer.toString(0 * 256 + 1));
        btok.addMouseListener(mslis);
        btok.setVisible(true);
        btok.setFocusable(false);
        btok.setText("SAVE");
        cp.add(btok);

        btupA = new JButton();
        btupA.setFont(myFont);
        btupA.setName(Integer.toString(0 * 256 + 2));
        btupA.addMouseListener(mslis);
        btupA.setFocusable(false);
        btupA.setText("Page Up");
        cp.add(btupA);
        btdnA = new JButton();
        btdnA.setFont(myFont);
        btdnA.setName(Integer.toString(0 * 256 + 3));
        btdnA.addMouseListener(mslis);
        btdnA.setFocusable(false);
        btdnA.setText("Page Down");
        cp.add(btdnA);

        lbpgA.setFont(myFont);
        lbpgA.setHorizontalAlignment(SwingConstants.CENTER);
        cp.add(lbpgA);

        lbpgB.setFont(myFont);
        lbpgB.setHorizontalAlignment(SwingConstants.CENTER);
        cp.add(lbpgB);

        btupB = new JButton();
        btupB.setFont(myFont);
        btupB.setName(Integer.toString(0 * 256 + 4));
        btupB.addMouseListener(mslis);
        btupB.setFocusable(false);
        btupB.setText("Page Up");
        cp.add(btupB);
        btdnB = new JButton();
        btdnB.setFont(myFont);
        btdnB.setName(Integer.toString(0 * 256 + 5));
        btdnB.addMouseListener(mslis);
        btdnB.setFocusable(false);
        btdnB.setText("Page Down");
        cp.add(btdnB);

        lbpgB.setFont(myFont);
        cp.add(lbpgB);

        //=======================================================
    }

    public void paint() {
        Insert cla = this;
        Rectangle r = new Rectangle();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        cla.lb1.setText(cla.title_str);
        cla.retf = 0;
        int cph = 0;
        int cpw = 0;
        String str;
        String[] strA;
        //======================================================
        //cla.winH=ih*(cla.yc+2)+tm*2+bm*2+xm*(yc+1); 

        //======================================================
        if (cla.fullScr_f == 1) {
            if (cla.frameOn_f == 1) {
                r.width = screenSize.width - GB.winDialog_wm1;
                r.height = screenSize.height - GB.winDialog_hm1;
                r.x = GB.winDialog_xm1;
                r.y = GB.winDialog_ym1;;
                cla.setBounds(r);
                cph = r.height - GB.winDialog_hc1;
                cpw = r.width - GB.winDialog_wc1;
                cla.cp.setBounds(0, 0, cpw, cph);
            } else {
                r.width = screenSize.width - GB.winDialog_wm2;
                r.height = screenSize.height - GB.winDialog_hm2;
                r.x = GB.winDialog_xm2;
                r.y = GB.winDialog_ym2;;
                cla.setBounds(r);
                cph = r.height - GB.winDialog_hc2;
                cpw = r.width - GB.winDialog_wc2;
                cla.cp.setBounds(0, 0, cpw, cph);

            }
        } else {
            if (cla.frameOn_f == 1) {
                r.width = cla.winW;
                r.height = cla.winH;
                if (r.height == 0) {
                    r.height = ih * (cla.yc + 2) + tm * 3 + bm * 1 + ym * (yc + 1) + GB.winDialog_hc3;
                }
                r.x = (screenSize.width - GB.winDialog_wm3 - r.width) / 2;
                r.y = (screenSize.height - GB.winDialog_hm3 - r.height) / 2;
                cla.setBounds(r);
                cph = r.height - GB.winDialog_hc3;
                cpw = r.width - GB.winDialog_wc3;
                cla.cp.setBounds(0, 0, cpw, cph);
            } else {
                r.width = cla.winW;
                r.height = cla.winH;
                if (r.height == 0) {
                    r.height = ih * (cla.yc + 2) + tm * 3 + bm * 1 + ym * (yc + 1) + GB.winDialog_hc4;
                }
                r.x = (screenSize.width - GB.winDialog_wm4 - r.width) / 2;
                r.y = (screenSize.height - GB.winDialog_hm4 - r.height) / 2;
                cla.setBounds(r);
                cph = r.height - GB.winDialog_hc4;
                cpw = r.width - GB.winDialog_wc4;
                cla.cp.setBounds(0, 0, cpw, cph);
            }
        }
        //cla.pn1.setLayout(null);
        //=================================
        cla.pnA.setLayout(null);
        cla.pnB.setLayout(null);

        MyLayout.ctrA[0] = cla.lb1;
        //MyLayout.rateH = cla.ih+cla.tm;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.ih = cla.ih;
        MyLayout.gridLy();
        //==
        MyLayout.yst = MyLayout.yend;

        MyLayout.ctrA[0] = cla.pnA;
        MyLayout.ctrA[1] = cla.pnB;
        MyLayout.yst = MyLayout.yend;
        MyLayout.rateH = cph - ih * 2 - 3 * cla.tm - cla.bm;

        MyLayout.eleAmt = 2;
        MyLayout.xc = 2;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.ym = 0;
        MyLayout.lm = 2;
        MyLayout.rm = 2;
        MyLayout.xm = 10;
        MyLayout.gridLy();

        MyLayout.yst = MyLayout.yend;
        MyLayout.ctrA[0] = cla.lbpgA;
        MyLayout.ctrA[1] = cla.btupA;
        MyLayout.ctrA[2] = cla.btdnA;
        MyLayout.ctrA[3] = cla.btesc;
        MyLayout.ctrA[4] = cla.btok;
        MyLayout.ctrA[5] = cla.btupB;
        MyLayout.ctrA[6] = cla.btdnB;
        MyLayout.ctrA[7] = cla.lbpgB;

        MyLayout.ofw[2] = 120;
        MyLayout.ofw[4] = 120;

        MyLayout.eleAmt = 8;
        MyLayout.xc = 8;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.rm = 10;
        MyLayout.lm = 10;
        MyLayout.xm = 10;
        MyLayout.ih = cla.ih;
        MyLayout.gridLy();

        Rectangle rc;

        rc = cla.btupA.getBounds();
        rc.height = 32;
        cla.btupA.setBounds(rc);
        rc = cla.btdnA.getBounds();
        rc.height = 32;
        cla.btdnA.setBounds(rc);

        rc = cla.btupB.getBounds();
        rc.height = 32;
        cla.btupB.setBounds(rc);
        rc = cla.btdnB.getBounds();
        rc.height = 32;
        cla.btdnB.setBounds(rc);

        selTmpA.clear();
        for (int i = 0; i < cla.selstrA.size(); i++) {
            selTmpA.add(cla.selstrA.get(i) + "<~>" + i);
        }
        if (cla.sort != 0) {
            Collections.sort(selTmpA);
        }

        int eInx = cla.pageA * cla.xc * cla.yc;
        for (int i = 0; i < cla.selPanel_cnt; i++) {
            MyLayout.ctrA[i] = cla.bta1[i];
            if ((eInx + i) >= cla.selstrA.size()) {
                cla.bta1[i].setVisible(false);
                continue;
            }
            str = selTmpA.get(eInx + i);
            strA = str.split("<~>");
            cla.bta1[i].setText(strA[0]);
            cla.bta1[i].setVisible(true);
        }

        MyLayout.eleAmt = cla.selPanel_cnt;
        MyLayout.xc = cla.xc;
        MyLayout.yc = cla.yc;
        MyLayout.tm = 0;
        MyLayout.bm = 0;
        MyLayout.ym = cla.ym;
        MyLayout.lm = cla.lm;
        MyLayout.rm = cla.rm;
        MyLayout.xm = cla.xm;
        MyLayout.gridLy();

        selTmpB.clear();
        for (int i = 0; i < cla.selstrB.size(); i++) {
            selTmpB.add(cla.selstrB.get(i) + "<~>" + i);
        }
        if (cla.sort != 0) {
            Collections.sort(selTmpB);
        }

        eInx = cla.pageB * cla.xc * cla.yc;
        for (int i = 0; i < cla.selPanel_cnt; i++) {
            MyLayout.ctrA[i] = cla.bta2[i];
            if ((eInx + i) >= cla.selstrB.size()) {
                cla.bta2[i].setVisible(false);
                continue;
            }
            str = selTmpB.get(eInx + i);
            strA = str.split("<~>");
            cla.bta2[i].setText(strA[0]);
            cla.bta2[i].setVisible(true);
        }

        MyLayout.eleAmt = cla.selPanel_cnt;
        MyLayout.xc = cla.xc;
        MyLayout.yc = cla.yc;
        MyLayout.tm = 0;
        MyLayout.bm = 0;
        MyLayout.ym = cla.ym;
        MyLayout.lm = cla.lm;
        MyLayout.rm = cla.rm;
        MyLayout.xm = cla.xm;
        MyLayout.gridLy();

        if (cla.pageA == 0) {
            cla.btupA.setEnabled(false);
        } else {
            cla.btupA.setEnabled(true);
        }

        if (((cla.pageA + 1) * cla.xc * cla.yc) >= cla.selstrA.size()) {
            cla.btdnA.setEnabled(false);
        } else {
            cla.btdnA.setEnabled(true);
        }

        if (cla.pageB == 0) {
            cla.btupB.setEnabled(false);
        } else {
            cla.btupB.setEnabled(true);
        }

        if (((cla.pageB + 1) * cla.xc * cla.yc) >= cla.selstrB.size()) {
            cla.btdnB.setEnabled(false);
        } else {
            cla.btdnB.setEnabled(true);
        }
        int ibuf;
        ibuf = (int) ((cla.selstrA.size() - 0.1) / (cla.xc * cla.yc));
        ibuf++;
        str = "" + (pageA + 1) + " / " + ibuf;
        cla.lbpgA.setText(str);

        ibuf = (int) ((cla.selstrB.size() - 0.1) / (cla.xc * cla.yc));
        ibuf++;
        str = "" + (pageB + 1) + " / " + ibuf;
        cla.lbpgB.setText(str);

    }
}

class InsertWinLis extends WindowAdapter {

    Insert cla;

    InsertWinLis(Insert owner) {
        cla = owner;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        cla.paint();
    }
}

class InsertMsLis extends MouseAdapter {

    int enkey_f;
    Insert cla;

    InsertMsLis(Insert owner) {
        cla = owner;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int index;
        String str;
        if (enkey_f != 1) {
            return;
        }
        index = Integer.parseInt(e.getComponent().getName());
        switch (index) {
            case 0 * 256 + 0:
                cla.dispose();
                cla.retf = 0;
                break;
            case 0 * 256 + 1:
                cla.dispose();
                cla.retf = 1;
                break;
            case 0 * 256 + 2:
                if (cla.pageA <= 0) {
                    break;
                }
                cla.pageA--;
                cla.paint();
                break;
            case 0 * 256 + 3:
                if (((cla.pageA + 1) * cla.xc * cla.yc) >= cla.selstrA.size()) {
                    break;
                }
                cla.pageA++;
                cla.paint();
                break;
            case 0 * 256 + 4:
                if (cla.pageB <= 0) {
                    break;
                }
                cla.pageB--;
                cla.paint();
                break;
            case 0 * 256 + 5:
                if (((cla.pageB + 1) * cla.xc * cla.yc) >= cla.selstrB.size()) {
                    break;
                }
                cla.pageB++;
                cla.paint();
                break;
            default:
                if ((index / 256) == 1) {
                    index = index % 256;
                    cla.reti = cla.pageA * cla.xc * cla.yc + index;
                    str = cla.selTmpA.get(cla.reti);
                    String[] strA;
                    strA = str.split("<~>");
                    if (strA.length == 2) {
                        cla.reti = Integer.parseInt(strA[1]);
                    }
                    cla.rets = cla.selstrA.get(cla.reti);
                    //cla.selstrB.add(cla.rets);
                    for (int i = 0; i < cla.selstrA.size(); i++) {
                        if (cla.selstrA.get(i).equals(cla.rets)) {
                            cla.selstrA.remove(i);
                            break;
                        }
                    }
                    //cla.pageB=(int)((cla.selstrB.size()-0.1)/(cla.xc*cla.yc));
                    cla.paint();
                    break;
                }
                if ((index / 256) == 2) {
                    index = index % 256;
                    cla.reti = cla.pageB * cla.xc * cla.yc + index;
                    str = cla.selTmpB.get(cla.reti);
                    String[] strA;
                    strA = str.split("<~>");
                    if (strA.length == 2) {
                        cla.reti = Integer.parseInt(strA[1]);
                    }
                    cla.rets = cla.selstrB.get(cla.reti);
                    int same_f = 0;
                    if (cla.disSame_f == 1) {
                        for (int i = 0; i < cla.selstrA.size(); i++) {
                            if (cla.selstrA.get(i).equals(cla.rets)) {
                                same_f = 1;
                                break;
                            }
                        }
                        if (same_f == 1) {
                            break;
                        }
                    }
                    cla.selstrA.add(cla.rets);

                    if (cla.removeSelect_f == 1) {
                        for (int i = 0; i < cla.selstrB.size(); i++) {
                            if (cla.selstrB.get(i).equals(cla.rets)) {
                                cla.selstrB.remove(i);
                                break;
                            }
                        }
                    }
                    cla.pageA = (int) ((cla.selstrA.size() - 0.1) / (cla.xc * cla.yc));
                    cla.paint();
                }
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
