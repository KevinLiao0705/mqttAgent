/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base3;

/**
 *
 * @author kevin
 */
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SetPanel extends javax.swing.JDialog {

    static public String version = "2.0";

    static public int ret_i;
    static public int ret_f = 0;
    static public String retStr = "";

    String title_str = "title_str";
    int fullScr_f = 0;
    int frameOn_f = 1;
    int winW = 800;
    int winH = 400;
    int firstShow_f = 0;
    int setPanelCnt = 0;
    int count = 0;
    int xc = 1;
    int yc = 0;
    int xm = 2;
    int ym = 6;
    int lm = 20;
    int rm = 20;
    int tm = 10;
    int bm = 10;
    int iw = 100;
    int ih = 40;
    int escWidth = 40;
    int selectAll = -1;
    double nameWideRate = 0.7;
    int keyCnt = 2;
    Container cp;
    JLabel lb1;
    int nameAlign = 0;
    int valueAlign = 1;
    int showNo = 0;
    int[] bounds;
    int marginH = 0;
    int onlyView_f=0;

    final static int MaxSetPanelAmt = 32;

    JButton[] bta1 = new JButton[MaxSetPanelAmt * 4];
    //public JTextField[] tfa1 = new JTextField[MaxSetPanelAmt * 4];
    JButton[] bta2 = new JButton[MaxSetPanelAmt * 4];
    public JPasswordField[] tfa1 = new JPasswordField[MaxSetPanelAmt * 4];

    //jPasswordField1.setEchoChar('*')
    JButton btesc;
    JButton btSave;
    JButton btup;
    JButton btdn;
    JLabel lbpg;
    JLabel lbitem;

    int page = 0;
    int pageAll = 1;

    //JPanel pn1;
    JPanel[] pnItems = new JPanel[MaxSetPanelAmt];
    ArrayList<MyData> list = new ArrayList<>();

    ArrayList<String> orgValue = new ArrayList<>();
    ArrayList<String> setValue = new ArrayList<>();

    JButton[] bta3 = new JButton[5];

    int editNew = 0;
    int edit_f = 0;
    int delete_f = 0;
    int swap_f = 0;
    int save_f = 0;
    int swapSel = -1;
    //==
    int editDisable_f = 0;
    int newDisable_f = 0;
    int deleteDisable_f = 0;
    int swapDisable_f = 0;
    int saveDisable_f = 0;

    StringCbk cbkNew;
    StringCbk cbkDelete;

    //static MyLayout ly=new MyLayout();
    public SetPanel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        SetPanel cla = this;
        cla.setBounds(-100, -100, 0, 0);
        /*
        setListAddJson("{'desc':'dsfsdfsd 明子','name':'明子','value':'KKK屋','max':123.0,'min':12,'enu':[ 12 , 4.5 , 67 ]}");
        setListAddJson("{'desc':'vxcxcgdsfsdfsd','name':'sssenu','value':'KdddKK屋','enu':[ '12' , '4.5' , '67' ]}");
        list.add(new MyData("name:vvvbaa;type:string;enu:qw~er~yu", "ewew"));
        list.add(new MyData("name1", 12));
        list.add(new MyData("name2", 23.6));
        list.add(new MyData("name3", "value1"));
        list.add(new MyData("name4", "value1"));
        list.add(new MyData("name5", "value1"));
         */

    }

    public void setListAddJson(String jsonStr) {
        MyData md = null;
        md = MyData.anaJson(jsonStr);
        if (md == null) {
            return;
        }
        this.list.add(md);
    }

    public void create() {
        final SetPanel cla = this;
        cla.setTitle("SetPanel");
        Font myFont = new Font("Serif", Font.BOLD, 20);
        cla.addWindowListener(new SetPanelWinLis(cla));
        SetPanelMsLis mslis = new SetPanelMsLis(cla);
        cla.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ev) {
                cla.windowShow();
            }
        });

        SetPanel.ret_f = 0;
        if (count == 0) {
            count = list.size();
        }
        //===========================================================================
        if (cla.yc == 0) {
            cla.yc = cla.count / cla.xc;
            if (cla.count % cla.xc != 0) {
                cla.yc++;
            }
        }
        while (cla.yc * cla.xc >= MaxSetPanelAmt) {
            cla.yc--;
        }
        if (winH == 0) {
            bounds = Lib.getBounds(fullScr_f, frameOn_f, winW, 1000);
            int cph = cla.ih * (cla.yc + 2) + 3 * cla.tm + cla.bm + (cla.yc - 1) * cla.ym;
            if (editNew != 0) {
                cph += cla.ih + cla.tm;
            }
            winH = cph + bounds[3] - bounds[7];
        }
        bounds = Lib.getBounds(fullScr_f, frameOn_f, winW, winH);
        //===========================================================================
        for (;;) {
            int cph = cla.ih * (cla.yc + 2) + 3 * cla.tm + cla.bm + (cla.yc - 1) * cla.ym;
            if (editNew != 0) {
                cph += cla.ih + cla.tm;
            }
            if (cph <= bounds[7]) {
                break;
            }
            cla.yc--;
        }
        if (fullScr_f == 0) {
            bounds = Lib.getBounds(fullScr_f, frameOn_f, winW, winH);
            int cpH = cla.ih * (cla.yc + 2) + 3 * cla.tm + cla.bm + (cla.yc - 1) * cla.ym;
            if (editNew != 0) {
                cpH += cla.ih + cla.tm;
            }
            cla.winH -= bounds[7] - cpH;
            bounds = Lib.getBounds(fullScr_f, frameOn_f, winW, winH);
            cla.marginH = 0;
        } else {
            bounds = Lib.getBounds(fullScr_f, frameOn_f, winW, winH);
            int cpH = cla.ih * (cla.yc + 2) + 3 * cla.tm + cla.bm + (cla.yc - 1) * cla.ym;
            if (editNew != 0) {
                cpH += cla.ih + cla.tm;
            }
            cla.marginH = bounds[7] - cpH;
        }
        setPanelCnt = cla.yc * cla.xc;
        //===============================================
        cp = cla.getContentPane();
        cp.setLayout(null);
        cp.setBackground(Color.CYAN);
        //===============================================
        lb1 = new JLabel();
        lb1.setFont(myFont);
        lb1.setHorizontalAlignment(JLabel.CENTER);
        cla.lb1.setText(cla.title_str);

        cp.add(lb1);

        //pn1=new JPanel();
        //pn1.setBackground(Color.CYAN);
        //cp.add(pn1);
        lbpg = new JLabel();
        lbpg.setFont(myFont);
        lbpg.setHorizontalAlignment(JLabel.LEFT);
        cp.add(lbpg);

        lbitem = new JLabel();
        lbitem.setFont(myFont);
        lbitem.setHorizontalAlignment(JLabel.RIGHT);
        cp.add(lbitem);

        //===============================================
        FocusListener defaultFocusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField tf = (JTextField) e.getComponent();
                tf.selectAll();

            }

            @Override
            public void focusLost(FocusEvent e) {
                // Your code ...
            }
        };

        KeyAdapter keyAdapter = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char ch = e.getKeyChar();
                int chi = e.getKeyCode();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    int index = Integer.parseInt(e.getComponent().getName());
                    int inx = index / 256;
                    int pInx = index & 256;
                    if (list.size() == 1) {
                        mouseFunc(99 * 256 + 2);
                    }
                }
            }
        };

        int i, j;
        String str;

        for (i = 0; i < setPanelCnt; i++) {
            pnItems[i] = new JPanel();
            pnItems[i].setLayout(null);
            pnItems[i].setBackground(Color.CYAN);
            //===========================    
            bta1[i * 4 + 0] = new JButton();
            bta1[i * 4 + 0].setFont(myFont);
            bta1[i * 4 + 0].setName(Integer.toString(i * 256 + 0));
            bta1[i * 4 + 0].addMouseListener(mslis);
            bta1[i * 4 + 0].setVisible(true);
            bta1[i * 4 + 0].setFocusable(false);
            bta1[i * 4 + 0].setBackground(Color.LIGHT_GRAY);
            if (nameAlign == 0) {
                bta1[i * 4 + 0].setHorizontalAlignment(SwingConstants.LEFT);
            }
            if (nameAlign == 1) {
                bta1[i * 4 + 0].setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (nameAlign == 2) {
                bta1[i * 4 + 0].setHorizontalAlignment(SwingConstants.RIGHT);
            }

            /*
            bta1[i * 4 + 0].setText(list.get(i).name);
            str = "";
            if (!list.get(i).desc.equals("")) {
                str += " " + list.get(i).desc;
            }
            if (list.get(i).max > list.get(i).min) {
                str += " max= " + list.get(i).max;
                str += " min=  " + list.get(i).min;
            }
            bta1[i * 4 + 0].setToolTipText(str);
             */
            pnItems[i].add(bta1[i * 4 + 0]);
            //===========================

            tfa1[i * 4 + 1] = new JPasswordField();
            tfa1[i * 4 + 1].setFont(myFont);
            tfa1[i * 4 + 1].setName(Integer.toString(i * 256 + 1));
            tfa1[i * 4 + 1].addMouseListener(mslis);
            tfa1[i * 4 + 1].addFocusListener(defaultFocusListener);
            tfa1[i * 4 + 1].addKeyListener(keyAdapter);
            tfa1[i * 4 + 1].setEchoChar((char) 0);
            tfa1[i * 4 + 1].enableInputMethods(true);

            tfa1[i * 4 + 1].setVisible(true);
            //tfa1[i * 4 + 1].setFocusable(false);
            tfa1[i * 4 + 1].setHorizontalAlignment(SwingConstants.CENTER);
            if (valueAlign == 0) {
                tfa1[i * 4 + 1].setHorizontalAlignment(SwingConstants.LEFT);
            }
            if (valueAlign == 1) {
                tfa1[i * 4 + 1].setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (valueAlign == 2) {
                tfa1[i * 4 + 1].setHorizontalAlignment(SwingConstants.RIGHT);
            }

            /*
            if (list.get(i).passw == 0) {
                tfa1[i * 4 + 1].setText(list.get(i).value.toString());
            } else {
                tfa1[i * 4 + 1].setText(Lib.createString('*', list.get(i).value.toString().length()));
            }

            if (list.get(i).edit == 0) {
                tfa1[i * 4 + 1].setFocusable(false);
            }

            if (list.get(i).type.equals("double")) {
                tfa1[i * 4 + 1].setForeground(Color.red);
            }
            if (list.get(i).type.equals("doubleArray")) {
                tfa1[i * 4 + 1].setForeground(Color.red);
            }
            if (list.get(i).type.equals("int")) {
                tfa1[i * 4 + 1].setForeground(Color.blue);
            }
            if (list.get(i).type.equals("intArray")) {
                tfa1[i * 4 + 1].setForeground(Color.blue);
            }
             */
            pnItems[i].add(tfa1[i * 4 + 1]);
            //===========================
            bta2[i * 4 + 2] = new JButton();
            bta2[i * 4 + 2].setFont(new Font("Serif", Font.PLAIN, 9));
            bta2[i * 4 + 2].setName(Integer.toString(i * 256 + 2));
            bta2[i * 4 + 2].addMouseListener(mslis);
            bta2[i * 4 + 2].setVisible(true);
            bta2[i * 4 + 2].setFocusable(false);
            bta2[i * 4 + 2].setText("");
            //bta2[i * 4 + 2].setBackground(Color.YELLOW);
            pnItems[i].add(bta2[i * 4 + 2]);
            cp.add(pnItems[i]);
        }

        for (i = 0; i < bta3.length; i++) {
            bta3[i] = new JButton();
            bta3[i].setFont(myFont);
            bta3[i].setName(Integer.toString(98 * 256 + i));
            bta3[i].addMouseListener(mslis);
            bta3[i].setFocusable(false);
            bta3[i].setVisible(true);
            cp.add(bta3[i]);
        }
        bta3[0].setText("Edit");
        bta3[1].setText("New");
        bta3[2].setText("Delete");
        bta3[3].setText("Swap");
        bta3[4].setText("Save");

        if (editDisable_f == 1) {
            bta3[0].setEnabled(false);
        }
        if (newDisable_f == 1) {
            bta3[1].setEnabled(false);
        }
        if (deleteDisable_f == 1) {
            bta3[2].setEnabled(false);
        }
        if (swapDisable_f == 1) {
            bta3[3].setEnabled(false);
        }
        if (saveDisable_f == 1) {
            bta3[4].setEnabled(false);
        }

        btup = new JButton();
        btup.setFont(myFont);
        btup.setName(Integer.toString(99 * 256 + 0));
        btup.addMouseListener(mslis);
        btup.setVisible(true);
        btup.setFocusable(false);
        btup.setText("UP");
        cp.add(btup);
        //=======================================================
        btdn = new JButton();
        btdn.setFont(myFont);
        btdn.setName(Integer.toString(99 * 256 + 3));
        btdn.addMouseListener(mslis);
        btdn.setVisible(true);
        btdn.setFocusable(false);
        btdn.setText("DOWN");
        cp.add(btdn);
        //=======================================================
        btesc = new JButton();
        btesc.setFont(myFont);
        btesc.setName(Integer.toString(99 * 256 + 1));
        btesc.addMouseListener(mslis);
        btesc.setVisible(true);
        btesc.setFocusable(false);
        btesc.setText("ESC");
        cp.add(btesc);
        //=======================================================
        btSave = new JButton();
        btSave.setFont(myFont);
        btSave.setName(Integer.toString(99 * 256 + 2));
        btSave.addMouseListener(mslis);
        btSave.setVisible(true);
        btSave.setFocusable(false);
        btSave.setText("OK");
        cp.add(btSave);
        renewCount();
        loadSetValue();
    }

    void addItem(MyData md){
        list.add(md);
        setValue.add(list.get(list.size()-1).value.toString());
        renewCount();
    }
    void delItem(int inx){
        list.remove(inx);
        setValue.remove(inx);
        renewCount();
    }
    void loadSetValue(){
        int i;
        orgValue.clear();
        setValue.clear();
        for (i = 0; i < list.size(); i++) {
            orgValue.add(list.get(i).value.toString());
            setValue.add(list.get(i).value.toString());
        }
        
    }
    
    void renewCount() {
        SetPanel cla = this;
        int i;
        cla.count = list.size();
        if (cla.count <= cla.yc * cla.xc) {
            btup.setVisible(false);
            btdn.setVisible(false);
            lbpg.setVisible(false);
            lbitem.setVisible(false);
        } else {
            btup.setVisible(true);
            btdn.setVisible(true);
            lbpg.setVisible(true);
            lbitem.setVisible(true);
        }

        pageAll = (int) (list.size() - 0.5) / (cla.xc * cla.yc);
        pageAll += 1;
    }

    void saveSetValue() {
        String str;
        SetPanel cla = this;
        int eInx = cla.page * cla.xc * cla.yc;
        for (int i = 0; i < cla.setPanelCnt; i++) {
            if ((eInx + i) >= cla.count) {
                continue;
            }
            String myPass = String.valueOf(tfa1[i * 4 + 1].getPassword());
            setValue.set((eInx + i), myPass);
        }
    }

    void paint() {
        String str;
        SetPanel cla = this;
        MyLayout.ctrA[0] = cla.lb1;
        MyLayout.rateH = cla.ih;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.gridLy();
        //==
        MyLayout.yst = MyLayout.yend;
        for (int i = 0; i < cla.setPanelCnt; i++) {
            MyLayout.ctrA[i] = cla.pnItems[i];
        }
        MyLayout.eleAmt = cla.setPanelCnt;
        MyLayout.rateH = cla.ih * cla.yc + cla.ym * (cla.yc - 1);

        MyLayout.xc = cla.xc;
        MyLayout.yc = cla.yc;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.ym = cla.ym;
        MyLayout.lm = cla.lm;
        MyLayout.rm = cla.rm;
        MyLayout.xm = cla.xm;
        MyLayout.gridLy();

        if (cla.marginH > 0) {
            MyLayout.yst = MyLayout.yend;
            MyLayout.rateH = cla.marginH;
            MyLayout.ctrA[1] = cla.lb1;
            MyLayout.tm = 0;
            MyLayout.bm = 0;
            MyLayout.gridLy();
        }

        if (editNew == 1) {
            MyLayout.yst = MyLayout.yend;
            for (int i = 0; i < cla.bta3.length; i++) {
                MyLayout.ctrA[i + 1] = cla.bta3[i];
            }
            MyLayout.rateH = cla.ih;
            MyLayout.eleAmt = cla.bta3.length + 2;
            MyLayout.xc = cla.bta3.length + 2;
            MyLayout.tm = cla.tm;
            MyLayout.bm = cla.bm;
            MyLayout.rm = 10;
            MyLayout.lm = 10;
            MyLayout.xm = 10;
            MyLayout.gridLy();
        }

        MyLayout.yst = MyLayout.yend;

        MyLayout.ctrA[0] = cla.lbpg;
        MyLayout.ctrA[1] = cla.btup;
        MyLayout.ctrA[2] = cla.btesc;
        MyLayout.ctrA[3] = cla.btSave;
        MyLayout.ctrA[4] = cla.btdn;
        MyLayout.ctrA[5] = cla.lbitem;
        MyLayout.eleAmt = 6;
        MyLayout.xc = 6;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.rm = 10;
        MyLayout.lm = 10;
        MyLayout.xm = 10;
        MyLayout.gridLy();

        /*
        MyLayout.ctrA[0] = cla.btesc;
        MyLayout.ctrA[1] = cla.btSave;
        MyLayout.eleAmt = cla.keyCnt;
        MyLayout.xcen = 1;
        MyLayout.xc = cla.keyCnt;
        MyLayout.xm = 20;
        MyLayout.iw = cla.iw;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.gridLy();
         */
        int eInx = cla.page * cla.xc * cla.yc;
        for (int i = 0; i < cla.setPanelCnt; i++) {

            if ((eInx + i) >= cla.count) {
                cla.pnItems[i].setVisible(false);
                continue;
            }
            cla.pnItems[i].setVisible(true);

            MyLayout.ctrA[0] = cla.bta1[i * 4 + 0];
            MyLayout.rateW = cla.nameWideRate;
            if (cla.list.get(eInx + i).nameWRate != 0) {
                MyLayout.rateW = cla.list.get(eInx + i).nameWRate;
            }

            MyLayout.gridLy();
            //
            
            
            
            if (cla.list.get(eInx + i).enu == null) {
                MyLayout.ctrA[0] = cla.tfa1[i * 4 + 1];
                MyLayout.xst = MyLayout.xend;
                MyLayout.rateW = 1;
                MyLayout.gridLy();
                if (selectAll == i) {
                    cla.tfa1[i * 4 + 1].selectAll();
                }
            } else {
                MyLayout.ctrA[0] = cla.tfa1[i * 4 + 1];
                MyLayout.xst = MyLayout.xend;
                MyLayout.rateW = -24;
                MyLayout.gridLy();
                MyLayout.lm = 0;
                MyLayout.ctrA[0] = cla.bta2[i * 4 + 2];
                MyLayout.xst = MyLayout.xend;
                MyLayout.rateW = 1;
                MyLayout.gridLy();

            }
            //=================
            if (showNo == 1) {
                str = "" + (eInx + i + 1) + ". " + list.get(eInx + i).name;
            } else {
                str = list.get(eInx + i).name;
            }
            bta1[i * 4 + 0].setText(str);
            str = "";
            if (!list.get(i).desc.equals("")) {
                str += " " + list.get(eInx + i).desc;
            }
            if (list.get(i).max > list.get(eInx + i).min) {
                str += " max= " + list.get(eInx + i).max;
                str += " min=  " + list.get(eInx + i).min;
            }
            bta1[i * 4 + 0].setToolTipText(str);

            //========================================================================
            if (list.get(eInx + i).passw == 0) {
                tfa1[i * 4 + 1].setEchoChar((char) 0);
            } else {
                tfa1[i * 4 + 1].setEchoChar('*');
            }
            tfa1[i * 4 + 1].setText(setValue.get(eInx + i));
            if (list.get(eInx + i).edit == 0) {
                tfa1[i * 4 + 1].setFocusable(false);
            }
            else{
                tfa1[i * 4 + 1].setFocusable(true);
            }
            if(onlyView_f==1)
                tfa1[i * 4 + 1].setFocusable(false);
            
            tfa1[i * 4 + 1].setForeground(Color.black);
            if (list.get(eInx + i).type.equals("double")) {
                tfa1[i * 4 + 1].setForeground(Color.red);
            }
            if (list.get(eInx + i).type.equals("doubleArray")) {
                tfa1[i * 4 + 1].setForeground(Color.red);
            }
            if (list.get(eInx + i).type.equals("int")) {
                tfa1[i * 4 + 1].setForeground(Color.blue);
            }
            if (list.get(eInx + i).type.equals("intArray")) {
                tfa1[i * 4 + 1].setForeground(Color.blue);
            }
            //========================================================================

        }

        if (cla.page == 0) {
            cla.btup.setEnabled(false);
        } else {
            cla.btup.setEnabled(true);
        }

        if (((cla.page + 1) * cla.xc * cla.yc) >= cla.count) {
            cla.btdn.setEnabled(false);
        } else {
            cla.btdn.setEnabled(true);
        }

        cla.lbpg.setText("" + (page + 1) + " / " + pageAll);

        cla.lbitem.setText("" + (eInx + 1) + " / " + cla.count);

        for (int i = 0; i < bta3.length; i++) {
            bta3[i].setForeground(Color.BLACK);
        }
        if (edit_f == 1) {
            bta3[0].setForeground(Color.RED);
        }
        if (delete_f == 1) {
            bta3[2].setForeground(Color.RED);
        }
        if (swap_f == 1) {
            bta3[3].setForeground(Color.RED);
        }
        if (save_f == 1) {
            bta3[4].setForeground(Color.RED);
        }

    }

    void windowShow() {
        SetPanel cla = this;

        if (cla.firstShow_f == 0) {
            cla.setBounds(cla.bounds[0], cla.bounds[1], cla.bounds[2], cla.bounds[3]);
            cla.cp.setBounds(cla.bounds[4], cla.bounds[5], cla.bounds[6], cla.bounds[7]);

        }
        cla.firstShow_f = 1;
        paint();

    }

    boolean chkListLegal() {
        int error = 0;
        String str;
        int eInx = page * xc * yc;
        for (int i = 0; i < setPanelCnt; i++) {
            if((eInx+i)>=list.size())
                continue;
            String text = String.valueOf(tfa1[i * 4 + 1].getPassword());
            list.get(eInx+i).chkLegal(text);
            if (list.get(eInx+i).error_f != 0) {
                Message mes1 = new Message(null, true);
                mes1.keyType_i = 0;
                mes1.mesType_i = 1;
                mes1.title_str = bta1[i * 4 + 0].getText() + ": Input Error !!!";
                mes1.create();
                mes1.setVisible(true);
                return false;
            }
            tfa1[i * 4 + 1].setText(list.get(eInx+i).valueStr);
        }
        return true;
    }

    void mouseFunc(int index) {
        SetPanel cla = this;
        switch (index) {
            case 99 * 256 + 0://up
                if (!cla.chkListLegal()) {
                    return;
                }    
                saveSetValue();
                if (cla.page <= 0) {
                    break;
                }
                cla.page--;
                cla.paint();
                break;
            case 99 * 256 + 1://esc
                saveSetValue();
                cla.dispose();
                SetPanel.ret_f = 0;
                break;
            case 99 * 256 + 2://ok
                if(onlyView_f==1){
                    saveSetValue();
                    cla.dispose();
                    SetPanel.ret_f = 0;
                    break;
                }
                saveSetValue();
                if (cla.chkListLegal()) {
                    cla.dispose();
                    SetPanel.ret_f = 1;
                }
                break;
            case 99 * 256 + 3://down
                if (!cla.chkListLegal()) {
                    return;
                }    
                saveSetValue();
                if (((cla.page + 1) * cla.xc * cla.yc) >= cla.count) {
                    break;
                }
                cla.page++;
                cla.paint();
                break;

            case 98 * 256 + 0://edit
                cla.edit_f ^= 1;
                cla.delete_f = 0;
                cla.swap_f = 0;
                cla.save_f = 0;
                saveSetValue();
                cla.paint();

                break;
            case 98 * 256 + 1://new
                cla.edit_f = 0;
                cla.delete_f = 0;
                cla.swap_f = 0;
                cla.save_f = 0;
                saveSetValue();
                if (cla.cbkNew != null) {
                    cla.cp.setVisible(false);
                    String str = cla.cbkNew.prg("");
                    cla.cp.setVisible(true);
                    if (str.split("<~>")[0].equals("ok")) {
                        cla.addItem(new MyData(str.split("<~>")[1], ""));
                        cla.renewCount();
                    }
                }

                cla.paint();
                break;
            case 98 * 256 + 2://delete
                cla.edit_f = 0;
                cla.delete_f ^= 1;
                cla.swap_f = 0;
                cla.save_f = 0;
                saveSetValue();
                cla.paint();
                break;
            case 98 * 256 + 3://swap
                cla.edit_f = 0;
                cla.delete_f = 0;
                cla.swap_f ^= 1;
                cla.save_f = 0;
                saveSetValue();
                cla.paint();
                break;
            case 98 * 256 + 4://save
                cla.edit_f = 0;
                cla.delete_f = 0;
                cla.swap_f = 0;
                cla.save_f = 0;
                saveSetValue();
                cla.paint();
                break;

            default:
                int einx=cla.page * cla.xc * cla.yc+(index/256);
                if (index % 256 == 0) {
                    if (delete_f == 1) {
                        if (cla.cbkDelete != null) {
                            cla.cp.setVisible(false);
                            String str = cla.cbkDelete.prg(""+(einx));
                            cla.cp.setVisible(true);
                            if (str.split("<~>")[0].equals("ok")) {
                                cla.delItem(einx);
                            }
                        }
                        else{
                            cla.delItem(einx);
                        }
                        cla.renewCount();
                        
                        cla.paint();
                    }
                    break;
                }

                if (index % 256 == 1 || index % 256 == 2) {
                    MyData md = cla.list.get(einx);

                    if(onlyView_f==1)
                        break;
                    if (md.enu == null) {
                        break;
                    }
                    if (index % 256 == 1 && md.edit == 1) {
                        break;
                    }

                    Select sel1 = new Select(null, true);
                    sel1.title_str = md.name;

                    sel1.count = md.enu.size();

                    if (md.enuXc == 0) {
                        sel1.xc = (sel1.count / 9) + 1;
                    } else {
                        sel1.xc = md.enuXc;
                        if (md.enuYc == 0) {
                            sel1.yc = sel1.count / md.enuXc;
                            if ((sel1.count % md.enuXc) != 0) {
                                sel1.yc++;
                            }
                            if (sel1.yc > 9) {
                                sel1.yc = 9;
                            }
                        } else {
                            sel1.yc = md.enuYc;
                        }
                    }
                    sel1.winW = md.enuW;
                    for (int i = 0; i < sel1.count; i++) {
                        sel1.selstr.add(md.enu.get(i).toString());
                    }
                    sel1.create();
                    sel1.setVisible(true);
                    if (Select.ret_f == 1) {
                        cla.tfa1[(index / 256) * 4 + 1].setText(Select.retStr);

                    }

                }
                break;

        }
    }

}

class SetPanelWinLis extends WindowAdapter {

    SetPanel cla;

    SetPanelWinLis(SetPanel owner) {
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

class SetPanelMsLis extends MouseAdapter {

    int enkey_f;
    SetPanel cla;

    SetPanelMsLis(SetPanel owner) {
        cla = owner;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int index;
        if (enkey_f != 1) {
            return;
        }
        index = Integer.parseInt(e.getComponent().getName());
        cla.mouseFunc(index);
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
