/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base3;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.logging.Level;
import javax.swing.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Administrator
 */
public class Base3 extends JFrame// implements ActionListener
{

    static Base3 base3Cla;//= new Base3();
    int debug_f = 0;
    int fullScr_f = 1;
    int frameOn_f = 1;
    int winW = 1620;
    int winH = 1036;
    int ret_f = 0;
    int ret_i;
    //===========================

    Container cp;
    Font myFont;
    JPanel pn1;
    JTextArea ta1;
    //JTextPane ta1;

    JScrollPane sp1;
    JButton[] bta1 = new JButton[16];
    Action x;
    JComponent cp1;
    JLabel lb1;
    UartTest uartTest1;
    public static final Logger log = Logger.getLogger(Base3.class);

    public Base3() {
        Base3 cla = this;
        String currentDir = System.getProperty("user.dir");
        PropertyConfigurator.configure(currentDir + "\\log4j.properties");
        //=============================================
        //Base3.log.debug("This is a Debug Information.");    //debug層級
        //Base3.log.info("This is a Info Information.");      //info層級
        //Base3.log.warn("This is a Warn Information.");      //warn層級
        //Base3.log.error("This is a Error Information.");    //error層級
        //Base3.log.fatal("This is a Fatal Information.");    //fatal層級        
        //===============================================
        Base3.log.info("MqttAgent Program Start.....\n");
        GB.initGB();
        GB.os = Lib.getOs();
        x = new Action(this);
        //=======================================
        cla.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cla.setTitle("Title");
        cla.addWindowListener(new Base3WinLis(cla));
        cla.setBounds(-100, -100, 0, 0);
        
        cla.setIconImage(cla.getToolkit().getImage("phone.png"));
        cla.cp = cla.getContentPane();
        cla.cp.setBackground(Color.WHITE);

        /* important Statement */
        //setUndecorated(true);           
        cla.myFont = new Font("Serif", Font.BOLD, 24);
        Base3MsLis mslis = new Base3MsLis(this);

        //=======================================================
        cla.ta1 = new JTextArea();
        cla.ta1.setFont(cla.myFont);
        cla.ta1.setName(Integer.toString(1 * 256 + 0));
        cla.ta1.setMargin(new Insets(10, 10, 10, 10));
        //frm.ta1.addMouseListener(mslis);
        //frm.ta1.setLineWrap(true);
        //cla.ta1.setText("one\ntwo\nthree");
        //cla.ta1.append("\nfour\nfive");
        cla.ta1.setWrapStyleWord(true);
        cla.sp1 = new JScrollPane(cla.ta1);
        cla.cp.add(cla.sp1);
        //=======================================================
        cla.pn1 = new JPanel();
        pn1.setBackground(Color.red);
        cla.cp.add(cla.pn1);

        cla.enableInputMethods(true);

        //=======================================================
        ImageIcon icon = new ImageIcon("title.jpg");
        cla.lb1 = new JLabel(icon);
        cla.cp.add(cla.lb1);

        int i;
        for (i = 0; i < bta1.length; i++) {
            bta1[i] = new JButton();
            bta1[i].setFont(myFont);
            bta1[i].setName(Integer.toString(1 * 256 + i));
            bta1[i].addMouseListener(mslis);
            bta1[i].setVisible(false);
            bta1[i].setText("" + i);
            pn1.add(bta1[i]);
        }

        bta1[0].setText("Test");
        bta1[1].setText("MqttAgent");
        bta1[15].setText("Exit");
        bta1[2].setText("DemoClass");
        bta1[3].setText("UartTest");
        bta1[4].setText("SetPanel");
        GB.coBt = bta1[0].getBackground();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int i;
        //=======================================================
        //=======================================================
        Base3 cla = new Base3();
        if (cla.frameOn_f == 0) {
            cla.setUndecorated(true);
        }
        base3Cla = cla;
        cla.x.act(0);
        cla.x.act(1);
        cla.x.act(2);
        netInf(0);
        cla.setVisible(true);
        if (cla.debug_f != 1) {
            MqttAgent mqttAgent = new MqttAgent(cla, true);
            mqttAgent.create();
            Lib.thSleep(10);
            mqttAgent.setVisible(true);
            mqttAgent.destroy();
            System.exit(0);
        }

    }

