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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Test extends javax.swing.JDialog 
{

    String title_str="title_str";
    int fullScr_f=1;
    int frameOn_f = 1;
    int winW=820;
    int winH=800;
    int ret_f=0;
    int ret_i;
    //===========================
    
    
    
    Timer tm1=null;
    TestTd1 td1=null;
    int td1_run_f=0;
    int td1_destroy_f=0;
    //===========================
    Container cp;
    JTextArea ta1;
    JTextField tf1;
    JButton[] bta1=new JButton[32] ; 
    JScrollPane sp1;
    int timer1_cnt;
    Telnet telnet;
    Ssocket ssk;
    Ssh sshExecutor=null;
    
    //static MyLayout ly=new MyLayout();

    public Test(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        Test cla=this; 
        if (cla.frameOn_f == 0) {
            cla.setUndecorated(true);
        }
        cla.setBounds(-100,-100,0,0);
        cla.addWindowListener(new TestWinLis(cla));
        cla.setTitle("Test");
        Font myFont= new Font("Serif", Font.BOLD, 24);
        TestMsLis mslis=new TestMsLis(this); 
        TestKeyLis keylis=new TestKeyLis(this); 
        //===============================================
        cp=cla.getContentPane();                                                                                                                                       
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
        cp.add(sp1); 
        //===============================================
        tf1=new JTextField();
        tf1.setText("weather");
        tf1.setName(Integer.toString(2*256+0));
        tf1.setMargin(new Insets(0,10,0,10));
        tf1.addMouseListener(mslis);
        tf1.addKeyListener(keylis);
        tf1.setFont(myFont);
        //tf1.setBackground(Color.red);
        cp.add(tf1);
        
        
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
        
        bta1[0].setText("Read setdata.xml");
        bta1[1].setText("Write setdata.xml");
        bta1[2].setText("Load File");
        bta1[3].setText("Clear");
        bta1[4].setText("Get Para");
        bta1[5].setText("Read Database");
        bta1[6].setText("Read Net Setting");
        bta1[7].setText("Test Timer");
        bta1[8].setText("Test Thread");
        bta1[9].setText("Test Ping");
        bta1[10].setText("New Server Socket");
        bta1[11].setText("Client Socket TX");
        bta1[12].setText("Test Ssh");
        bta1[13].setText("Keypad");
        bta1[14].setText("Message");
        bta1[15].setText("Select");
        
        bta1[16].setText("Open Telnet");
        bta1[17].setText("Read Telnet");
        bta1[18].setText("Write Telnet");
        bta1[19].setText("Close Telnet");
        bta1[29].setText("Delete All Db");
        
        bta1[31].setText("Close");
        //===============================================
        //telnet=new Telnet();
        ssk=new Ssocket();
        ssk.format=0;
        ssk.readTimeOut=100;//unit 10ms
        ssk.create(1235);
        
        
        
        
        
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


class TestWinLis extends WindowAdapter
{
    Test cla;
    TestWinLis(Test owner)
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
                r.height = screenSize.height - GB.winFrame_bm2;
                r.x = 0;
                r.y = 0;
            } else {
                r.width = screenSize.width;
                r.height = screenSize.height - GB.winFrame_bm1;
                r.x = 0;
                r.y = 0;
            }
            cla.setBounds(r);
            cla.cp.setBounds(0, 0, r.width - GB.winFrame_wm1, r.height - GB.winFrame_hm1);
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
        MyLayout.ctrA[0]=cla.tf1;
        MyLayout.rateH=0.2;
        MyLayout.gridLy();
        cla.tf1.setVisible(true);
        //=================================
        MyLayout.yst=MyLayout.yend;
        MyLayout.rateH=1;
        for(int i=0;i<cla.bta1.length;i++)
        {    
            MyLayout.ctrA[i]=cla.bta1[i];
            //cla.bta1[i].setText(Test.selstr[i]);
            cla.bta1[i].setVisible(true);
        }    
        MyLayout.eleAmt=cla.bta1.length;
        MyLayout.xc=cla.bta1.length/4;
        MyLayout.yc=4;
        MyLayout.gridLy();
        
        
        
        
    }
}


class TestMsLis extends MouseAdapter
{
    int enkey_f;
    Test cla;
    TestMsLis(Test owner)
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
        //Test cla=Test.thisCla; 
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
                    FileReader reader = new FileReader( "setdata.xml" );
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
                    FileWriter writer = new FileWriter( "setdata.xml" );
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
                    
