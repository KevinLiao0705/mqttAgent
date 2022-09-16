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

public class LoopSelect extends javax.swing.JDialog {
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
    //ArrayList<String> selstr =new ArrayList();
    ArrayList<SelData> selDataLs =new ArrayList();
    ArrayList<String> selTmp =new ArrayList();
    
    int count = 0;
    int xc = 1;
    int yc = 0;
    int xm = 20;
    int ym = 20;
    int lm = 20;
    int rm = 20;
    int tm = 20;
    int bm = 20;
    int iw = 0;
    int ih = 0;
    int escWidth = 40;
    int page = 0;
    int selPanel_cnt=0;
    int pageAll=1;
    //int nowLevel = 0;
    
    int nowSelect= -1;

    Container cp;
    JLabel lb1;
    JButton[] bta1 = new JButton[256];
    JButton btesc = new JButton();
    JButton btup = new JButton();
    JButton btdn = new JButton();
    JButton btret = new JButton();
    JPanel pn1;
    JLabel lbpg;
    JLabel lbno;
    LoopSelectEnter lse=null;
    
    int sort=1;

    //static MyLayout ly=new MyLayout();
    public LoopSelect(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        LoopSelect cla = this;
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
        LoopSelect cla = this;
        cla.setTitle("LoopSelect");
        Font myFont = new Font("Serif", Font.BOLD, 24);
        cla.addWindowListener(new LoopSelectWinLis(cla));
        LoopSelectMsLis mslis = new LoopSelectMsLis(cla);
        //===============================================
        cp = cla.getContentPane();
        cp.setLayout(null);
        cp.setBackground(Color.CYAN);
        //===============================================
        lb1 = new JLabel();
        lb1.setFont(myFont);
        lb1.setHorizontalAlignment(JLabel.CENTER);
        cp.add(lb1);
        while(cla.yc*cla.xc>=256){
            cla.yc--;
        }
        selPanel_cnt=cla.yc*cla.xc;
        //===============================================
        int i;
        for (i = 0; i < selPanel_cnt; i++) {
            bta1[i] = new JButton();
            bta1[i].setFont(myFont);
            bta1[i].setName(Integer.toString(1 * 256 + i));
            bta1[i].addMouseListener(mslis);
            bta1[i].setFocusable(false);
            bta1[i].setVisible(true);
            cp.add(bta1[i]);
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
        btdn.setName(Integer.toString(0 * 256 + 3));
        btdn.addMouseListener(mslis);
        btdn.setFocusable(false);
        btdn.setText("Page Down");
        cp.add(btdn);

        btret = new JButton();
        btret.setFont(myFont);
        btret.setName(Integer.toString(0 * 256 + 2));
        btret.addMouseListener(mslis);
        btret.setVisible(true);
        btret.setFocusable(false);
        btret.setText("RETURN");
        cp.add(btret);

        
        lbpg = new JLabel();
        lbpg.setFont(myFont);
        lbpg.setHorizontalAlignment(JLabel.LEFT);
        cp.add(lbpg);
        
        lbno = new JLabel();
        lbno.setFont(myFont);
        lbno.setHorizontalAlignment(JLabel.RIGHT);
        cp.add(lbno);
        
        //=======================================================
    }

    public void paint() {
        LoopSelect cla = this;
        String str;
        String[] strA;

        Rectangle r = new Rectangle();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Select.ret_f = 0;
        reti=0;
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
        } else {
            
                cla.winH = cla.ih * (cla.yc + 2) + 3 * cla.tm + cla.bm + (cla.yc - 1) * cla.ym + GB.winDialog_hm4;
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
                    //r.y = 0;
                    //r.x = 0;
                    cla.setBounds(r);
                    cla.cp.setBounds(0, 0, r.width - GB.winDialog_wc4, r.height - GB.winDialog_hc4);
                }
            
            
        }
        //cla.pn1.setLayout(null);
        //=================================
        MyLayout.ctrA[0] = cla.lb1;
        MyLayout.rateH = cla.ih;
        MyLayout.tm = cla.tm;
        MyLayout.bm = cla.bm;
        MyLayout.gridLy();
        //==
        MyLayout.yst = MyLayout.yend;
        
        SelData sd=cla.selDataLs.get(cla.selDataLs.size()-1);
        cla.count= sd.slst.size();
        selTmp.clear();
        for(int i=0;i<cla.count;i++){
            selTmp.add(sd.slst.get(i)+"<~>"+i);
        }
        if(cla.sort!=0){
            Collections.sort(selTmp);
        }
                
        cla.lb1.setText(sd.title);