    void readDatabase() {
        Connection c = null;
        String dbPath = GB.setdata_db;
        try {
            File f = new File(dbPath);
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            //==============================================
            java.sql.Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM paraTable;");
            String paraName = "", paraValue = "";
            while (rs.next()) {
                paraName = rs.getString("paraName");
                paraValue = rs.getString("paraValue");
                GB.editNewPara(paraName, paraValue);
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e2) {
            System.err.println(e2);
            Base3.log.error(e2);
            JOptionPane.showMessageDialog(null, e2.toString(), "Error", 0);
            System.exit(1);
        }
    }

    /*
    void wdateDb(String paraName, String paraValue) {
        Connection c = null;
        String dbPath = GB.setdata_db;
        String sql;
        try {
            File f = new File(dbPath);
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            java.sql.Statement stmt = c.createStatement();
            sql = "UPDATE paraTable set paraValue = \"";
            sql = sql + paraValue;
            sql = sql + "\" where paraName=\"";
            sql = sql + paraName;
            sql = sql + "\";";
            stmt.executeUpdate(sql);
            c.commit();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
    
     */
    public static boolean checkDb(String paraName) {
        String dbPath = GB.setdata_db;
        Connection con = null;
        String pName;
        String pValue;
        String sbuf;
        boolean ret = false;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            con.setAutoCommit(false);
            //==============================================
            java.sql.Statement stmt = con.createStatement();
            sbuf = "SELECT * FROM paraTable;";
            ResultSet rs = stmt.executeQuery(sbuf);
            while (rs.next()) {
                pName = rs.getString("paraName");
                pValue = rs.getString("paraValue");
                if (pName.equals(paraName)) {
                    ret = true;
                    break;
                }
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e2) {
            System.err.println(e2);
            Base3.log.error(e2);
        }
        return ret;
    }

    public static String readDb(String paraName) {
        String dbPath = GB.setdata_db;
        Connection con = null;
        String pName;
        String pValue;
        String sbuf;
        String ret = "";
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            con.setAutoCommit(false);
            //==============================================
            java.sql.Statement stmt = con.createStatement();
            sbuf = "SELECT * FROM paraTable;";
            ResultSet rs = stmt.executeQuery(sbuf);
            while (rs.next()) {
                pName = rs.getString("paraName");
                pValue = rs.getString("paraValue");
                if (pName.equals(paraName)) {
                    ret = pValue;
                    break;
                }
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e2) {
            System.err.println(e2);
            Base3.log.error(e2);
        }
        return ret;
    }

    public static int editNewDb(String paraName, String paraValue) {
        int line;
        line = editDb(paraName, paraValue);
        if (line > 0) {
            return line;
        }
        line = insertDb(paraName, paraValue);
        return line;
    }

    public static int editDb(String paraName, String paraValue) {
        String dbPath = GB.setdata_db;
        Connection con;
        String sql;
        int chgLine = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            con.setAutoCommit(false);
            java.sql.Statement stmt = con.createStatement();
            //UPDATE paraTable set 
            sql = "UPDATE paraTable set paraValue = \"";
            sql = sql + paraValue;
            sql = sql + "\" where paraName=\"";
            sql = sql + paraName;
            sql = sql + "\";";
            chgLine = stmt.executeUpdate(sql);
            con.commit();
            stmt.close();
            con.close();
        } catch (Exception e2) {
            System.err.println(e2);
            Base3.log.error(e2);
        }
        return chgLine;
    }

