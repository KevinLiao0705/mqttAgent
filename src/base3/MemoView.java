package base3;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MemoView extends javax.swing.JDialog 
{

    String title_str="title_str";
    int fullScr_f=0;
    int frameOn_f=0;
    int winW=800;
    int winH=800;
    int ret_f=0;
    int ret_i;
    //===========================
    
    
    
    Timer tm1=null;
    MemoViewTd1 td1=null;
    int td1_run_f=0;
    int td1_destroy_f=0;
    //===========================
    Container cp;
    JTextArea ta1;
    JButton[] bta1=new JButton[32] ; 
    JScrollPane sp1;
    int timer1_cnt;
    Telnet telnet=null; 
    
    //static MyLayout ly=new MyLayout();

    public MemoView(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        this.addWindowListener(new MemoViewWinLis(this));
        this.setTitle("MemoView");
        Font myFont= new Font("Serif", Font.BOLD, 24);
        MemoViewMsLis mslis=new MemoViewMsLis(this); 
        //===============================================
        cp=this.getContentPane();                                                                                                                                       
        cp.setLayout(null);
        cp.setBackground(Color.CYAN);
        //===============================================
        ta1=new JTextArea();
        ta1.setFont(myFont);
        ta1.setName(Integer.toString(1*256+0));
        ta1.setMargin(new Insets(10,10,10,10));
        //ta1.addMouseListener(mslis);
        //ta1.setLineWrap(true);
        ta1.setText("one\ntwo\nthree");
        ta1.append("\nfour\nfive");
        
        ta1.setWrapStyleWord(true);
        sp1=new JScrollPane(ta1);
        sp1.setBounds(0, 0, 500,300);
        cp.add(sp1); 
        //===============================================
        int i;
        for(i=0;i<bta1.length;i++)
        {    
            bta1[i]=new JButton();
            bta1[i].setFont(myFont);
            bta1[i].setName(Integer.toString(1*256+i));
            bta1[i].addMouseListener(mslis);
            bta1[i].setVisible(false);
            cp.add(bta1[i]);
        }
        
        bta1[0].setText("Read setdata.txt");
        bta1[1].setText("Write setdata.txt");
        bta1[2].setText("Load File");
        bta1[3].setText("Clear");
        bta1[4].setText("Get Para");
        bta1[5].setText("Read Database");
        bta1[6].setText("Read Net Setting");
        bta1[7].setText("Test Timer");
        bta1[8].setText("Test Thread");
        bta1[9].setText("Test Ping");
        bta1[10].setText("Open Telnet");
        bta1[11].setText("Read Telnet");
        bta1[13].setText("Keypad");
        bta1[31].setText("Close");
        //===============================================
        
        
        
        
        
        //=======================================================
    }
    
    
    
   void readDatabase()
   {
        Connection c = null; 
        String dbPath="./setdata.db";
        try 
        {
            File f = new File(dbPath);
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            //==============================================
            java.sql.Statement stmt=c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM paraTable;" );
            String paraName="",paraValue="";
            ta1.setText("");
            while ( rs.next() ) 
            {
                paraName = rs.getString("paraName");
                paraValue = rs.getString("paraValue");
                System.out.println( ">" + paraName+"  "+paraValue);
                ta1.append( ">" + paraName+"  "+paraValue+"\n");
                switch (paraName) 
                {
                    case "test1Name":
                        break;
                    case "test2Name":
                        break;
                    default:
                        break;
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } 
        catch ( Exception e ) 
        {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
   }
   
   
   void wdateDb(String paraName,String paraValue)
   {
        Connection c = null;
        String dbPath="./setdata.db";
        String sql;
        try
        {    
            File f = new File(dbPath);
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            java.sql.Statement stmt=c.createStatement();
            sql = "UPDATE paraTable set paraValue = \"";
            sql=sql+paraValue;
            sql=sql+"\" where paraName=\"";
            sql=sql+paraName;
            sql=sql+"\";";
            stmt.executeUpdate(sql);
            c.commit();
            stmt.close();
            c.close();
        }
        catch ( Exception e ) 
        {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
       
   }         
   
   
   
   
}


class MemoViewWinLis extends WindowAdapter
{
    MemoView cla;
    MemoViewWinLis(MemoView owner)
    {
        cla=owner;
    }
    @Override
    public void windowClosing(WindowEvent e) 
    {
        //System.exit(0);
    }
    
    @Override
    public void windowOpened(WindowEvent e) 
    {
        Rectangle r=new Rectangle();
        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();        
        cla.ret_f=0;
        //======================================================
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
        MyLayout.ctrA[0]=cla.sp1;
        MyLayout.rateH=0.8;
        MyLayout.gridLy();
        cla.ta1.setVisible(true);
        //=================================
        MyLayout.yst=MyLayout.yend;
        MyLayout.rateH=1;
        for(int i=0;i<cla.bta1.length;i++)
        {    
            MyLayout.ctrA[i]=cla.bta1[i];
            //cla.bta1[i].setText(MemoView.selstr[i]);
            cla.bta1[i].setVisible(true);
        }    
        MyLayout.eleAmt=cla.bta1.length;
        MyLayout.xc=cla.bta1.length/4;
        MyLayout.yc=4;
        MyLayout.gridLy();
        
        
        
        
    }
}


class MemoViewMsLis extends MouseAdapter
{
    MemoView cla;
    int enkey_f;
    MemoViewMsLis(MemoView owner)
    {
        cla=owner;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        int index;
        if(enkey_f!=1)
            return;
        index = Integer.parseInt(e.getComponent().getName());
        //MemoView cla=MemoView.thisCla; 
        switch(index)
        {
            case 0*256+0:
                break;
            case 1*256+31:
                cla.dispose();
                cla.ret_f=0;
                break;
            case 1*256+0:
               try
                {
                    FileReader reader = new FileReader( "setdata.txt" );
                    BufferedReader br = new BufferedReader(reader);
                    cla.ta1.read( br, null );
                    br.close();
                    cla.ta1.requestFocus();
                }
                catch(Exception e2){System.out.println(e2);}
                break;
                
            case 1*256+1:
               try
                {
                    FileWriter writer = new FileWriter( "setdata.txt" );
                    BufferedWriter br = new BufferedWriter(writer);
                    cla.ta1.write( br);
                    br.close();
                    cla.ta1.requestFocus();
                }
                catch(Exception e2){System.out.println(e2);}
                break;

            case 1*256+2:
                //JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                JFileChooser jfc = new JFileChooser(".");
                FileFilter filter = new FileNameExtensionFilter("Text File","txt");
                jfc.setFileFilter(filter);
		int returnValue = jfc.showOpenDialog(null);
		// int returnValue = jfc.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) 
                {
                    File selectedFile = jfc.getSelectedFile();
                    try
                    {
                        FileReader reader = new FileReader( selectedFile );
                        BufferedReader br = new BufferedReader(reader);
                        cla.ta1.read( br, null );
                        br.close();
                        cla.ta1.requestFocus();
                    }
                    catch(Exception e2){System.out.println(e2);}
		}                
                break;
            case 1*256+3:   //clear memo
                cla.ta1.setText("");
                break;
            case 1*256+4:
                    int line;
                    String paraN;
                    String paraV;
                    for(int i=0;i<GB.paraLen;i++)
                    {    
                        GB.paraName[i]=null;
                        GB.paraValue[i]=null;
                        GB.paraLen=0;
                    }
                    line=cla.ta1.getLineCount();
                    String[] lines = cla.ta1.getText().split("\\n"); 
                    for(int i=0;i<lines.length;i++)
                    {    
                        if(Lib.search(lines[i],"[","]")==1)
                        {
                            paraN=Lib.retstr;
                            if(Lib.search(lines[i],"<",">")==1)
                            {
                                paraV=Lib.retstr;
                                GB.newPara(paraN,paraV);
                            }
                        }    
                    }   
                    cla.ta1.setText("");
                    String str;
                    for(int i=0;i<GB.paraLen;i++)
                    {   
                        str=GB.paraName[i]+" -----> "+GB.paraValue[i]+"\n";
                        cla.ta1.append(str);
                    }    
                    
                    break;
            case 1*256+5:
                cla.readDatabase();
                break;
            case 1*256+6:
                break;
            case 1*256+7:
                if(cla.tm1==null)
                {    
                    //MemoViewTm1 tm1=new MemoViewTm1(this);
                    cla.tm1=new Timer(100,new MemoViewTm1(cla));
                    //cla.tm1=new Timer(100,tm1);
                    cla.tm1.start();
                    break;
                }    
                if(cla.tm1.isRunning())
                    cla.tm1.stop();
                else
                    cla.tm1.start();
                //MemoView.setDelay(200);
                break;
            case 1*256+8:
                if(cla.td1==null)
                {    
                    cla.td1=new MemoViewTd1(cla);
                    cla.td1.start();
                    cla.td1_run_f=1;
                    cla.td1_destroy_f=0;
                    break;
                }
                cla.td1_run_f^=1;
                break;
            case 1*256+9:
                String ipaddr="8.8.8.8";
                cla.ta1.setText("Wait ping "+ipaddr);
                cla.ta1.update(cla.ta1.getGraphics());
                if(Lib.ping(ipaddr)==0)
                    cla.ta1.append("\n ping yes");
                else
                    cla.ta1.append("\n ping no");
                break;
            case 1*256+10:
                break;
            case 1*256+11:
                break;
                
            case 1*256+13:
                break;
               
               
            case 1*256+30:
                byte[] cmd =  new byte[100];
                int i;
                String tmp;
		cmd[0]=27;
		cmd[1]='0';
		cmd[2]='A';
		tmp = new String(cmd,0,3);
                i=tmp.length();
		tmp = (char)27+"OA";
                i=tmp.length();
                
                

                
                InetAddress address = null;
                try 
                {
                    address = InetAddress.getLocalHost();
                } 
                catch (UnknownHostException ex){System.err.println( ex.getClass().getName() + ": " + ex.getMessage() );}
                cla.ta1.append("\n"+address.getHostName());
                cla.ta1.append("\n"+address.getHostAddress());
                break;
           default:
               break;
                
                
                
        }    
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        enkey_f=0;
    }    
    @Override
    public void mousePressed(MouseEvent e)
    {
        enkey_f=1;
    } 

    
    //public void mouseClicked(MouseEvent e){} //在源组件上点击鼠标按钮
    //public void mousePressed(MouseEvent e){} //在源组件上按下鼠标按钮
    //public void mouseReleased(MouseEvent e){} //释放源组件上的鼠标按钮
    //public void mouseEntered(MouseEvent e){} //在鼠标进入源组件之后被调用
    //public void mouseExited(MouseEvent e){} //在鼠标退出源组件之后被调用
    //public void mouseDragged(MouseEvent e){} //按下按钮移动鼠标按钮之后被调用
    //public void mouseMoved(MouseEvent e){} //不按住按钮移动鼠标之后被调用
    
        
}    
            
    
    


class MemoViewTm1 implements ActionListener
{
    MemoView cla;
    MemoViewTm1(MemoView owner)
    {
        cla=owner;
    }
    @Override
    public void actionPerformed(ActionEvent evt) 
    {
        cla.bta1[30].setText("Timer "+Integer.toString(cla.timer1_cnt++%100));
    }

}

class MemoViewTd1 extends Thread 
{
    MemoView cla;
    MemoViewTd1(MemoView owner)
    {
        cla=owner;
    }
    @Override
    public void run() 
    { // override Thread's run()
        //MemoView cla=MemoView.thisCla;
        for (;;) 
        { 
            
            if(cla.td1_run_f==1)
            {    
                cla.bta1[30].setText("Thread "+Integer.toString(cla.timer1_cnt++%100));
            }    
            try{Thread.sleep(100);}
            catch(InterruptedException ex){Thread.currentThread().interrupt();}            
            if(cla.td1_destroy_f==1)
                break;
        }
    }
}