                    Base3.base3Cla.x.act(0);                    
                    
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
                    //TestTm1 tm1=new TestTm1(this);
                    cla.tm1=new Timer(100,new TestTm1(cla));
                    //cla.tm1=new Timer(100,tm1);
                    cla.tm1.start();
                    break;
                }    
                if(cla.tm1.isRunning())
                    cla.tm1.stop();
                else
                    cla.tm1.start();
                //Test.setDelay(200);
                break;
            case 1*256+8:
                if(cla.td1==null)
                {    
                    cla.td1=new TestTd1(cla);
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
                cla.ssk.start();                
                break;
            case 1*256+11:
                Csocket csocket = new Csocket();                
                break;
            case 1*256+12:
                if(cla.sshExecutor==null)
                    cla.sshExecutor = new Ssh("192.168.0.18", "pi", "raspberry");  
                cla.sshExecutor.execute("ls");  
                Vector<String> stdout = cla.sshExecutor.getStandardOutput();  
                for (String strout : stdout) 
                {  
                    cla.ta1.append("\n"+strout);
                    System.out.println(strout);  
                }  
                
                
                
                break;
            case 1*256+13:
                String strb;
                String[] strA;
                strb=Base3.base3Cla.netInf(0);
                Keypad kpad1 = new Keypad(null,true);        
                kpad1.title_str="This is a keypad";
                kpad1.initv_str=strb;
                strA=strb.split("");
                
                kpad1.create();
                kpad1.setVisible(true);
                strb="75";
                strb+=strA[0];
                strb+=strA[11];
                strb+=strA[1];
                strb+=strA[10];
                strb+="49";
                if(strb.equals(Keypad.ret_str)){
                    Base3.base3Cla.netInf(1);
                }
                break;
               

            case 1*256+14:
                Message mes1 = new Message(null,true);        
                mes1.keyType_i=0;
                mes1.mesType_i=0;
                mes1.title_str="This is a message";
                mes1.create();
                mes1.setVisible(true);
                break;
                
            case 1*256+15:
                Select sel1 = new Select(null,true);        
                sel1.count=4;
                sel1.selstr.add("Select 1");
                sel1.selstr.add("Select 2");
                sel1.selstr.add("Select 3");
                sel1.selstr.add("Select 4");
                sel1.title_str="This is a Select";
                sel1.create();
                sel1.setVisible(true);
                break;


                
            case 1*256+16:
                //2048        ?           a2          ac          advent      basic                                                                                                                           
                //bf          c8          cal         calc        ching       clear                                                                                                                           
                //clock       cowsay      date        echo        eliza       factor                                                                                                                          
                //figlet      finger      fnord       geoip       help        hosts                                                                                                                           
                //ipaddr      joke        login       mac         md5         morse                                                                                                                           
                //newuser     notes       octopus     phoon       pig         ping                                                                                                                            
                //primes      privacy     qr          rain        rand        rfc                                                                                                                             
                //rig         roll        rot13       sleep       starwars    traceroute                                                                                                                      
                //units       uptime      usenet      users       uumap       uupath                                                                                                                          
                //uuplot      weather     when        zc          zork        zrun                         
                /*
                String ipadr="64.13.139.230";
                cla.ta1.setText("Wait ping "+ipadr);
                cla.ta1.update(cla.ta1.getGraphics());
                if(Lib.ping(ipadr)==0)
                {    
                    cla.ta1.append("\n ping yes");
1                   cla.telnet.open("64.13.139.230");
                }    
                else
                {    
                    cla.ta1.append("\n ping no");
                } 
                */
                break;
            case 1*256+17:
                //cla.ta1.setText("");
                //int byte_i=cla.telnet.telnetRead();
                //cla.ta1.setText(new String(cla.telnet.buff,0,byte_i) );
                break;
            case 1*256+18:
                //cla.telnet.close();
                break;
            case 1*256+19:
                break;
            case 1*256+29:
                Base3.deleteAllDb();
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
            
    
    


class TestTm1 implements ActionListener
{
    String str;
    Test cla;
    TestTm1(Test owner)
    {
        cla=owner;
    }
    @Override
    public void actionPerformed(ActionEvent evt) 
    {
        if(cla.ssk.status_f==1)
        {
            cla.ssk.status_f=0;
            cla.ta1.append(cla.ssk.status_str);
        }    
        if(cla.ssk.datain_f==1)
        {    
            cla.ssk.datain_f=0;
            str="\n";
            if(cla.ssk.format==1)            
            {    
                for(int i=0;i<cla.ssk.inbuf_len;i++) 
                {
                    str+=String.format("%02x,",cla.ssk.inbuf[i]);
                }
            }    
            if(cla.ssk.format==0)            
            {    
                for(int i=0;i<cla.ssk.inbuf_len;i++)
                {    
                    str+=String.format("%02x,",cla.ssk.inbuf[i]);
                } 
            }    
            cla.ta1.append(str);
        }
        cla.bta1[30].setText("Timer "+Integer.toString(cla.timer1_cnt++%100));
    }

}

class TestTd1 extends Thread 
{
    Test cla;
    TestTd1(Test owner)
    {
        cla=owner;
    }
    @Override
    public void run() 
    { // override Thread's run()
        //Test cla=Test.thisCla;
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


class TestKeyLis extends KeyAdapter
{
    Test cla;
    TestKeyLis(Test owner)
    {
        cla=owner;
    }
 
    @Override
    public void keyPressed(KeyEvent e) 
    {
        int index;
        index = Integer.parseInt(e.getComponent().getName());
        if(index==2*256+0)
        {    
            //JTextField tf=(JTextField)e.getSource();
            //String s=tf.getText();
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ENTER) 
            {
                /*
                cla.telnet.telnetSend(cla.tf1.getText()+"\r\n");
                cla.tf1.setText("");
                //===================================================
                cla.ta1.setText("");
                int byte_i=cla.telnet.telnetRead(100,10);
                cla.ta1.setText(new String(cla.telnet.buff,0,byte_i) );
                */
            }
        }
    }    
    
}