    public static int deleteDb(String paraName) {
        String dbPath = GB.setdata_db;
        Connection con;
        String sql;
        int chgLine = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            con.setAutoCommit(false);
            java.sql.Statement stmt = con.createStatement();
            //UPDATE paraTable set 
            sql = "DELETE from paraTable where paraName=\"";
            sql = sql + paraName;
            sql = sql + "\";";
            chgLine = stmt.executeUpdate(sql);
            con.commit();
            stmt.close();
            con.close();
        } catch (Exception e2) {
            System.err.println(e2);
            Base3.log.error(e2);
        }
        return chgLine;
    }

    public static int deleteAllDb() {
        String dbPath = GB.setdata_db;
        Connection con;
        String sql;
        int chgLine = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            con.setAutoCommit(false);
            java.sql.Statement stmt = con.createStatement();
            //UPDATE paraTable set 
            sql = "DELETE from paraTable;";
            chgLine = stmt.executeUpdate(sql);
            con.commit();
            stmt.close();
            con.close();
        } catch (Exception e2) {
            System.err.println(e2);
            Base3.log.error(e2);
        }
        return chgLine;
    }

    //statement.executeUpdate("INSERT INTO Customers " + "VALUES (1001, 'Simpson', 'Mr.', 'Springfield', 2001)");
    public static int insertDb(String paraName, String paraValue) {
        String dbPath = GB.setdata_db;
        Connection con = null;
        String sql;
        int chgLine = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            con.setAutoCommit(false);
            java.sql.Statement stmt = con.createStatement();
            sql = "INSERT INTO paraTable VALUES ('";
            sql += paraName;
            sql += "','";
            sql += paraValue;
            sql += "');";
            chgLine = stmt.executeUpdate(sql);
            con.commit();
            stmt.close();
            con.close();
        } catch (Exception e2) {
            System.err.println(e2);
            Base3.log.error(e2);
        }
        return chgLine;
    }

    public static String netInf(int ww) {
        InetAddress ip;
        int i;
        int sti;
        String str = null;
        String retStr = null;
        String localIp = null;
        StringBuilder sb;
        String[] strA;
        strA = GB.set_localNet_Inf.split("~");
        if (strA.length >= 4) {
            GB.set_localNet_ip = strA[0];
            GB.set_localNet_mask = strA[1];
            GB.set_localNet_gateway = strA[2];
        }

        String fnameInterfaces;
        fnameInterfaces = GB.interfaces;
        if (Lib.fsearchEnd(fnameInterfaces, "address ", " ") == 1) {
            GB.set_localNet_ip = Lib.retstr;
        }
        if (Lib.fsearchEnd(fnameInterfaces, "netmask ", " ") == 1) {
            GB.set_localNet_mask = Lib.retstr;
        }
        if (Lib.fsearchEnd(fnameInterfaces, "gateway ", " ") == 1) {
            GB.set_localNet_gateway = Lib.retstr;
        }

        strA[0] = GB.set_localNet_ip;
        strA[1] = GB.set_localNet_mask;
        strA[2] = GB.set_localNet_gateway;

        GB.set_localNet_Inf = strA[0] + "~" + strA[1] + "~" + strA[2] + "~" + strA[3];

        try {

            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress ia = (InetAddress) ee.nextElement();
                    str = ia.getHostAddress();
                    System.out.println(str);
                    sti = str.indexOf("192.168.");
                    if (sti >= 0) {
                        localIp = str;
                    }
                }
            }

            if (localIp == null) {
                return "";
            }
            //ip = InetAddress.getLocalHost();
            //ip = InetAddress.getByName("192.168.0.57");
            ip = InetAddress.getByName(localIp);
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            //if(network==null)
            //    return;
            byte[] mac = network.getHardwareAddress();

            System.out.print("Current MAC address : ");

            sb = new StringBuilder();
            for (i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            System.out.println("sb.toString " + sb.toString());
            str = "";
            if (mac.length < 6) {
                GB.syssec_f = 1;
            } else {
                sb = new StringBuilder();
                sb.append(String.format("%02X%s", mac[0], (i < mac.length - 1) ? "" : ""));
                sb.append(String.format("%02X%s", mac[1], (i < mac.length - 1) ? "" : ""));
                sb.append(String.format("%02X%s", mac[2], (i < mac.length - 1) ? "" : ""));
                sb.append(String.format("%02X%s", mac[3], (i < mac.length - 1) ? "" : ""));
                sb.append(String.format("%02X%s", mac[4], (i < mac.length - 1) ? "" : ""));
                sb.append(String.format("%02X%s", mac[5], (i < mac.length - 1) ? "" : ""));
                retStr = sb.toString();
                //=======================================================================
                mac[0] ^= 0xab;
                mac[1] ^= 0x12;
                mac[2] ^= 0x83;
                mac[3] ^= 0x29;
                mac[4] ^= 0x1b;
                mac[5] ^= 0x40;
                mac[0] ^= GB.syssec_xor;
                mac[1] ^= GB.syssec_xor;
                mac[2] ^= GB.syssec_xor;
                mac[3] ^= GB.syssec_xor;
                mac[4] ^= GB.syssec_xor;
                mac[5] ^= GB.syssec_xor;
                sb = new StringBuilder();
                sb.append(String.format("%02X%s", mac[3], (i < mac.length - 1) ? "" : ""));
                sb.append(String.format("%02X%s", mac[2], (i < mac.length - 1) ? "" : ""));
                sb.append(String.format("%02X%s", mac[5], (i < mac.length - 1) ? "" : ""));
                sb.append(String.format("%02X%s", mac[0], (i < mac.length - 1) ? "" : ""));
                sb.append(String.format("%02X%s", mac[1], (i < mac.length - 1) ? "" : ""));
                sb.append(String.format("%02X%s", mac[4], (i < mac.length - 1) ? "" : ""));
                str = sb.toString();
                System.out.println("str= " + str);
            }
            if (GB.syssec.equals(str)) {
                GB.syssec_f = 1;
            } else {
                if (ww != 0) {
                    editNewDb("syssec", str);
                }
            }
        } catch (UnknownHostException | SocketException e2) {
            System.out.println(e2);
            Base3.log.error(e2);
            JOptionPane.showMessageDialog(null, e2.toString(), "Error", 0);
            System.exit(1);
        }
        return retStr;
    }

    public static void wdateDb(String paraName, String paraValue) {
        Connection c = null;
        String dbPath = GB.setdata_db;
        String sql;
        try {
            File f = new File(dbPath);
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            java.sql.Statement stmt = c.createStatement();
            sql = "UPDATE paraTable set paraValue = \"";
            sql = sql + paraValue;
            sql = sql + "\" where paraName=\"";
            sql = sql + paraName;
            sql = sql + "\";";
            stmt.executeUpdate(sql);
            c.commit();
            stmt.close();
            c.close();
        } catch (Exception e2) {
            System.err.println(e2);
            Base3.log.error(e2);
        }

    }

}