        int eInx = cla.page * cla.xc * cla.yc;
        for (int i = 0; i < cla.selPanel_cnt; i++) {
            MyLayout.ctrA[i] = cla.bta1[i];
            if((eInx+i)>=cla.count){
                cla.bta1[i].setVisible(false);
                continue;
            }    
            //cla.bta1[i].setText(sd.slst.get(eInx+i));
            str=selTmp.get(eInx+i);
            strA=str.split("<~>");
            cla.bta1[i].setText(strA[0]);
            cla.bta1[i].setBackground(GB.coBt);
            cla.bta1[i].setVisible(true);
            if((eInx+i)==nowSelect){
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

        MyLayout.yst = MyLayout.yend;
        MyLayout.ctrA[0] = cla.lbpg;
        MyLayout.ctrA[1] = cla.btup;
        MyLayout.ctrA[2] = cla.btesc;
        MyLayout.ctrA[3] = cla.btret;
        MyLayout.ctrA[4] = cla.btdn;
        MyLayout.ctrA[5] = cla.lbno;
        MyLayout.eleAmt = 6;
        MyLayout.xc = 6;
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
        
        pageAll=(int)(cla.count-0.5)/(cla.xc * cla.yc);
        pageAll+=1;

        cla.lbpg.setText("Page: "+(page+1)+" / "+pageAll);
        cla.lbno.setText("No: "+(cla.xc * cla.yc*page+1)+" / "+cla.count);
        
        
        if (cla.count <= cla.yc * cla.xc) {
            btup.setVisible(false);
            btdn.setVisible(false);
            lbpg.setVisible(false);
        }else{
            btup.setVisible(true);
            btdn.setVisible(true);
            lbpg.setVisible(true);
            
        }
        
        
    }
}

class SelData{
    int page=0;
    int nowSelect=-1;
    String nowSelectS="";
    String title="Select";
    ArrayList<String> slst=new ArrayList();
    ArrayList<String> mapKeys=new ArrayList();
    Object actObject;
    int actObjectType=0; //0:objectList,1:object
}

class LoopSelectWinLis extends WindowAdapter {

    LoopSelect cla;

    LoopSelectWinLis(LoopSelect owner) {
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

class LoopSelectMsLis extends MouseAdapter {

    int enkey_f;
    LoopSelect cla;

    LoopSelectMsLis(LoopSelect owner) {
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
            case 0 * 256 + 0:
                if (cla.page <= 0) {
                    break;
                }
                cla.page--;
                cla.paint();
                break;
            case 0 * 256 + 1:
                cla.dispose();
                cla.retf=0;
                break;
            case 0 * 256 + 3:
                if (((cla.page + 1) * cla.xc * cla.yc) >= cla.count) {
                    break;
                }
                cla.page++;
                cla.paint();
                break;
            case 0 * 256 + 2:
                if(cla.selDataLs.size()<=1){
                    cla.dispose();
                    cla.retf=0;
                    break;
                }
                cla.selDataLs.remove(cla.selDataLs.size()-1);
                cla.page= cla.selDataLs.get(cla.selDataLs.size()-1).page;
                cla.paint();
                break;
                
                
            default:
                if ((index / 256) == 1) {
                    index = index % 256;
                    cla.retf=1;
                    cla.reti=cla.page  * cla.xc * cla.yc+index;
                    cla.selDataLs.get(cla.selDataLs.size()-1).page=cla.page;
                    String str=cla.selTmp.get(cla.reti);
                    String [] strA;
                    strA=str.split("<~>");
                    if(strA.length==2){
                        cla.reti=Integer.parseInt(strA[1]);
                    }                    
                    cla.rets = cla.selDataLs.get(cla.selDataLs.size()-1).slst.get(cla.reti);
                    
                    
                    if(cla.lse!=null){
                        int reti=cla.lse.enter(cla.reti,cla.rets);
                        if(reti==0){
                            cla.retf=1;
                            cla.reti=cla.page  * cla.xc * cla.yc+index;
                            cla.rets = cla.selDataLs.get(cla.selDataLs.size()-1).slst.get(cla.reti);
                            cla.dispose();
                            break;
                        }    
                        if(reti==1){
                            cla.page=0;
                            cla.paint();
                            break;
                        }    
                        break;
                    }
                    cla.retf=1;
                    cla.dispose();
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


abstract class LoopSelectEnter {
    public abstract int enter(int reti,String rets);
}

/*
        LoopSelectEnter.lse = new LoopSelectEnter() {
            @Override
            public void enter() {
            }
        };
*/