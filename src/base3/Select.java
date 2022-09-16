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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Select extends javax.swing.JDialog {

    static public int ret_i;
    static public int ret_f = 0;
    static public String retStr = "";

    public int reti;
    public int retf = 0;
    public String rets = "";

    String title_str = "title_str";
    int fullScr_f = 0;
    int frameOn_f = 1;
    int winW = 800;
    int winH = 400;
    //String[] selstr = new String[1024];
    ArrayList<String> selstr = new ArrayList();
    ArrayList<String> editStr = new ArrayList();

    StringCbk cbkSelect;
    StringCbk cbkNew;
    StringCbk cbkDelete;
    StringCbk cbkEdit;

    int count = 0;
    int xc = 1;
    int yc = 0;
    int xm = 20;
    int ym = 15;
    int lm = 20;
    int rm = 20;
    int tm = 30;
    int bm = 20;
    int iw = 0;
    int ih = 0;
    int escWidth = 40;
    int page = 0;
    int selPanel_cnt = 0;
    int pageAll = 1;
    int align = 1;
    int dispNo = 0;
    int nowSelect = -1;
    int editNew = 0;

    
    Container cp;
    JLabel lb1;
    JButton[] bta1 = new JButton[256];
    JButton btesc = new JButton();
    JButton btup = new JButton();
    JButton btdn = new JButton();
    JPanel pn1;
    JLabel lbpg;
    JLabel lbitem;
    JButton[] bta2 = new JButton[5];
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

    int sameNameError = 1;

    //static MyLayout ly=new MyLayout();
    public Select(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        Select cla = this;
        cla.setBounds(-100, -100, 0, 0);
        cla.tm = 10;
        cla.bm = 10;
        cla.lm = 20;
        cla.rm = 20;
        cla.ym = 6;
        cla.xm = 10;
        cla.ih = 40;
        cla.iw = 200;

    }

    public void create() {
        Select cla = this;
        cla.setTitle("Select");
        Font myFont = new Font("Serif", Font.BOLD, 24);
        cla.addWindowListener(new SelectWinLis(cla));
        SelectMsLis mslis = new SelectMsLis(cla);
        //===============================================
        cp = cla.getContentPane();
        cp.setLayout(null);
        cp.setBackground(Color.CYAN);

        //===============================================
        if (count == 0) {
            count = selstr.size();
        }

        for (int i = 0; i < selstr.size(); i++) {
            editStr.add(selstr.get(i));
        }
        lb1 = new JLabel();
        lb1.setFont(myFont);
        lb1.setHorizontalAlignment(JLabel.CENTER);
        cp.add(lb1);
        if (cla.yc == 0) {
            cla.yc = cla.count / cla.xc;
            if (cla.count % cla.xc != 0) {
                cla.yc++;
            }
        }
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

            if (align == 0) {
                bta1[i].setHorizontalAlignment(SwingConstants.LEFT);
            }
            if (align == 1) {
                bta1[i].setHorizontalAlignment(SwingConstants.CENTER);
            }
            if (align == 2) {
                bta1[i].setHorizontalAlignment(SwingConstants.RIGHT);
            }

            cp.add(bta1[i]);
        }

        for (i = 0; i < bta2.length; i++) {
            bta2[i] = new JButton();
            bta2[i].setFont(myFont);
            bta2[i].setName(Integer.toString(2 * 256 + i));
            bta2[i].addMouseListener(mslis);
            bta2[i].setFocusable(false);
            bta2[i].setVisible(true);
            cp.add(bta2[i]);
        }
        bta2[0].setText("Edit");
        bta2[1].setText("New");
        bta2[2].setText("Delete");
        bta2[3].setText("Swap");
        bta2[4].setText("Save");

        if (editDisable_f == 1) {
            bta2[0].setEnabled(false);
        }
        if (newDisable_f == 1) {
            bta2[1].setEnabled(false);
        }
        if (deleteDisable_f == 1) {
            bta2[2].setEnabled(false);
        }
        if (swapDisable_f == 1) {
            bta2[3].setEnabled(false);
        }
        if (saveDisable_f == 1) {
            bta2[4].setEnabled(false);
        }

        btesc = new JButton();
        btesc.setFont(myFont);
        btesc.setName(Integer.toString(0 * 256 + 1));
        btesc.addMouseListener(mslis);
        btesc.setVisible(true);
        btesc.setFocusable(false);
        btesc.setText("ESC");
        cp.add(btesc);
        btup = new JButton();
        btup.setFont(myFont);
        btup.setName(Integer.toString(0 * 256 + 0));
        btup.addMouseListener(mslis);
        btup.setFocusable(false);
        btup.setText("Page Up");
        cp.add(btup);
        btdn = new JButton();
        btdn.setFont(myFont);
        btdn.setName(Integer.toString(0 * 256 + 2));
        btdn.addMouseListener(mslis);
        btdn.setFocusable(false);
        btdn.setText("Page Down");
        cp.add(btdn);

        lbpg = new JLabel();
        lbpg.setFont(myFont);
        lbpg.setHorizontalAlignment(JLabel.LEFT);
        cp.add(lbpg);

        lbitem = new JLabel();
        lbitem.setFont(myFont);
        lbitem.setHorizontalAlignment(JLabel.RIGHT);
        cp.add(lbitem);

        //=======================================================
    }

    public void paint() {
        Select cla = this;

        Rectangle r = new Rectangle();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        cla.lb1.setText(cla.title_str);
        Select.ret_f = 0;
        reti = 0;
        //======================================================
        if (cla.fullScr_f == 1) {
            if (cla.frameOn_f == 1) {
                r.width = screenSize.width - GB.winDialog_wm1;
                r.height = screenSize.height - GB.winDialog_hm1;
                r.x = GB.winDialog_xm1;
                r.y = GB.winDialog_ym1;;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width - GB.winDialog_wc1, r.height - GB.winDialog_hc1);
            } else {
                r.width = screenSize.width - GB.winDialog_wm2;
                r.height = screenSize.height - GB.winDialog_hm2;
                r.x = GB.winDialog_xm2;
                r.y = GB.winDialog_ym2;;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width - GB.winDialog_wc2, r.height - GB.winDialog_hc2);
            }
        } else {

            cla.winH = cla.ih * (cla.yc + 2) + 3 * cla.tm + cla.bm + (cla.yc - 1) * cla.ym + GB.winDialog_hm4;
            if (editNew != 0) {
                cla.winH += cla.ih + cla.tm;

            }
            if (cla.frameOn_f == 1) {
                r.width = cla.winW;
                r.height = cla.winH;
                r.x = (screenSize.width - GB.winDialog_wm3 - r.width) / 2;
                r.y = (screenSize.height - GB.winDialog_hm3 - r.height) / 2;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width - GB.winDialog_wc3, r.height - GB.winDialog_hc3);
            } else {
                r.width = cla.winW;
                r.height = cla.winH;
                r.x = (screenSize.width - GB.winDialog_wm4 - r.width) / 2;
                r.y = (screenSize.height - GB.winDialog_hm4 - r.height) / 2;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width - GB.winDialog_wc4, r.height - GB.winDialog_hc4);
            }

        }
        //cla.pn1.setLayout(null);
        for (;;) {
            save_f = 0;
            if (selstr.size() != editStr.size()) {
                save_f = 1;
                break;
            }
            for (int i = 0; i < selstr.size(); i++) {
                if (!selstr.get(i).equals(editStr.get(i))) {
                    save_f = 1;
                    break;
                }
            }
            break;
        }

        cla.count = cla.editStr.size();
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

        pageAll = (int) (cla.count - 0.5) / (cla.xc * cla.yc);
        pageAll += 1;

        //=================================
        MyLayout.ctrA[0] = cla.lb1;
        MyLayout.rateH = cla.ih;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.gridLy();
        //==
        MyLayout.yst = MyLayout.yend;

        int eInx = cla.page * cla.xc * cla.yc;
        for (int i = 0; i < cla.selPanel_cnt; i++) {
            MyLayout.ctrA[i] = cla.bta1[i];
            if ((eInx + i) >= cla.count) {
                cla.bta1[i].setVisible(false);
                continue;
            }
            if (dispNo != 0) {
                cla.bta1[i].setText("" + (eInx + i + 1) + ". " + cla.editStr.get(eInx + i));
            } else {
                cla.bta1[i].setText(cla.editStr.get(eInx + i));
            }

            cla.bta1[i].setBackground(GB.coBt);
            cla.bta1[i].setVisible(true);
            cla.bta1[i].setForeground(Color.BLACK);
            if (swap_f == 1) {
                if (swapSel == (eInx + i)) {
                    cla.bta1[i].setForeground(Color.RED);
                }
            }
            if ((eInx + i) == nowSelect) {
                cla.bta1[i].setBackground(GB.green0);
            }
        }

        MyLayout.eleAmt = cla.selPanel_cnt;
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

        if (editNew == 1) {
            MyLayout.yst = MyLayout.yend;
            for (int i = 0; i < cla.bta2.length; i++) {
                MyLayout.ctrA[i + 1] = cla.bta2[i];
            }
            MyLayout.rateH = cla.ih;
            MyLayout.eleAmt = cla.bta2.length + 2;
            MyLayout.xc = cla.bta2.length + 2;
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
        MyLayout.ctrA[3] = cla.btdn;
        MyLayout.ctrA[4] = cla.lbitem;
        MyLayout.eleAmt = 5;
        MyLayout.xc = 5;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.rm = 10;
        MyLayout.lm = 10;
        MyLayout.xm = 10;
        MyLayout.gridLy();

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

        for (int i = 0; i < bta2.length; i++) {
            bta2[i].setForeground(Color.BLACK);
        }
        if (edit_f == 1) {
            bta2[0].setForeground(Color.RED);
        }
        if (delete_f == 1) {
            bta2[2].setForeground(Color.RED);
        }
        if (swap_f == 1) {
            bta2[3].setForeground(Color.RED);
        }
        if (save_f == 1) {
            bta2[4].setForeground(Color.RED);
        }

    }
}