class Base3WinLis extends WindowAdapter {

    Base3 cla;

    Base3WinLis(Base3 owner) {
        cla = owner;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        int ibuf;
        Rectangle r = new Rectangle();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        cla.cp.setLayout(null);
        cla.pn1.setLayout(null);
        cla.ret_f = 0;

        if (cla.debug_f == 1) {
            cla.sp1.setVisible(true);
            cla.ta1.setVisible(true);
            cla.pn1.setVisible(true);
        } else {
            cla.sp1.setVisible(false);
            cla.ta1.setVisible(false);
            cla.pn1.setVisible(false);
        }

        //======================================================
        if (cla.fullScr_f == 1) {
            if (cla.frameOn_f == 1) {
                cla.setExtendedState(JFrame.MAXIMIZED_BOTH);
                cla.cp.setBounds(0, 0, screenSize.width - GB.winFrame_wm1, screenSize.height - GB.winFrame_hm1);
            } else {
                cla.setExtendedState(JFrame.MAXIMIZED_BOTH);
                cla.cp.setBounds(0, 0, screenSize.width - GB.winFrame_wm2, screenSize.height - GB.winFrame_hm2);
            }
        } else {
            if (cla.frameOn_f == 1) {
                r.width = cla.winW;
                r.height = cla.winH;
                r.x = (screenSize.width - r.width) / 2;
                r.y = (screenSize.height - r.height - GB.winFrame_th3) / 2;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width - GB.winFrame_wm3, r.height - GB.winFrame_hm3);
            } else {
                r.width = cla.winW;
                r.height = cla.winH;
                r.x = (screenSize.width - r.width) / 2;
                r.y = (screenSize.height - r.height - GB.winFrame_th4) / 2;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width - GB.winFrame_wm4, r.height - GB.winFrame_hm4);
            }
        }
        cla.cp.setBackground(Color.blue);
        cla.pn1.setBackground(Color.black);
        //=================================
        MyLayout.ctrA[0] = cla.sp1;
        MyLayout.rateH = 0.9;
        MyLayout.gridLy();
        ibuf = MyLayout.yend;
        //=================================
        MyLayout.ctrA[0] = cla.lb1;
        MyLayout.rateH = 1;
        MyLayout.gridLy();
        //=================================
        MyLayout.yst = ibuf;
        MyLayout.ctrA[0] = cla.pn1;
        MyLayout.rateH = 100;
        MyLayout.gridLy();
        //=================================

        for (int i = 0; i < cla.bta1.length; i++) {
            MyLayout.ctrA[i] = cla.bta1[i];
            cla.bta1[i].setVisible(true);
        }
        MyLayout.eleAmt = cla.bta1.length;
        MyLayout.xc = cla.bta1.length / 2;
        MyLayout.yc = 2;
        MyLayout.gridLy();

    }
}

//act 0: read para from setdata.xml
//act 1: read para frm database
//act 2: trans para to global various 
class Action {

    Base3 cla;

    Action(Base3 owner) {
        cla = owner;
    }

    public void act(int index) {
        String str;
        switch (index) {
            case 0: //read setdata.xml to GB.paraName[],GB.paraValue[],  
                System.out.println("read setdata.xml");
                try {
                    FileReader reader = new FileReader(GB.setdata_xml);
                    BufferedReader br = new BufferedReader(reader);
                    String line;
                    String paraN;
                    String paraV;
                    GB.clrPara();
                    while ((line = br.readLine()) != null) {
                        if (Lib.search(line, "[", "]") == 1) {
                            paraN = Lib.retstr;
                            if (Lib.search(line, "<", ">") == 1) {
                                paraV = Lib.retstr;
                                GB.newPara(paraN, paraV);
                            }
                        }
                    }
                } catch (IOException e2) {
                    System.err.println(e2);
                    Base3.log.error(e2);
                    JOptionPane.showMessageDialog(null, e2.toString(), "Error", 0);
                    System.exit(1);
                }
                break;
            case 1:
                System.out.println("Action 1");
                cla.readDatabase();
                break;
            case 2:
                System.out.println("Action 2");
                GB.loadPara2Form();
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            case 13:
                break;
            case 14:
                break;
            case 15:
                break;
            case 16:
                break;
            case 17:
                break;

        }

    }
}

class Base3MsLis extends MouseAdapter {

    int enkey_f;
    Base3 cla;