class SelectWinLis extends WindowAdapter {

    Select cla;

    SelectWinLis(Select owner) {
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

class SelectMsLis extends MouseAdapter {

    int enkey_f;
    Select cla;

    SelectMsLis(Select owner) {
        cla = owner;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        String str;
        int index;
        if (enkey_f != 1) {
            return;
        }
        index = Integer.parseInt(e.getComponent().getName());
        switch (index) {
            case 0 * 256 + 0:
                if (cla.page <= 0) {
                    break;
                }
                cla.page--;
                cla.paint();
                break;
            case 0 * 256 + 1:
                //cla.setVisible(false);
                cla.dispose();
                Select.ret_f = 0;
                cla.retf = 0;
                break;
            case 0 * 256 + 2:
                if (((cla.page + 1) * cla.xc * cla.yc) >= cla.count) {
                    break;
                }
                cla.page++;
                cla.paint();
                break;

            case 2 * 256 + 0://edit
                cla.delete_f = 0;
                cla.swap_f = 0;
                cla.edit_f ^= 1;
                cla.paint();
                break;
            case 2 * 256 + 1://new
                cla.edit_f = 0;
                cla.delete_f = 0;
                cla.swap_f = 0;

                if (cla.cbkNew != null) {
                    cla.cp.setVisible(false);
                    str = cla.cbkNew.prg("");
                    cla.cp.setVisible(true);
                    if (str.split("~")[0].equals("ok")) {
                        cla.editStr.add(str.split("~")[1]);
                    }
                    cla.paint();
                    return;
                }

                SetPanel spn1;
                spn1 = new SetPanel(null, true);
                spn1.winW = 1000;
                spn1.nameWideRate = 0.1;
                spn1.title_str = "Setting";
                str = "" + (cla.count + 1);
                spn1.list.add(new MyData(str, ""));
                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 1) {
                    str = spn1.setValue.get(SetPanel.ret_i);
                    if (cla.sameNameError == 1) {
                        for (int i = 0; i < cla.editStr.size(); i++) {
                            if (cla.editStr.get(i).equals(str)) {
                                Message.errorBox("The Name Is Exist !!!");
                                return;
                            }
                        }

                        cla.editStr.add(str);
                    }
                }
                cla.paint();

                break;

            case 2 * 256 + 2://delete
                cla.edit_f = 0;
                cla.swap_f = 0;
                cla.delete_f ^= 1;
                cla.paint();
                break;
            case 2 * 256 + 3://swap
                cla.edit_f = 0;
                cla.delete_f = 0;
                cla.swap_f ^= 1;
                cla.swapSel = -1;
                cla.paint();
                break;
            case 2 * 256 + 4://save
                cla.edit_f = 0;
                cla.delete_f = 0;
                cla.swap_f = 0;
                if (cla.save_f == 1) {
                    cla.selstr.clear();
                    for (int i = 0; i < cla.editStr.size(); i++) {
                        cla.selstr.add(cla.editStr.get(i));
                    }
                }
                cla.paint();
                break;

            default:
                if ((index / 256) == 1) {
                    index = index % 256;

                    int selectInx = cla.page * cla.xc * cla.yc + index;

                    if (cla.edit_f == 1) {
                        
                        if (cla.cbkEdit != null) {
                            cla.cp.setVisible(false);
                            
                            str = cla.cbkEdit.prg("" + 1 + "<~>" + selectInx + "<~>" + cla.editStr.get(selectInx));
                            cla.cp.setVisible(true);
                            if (str.split("~")[0].equals("ok")) {
                            }
                            cla.paint();
                            return;

                        }
                        
                        
                        
                        spn1 = new SetPanel(null, true);
                        spn1.winW = 1000;
                        spn1.nameWideRate = 0.1;
                        spn1.title_str = "Setting";
                        str = "" + (selectInx + 1);
                        spn1.list.add(new MyData(str, cla.editStr.get(selectInx)));
                        spn1.create();
                        spn1.setVisible(true);
                        if (SetPanel.ret_f == 1) {
                            str = spn1.setValue.get(SetPanel.ret_i);
                            cla.editStr.set(selectInx, str);
                            cla.edit_f = 0;
                            cla.paint();
                        }
                        return;
                    }
                    if (cla.delete_f == 1) {
                        if (cla.cbkDelete != null) {
                            str = cla.cbkDelete.prg(cla.editStr.get(selectInx));
                            if (str.split("~")[0].equals("ok")) {
                                cla.editStr.remove(selectInx);
                            }
                            cla.paint();
                            return;
                        }
                        cla.editStr.remove(selectInx);
                        cla.paint();
                        return;

                    }
                    if (cla.swap_f == 1) {
                        if (cla.swapSel == -1) {
                            cla.swapSel = selectInx;
                        } else {
                            String str1 = cla.editStr.get(cla.swapSel);
                            String str2 = cla.editStr.get(selectInx);
                            cla.editStr.set(cla.swapSel, str2);
                            cla.editStr.set(selectInx, str1);
                            cla.swap_f = 0;
                        }
                        cla.paint();
                        return;

                    }

                    Select.ret_f = 1;
                    Select.ret_i = cla.page * cla.xc * cla.yc + index;
                    Select.retStr = cla.editStr.get(Select.ret_i);
                    cla.retf = Select.ret_f;
                    cla.reti = Select.ret_i;
                    cla.rets = Select.retStr;

                    if (cla.cbkSelect != null) {
                        cla.cp.setVisible(false);
                        str = cla.cbkSelect.prg("" + cla.retf + "<~>" + cla.reti + "<~>" + cla.rets);
                        cla.cp.setVisible(true);

                        if (str != null) {

                            return;
                        }

                    }
                    cla.dispose();
                }
                break;

        }
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