    Base3MsLis(Base3 owner) {
        cla = owner;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int index;
        if (enkey_f != 1) {
            return;
        }
        index = Integer.parseInt(e.getComponent().getName());
        //Test cla=Test.thisCla; 
        switch (index) {
            case 1 * 256 + 0:
                Test test = new Test(cla, true);
                test.setVisible(true);
                break;
            case 1 * 256 + 1:
                MqttAgent mqttAgent = new MqttAgent(cla, true);
                mqttAgent.create();
                mqttAgent.setVisible(true);
                break;
            /*
                ScrSpA phone1 = new ScrSpA(cla, true);
                phone1.title_str = "JOSN Sip ScrSpA";
                phone1.create();
                phone1.setVisible(true);
             */
            case 1 * 256 + 2:
                DemoClass demo1 = new DemoClass(null, true);
                demo1.create();
                demo1.setVisible(true);
                break;
            case 1 * 256 + 3:
                if (cla.uartTest1 == null) {
                    cla.uartTest1 = new UartTest(null, true);
                    cla.uartTest1.create();
                }
                cla.uartTest1.setVisible(true);
                break;
            case 1 * 256 + 4:
                SetPanel spn1;
                spn1 = new SetPanel(null, true);
                spn1.xc = 3;
                spn1.winW = 600;
                spn1.title_str = "Set Boudrate";
                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 1) {
                    //cla.comBoudRate = Select.retStr;
                    //GB.comBoudRate = cla.comBoudRate;
                    //Base3.editNewDb("comBoudRate", GB.comBoudRate);
                }
                break;
            case 1 * 256 + 8:
                FileReader reader;
                try {
                    reader = new FileReader("./tras.txt");
                    BufferedReader br = new BufferedReader(reader);
                    String[] outStrA = new String[100];
                    String[] strA;
                    String str;
                    int arrayCnt;
                    int rlen;
                    int totalLen;
                    int outStrA_inx = 0;
                    int i;
                    totalLen = 0;
                    int tp = 0;
                    while ((str = br.readLine()) != null) {
                        strA = Lib.sepCppIns(str);
                        if (strA == null) {
                            continue;
                        }
                        rlen = 0;
                        switch (strA[0]) {
                            case "char":
                                tp = 0;
                                rlen = 1;
                                break;
                            case "unsigned char":
                                tp = 1;
                                rlen = 1;
                                break;
                            case "int8_t":
                                tp = 2;
                                rlen = 1;
                                break;
                            case "uint8_t":
                                tp = 3;
                                rlen = 1;
                                break;
                            case "int16_t":
                                tp = 4;
                                rlen = 2;
                                break;
                            case "uint16_t":
                                tp = 5;
                                rlen = 2;
                                break;
                            case "int32_t":
                            case "int":
                                tp = 6;
                                rlen = 4;
                                break;
                            case "uint32_t":
                            case "unsigned int":
                                tp = 7;
                                rlen = 4;
                                break;
                            case "float":
                                tp = 8;
                                rlen = 4;
                                break;
                            case "double":
                                tp = 9;
                                rlen = 8;
                                break;

                            default:
                                rlen = 0;
                                break;

                        }
                        if (rlen == 0) {
                            continue;
                        }
                        if (rlen == 2) {
                            if ((totalLen & 1) != 0) {
                                totalLen++;
                            }
                        }
                        if (rlen == 4) {
                            if ((totalLen & 3) != 0) {
                                totalLen &= 0xfffffffc;
                                totalLen += 4;
                            }
                        }
                        if (rlen == 8) {
                            if ((totalLen & 7) != 0) {
                                totalLen &= 0xfffffff8;
                                totalLen += 8;
                            }
                        }

                        arrayCnt = Lib.str2int(strA[2], 0);
                        str = "" + tp;
                        str += "~" + rlen;
                        str += "~" + String.format("%4d", arrayCnt);
                        str += "~" + String.format("%4d", totalLen);
                        str += "~" + strA[1];
                        if (strA[3].charAt(0) == '{') {
                            strA[3] = strA[3].substring(1, strA[3].length() - 1);
                            strA[3] = "[" + strA[3] + "]";
                        }
                        str += "~" + strA[3];

                        outStrA[outStrA_inx++] = str;
                        if (arrayCnt > 0) {
                            totalLen += rlen * arrayCnt;
                        } else {
                            totalLen += rlen;
                        }

                    }

                    FileWriter fw = new FileWriter("./testOut.txt");
                    str = "//=====================================\n";
                    str += "// 0: char, 1: uchar, 2: int8_t, 3: uint8_t, 4: int16_t, 5: uint16_t, \n";
                    str += "// 6: int, 7: uint, 8: float, 9: double, \n";
                    str += "const int decA_len=" + outStrA_inx + ";\n";
                    str += "string dscA[decA_len] = {\n";
                    for (i = 0; i < outStrA_inx; i++) {
                        if (i != 0) {
                            str += ",\n";
                        }
                        str += "    \"";
                        str += outStrA[i];
                        str += "\"";
                    }
                    str += "\n};\n";
                    str += "//=====================================\n";
                    fw.write(str);
                    fw.flush();
                    fw.close();

                } catch (Exception e2) {
                    System.err.println(e2);
                    Base3.log.error(e2);
                }

                break;

            case 1 * 256 + 15:
                System.exit(0);
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
