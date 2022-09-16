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
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.json.JSONObject;
import redis.clients.jedis.Response;

/*
    Resis JidObj format:
key: hash~jid~Jxxxxxxxxxxxxxx
name:<type>~<name>          //type:U:undefined, S:string, L:long, D:Double,B:byte,O:object, SA,LA,DA,BA...... 
value:<data>
...
...

 */
public class MqttAgent extends javax.swing.JDialog {

    static public int ret_i;
    static public int ret_f = 0;
    static public String retStr = "";
    static MqttAgent scla;

    int mqttPubCnt = 0;

    MqttAgent cla = this;

    String title_str = "title_str";
    int fullScr_f = 1;
    int frameOn_f = 1;
    int winW = 1920;//1934
    int winH = 1040;//1947

    int deviceClearConnectTime = 1500 * 60;//suggest set the device tick period is 1 minute.

    Timer tm1 = null;
    int td1_run_f = 0;
    MqttAgentTd1 td1 = null;
    int td1_destroy_f = 0;

    Container cp;
    JButton[] btBarA = new JButton[16];
    JButton[] btRightA = new JButton[24];
    JButton[] btLeftA = new JButton[48];
    JPanel pnMain, pnBar;
    JPanel pnCenter, pnLeft, pnRight;
    JTextPane tp1;
    JScrollPane sp1;
    SimpleAttributeSet tp1FontSet = new SimpleAttributeSet();

    JLabel lbMessage;
    JLabel lbStatus;
    MyMqtt myMqtt = new MyMqtt();
    MyMqtt webMqtt = new MyMqtt();
    MyMqtt deviceMqtt = new MyMqtt();

    int viewLineCount = 0;
    int viewLineChars = 0;
    int viewTrace_f = 1;
    Color coBt;
    int showStatusTime = 0;

    int funcMode = 0;

    int agentTxCmdNo = 0;
    int webTxCmdNo = 0;
    int deviceTxCmdNo = 0;

    int disShowTx_f = 0;
    int disShowRx_f = 0;
    int disShowAgent_f = 0;
    int disShowWeb_f = 0;
    int disShowDevice_f = 0;

    Map<String, JidObj> jidMap = new HashMap();
    Map<String, String> pathTreeMap = new HashMap();
    Map<String, String> connectMap = new HashMap();

    Jdsc jdsc = new Jdsc(this);
    ClassDsc cdsc = new ClassDsc(this);

    //ArrayList<JidObj> agentLs = new ArrayList<>();
    //ArrayList<JidObj> webLs = new ArrayList<>();
    //ArrayList<JidObj> groupLs = new ArrayList<>();
    //ArrayList<JidObj> truckLs = new ArrayList<>();
    //ArrayList<JidObj> relayLs = new ArrayList<>();
    //ArrayList<JidObj> tyreLs = new ArrayList<>();
    static final int TRUCKS_AMT = 32;
    static final int RELAYS_AMT = TRUCKS_AMT * 3;
    static final int TYRE_AMT = RELAYS_AMT * 8;
    EmuTpmsAiot eta = new EmuTpmsAiot(cla);
    int redis_ok_f = 0;

    //TruckObject[] trucks = new TruckObject[TRUCKS_AMT];
    //RelayObject[] relays = new RelayObject[RELAYS_AMT];
    //TyreObject[] tyres = new TyreObject[TYRE_AMT];
    //static MyLayout ly=new MyLayout();
    public MqttAgent(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        MqttAgent cla = this;
        MqttAgent.scla = this;
        //jobjDsc.mja.put("agentLs", agentLs);
        //jobjDsc.mja.put("webLs", webLs);
        //jobjDsc.mja.put("groupLs", groupLs);
        //jobjDsc.mja.put("truckLs", truckLs);
        //jobjDsc.mja.put("relayLs", relayLs);
        //jobjDsc.mja.put("tyreLs", tyreLs);
        if (cla.frameOn_f == 0) {
            cla.setUndecorated(true);
        }
        cla.setBounds(-100, -100, 0, 0);

        //JidObj jo = cdsc.newClass("TruckJobj");
        //agentLs.add(new AgentObject("J003F0000000000","josnMqttAgent"));
        //webLs.add(new WebObject("J010F0000000000","webUser1"));
        //webLs.add(new WebObject("J010F0000000001","webUser2"));
        //webLs.add(new WebObject("J010F0000000002","webUser3"));

        /*
            jary = new JidArray("TruckObject", 10);
            for (int j = 0; j < jary.amount; j++) {
                jary.jids.set(j, "J403F00000000" + i + j);
            }
            obj.jidArrayList.add(jary);
            jidMap.put(obj.jid, obj);
            groupLs.add(obj);
         */
        //initTyre();
        //initRelay();
        //initTruck();
    }

    public void destroy() {
    }

    public void create() {
        int i;
        MqttAgent cla = this;
        cla.setTitle("MqttAgent " + GB.version);
        Font myFont = new Font("Serif", Font.BOLD, 14);

        cla.addWindowListener(new MqttAgentWinLis(cla));
        MqttAgentMsLis mslis = new MqttAgentMsLis(cla);
        myMqtt.mqttGetMessage = new MqttGetMessage() {
            @Override
            public void getMessage(String topic, String msg) {
                getAgentMqttMsg(topic, msg);
            }
        };

        webMqtt.mqttGetMessage = new MqttGetMessage() {
            @Override
            public void getMessage(String topic, String msg) {
                getWebMqttMsg(topic, msg);
            }
        };

        deviceMqtt.mqttGetMessage = new MqttGetMessage() {
            @Override
            public void getMessage(String topic, String msg) {
                getDeviceMqttMsg(topic, msg);
            }
        };

        //===============================================
        cp = cla.getContentPane();
        cp.setLayout(null);
        cp.setBackground(Color.CYAN);
        pnMain = new JPanel();
        pnBar = new JPanel();
        pnLeft = new JPanel();
        pnRight = new JPanel();
        pnCenter = new JPanel();
        cp.add(pnMain);
        cp.add(pnBar);
        pnMain.add(pnLeft);
        pnMain.add(pnCenter);
        pnMain.add(pnRight);
        pnMain.setBackground(Color.black);

        tp1 = new JTextPane();
        sp1 = new JScrollPane(cla.tp1);
        pnCenter.add(cla.sp1);
        tp1.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        StyleConstants.setFontSize(tp1FontSet, 12);//设置字体大小

        lbMessage = new JLabel();
        lbMessage.setBackground(Color.yellow);
        lbMessage.setOpaque(true);
        lbMessage.setText("");
        pnCenter.add(cla.lbMessage);

        lbStatus = new JLabel();
        lbStatus.setBackground(Color.yellow);
        lbStatus.setOpaque(true);
        lbStatus.setText("");
        pnLeft.add(cla.lbStatus);

        //===============================================
        for (i = 0; i < cla.btBarA.length; i++) {
            btBarA[i] = new JButton();
            btBarA[i].setFont(new Font(Font.SERIF, Font.BOLD, 14));
            btBarA[i].setName(Integer.toString(0 * 256 + i));
            btBarA[i].addMouseListener(mslis);
            btBarA[i].setVisible(true);
            btBarA[i].setFocusable(false);
            pnBar.add(btBarA[i]);
        }
        coBt = btBarA[0].getBackground();

        btBarA[0].setText("Delete All Database");
        btBarA[1].setText("Show Redis Keys");
        btBarA[2].setText("Del Redis Key");
        btBarA[3].setText("Show Redis Key Value");
        btBarA[4].setText("");
        btBarA[5].setText("");
        btBarA[8].setText("Get Jid Tree Map");
        btBarA[9].setText("Agent Set");
        btBarA[15].setText("ESC");
        //===============================================
        for (i = 0; i < cla.btLeftA.length; i++) {
            btLeftA[i] = new JButton();
            btLeftA[i].setFont(new Font(Font.SERIF, Font.BOLD, 12));
            btLeftA[i].setName(Integer.toString(1 * 256 + i));
            btLeftA[i].addMouseListener(mslis);
            btLeftA[i].setVisible(true);
            btLeftA[i].setFocusable(false);
            pnLeft.add(btLeftA[i]);
        }
        btLeftA[0].setText("REDIS Server Setting");
        btLeftA[1].setText("REDIS Server Test");

        btLeftA[2].setText("AgentMQTT Server Setting");
        btLeftA[3].setText("WebMQTT Server Setting");
        btLeftA[4].setText("DeviceMQTT Server Setting");
        btLeftA[5].setText("MQTT Connect All");
        btLeftA[6].setText("MQTT Tick All");
        btLeftA[7].setText("Get Connected Jid");

        btLeftA[12].setText("Emu TPMS Aiot");

//===============================================
        for (i = 0; i < cla.btRightA.length; i++) {
            btRightA[i] = new JButton();
            btRightA[i].setFont(myFont);
            btRightA[i].setName(Integer.toString(2 * 256 + i));
            btRightA[i].addMouseListener(mslis);
            btRightA[i].setVisible(true);
            btRightA[i].setFocusable(false);
            pnRight.add(btRightA[i]);
        }
        btRightA[1].setText("Clear");
        btRightA[2].setText("Trace");
        btRightA[3].setText("Disable Show Tx");
        btRightA[4].setText("Disable Show Rx");
        btRightA[5].setText("Disable Show Agent");
        btRightA[6].setText("Disable Show Web");
        btRightA[7].setText("Disable Show Device");

        //==
        if (cla.tm1 == null) {
            cla.tm1 = new Timer(20, new MqttAgentTm1(cla));
            cla.tm1.start();
        }
        /*
        //==
        if (cla.td1 == null) {
            cla.td1 = new MqttAgentTd1(cla);
            cla.td1.start();
            cla.td1_run_f = 1;
            cla.td1_destroy_f = 0;
        }
         */

        KvRedis.host = GB.redisServerAddr;
        KvRedis.port = Integer.parseInt(GB.redisServerPort);
        KvRedis.passw = GB.redisServerPassword;

        //eta.init();
    }

    public int agentMqttPub(String topic, String mes) {
        String pubStatus;
        if (myMqtt.connected == 0) {
            return 1;
        }
        if (disShowTx_f == 0) {
            if (disShowAgent_f == 0) {
                insertDocument("A: " + topic + "\n", GB.blue0);
                insertDocument("   " + mes + "\n", GB.blue0);
            }
        }
        pubStatus = myMqtt.pub(topic, mes);
        if (pubStatus == null) {
            return 0;
        }
        return 2;
    }

    public void webMqttPub(String topic, String mes) {
        if (webMqtt.connected == 0) {
            insertDocument("S: " + "Pub Error, Web Mqtt No Connection !!!" + "\n", GB.red0);
            return;
        }
        if (disShowTx_f == 0) {
            if (disShowWeb_f == 0) {
                insertDocument("W: " + topic + "\n", GB.blue0);
                insertDocument("   " + mes + "\n", GB.blue0);
            }
        }
        webMqtt.pub(topic, mes);
    }

    public void deviceMqttPub(String topic, String mes) {
        if (deviceMqtt.connected == 0) {
            return;
        }
        if (disShowTx_f == 0) {
            if (disShowDevice_f == 0) {
                insertDocument("D: " + topic + "\n", GB.blue0);
                insertDocument("   " + mes + "\n", GB.blue0);
            }
        }
        deviceMqtt.pub(topic, mes);
    }

    public void getAgentMqttMsg(String topic, String msg) {
        String[] strA = topic.split("/");
        if (strA.length != 2) {
            return;
        }
        if (!strA[0].equals("mqttAgentRX")) {
            return;
        }
        if (disShowRx_f == 0) {
            if (disShowAgent_f == 0) {
                synchronized (tp1) {
                    insertDocument("A: " + topic + "\n", GB.green0);
                    insertDocument("   " + msg + "\n", GB.green0);
                }
            }
        }
        String sendJid = strA[1];
        Myjs mj = new Myjs();
        mj.cbk = new MapCbk1() {
            @Override
            public String prg(String sendJid, Map<String, String> map, String para) {
                return agentMqttRxProcess(sendJid, map, para);
            }
        };
        mj.anaRxJs(sendJid, msg, "");
    }

    public String prg(Map<String, String> map) {
        return null;
    }

    public void getWebMqttMsg(String topic, String msg) {
        if (disShowRx_f == 0) {
            if (disShowWeb_f == 0) {
                synchronized (tp1) {
                    insertDocument("W: " + topic + "\n", GB.green0);
                    insertDocument("   " + msg + "\n", GB.green0);
                }
            }
        }
    }

    public void getDeviceMqttMsg(String topic, String msg) {

        if (disShowRx_f == 0) {
            if (disShowDevice_f == 0) {
                synchronized (tp1) {
                    insertDocument("D: " + topic + "\n", GB.green0);
                    insertDocument("   " + msg + "\n", GB.green0);
                }    
            }
        }

        String[] strA = topic.split("/");
        if (strA.length != 2) {
            return;
        }
        if (!strA[0].equals("mqttAgentRX")) {
            return;
        }

    }

    public void mqttGet(String topic, String mes) {
    }

    public void insertDocument(String text, Color textColor)//根据传入的颜色及文字，将文字插入文本域
    {

        if (tp1 == null) {
            return;
        }
        String str = "";
        char[] bts = text.toCharArray();
        for (int i = 0; i < bts.length; i++) {
            str += bts[i];
            if (bts[i] == '\n') {
                viewLineChars = 0;
                viewLineCount++;
                continue;
            }
            viewLineChars += 1;
            if (viewLineChars >= GB.viewMaxLineChars) {
                str += "\n";
                viewLineChars = 0;
                viewLineCount++;
            }
        }
        SimpleAttributeSet fontSet = new SimpleAttributeSet();
        
        
        StyleConstants.setForeground(fontSet, textColor);//设置文字颜色
        Document doc = tp1.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), str, fontSet);//插入文字
        } catch (BadLocationException e) {
        }
    }

    public void addTree(String pathRoot, String jid, int loopCnt) {
        String namePath;
        String pathStr;
        String newJid;

        JidObj jobj = jidMap.get(jid);
        if (jobj == null) {
            return;
        }
        for (int i = 0; i < jobj.jidArrayList.size(); i++) {
            JidArray ja = jobj.jidArrayList.get(i);
            for (int j = 0; j < ja.jids.size(); j++) {
                newJid = ja.jids.get(j);
                namePath = pathRoot + "." + ja.groupName + "#" + j + "{" + newJid + "}";
                //==============================
                pathStr = cla.pathTreeMap.get(newJid);
                if (pathStr == null) {
                    pathTreeMap.put(newJid, namePath);
                } else {
                    pathTreeMap.put(newJid, pathStr + "<~>" + namePath);
                }
                //======
                loopCnt++;
                if (loopCnt >= 16) {
                    return; //overflow 
                }
                addTree(namePath, newJid, loopCnt);
                loopCnt--;
            }
        }
    }

    public void reBuildTree() {
        pathTreeMap.clear();
        Map<String, JidObj> mj = cla.jdsc.getSjmByName("agentLs");
        String path = "agents";
        int inx = 0;
        for (String jid : mj.keySet()) {
            cla.addTree(path + "#" + inx + "{" + jid + "}", mj.get(jid).getv("jid"), 0);
            inx++;
        }
    }

    public void editWebPass(String jid, String minJid, String maxJid) {
        String str;
        Select sel1;
        SetPanel spn1;
        String[] strA;

        sel1 = new Select(null, true);
        sel1.title_str = "Select Web NO.";
        sel1.xc = 1;
        sel1.winW = 1000;
        sel1.selstr.add("Web 1");
        sel1.selstr.add("Web 2");
        sel1.selstr.add("Web 3");
        sel1.selstr.add("Web 4");
        sel1.selstr.add("Web 5");
        sel1.selstr.add("Web 6");
        sel1.create();
        sel1.setVisible(true);

        if (Select.ret_f == 0) {
            return;
        }
        str = GB.webGroupPass[Select.ret_i];
        strA = str.split("~");
        if (strA.length != 9) {
            strA = new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0"};
        }
        spn1 = new SetPanel(null, true);
        spn1.winW = 1000;
        spn1.winH = 0;
        spn1.nameWideRate = 0.6;
        spn1.title_str = "Edit Web Group Pass";
        str = "name:Web Jid;nameWRate:0.3;edit:1;check:6";
        spn1.list.add(new MyData(str, strA[0]));
        //==
        str = "name:1. Min Jid;edit:1;check:10";
        spn1.list.add(new MyData(str, strA[1]));
        str = "name:1. Max Jid;edit:1;check:10";
        spn1.list.add(new MyData(str, strA[2]));
        //==
        str = "name:2. Min Jid;edit:1;check:10";
        spn1.list.add(new MyData(str, strA[3]));
        str = "name:2. Max Jid;edit:1;check:10";
        spn1.list.add(new MyData(str, strA[4]));
        //==
        str = "name:3. Min Jid;edit:1;check:10";
        spn1.list.add(new MyData(str, strA[5]));
        str = "name:3. Max Jid;edit:1;check:10";
        spn1.list.add(new MyData(str, strA[6]));
        //==
        str = "name:4. Min Jid;edit:1;check:10";
        spn1.list.add(new MyData(str, strA[7]));
        str = "name:4. Max Jid;edit:1;check:10";
        spn1.list.add(new MyData(str, strA[8]));

        spn1.create();
        spn1.setVisible(true);
        if (SetPanel.ret_f == 1) {
            String str1 = spn1.tfa1[4 * 0 + 1].getText();
            String str2 = spn1.tfa1[4 * 1 + 1].getText();
            String str3 = spn1.tfa1[4 * 2 + 1].getText();
            String str4 = spn1.tfa1[4 * 3 + 1].getText();
            String str5 = spn1.tfa1[4 * 4 + 1].getText();
            String str6 = spn1.tfa1[4 * 5 + 1].getText();
            String str7 = spn1.tfa1[4 * 6 + 1].getText();
            String str8 = spn1.tfa1[4 * 7 + 1].getText();
            String str9 = spn1.tfa1[4 * 8 + 1].getText();
            str = str1 + "~" + str2 + "~" + str3;
            str += "~" + str4 + "~" + str5 + "~" + str6;
            str += "~" + str7 + "~" + str8 + "~" + str9;
            GB.webGroupPass[Select.ret_i] = str;
            Base3.editNewDb("webGroupPass~" + Select.ret_i, str);
        }

    }

    public void showPathTree() {
        for (String jid : pathTreeMap.keySet()) {
            insertDocument("\n" + jid + " ", GB.blue0);
            insertDocument(pathTreeMap.get(jid), GB.black);
        }
    }

    public void btBarFunc(int index) {
        Select sel1;
        String[] strA;
        String key;
        String value;
        Set<String> ss;
        SetPanel spn1;
        Message mes1;
        String str;
        switch (index) {
            case 0:
                mes1 = new Message(null, true);
                mes1.keyType_i = 1;
                mes1.mesType_i = 1;
                mes1.title_str = "Delete All Database ?";
                mes1.create();
                mes1.setVisible(true);
                if (Message.ret_i != 0) {
                    Base3.deleteAllDb();
                }
                break;
            case 1:
                spn1 = new SetPanel(null, true);
                spn1.winW = 800;
                spn1.title_str = "Show Redis Key";
                str = "name:Input Key;edit:1;nameWRate:0.4;";
                spn1.list.add(new MyData(str, ""));
                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 1) {
                    String retStr = spn1.tfa1[4 * 0 + 1].getText();
                    if (KvRedis.actOne("getKeys", "*".split("~"), null)) {
                        if (retStr.equals("*")) {
                            insertDocument(KvRedis.strSet.toString(), GB.black);
                            break;
                        }
                        if (retStr.length() == 0) {
                            insertDocument(KvRedis.strSet.toString(), GB.black);
                            break;
                        }
                        int next = 0;
                        insertDocument("[", GB.black);
                        for (String keyStr : KvRedis.strSet) {
                            if (Lib.compareString(keyStr, retStr) == 0) {
                                continue;
                            }
                            if (next == 1) {
                                insertDocument(", ", GB.black);
                            }
                            insertDocument(keyStr, GB.black);
                            next = 1;
                        }
                        insertDocument("]", GB.black);
                    }

                }

                break;
            case 2:
                spn1 = new SetPanel(null, true);
                spn1.winW = 600;
                spn1.title_str = "Delete Redis Key";
                str = "name:Name;edit:1;nameWRate:0.3;";
                spn1.list.add(new MyData(str, ""));
                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 1) {
                    KvRedis.actOne("delKeysFilter", spn1.tfa1[4 * 0 + 1].getText().split("<~>"), null);
                    cla.showStatus("redis");
                }
                break;
            case 3:
                spn1 = new SetPanel(null, true);
                spn1.winW = 800;
                spn1.title_str = "Show Redis Value";
                str = "name:Input Key;edit:1;nameWRate:0.4;";
                spn1.list.add(new MyData(str, ""));
                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 1) {
                    str = spn1.tfa1[4 * 0 + 1].getText();
                    strA = str.split("~");
                    if (strA[0].equals("set")) {
                        KvRedis.actOne("getSet", str.split("<~>"), null);
                        insertDocument(KvRedis.strSet.toString(), GB.black);
                    }
                    if (strA[0].equals("hash")) {
                        KvRedis.actOne("getHashAll", str.split("<~>"), null);
                        insertDocument(KvRedis.map.toString(), GB.black);
                    }
                    if (strA[0].equals("list")) {
                        str += "<~>" + "0";
                        str += "<~>" + "-1";
                        KvRedis.actOne("getList", str.split("<~>"), null);
                        insertDocument(KvRedis.strList.toString(), GB.black);
                    }

                    cla.showStatus("redis");
                }
                break;
            case 4:
                MesBox.mBox("", "456~agjkjkgkjgkkjkj", 0);
                MesBox.wBox(null, "456~agjkjkgkjgkkjkj", 1);
                MesBox.eBox(null, "456~agjkjkgkjgkkjkj", 2);
                MesBox.oBox(null, "456~agjkjkgkjgkkjkj", 0);
                if (MesBox.ret_f == 0) {

                }
                break;

            case 5:
                break;

            case 6:
                MyData initData = new MyData();
                Lib.setMyData(initData);
                break;

            case 11:

                break;

            case 7:
                break;
            case 8:
                cla.reBuildTree();
                cla.showPathTree();
                break;
            case 9:
                sel1 = new Select(null, true);
                sel1.title_str = "Agent Set";
                sel1.xc = 1;
                sel1.yc = 9;
                sel1.winW = 1000;
                sel1.selstr.add("Edit Webs Group Pass");
                sel1.selstr.add("Delete JidObj");
                sel1.selstr.add("View Group Model");
                sel1.selstr.add("Edit Group Model");
                sel1.selstr.add("View JidClass");
                sel1.selstr.add("Edit JidClass");

                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    break;
                }
                switch (Select.ret_i) {
                    case 0:
                        cla.editWebPass("", "", "");
                        break;
                    case 1:
                        spn1 = new SetPanel(null, true);
                        spn1.winW = 1000;
                        spn1.title_str = "Delete JidObj";
                        str = "name:Jid;edit:1;nameWRate:0.3;";
                        spn1.list.add(new MyData(str, ""));
                        spn1.create();
                        spn1.setVisible(true);
                        if (SetPanel.ret_f == 1) {
                            String jid = spn1.tfa1[4 * 0 + 1].getText();
                            jdsc.deleteJid(jid);

                            KvRedis.pipeAct("connect", null, null);
                            for (String name : cla.jdsc.mmDsc.keySet()) {
                                Map<String, String> mDsc = jdsc.mmDsc.get(name);
                                key = "set~jid~" + mDsc.get("className") + "~" + name;
                                KvRedis.pipeAct("delSet", key.split("<~>"), jid.split("~"));

                            }
                            key = "hash~jid~" + jid;
                            KvRedis.pipeAct("delKey", key.split("<~>"), null);
                            KvRedis.pipeAct("execute", null, null);

                            //KvRedis.actOne("delKeysFilter", spn1.tfa1[4 * 0 + 1].getText().split("~"), null);
                            //cla.showStatus("redis");
                        }
                        break;
                    case 2:
                        sel1 = new Select(null, true);
                        sel1.title_str = "View GroupModel";
                        sel1.xc = 2;
                        sel1.yc = 15;
                        sel1.winW = 1000;
                        sel1.align = 0;
                        sel1.dispNo = 1;
                        sel1.editNew = 0;
                        for (String jkey : jdsc.mmDsc.keySet()) {
                            sel1.selstr.add(jkey);
                        }
                        sel1.create();
                        sel1.cbkSelect = new StringCbk() {
                            @Override
                            public String prg(String istr) {
                                String str;
                                String[] strA = istr.split("<~>");
                                Map<String, String> smap = jdsc.mmDsc.get(strA[2]);
                                SetPanel spn2 = new SetPanel(null, true);
                                spn2.winW = 1000;
                                spn2.xc = 1;
                                spn2.yc = 9;
                                spn2.nameWideRate = 0.4;
                                spn2.title_str = strA[2];
                                for (String jkey : smap.keySet()) {
                                    str = "name:" + jkey;
                                    str += ";edit:" + "0";
                                    spn2.list.add(new MyData(str, smap.get(jkey)));

                                }
                                spn2.create();
                                spn2.setVisible(true);
                                if (SetPanel.ret_f == 1) {
                                }
                                return "ok";
                            }
                        };
                        sel1.setVisible(true);
                        break;
                    case 3:
                        Jdsc tmp = new Jdsc(cla);
                        final Select selGroupModel = new Select(null, true);
                        selGroupModel.title_str = "Edit GroupModel";
                        selGroupModel.xc = 2;
                        selGroupModel.yc = 15;
                        selGroupModel.winW = 1000;
                        selGroupModel.align = 0;
                        selGroupModel.dispNo = 1;
                        selGroupModel.editNew = 1;
                        selGroupModel.swapDisable_f = 1;
                        selGroupModel.saveDisable_f = 1;
                        for (String jkey : jdsc.mmDsc.keySet()) {
                            int same = 0;
                            for (String jjkey : tmp.mmDsc.keySet()) {
                                if (jjkey.equals(jkey)) {
                                    same = 1;
                                    break;
                                }
                            }
                            if (same == 1) {
                                continue;
                            }
                            selGroupModel.selstr.add(jkey);
                        }
                        selGroupModel.create();
                        //========================
                        selGroupModel.cbkEdit = new StringCbk() {
                            @Override
                            public String prg(String istr) {
                                return selGroupModel.cbkNew.prg(istr + "<~>edit");
                            }
                        };

                        selGroupModel.cbkSelect = new StringCbk() {
                            @Override
                            public String prg(String istr) {
                                return selGroupModel.cbkNew.prg(istr + "<~>view");
                            }
                        };

                        selGroupModel.cbkDelete = new StringCbk() {
                            @Override
                            public String prg(String istr) {
                                jdsc.delete(istr);
                                KvRedis.actOne("delSet", "set~groupModel".split("<~>"), istr.split("<~>"));
                                KvRedis.actOne("delKey", ("hash~groupModel~" + istr).split("<~>"), null);
                                selGroupModel.delete_f = 0;
                                return "ok~" + istr;
                            }
                        };
                        selGroupModel.cbkNew = new StringCbk() {
                            @Override
                            public String prg(String istr) {
                                String str;
                                String action = "";
                                final SetPanel spn1;
                                spn1 = new SetPanel(null, true);
                                spn1.winW = 1000;
                                spn1.winH = 0;
                                spn1.yc = 6;
                                spn1.editNew = 1;
                                spn1.editDisable_f = 1;
                                spn1.swapDisable_f = 1;
                                spn1.saveDisable_f = 1;
                                spn1.nameWideRate = 0.3;

                                String[] strA = istr.split("<~>");
                                if (strA.length == 1) {
                                    action = "new";
                                }
                                if (strA.length == 4) {
                                    if (strA[3].equals("edit")) {
                                        action = "edit";
                                    }
                                    if (strA[3].equals("view")) {
                                        action = "view";
                                    }
                                }
                                if (action.equals("new")) {
                                    spn1.title_str = "New GroupModel";
                                    spn1.editNew = 1;
                                    str = "name:name;check:8";
                                    spn1.list.add(new MyData(str, ""));
                                    str = "name:className;check:8;edit:0;enu:";
                                    int inx = 0;
                                    for (String keyName : cla.cdsc.mmCdsc.keySet()) {
                                        if (inx != 0) {
                                            str += "~";
                                        }
                                        str += keyName;
                                        inx++;
                                    }
                                    spn1.list.add(new MyData(str, ""));
                                    str = "name:sname";
                                    spn1.list.add(new MyData(str, ""));
                                    str = "name:jidHead;check:9";
                                    spn1.list.add(new MyData(str, ""));
                                }
                                if (action.equals("edit") || action.equals("view")) {
                                    if (action.equals("edit")) {
                                        spn1.title_str = "Edit GroupModel";
                                        spn1.editNew = 1;
                                    }
                                    if (action.equals("view")) {
                                        spn1.title_str = "View GroupModel";
                                        spn1.editNew = 0;
                                        spn1.onlyView_f = 1;
                                    }
                                    Map<String, String> smap = jdsc.mmDsc.get(strA[2]);
                                    for (String jkey : smap.keySet()) {
                                        str = "name:" + jkey;
                                        if (jkey.equals("name")) {
                                            str += ";check:8";
                                        }
                                        if (jkey.equals("jidHead")) {
                                            str += ";check:9";
                                        }
                                        if (jkey.equals("className")) {
                                            str += ";edit:0;check:9;enu:";
                                            int inx = 0;
                                            for (String keyName : cla.cdsc.mmCdsc.keySet()) {
                                                if (inx != 0) {
                                                    str += "~";
                                                }
                                                str += keyName;
                                                inx++;
                                            }
                                        }

                                        spn1.list.add(new MyData(str, smap.get(jkey)));

                                    }
                                }

                                spn1.cbkNew = new StringCbk() {
                                    @Override
                                    public String prg(String istr) {
                                        String str;
                                        SetPanel spn3 = new SetPanel(null, true);
                                        spn3.winW = 1000;
                                        spn3.xc = 1;
                                        spn3.nameWideRate = 0.2;
                                        spn3.title_str = "Input Son Group Name";
                                        str = "name:" + "Name";
                                        str += ";edit:" + "1";
                                        spn3.list.add(new MyData(str, ""));
                                        spn3.create();
                                        spn3.setVisible(true);
                                        if (SetPanel.ret_f == 1) {
                                            String name = "son~" + spn3.setValue.get(0);
                                            for (int i = 0; i < spn1.list.size(); i++) {
                                                if (spn1.list.get(i).name.equals(name)) {
                                                    Message.warnBox("This Name Is Existed !!!");
                                                    return "error";
                                                }
                                            }

                                            str = "name:" + "son~" + spn3.setValue.get(0);
                                            str += ";type:string";
                                            return "ok<~>" + str;
                                        }
                                        return "esc";
                                    }
                                };
                                spn1.cbkDelete = new StringCbk() {
                                    @Override
                                    public String prg(String istr) {
                                        String name = spn1.list.get(Integer.parseInt(istr)).name;
                                        if (name.split("~")[0].equals("son")) {
                                            spn1.delete_f = 0;
                                            return "ok";
                                        }
                                        return "error";
                                    }
                                };

                                spn1.create();

                                spn1.setVisible(true);
                                String modelName = "";
                                if (SetPanel.ret_f == 1) {
                                    for (int i = 0; i < spn1.list.size(); i++) {
                                        MyData md = spn1.list.get(i);
                                        if (md.name.equals("name")) {
                                            modelName = spn1.setValue.get(i);
                                        }
                                    }
                                    Map<String, String> mdsc = jdsc.mmDsc.get(modelName);
                                    if (mdsc != null) {
                                        Message.errorBox("This Name Is Existed !!!");
                                        return "error";
                                    }
                                    Map<String, String> modelMap = new HashMap();
                                    for (int i = 0; i < spn1.list.size(); i++) {
                                        MyData md = spn1.list.get(i);
                                        modelMap.put(md.name, spn1.setValue.get(i));
                                    }
                                    jdsc.mmDsc.put(modelName, modelMap);
                                    KvRedis.pipeAct("connect", null, null);
                                    cla.eta.setGroupModelToRedis(modelName, modelMap, 1);
                                    KvRedis.pipeAct("execute", null, null);
                                    return "ok~" + modelName;
                                }
                                return "esc";
                            }
                        };
                        selGroupModel.setVisible(true);
                        break;

                    case 4:
                        sel1 = new Select(null, true);
                        sel1.title_str = "View JidClass";
                        sel1.xc = 2;
                        sel1.yc = 15;
                        sel1.winW = 1000;
                        sel1.align = 0;
                        sel1.dispNo = 1;
                        sel1.editNew = 0;
                        for (String jkey : cdsc.mmCdsc.keySet()) {
                            sel1.selstr.add(jkey);
                        }
                        sel1.create();
                        sel1.cbkSelect = new StringCbk() {
                            @Override
                            public String prg(String istr) {
                                String str;
                                String[] strA = istr.split("<~>");
                                Map<String, String> smap = cdsc.mmCdsc.get(strA[2]);
                                SetPanel spn2 = new SetPanel(null, true);
                                spn2.winW = 1000;
                                spn2.xc = 1;
                                spn2.yc = 9;
                                spn2.nameWideRate = 0.4;
                                spn2.title_str = strA[2];
                                for (String jkey : smap.keySet()) {
                                    str = "name:" + jkey;
                                    str += ";edit:" + "0";
                                    spn2.list.add(new MyData(str, smap.get(jkey)));

                                }
                                spn2.create();
                                spn2.setVisible(true);
                                if (SetPanel.ret_f == 1) {
                                }
                                return "ok";
                            }
                        };
                        sel1.setVisible(true);
                        break;

                    case 5:
                        ClassDsc tmp1 = new ClassDsc(cla);
                        final Select selJidClass = new Select(null, true);
                        selJidClass.title_str = "Edit JidClass";
                        selJidClass.xc = 2;
                        selJidClass.yc = 15;
                        selJidClass.winW = 1000;
                        selJidClass.align = 0;
                        selJidClass.dispNo = 1;
                        selJidClass.editNew = 1;
                        selJidClass.swapDisable_f = 1;
                        selJidClass.saveDisable_f = 1;
                        for (String jkey : cdsc.mmCdsc.keySet()) {
                            int same = 0;
                            for (String jjkey : tmp1.mmCdsc.keySet()) {
                                if (jjkey.equals(jkey)) {
                                    same = 1;
                                    break;
                                }
                            }
                            if (same == 1) {
                                continue;
                            }
                            selJidClass.selstr.add(jkey);
                        }
                        selJidClass.create();
                        //========================
                        selJidClass.cbkEdit = new StringCbk() {
                            @Override
                            public String prg(String istr) {
                                return selJidClass.cbkNew.prg(istr + "<~>edit");
                            }
                        };

                        selJidClass.cbkSelect = new StringCbk() {
                            @Override
                            public String prg(String istr) {
                                return selJidClass.cbkNew.prg(istr + "<~>view");
                            }
                        };

                        selJidClass.cbkDelete = new StringCbk() {
                            @Override
                            public String prg(String istr) {
                                cdsc.delete(istr);
                                KvRedis.actOne("delSet", "set~jidClass".split("<~>"), istr.split("<~>"));
                                KvRedis.actOne("delKey", ("hash~jidClass~" + istr).split("<~>"), null);
                                cla.showStatus("redis");
                                selJidClass.delete_f = 0;
                                return "ok~" + istr;
                            }
                        };
                        selJidClass.cbkNew = new StringCbk() {
                            @Override
                            public String prg(String istr) {
                                String str;
                                String className = "";
                                String action = "";
                                final SetPanel spn1;
                                spn1 = new SetPanel(null, true);
                                spn1.winW = 1000;
                                spn1.winH = 0;
                                spn1.yc = 6;
                                spn1.editNew = 1;
                                spn1.editDisable_f = 1;
                                spn1.swapDisable_f = 1;
                                spn1.saveDisable_f = 1;
                                spn1.nameWideRate = 0.3;

                                String[] strA = istr.split("<~>");
                                if (strA.length == 1) {
                                    action = "new";
                                }
                                if (strA.length == 4) {
                                    if (strA[3].equals("edit")) {
                                        action = "edit";
                                    }
                                    if (strA[3].equals("view")) {
                                        action = "view";
                                    }
                                }
                                if (action.equals("new")) {
                                    spn1.title_str = "New JidClass";
                                    spn1.editNew = 1;
                                    str = "name:name;check:8";
                                    spn1.list.add(new MyData(str, ""));
                                }
                                if (action.equals("edit") || action.equals("view")) {
                                    className = strA[2];
                                    if (action.equals("edit")) {
                                        spn1.title_str = "Edit JidClass";
                                        spn1.editNew = 1;
                                    }
                                    if (action.equals("view")) {
                                        spn1.title_str = "View JidClass";
                                        spn1.editNew = 0;
                                        spn1.onlyView_f = 1;
                                    }
                                    Map<String, String> smap = cdsc.mmCdsc.get(strA[2]);
                                    for (String jkey : smap.keySet()) {
                                        str = "name:" + jkey;
                                        if (jkey.equals("name")) {
                                            str += ";check:8";
                                        }
                                        spn1.list.add(new MyData(str, smap.get(jkey)));

                                    }
                                }

                                spn1.cbkNew = new StringCbk() {
                                    @Override
                                    public String prg(String istr) {
                                        String str;
                                        SetPanel spn3 = new SetPanel(null, true);
                                        spn3.winW = 1000;
                                        spn3.xc = 1;
                                        spn3.nameWideRate = 0.2;
                                        spn3.title_str = "Input Class Menber Name";
                                        str = "name:" + "Name";
                                        str += ";edit:" + "1";
                                        spn3.list.add(new MyData(str, ""));
                                        spn3.create();
                                        spn3.setVisible(true);
                                        if (SetPanel.ret_f == 1) {
                                            String name = spn3.setValue.get(0);
                                            for (int i = 0; i < spn1.list.size(); i++) {
                                                if (spn1.list.get(i).name.equals(name)) {
                                                    Message.warnBox("This Name Is Existed !!!");
                                                    return "error";
                                                }
                                            }

                                            str = "name:" + spn3.setValue.get(0);
                                            str += ";type:string";
                                            return "ok<~>" + str;
                                        }
                                        return "esc";
                                    }
                                };
                                spn1.cbkDelete = new StringCbk() {
                                    @Override
                                    public String prg(String istr) {
                                        String name = spn1.list.get(Integer.parseInt(istr)).name;
                                        spn1.delete_f = 0;
                                        return "ok";
                                    }
                                };

                                spn1.create();

                                spn1.setVisible(true);
                                if (SetPanel.ret_f == 1) {
                                    for (int i = 0; i < spn1.list.size(); i++) {
                                        MyData md = spn1.list.get(i);
                                        if (md.name.equals("name")) {
                                            className = spn1.setValue.get(i);
                                        }
                                    }
                                    Map<String, String> mdsc = cdsc.mmCdsc.get(className);
                                    if (action.equals("new")) {
                                        if (mdsc != null) {
                                            Message.warnBox("This Name Is Existed !!!");
                                            return "error";
                                        }
                                    }
                                    Map<String, String> classMap = new HashMap();
                                    for (int i = 0; i < spn1.list.size(); i++) {
                                        MyData md = spn1.list.get(i);
                                        if (md.name.equals("name")) {
                                            continue;
                                        }
                                        classMap.put(md.name, spn1.setValue.get(i));
                                    }
                                    cdsc.mmCdsc.put(className, classMap);
                                    KvRedis.pipeAct("connect", null, null);
                                    cla.eta.setJidClassToRedis(className, classMap, 1);
                                    KvRedis.pipeAct("execute", null, null);
                                    return "ok~" + className;
                                }
                                return "esc";
                            }
                        };
                        selJidClass.setVisible(true);
                        break;
                }
                break;
            case 10:
                break;
            case 15:
                cla.dispose();
                MqttAgent.ret_f = 0;
                break;
        }

    }

    public void btRightFunc(int index) {
        String str;
        Select sel1;
        Numpad kpad;
        SetPanel spn1;
        String topic, mes;

        switch (index) {
            case 0:
                spn1 = new SetPanel(null, true);
                spn1.winW = 600;
                spn1.title_str = "Set View Parameter";
                str = "Max Chars In A Line";
                spn1.list.add(new MyData(str, GB.viewMaxLineChars));
                str = "Max Lines";
                spn1.list.add(new MyData(str, GB.viewMaxLines));

                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 1) {
                    GB.viewMaxLineChars = Lib.str2int(spn1.tfa1[4 * 0 + 1].getText(), 100, 1000, 10);
                    GB.viewMaxLines = Lib.str2int(spn1.tfa1[4 * 1 + 1].getText(), 200, 1000, 10);
                    Base3.editNewDb("viewMaxLineChars", "" + GB.viewMaxLineChars);
                    Base3.editNewDb("viewMaxLines", "" + GB.viewMaxLines);
                }
                break;

            case 1:
                cla.tp1.setText("");
                cla.viewLineCount = 0;
                cla.viewLineChars = 0;
                break;
            case 2:
                cla.viewTrace_f ^= 1;
                break;
            case 3:
                cla.disShowTx_f ^= 1;
                break;
            case 4:
                cla.disShowRx_f ^= 1;
                break;
            case 5:
                cla.disShowAgent_f ^= 1;
                break;
            case 6:
                cla.disShowWeb_f ^= 1;
                break;
            case 7:
                cla.disShowDevice_f ^= 1;
                break;
            case 23:
                insertDocument("Test color blue0" + "\n", GB.blue0);
                insertDocument("Test color green0" + "\n", GB.green0);
                insertDocument("Test color red0" + "\n", GB.red0);
                insertDocument("Test color blue1" + "\n", GB.blue1);
                insertDocument("Test color green1" + "\n", GB.green1);
                insertDocument("Test color red1" + "\n", GB.red1);

                break;

        }
    }

    void showStatus(String range) {
        switch (range) {
            case "redis":
                if (KvRedis.errCode == 0) {
                    setStatus(KvRedis.actStr, 1);
                }
                if (KvRedis.errCode != 0) {
                    setStatus(KvRedis.actStr, 2);
                }
                break;
        }
    }

    // type 0:normal 1:ok 2:error 
    public void setStatus(String str, int type) {
        switch (type) {
            default:
            case 0:
                cla.lbStatus.setForeground(GB.black);
                break;
            case 1:
                cla.lbStatus.setForeground(GB.green0);
                break;
            case 2:
                cla.lbStatus.setForeground(GB.red0);
                break;
        }
        cla.lbStatus.setText(str);
        cla.showStatusTime = 100;

    }

    public void checkRedisConnect() {
        setStatus("Redis Connect Checking ....", 0);
        cla.showStatusTime = 1000;
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                String str;
                if (KvRedis.testConnect()) {
                    setStatus("Redis Checked OK.", 1);
                    redis_ok_f = 1;
                    eta.init();
                } else {
                    setStatus("Redis Checked Error !!!", 2);
                    redis_ok_f = 0;
                }
            }
        });
        t1.start();
    }

    public void agentMqttConnect() {
        String str;
        myMqtt.broker = "tcp://" + GB.mqttServerAddr + ":" + GB.mqttServerPort;
        myMqtt.userName = GB.mqttServerUserName;
        myMqtt.passWord = GB.mqttServerPassword;
        myMqtt.qos = Integer.parseInt(GB.mqttQoS);
        myMqtt.clientId = GB.agentMqttClientId;
        setStatus("Agent MQTT Connecting ....", 0);
        cla.showStatusTime = 1000;
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                String str;
                try {
                    myMqtt.connect();
                    if (myMqtt.connected != 0) {
                        myMqtt.clearSubTopic();
                        for (int i = 0; i < GB.agentMqttSubA.length; i++) {
                            str = GB.agentMqttSubA[i].trim();
                            if (str.length() > 0) {
                                myMqtt.addSubTopic(str);
                            }
                        }
                        myMqtt.sub();
                        setStatus("Agent MQTT Connect OK.", 1);
                        return;
                    }
                    setStatus("Agent MQTT Connect Error !!!", 2);
                } catch (Exception e2) {
                    System.err.println(e2);
                    Base3.log.error(e2);
                }
            }
        });
        t1.start();

        /*
        myMqtt.connect();
        if (myMqtt.connected != 0) {
            myMqtt.clearSubTopic();
            for (int i = 0; i < GB.agentMqttSubA.length; i++) {
                str = GB.agentMqttSubA[i].trim();
                if (str.length() > 0) {
                    myMqtt.addSubTopic(str);
                }
            }
            myMqtt.sub();
        }
         */
    }

    public void webMqttConnect() {
        String str;
        webMqtt.broker = "tcp://" + GB.mqttServerAddr + ":" + GB.mqttServerPort;
        webMqtt.userName = GB.mqttServerUserName;
        webMqtt.passWord = GB.mqttServerPassword;
        webMqtt.qos = Integer.parseInt(GB.mqttQoS);
        webMqtt.clientId = GB.webMqttClientId;

        setStatus("Connect to MQTT ....", 0);
        webMqtt.connect();

        if (webMqtt.connected != 0) {
            webMqtt.clearSubTopic();
            for (int i = 0; i < GB.webMqttSubA.length; i++) {
                str = GB.webMqttSubA[i].trim();
                if (str.length() > 0) {
                    webMqtt.addSubTopic(str);
                }
            }
            webMqtt.sub();
        }
    }

    public void deviceMqttConnect() {
        String str;
        deviceMqtt.broker = "tcp://" + GB.mqttServerAddr + ":" + GB.mqttServerPort;
        deviceMqtt.userName = GB.mqttServerUserName;
        deviceMqtt.passWord = GB.mqttServerPassword;
        deviceMqtt.qos = Integer.parseInt(GB.mqttQoS);
        deviceMqtt.clientId = GB.deviceMqttClientId;
        deviceMqtt.connect();

        if (deviceMqtt.connected != 0) {
            deviceMqtt.clearSubTopic();
            for (int i = 0; i < GB.deviceMqttSubA.length; i++) {
                str = GB.deviceMqttSubA[i].trim();
                if (str.length() > 0) {
                    deviceMqtt.addSubTopic(str);
                }
            }
            deviceMqtt.sub();
        }
    }

    public void btLeftFunc(int index) {
        String str;
        Select sel1;
        Numpad kpad;
        SetPanel spn1;
        String topic, mes;

        switch (index) {
            case 0:
                spn1 = new SetPanel(null, true);
                spn1.winW = 600;
                spn1.nameWideRate = 0.6;
                spn1.title_str = "REDIS Server Set";
                str = "name:Server Address;edit:1;check:5";
                spn1.list.add(new MyData(str, GB.redisServerAddr));
                str = "name:Port;edit:1;check:2";
                spn1.list.add(new MyData(str, GB.redisServerPort));
                str = "name:Username;edit:1;";
                spn1.list.add(new MyData(str, GB.redisServerUsername));
                str = "name:Password;edit:1;passw:0";
                spn1.list.add(new MyData(str, GB.redisServerPassword));
                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 1) {
                    GB.redisServerAddr = spn1.tfa1[4 * 0 + 1].getText();
                    GB.redisServerPort = spn1.tfa1[4 * 1 + 1].getText();
                    GB.redisServerUsername = spn1.tfa1[4 * 2 + 1].getText();
                    GB.redisServerPassword = spn1.tfa1[4 * 3 + 1].getText();
                    Base3.editNewDb("redisServerAddr", GB.redisServerAddr);
                    Base3.editNewDb("redisServerPort", GB.redisServerPort);
                    Base3.editNewDb("redisServerUsername", GB.redisServerUsername);
                    Base3.editNewDb("redisServerPassword", GB.redisServerPassword);
                }
                break;
            case 1:
                KvRedis.host = GB.redisServerAddr;
                KvRedis.port = Integer.parseInt(GB.redisServerPort);
                KvRedis.passw = GB.redisServerPassword;

                if (KvRedis.testConnect()) {
                    cla.setStatus("Redis Server is Ready.", 1);
                    redis_ok_f = 1;
                } else {
                    cla.setStatus("Redis Server is Error !!!", 2);
                    redis_ok_f = 0;
                }
                break;

            case 2:
                sel1 = new Select(null, true);
                sel1.title_str = "Agent Mqtt Setting";
                sel1.count = 4;
                sel1.xc = 1;
                sel1.yc = 9;
                sel1.winW = 1000;
                sel1.selstr.add("Agent Mqtt Server Setting");
                sel1.selstr.add("Agent Mqtt Subscribe");
                sel1.selstr.add("Agent Mqtt Discoonnect");
                sel1.selstr.add("Agent Mqtt Connect");
                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    break;
                }
                switch (Select.ret_i) {
                    case 0:
                        spn1 = new SetPanel(null, true);
                        spn1.winW = 1000;
                        spn1.winH = 0;
                        spn1.nameWideRate = 0.6;
                        spn1.title_str = "MQTT Server Set";
                        str = "name:MQTT Server Address;edit:1;check:5";
                        spn1.list.add(new MyData(str, GB.mqttServerAddr));
                        str = "name:Port;edit:1;check:2";
                        spn1.list.add(new MyData(str, GB.mqttServerPort));
                        str = "name:UserName;edit:1";
                        spn1.list.add(new MyData(str, GB.mqttServerUserName));
                        str = "name:Password;edit:1;passw:0";
                        spn1.list.add(new MyData(str, GB.mqttServerPassword));
                        str = "name:QoS;edit:0;";
                        str += "enu:0~1~2";
                        spn1.list.add(new MyData(str, GB.mqttQoS));
                        str = "name:Client ID;edit:1";
                        spn1.list.add(new MyData(str, GB.agentMqttClientId));
                        spn1.create();
                        spn1.setVisible(true);
                        if (SetPanel.ret_f == 1) {
                            GB.mqttServerAddr = spn1.tfa1[4 * 0 + 1].getText();
                            GB.mqttServerPort = spn1.tfa1[4 * 1 + 1].getText();
                            GB.mqttServerUserName = spn1.tfa1[4 * 2 + 1].getText();
                            GB.mqttServerPassword = spn1.tfa1[4 * 3 + 1].getText();
                            GB.mqttQoS = spn1.tfa1[4 * 4 + 1].getText();
                            GB.agentMqttClientId = spn1.tfa1[4 * 5 + 1].getText();
                            Base3.editNewDb("mqttServerAddr", GB.mqttServerAddr);
                            Base3.editNewDb("mqttServerPort", GB.mqttServerPort);
                            Base3.editNewDb("mqttServerUserName", GB.mqttServerUserName);
                            Base3.editNewDb("mqttServerPassword", GB.mqttServerPassword);
                            Base3.editNewDb("mqttQoS", GB.mqttQoS);
                            Base3.editNewDb("agentMqttClientId", GB.agentMqttClientId);
                        }
                        break;
                    case 1:
                        spn1 = new SetPanel(null, true);
                        spn1.winW = 600;
                        spn1.nameWideRate = 0.1;
                        spn1.title_str = "MQTT Subscribe";
                        str = "name:1;edit:1";
                        spn1.list.add(new MyData(str, GB.agentMqttSubA[0]));
                        str = "name:2;edit:1";
                        spn1.list.add(new MyData(str, GB.agentMqttSubA[1]));
                        str = "name:3;edit:1";
                        spn1.list.add(new MyData(str, GB.agentMqttSubA[2]));
                        str = "name:4;edit:1";
                        spn1.list.add(new MyData(str, GB.agentMqttSubA[3]));
                        str = "name:5;edit:1";
                        spn1.list.add(new MyData(str, GB.agentMqttSubA[4]));
                        str = "name:6;edit:1";
                        spn1.list.add(new MyData(str, GB.agentMqttSubA[5]));
                        spn1.create();
                        spn1.setVisible(true);
                        if (SetPanel.ret_f == 1) {
                            GB.agentMqttSubA[0] = spn1.tfa1[4 * 0 + 1].getText();
                            GB.agentMqttSubA[1] = spn1.tfa1[4 * 1 + 1].getText();
                            GB.agentMqttSubA[2] = spn1.tfa1[4 * 2 + 1].getText();
                            GB.agentMqttSubA[3] = spn1.tfa1[4 * 3 + 1].getText();
                            GB.agentMqttSubA[4] = spn1.tfa1[4 * 4 + 1].getText();
                            GB.agentMqttSubA[5] = spn1.tfa1[4 * 5 + 1].getText();
                            Base3.editNewDb("agentMqttSubA~0", GB.agentMqttSubA[0]);
                            Base3.editNewDb("agentMqttSubA~1", GB.agentMqttSubA[1]);
                            Base3.editNewDb("agentMqttSubA~2", GB.agentMqttSubA[2]);
                            Base3.editNewDb("agentMqttSubA~3", GB.agentMqttSubA[3]);
                            Base3.editNewDb("agentMqttSubA~4", GB.agentMqttSubA[4]);
                            Base3.editNewDb("agentMqttSubA~5", GB.agentMqttSubA[5]);
                        }
                        break;
                    case 2:
                        myMqtt.disConnect(myMqtt.clientId);
                        break;
                    case 3:
                        agentMqttConnect();
                        break;

                }
                break;

            case 3:
                sel1 = new Select(null, true);
                sel1.title_str = "Web Mqtt Setting";
                sel1.count = 5;
                sel1.xc = 1;
                sel1.yc = 9;
                sel1.winW = 1000;
                sel1.selstr.add("Web Mqtt Server Setting");
                sel1.selstr.add("Web Mqtt Subscribe");
                sel1.selstr.add("Web Mqtt Discoonnect");
                sel1.selstr.add("Web Mqtt Connect");
                sel1.selstr.add("Web Connect Tick");
                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    break;
                }
                switch (Select.ret_i) {
                    case 0:
                        spn1 = new SetPanel(null, true);
                        spn1.winW = 1000;
                        spn1.winH = 0;
                        spn1.nameWideRate = 0.6;
                        spn1.title_str = "Web MQTT Set";
                        str = "name:Client ID;edit:1";
                        spn1.list.add(new MyData(str, GB.webMqttClientId));
                        str = "Web Jid";
                        spn1.list.add(new MyData(str, GB.webJid));
                        spn1.create();
                        spn1.setVisible(true);
                        if (SetPanel.ret_f == 1) {
                            GB.webMqttClientId = spn1.tfa1[4 * 0 + 1].getText();
                            GB.webJid = spn1.tfa1[4 * 1 + 1].getText();
                            Base3.editNewDb("webMqttClientId", GB.webMqttClientId);
                            Base3.editNewDb("webJid", GB.webJid);
                        }
                        break;
                    case 1:
                        spn1 = new SetPanel(null, true);
                        spn1.winW = 1000;
                        spn1.winH = 0;
                        spn1.nameWideRate = 0.1;
                        spn1.title_str = "MQTT Subscribe";
                        str = "name:1;edit:1";
                        spn1.list.add(new MyData(str, GB.webMqttSubA[0]));
                        str = "name:2;edit:1";
                        spn1.list.add(new MyData(str, GB.webMqttSubA[1]));
                        str = "name:3;edit:1";
                        spn1.list.add(new MyData(str, GB.webMqttSubA[2]));
                        str = "name:4;edit:1";
                        spn1.list.add(new MyData(str, GB.webMqttSubA[3]));
                        str = "name:5;edit:1";
                        spn1.list.add(new MyData(str, GB.webMqttSubA[4]));
                        str = "name:6;edit:1";
                        spn1.list.add(new MyData(str, GB.webMqttSubA[5]));
                        spn1.create();
                        spn1.setVisible(true);
                        if (SetPanel.ret_f == 1) {
                            GB.webMqttSubA[0] = spn1.tfa1[4 * 0 + 1].getText();
                            GB.webMqttSubA[1] = spn1.tfa1[4 * 1 + 1].getText();
                            GB.webMqttSubA[2] = spn1.tfa1[4 * 2 + 1].getText();
                            GB.webMqttSubA[3] = spn1.tfa1[4 * 3 + 1].getText();
                            GB.webMqttSubA[4] = spn1.tfa1[4 * 4 + 1].getText();
                            GB.webMqttSubA[5] = spn1.tfa1[4 * 5 + 1].getText();
                            Base3.editNewDb("webMqttSubA~0", GB.webMqttSubA[0]);
                            Base3.editNewDb("webMqttSubA~1", GB.webMqttSubA[1]);
                            Base3.editNewDb("webMqttSubA~2", GB.webMqttSubA[2]);
                            Base3.editNewDb("webMqttSubA~3", GB.webMqttSubA[3]);
                            Base3.editNewDb("webMqttSubA~4", GB.webMqttSubA[4]);
                            Base3.editNewDb("webMqttSubA~5", GB.webMqttSubA[5]);
                        }
                        break;
                    case 2:
                        webMqtt.disConnect(webMqtt.clientId);
                        break;
                    case 3:
                        webMqttConnect();
                        break;

                    case 4:
                        Myjs mj = new Myjs();
                        mj.stObj();
                        mj.addKeyVobj("cmdNo", webTxCmdNo++);
                        mj.addKeyVstr("act", "connect");
                        mj.end();
                        webMqttPub("mqttAgentRX/" + GB.webJid, mj.jstr);
                        break;

                }
                break;

            case 4:
                sel1 = new Select(null, true);
                sel1.title_str = "Device Mqtt Setting";
                sel1.count = 5;
                sel1.xc = 1;
                sel1.yc = 9;
                sel1.selstr.add("Device Mqtt Server Setting");
                sel1.selstr.add("Device Mqtt Subscribe");
                sel1.selstr.add("Device Mqtt Discoonnect");
                sel1.selstr.add("Device Mqtt Connect");
                sel1.selstr.add("Device Connect Tick");
                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    break;
                }
                switch (Select.ret_i) {
                    case 0:
                        spn1 = new SetPanel(null, true);
                        spn1.winW = 600;
                        spn1.nameWideRate = 0.6;
                        spn1.title_str = "Device MQTT Set";
                        str = "name:Client ID;edit:1";
                        spn1.list.add(new MyData(str, GB.deviceMqttClientId));
                        str = "Device Jid";
                        spn1.list.add(new MyData(str, GB.deviceJid));
                        spn1.create();
                        spn1.setVisible(true);
                        if (SetPanel.ret_f == 1) {
                            GB.deviceMqttClientId = spn1.tfa1[4 * 0 + 1].getText();
                            GB.deviceJid = spn1.tfa1[4 * 1 + 1].getText();
                            Base3.editNewDb("deviceMqttClientId", GB.deviceMqttClientId);
                            Base3.editNewDb("deviceJid", GB.deviceJid);
                        }
                        break;
                    case 1:
                        spn1 = new SetPanel(null, true);
                        spn1.winW = 1000;
                        spn1.winH = 0;
                        spn1.nameWideRate = 0.1;
                        spn1.title_str = "MQTT Subscribe";
                        str = "name:1;edit:1";
                        spn1.list.add(new MyData(str, GB.deviceMqttSubA[0]));
                        str = "name:2;edit:1";
                        spn1.list.add(new MyData(str, GB.deviceMqttSubA[1]));
                        str = "name:3;edit:1";
                        spn1.list.add(new MyData(str, GB.deviceMqttSubA[2]));
                        str = "name:4;edit:1";
                        spn1.list.add(new MyData(str, GB.deviceMqttSubA[3]));
                        str = "name:5;edit:1";
                        spn1.list.add(new MyData(str, GB.deviceMqttSubA[4]));
                        str = "name:6;edit:1";
                        spn1.list.add(new MyData(str, GB.deviceMqttSubA[5]));
                        spn1.create();
                        spn1.setVisible(true);
                        if (SetPanel.ret_f == 1) {
                            GB.deviceMqttSubA[0] = spn1.tfa1[4 * 0 + 1].getText();
                            GB.deviceMqttSubA[1] = spn1.tfa1[4 * 1 + 1].getText();
                            GB.deviceMqttSubA[2] = spn1.tfa1[4 * 2 + 1].getText();
                            GB.deviceMqttSubA[3] = spn1.tfa1[4 * 3 + 1].getText();
                            GB.deviceMqttSubA[4] = spn1.tfa1[4 * 4 + 1].getText();
                            GB.deviceMqttSubA[5] = spn1.tfa1[4 * 5 + 1].getText();
                            Base3.editNewDb("deviceMqttSubA~0", GB.deviceMqttSubA[0]);
                            Base3.editNewDb("deviceMqttSubA~1", GB.deviceMqttSubA[1]);
                            Base3.editNewDb("deviceMqttSubA~2", GB.deviceMqttSubA[2]);
                            Base3.editNewDb("deviceMqttSubA~3", GB.deviceMqttSubA[3]);
                            Base3.editNewDb("deviceMqttSubA~4", GB.deviceMqttSubA[4]);
                            Base3.editNewDb("deviceMqttSubA~5", GB.deviceMqttSubA[5]);
                        }
                        break;
                    case 2:
                        deviceMqtt.disConnect(deviceMqtt.clientId);
                        break;
                    case 3:
                        deviceMqttConnect();
                        break;

                    case 4:
                        Myjs mj = new Myjs();
                        mj.stObj();
                        mj.addKeyVobj("cmdNo", deviceTxCmdNo++);
                        mj.addKeyVstr("act", "connect");
                        mj.end();
                        deviceMqttPub("mqttAgentRX/" + GB.deviceJid, mj.jstr);
                        break;

                }
                break;
            case 5:
                agentMqttConnect();
                webMqttConnect();
                deviceMqttConnect();
                break;
            case 6://test ok
                Myjs mj = new Myjs();
                mj.stObj();
                mj.addKeyVobj("cmdNo", webTxCmdNo++);
                mj.addKeyVstr("act", "connect");
                mj.end();
                webMqttPub("mqttAgentRX/" + GB.webJid, mj.jstr);
                //==
                mj = new Myjs();
                mj.stObj();
                mj.addKeyVobj("cmdNo", deviceTxCmdNo++);
                mj.addKeyVstr("act", "connect");
                //mj.addKeyVstr("target", "J403F0000000000");
                mj.end();
                deviceMqttPub("mqttAgentRX/" + GB.deviceJid, mj.jstr);
                break;

            case 7:
                for (String key : cla.connectMap.keySet()) {
                    insertDocument("ConnectJid: " + key + "\n", GB.black);
                }
                insertDocument("Total Connected: " + cla.connectMap.size() + "\n", GB.black);
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
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                cla.funcMode = index - 12;
                for (int i = 0; i < 28; i++) {
                    cla.btLeftA[i + 20].setText("");
                }
                break;
            default:
                if (index < 20) {
                    break;
                }
                switch (cla.funcMode) {
                    case 0:
                        cla.eta.funcKeyAct(index - 20);
                        break;
                }

        }

    }

    public void addJidToObject(String objJid, String sonGroupName, String addJid) {
        int i;
        int exist;
        JidObj toJobj = jidMap.get(objJid);
        if (toJobj == null) {
            return;
        }
        String className = jdsc.getClassName(addJid);
        toJobj.addJid(className, sonGroupName, addJid);
    }

    public int agentMqttTick() {
        java.util.Date now = new java.util.Date();
        return agentMqttPub("mqttAgentTX/tick", "{'tick': " + now.getTime() + "}");
    }

    public void agentMqttResp(String respJid, String act, String cmdNo, String actStatus) {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVstr("respAct", act);
        mj.addKeyVobj("cmdNo", cmdNo);
        mj.addKeyVstr("status", actStatus);
        mj.end();
        agentMqttPub("mqttAgentTX/" + respJid, mj.jstr);
    }

    public void agentMqttVobj(String toJid, String act, String cmdNo, String target, String value) {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVstr("act", act);
        mj.addKeyVobj("cmdNo", cmdNo);
        mj.addKeyVstr("target", target);
        mj.addKeyVobj("value", value);
        mj.end();
        agentMqttPub("mqttAgentTX/" + toJid, mj.jstr);
    }

    public void agentMqttVstr(String toJid, String act, String cmdNo, String target, String value) {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVstr("act", act);
        mj.addKeyVobj("cmdNo", cmdNo);
        mj.addKeyVstr("target", target);
        mj.addKeyVstr("value", value);
        mj.end();
        agentMqttPub("mqttAgentTX/" + toJid, mj.jstr);
    }

    public String saveJidobjValue(String jid, String name, String value) {
        JidObj jobj = cla.jidMap.get(jid);
        if (jobj == null) {
            return "Set Data Is A Empty JidObj !!!";
        }
        int okf = jobj.setv(name, value);
        if (okf == 0) {
            return name + " Is Not My Menber !!!";
        }
        String hashKey;
        String itemName;
        hashKey = "hash~jid~" + jid;
        itemName = "L" + "~" + "editTime";
        KvRedis.pipeAct("setHashKeyValue", new String[]{hashKey, itemName, jobj.getv("editTime")}, null);
        itemName = jobj.gett(name) + "~" + name;
        KvRedis.pipeAct("setHashKeyValue", new String[]{hashKey, itemName, value}, null);

        int record = jobj.getRecord(name);

        if (record > 0) {
            if (record == 1) {
                KvRedis.pipeAct("trimList", new String[]{"list~jidValue~" + jid + "~" + name}, new long[]{0, 998});
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(Long.parseLong(jobj.getv("editTime")));
            strDate = "\"" + strDate + "\"";
            KvRedis.pipeAct("pushList", new String[]{"list~jidValue~" + jid + "~" + name}, (strDate + "~" + jobj.geto(name)).split("<~>"));
        }
        return null;
    }

    public void trsJidValueToMap(String inJid, String itemName, String value, Map<String, String> map, int deepCnt, int admin) {
        String actJid;
        String[] strA;
        actJid = JidObj.chkJidstrInJid(inJid, itemName);
        if (actJid == null) {
            if (admin == 0) {
                map.put("_sysError", inJid + "~" + itemName + "~" + "Set Data Position Is Out Of Mine");
                return;
            }
            actJid = itemName;
        }
        strA = actJid.split("\\.");
        if (strA.length == 1) {
            if (deepCnt == 0) {
                map.put("_rootJid", strA[0]);
            }
            JSONObject js;
            String kstr;
            String vstr;
            try {
                js = new JSONObject(value);
                Iterator<String> it = js.keys();
                while (it.hasNext()) {
                    kstr = it.next();
                    vstr = js.get(kstr).toString();
                    deepCnt++;
                    if (deepCnt >= 10) {
                        map.put("_sysError", "Call Over Deep Stack 10");
                        return;
                    }
                    trsJidValueToMap(strA[0], kstr, vstr, map, deepCnt, admin);
                    deepCnt--;
                }

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

        }
        if (strA.length == 2) {
            map.put(strA[0] + "~" + strA[1], value);
            if (deepCnt == 0) {
                map.put("_rootJid", strA[0] + "." + strA[1]);
            }
        }
    }

    //return error string
    public Map<String, String> setJidobjData(String deviceJid, String act, String target, String value, int admin) {
        String errStr;
        String[] strA;
        Map<String, String> map = new HashMap();
        trsJidValueToMap(deviceJid, target, value, map, 0, admin);
        errStr = map.get("_sysError");
        if (errStr != null) {
            return map;
        }
        KvRedis.pipeAct("connect", null, null);
        for (String key : map.keySet()) {
            strA = key.split("~");
            if (strA.length != 2) {
                continue;
            }
            errStr = saveJidobjValue(strA[0], strA[1], map.get(key));
            if (errStr != null) {
                break;
            }
        }
        KvRedis.pipeAct("execute", null, null);
        KvRedis.chkPipe();
        if (errStr != null) {
            map.put("_sysError", errStr);
            return map;
        }
        errStr = KvRedis.errStr;
        if (errStr != null) {
            map.put("_sysError", errStr);
        }
        return map;
    }

    public String respDataToSend(String sendJid, String act, String target, String type, String cmdNo, int admin) {
        String[] strA;
        int ibuf = 0;
        String actJid;
        actJid = JidObj.chkJidstrInJid(sendJid, target);
        if (actJid == null) {
            if (admin == 0) {
                return "'" + target + "'  Is Not In " + sendJid + " !!!";
            } else {
                actJid = target;
            }
        }
        strA = actJid.split("\\.");
        JidObj jobj = jidMap.get(strA[0]);
        if (jobj == null) {
            return "The Instance Of " + strA[0] + " Is Unavalible !!!";
        }

        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVstr("respAct", act);
        mj.addKeyVobj("cmdNo", cmdNo);
        mj.addKeyVobj("target", "\"" + target + "\"");
        mj.addKeyVobj("value", JidObj.getValue(target, actJid, type));
        mj.end();
        agentMqttPub("mqttAgentTX/" + sendJid, mj.jstr);
        return null;

    }

    public String respRecordToSend(String sendJid, String act, String target, String type, String cmdNo, int admin) {
        String[] strA;
        int ibuf = 0;
        String actJid;
        actJid = JidObj.chkJidstrInJid(sendJid, target);
        if (actJid == null) {
            if (admin == 0) {
                return "'" + target + "'  Is Not In " + sendJid + " !!!";
            } else {
                actJid = target;
            }
        }
        strA = actJid.split("\\.");
        JidObj jobj = jidMap.get(strA[0]);
        if (jobj == null) {
            return "The Instance Of " + strA[0] + " Is Unavalible !!!";
        }

        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVstr("respAct", act);
        mj.addKeyVobj("cmdNo", cmdNo);
        mj.addKeyVobj("target", "\"" + target + "\"");
        mj.addKeyVobj("value", JidObj.getRecord(target, actJid, type));
        mj.end();
        agentMqttPub("mqttAgentTX/" + sendJid, mj.jstr);
        return null;

    }

    public Map<String, String> chkJidInConnect(String chkJid, String bypassJid) {
        Map<String, String> map = new HashMap();
        for (String jid : cla.connectMap.keySet()) {
            if (jid.equals(bypassJid)) {
                continue;
            }
            String item = "";
            String[] strA = chkJid.split("\\.");
            if (strA.length == 2) {
                item = "." + strA[1];
            }
            String path = jid;
            ArrayList<String> retStrA = new ArrayList();
            JidObj.getJidInJid(jid, strA[0], retStrA, path, 0);
            for (int i = 0; i < retStrA.size(); i++) {
                map.put(jid + "~" + i, retStrA.get(i) + item);
            }
        }
        return map;
    }

    public Map<String, String> chkJidInWebs(String chkJid, String agentJid) {
        Map<String, String> map = new HashMap();
        String[] strE = chkJid.split("\\.");
        String item = "";
        if (strE.length == 2) {
            item = "." + strE[1];
        }
        String groupName = "webs";
        JidObj fatherJobj = jidMap.get(agentJid);
        String path = pathTreeMap.get(strE[0]);
        for (int k = 0; k < fatherJobj.jidArrayList.size(); k++) {
            JidArray ja = fatherJobj.jidArrayList.get(k);
            if (!ja.groupName.equals(groupName)) {
                continue;
            }
            for (int m = 0; m < ja.jids.size(); m++) {
                String[] strA = path.split("<~>");
                for (int i = 0; i < strA.length; i++) {
                    String[] strB = strA[i].split("\\.");
                    for (int j = 0; j < strB.length; j++) {
                        String[] strC = strB[j].split("\\{");
                        if (strC.length == 2) {
                            String[] strD = strC[1].split("\\}");
                            if (strD[0].equals(ja.jids.get(m))) {
                                String valuePath = "";
                                for (int n = 2; n < strB.length; n++) {
                                    if (n != 2) {
                                        valuePath += ".";
                                    }
                                    valuePath += strB[n];
                                }
                                map.put(strD[0], valuePath + item);
                            }
                        }
                    }
                }
            }

        }
        return map;
    }

    public void agentRespError(String sendJid, String act, String cmdNo, String para, String errStr) {
        String err = "";
        if (para.length() == 0) {
            err += "ERROR: " + errStr;
        } else {
            err += "ERROR:[" + para + "] " + errStr;
        }
        cla.setStatus(act + " >> " + err, 2);
        agentMqttResp(sendJid, act, cmdNo, err);
    }

    public String agentMqttRxProcess(String sendJid, Map<String, String> map, String para) {
        String act;
        String target;
        String type;
        String groupName = "";
        String gid;
        String sname;
        String value = null;
        String objects;
        String name;
        String id;
        String cmdNo;
        String errStr;
        String password;
        int cmdNoCnt;
        int admin = 0;
        int commandsCnt = 0;

        int exist = 0;

        if (JidObj.isJid(sendJid) != 0) {
            return null;
        }
        errStr = map.get("_sysError");
        if (errStr != null) {
            agentRespError(sendJid, "null", "0", para, errStr);
            return null;
        }

        cmdNo = map.get("cmdNo");
        if (cmdNo == null) {
            agentRespError(sendJid, "null", "0", para, "No 'cmdNo' Item !!!");
            return null;
        }

        password = map.get("password");
        if (password != null) {
            if (password == GB.mqttAgentAdminPass) {
                admin = 1;
            }
        }

        act = map.get("act");
        if (act == null) {
            String commandsCntStr = map.get("commandsCnt");
            if (commandsCntStr == null) {
                agentRespError(sendJid, "null", "0", para, "No 'act' Item !!!");
                return null;
            }
        }

        int pass = 0;
        String webId;

        switch (act) {
            case "connect":
                cla.connectMap.put(sendJid, "0");
                if (JidObj.cmpJid(sendJid, "J010") == 2) {//web
                    pass = 0;
                    int webInx = 0;
                    for (int i = 0; i < GB.webGroupPass.length; i++) {
                        String[] strA = GB.webGroupPass[i].split("~");
                        if (strA[0].equals(sendJid)) {
                            pass = 1;
                            webInx = i;
                            break;
                        }
                    }
                    if (pass == 0) {
                        agentRespError(sendJid, act, cmdNo, para, "No This Web ID " + sendJid);
                        return null;
                    }

                    JidObj obj = jidMap.get(sendJid);
                    if (obj == null) {
                        gid = map.get("gid");
                        sname = map.get("sname");
                        if (gid == null) {
                            gid = "web" + webInx;
                        }
                        if (sname == null) {
                            sname = "web-" + sendJid;
                        }
                        obj = cla.eta.newWeb(sendJid, gid, sname);
                    }
                    addJidToObject("J003F0000000000", "webs", sendJid);

                }
                agentMqttResp(sendJid, act, cmdNo, "OK:" + para);

                break;

            case "getData":
                target = map.get("target");
                if (target == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'target' Item !!!");
                    return null;
                }
                type = map.get("type");
                if (type == null) {
                    type = "A00";
                }
                errStr = cla.respDataToSend(sendJid, act, target, type, cmdNo, admin);
                if (errStr != null) {
                    agentRespError(sendJid, act, cmdNo, para, errStr);
                }
                break;
            case "setData":
                target = map.get("target");
                if (target == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'target' Item !!!");
                    return null;
                }
                value = map.get("value");
                if (value == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'value' Item !!!");
                    return null;
                }

                Map<String, String> pm = cla.setJidobjData(sendJid, act, target, value, admin);
                errStr = pm.get("_sysError");
                if (errStr != null) {
                    agentRespError(sendJid, act, cmdNo, para, errStr);
                    return null;
                } else {
                    agentMqttResp(sendJid, act, cmdNo, "OK:" + para);

                    Map<String, String> mapResp = chkJidInConnect(pm.get("_rootJid"), sendJid);
                    {
                        for (String key : mapResp.keySet()) {
                            agentMqttVobj(key.split("~")[0], "setData", "" + agentTxCmdNo, mapResp.get(key), value);
                            agentTxCmdNo++;
                        }
                    }
                }
                break;

            case "newData":
                target = map.get("target");
                if (target == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'target' Item !!!");
                    return null;
                }
                type = map.get("type");
                if (type == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'type' Item !!!");
                    return null;
                }
                value = map.get("value");
                if (value == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'value' Item !!!");
                    return null;
                }

                Map<String, JidObj> jobjLs = cla.jdsc.mmJobj.get(type);
                Map<String, String> mDsc = cla.jdsc.mmDsc.get(type);
                if (mDsc == null || jobjLs == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No Group Modle Of '" + type + "' !!!");
                    return null;
                }
                String jidHead = mDsc.get("jidHead");
                String className = mDsc.get("className");
                JidObj obj = cla.cdsc.newClass(className);
                //===================================
                obj.mp.put("jid", new JidData("S", target));
                if (Jdsc.chkJidHead(target, jidHead) != 2) {
                    agentRespError(sendJid, act, cmdNo, para, "The Target Jid Is Out Of Range In '" + type + "' !!!");
                    return null;
                }
                //===================================
                Map<String, String> vmap = Myjs.trsJsToMap(value);
                if (vmap == null) {
                    agentRespError(sendJid, act, cmdNo, para, "The Value Format Error !!!");
                    return null;

                }
                //===================================
                String sonClassName;
                String sonGroupName;
                Object[] objA = vmap.keySet().toArray();
                Arrays.sort(objA);

                //for (String vkey : vmap.keySet()) {
                for (int j = 0; j < objA.length; j++) {
                    String vkey = (String) objA[j];

                    String[] strB = vkey.split("#");
                    if (strB.length == 1) {
                        obj.setv(vkey, vmap.get(vkey));
                        continue;
                    }
                    if (strB.length == 2) {
                        int yes_f = 0;
                        for (String sonKey : mDsc.keySet()) {
                            String[] strC = sonKey.split("~");
                            if (strC.length != 2) {
                                continue;
                            }
                            if (!strC[0].equals("son")) {
                                continue;
                            }
                            //className~groupName~jidHeadStart~jidHeadEnd
                            String[] strD = mDsc.get(sonKey).split("~");
                            if (strD.length < 3) {
                                continue;
                            }
                            if (strD[1].equals("any")) {
                                String[] strE = strB[0].split("~");
                                if (strE.length == 2) {
                                    sonClassName = strE[0];
                                    sonGroupName = strE[1];
                                } else {
                                    sonClassName = strD[0];
                                    sonGroupName = strD[1];
                                }
                            } else {
                                if (!strD[1].equals(strB[0])) {
                                    continue;
                                }
                                sonClassName = strD[0];
                                sonGroupName = strD[1];
                            }
                            jidHead = strD[2];
                            if (strD.length == 4) {
                                jidHead += "~" + strD[3];
                            }

                            String sonJid = vmap.get(vkey);
                            if (!jidHead.equals("J002")) {  //for any jid
                                if (Jdsc.chkJidHead(sonJid, jidHead) != 2) {
                                    continue;
                                }
                            }

                            Map<String, String> mCdsc = cla.cdsc.mmCdsc.get(className);
                            if (mCdsc == null) {
                                continue;
                            }
                            obj.addJid(sonClassName, sonGroupName, sonJid);
                            yes_f = 1;
                        }
                        if (yes_f == 0) {
                            agentRespError(sendJid, act, cmdNo, para, "Add Group Member Error !!!");
                            return null;
                        }

                    }
                }
                cla.jidMap.put(target, obj);
                jobjLs.put(target, obj);

                KvRedis.pipeAct("connect", null, null);
                cla.eta.addJsobjToRedis(className, type, obj, 1);
                KvRedis.pipeAct("execute", null, null);

                String[] strAA = para.split("#");
                if (strAA.length == 2) {
                    String[] strBB = strAA[1].split("/");
                    if (strBB[0].equals(strBB[1])) {
                        agentMqttResp(sendJid, act, cmdNo, "OK:" + para);
                    }

                } else {
                    agentMqttResp(sendJid, act, cmdNo, "OK:" + para);
                }

                break;

            case "newGroupModel":
                target = map.get("target");
                if (target == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'target' Item !!!");
                    return null;
                }
                password = map.get("password");
                if (password == null || !password.equals(GB.mqttAgentNewModelPass)) {
                    agentRespError(sendJid, act, cmdNo, para, "Pssword Error !!!");
                    return null;
                }
                value = map.get("value");
                if (value == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'value' Item !!!");
                    return null;
                }
                vmap = Myjs.trsJsToMap(value);
                if (vmap == null) {
                    agentRespError(sendJid, act, cmdNo, para, "The Value Format Error !!!");
                    return null;
                }
                className = vmap.get("className");
                if (className == null) {
                    agentRespError(sendJid, act, cmdNo, para, "Value Have No 'ClassName' Item !!!");
                    return null;
                }
                jidHead = vmap.get("jidHead");
                if (jidHead == null) {
                    agentRespError(sendJid, act, cmdNo, para, "Value Have No 'jidHead' Item !!!");
                    return null;
                }

                Map<String, String> mdsc = jdsc.mmDsc.get(target);
                if (mdsc != null) {
                    agentRespError(sendJid, act, cmdNo, para, "The Model Name Is Existed !!!");
                    return null;
                }
                Map<String, String> modelMap = new HashMap();
                modelMap.put("name", target);
                for (String key : vmap.keySet()) {
                    modelMap.put(key, vmap.get(key));
                }
                jdsc.mmDsc.put(target, modelMap);

                KvRedis.pipeAct("connect", null, null);
                cla.eta.setGroupModelToRedis(target, modelMap, 1);
                KvRedis.pipeAct("execute", null, null);

                agentMqttResp(sendJid, act, cmdNo, "OK:" + para);
                break;

            case "newJidClass":
                target = map.get("target");
                if (target == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'target' Item !!!");
                    return null;
                }
                password = map.get("password");
                if (password == null || !password.equals(GB.mqttAgentNewModelPass)) {
                    agentRespError(sendJid, act, cmdNo, para, "Pssword Error !!!");
                    return null;
                }
                value = map.get("value");
                if (value == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'value' Item !!!");
                    return null;
                }
                vmap = Myjs.trsJsToMap(value);
                if (vmap == null) {
                    agentRespError(sendJid, act, cmdNo, para, "The Value Format Error !!!");
                    return null;
                }

                mdsc = cdsc.mmCdsc.get(target);
                if (mdsc != null) {
                    agentRespError(sendJid, act, cmdNo, para, "The JidClass Name Is Existed !!!");
                    return null;
                }
                Map<String, String> cdscMap = new HashMap();
                for (String key : vmap.keySet()) {
                    cdscMap.put(key, vmap.get(key));
                }
                cdsc.mmCdsc.put(target, cdscMap);

                KvRedis.pipeAct("connect", null, null);
                cla.eta.setJidClassToRedis(target, cdscMap, 1);
                KvRedis.pipeAct("execute", null, null);

                agentMqttResp(sendJid, act, cmdNo, "OK:" + para);
                break;

            case "newGroup":
                if (JidObj.cmpJid(sendJid, "J010") != 2) {
                    agentRespError(sendJid, act, cmdNo, para, "Just Web Jid Can Use This Command !!!");
                    return null;
                }
                String[] jidRangeA = null;
                pass = 0;
                for (int i = 0; i < GB.webGroupPass.length; i++) {
                    jidRangeA = GB.webGroupPass[i].split("~");
                    if (jidRangeA.length != 9) {
                        continue;
                    }
                    if (jidRangeA[0].equals(sendJid)) {
                        webId = jidRangeA[0];
                        pass = 1;
                        break;
                    }
                }
                if (pass == 0) {
                    agentRespError(sendJid, act, cmdNo, para, "The Web Jid Is Out Of List !!!");
                    return null;
                }
                //==================================
                target = map.get("target");
                if (target == null) {
                    agentRespError(sendJid, act, cmdNo, para, "Cannot Find 'target' item !!!");
                    return null;
                }
                gid = map.get("gid");//gid
                if (gid == null) {
                    gid = "gid-" + target;
                }
                sname = map.get("sname");//sname
                if (sname == null) {
                    sname = "sname-" + target;
                }
                //============================================================================
                JidObj wjo = jidMap.get(sendJid);
                if (wjo == null) {
                    agentRespError(sendJid, act, cmdNo, para, "Cannot Find This Instance, Use The Connect Command First !!!");
                    return null;
                }
                pass = 0;
                int ibuf = 0;
                for (int j = 0; j < 4; j++) {
                    if (jidRangeA[j * 2 + 1].equals("0")) {
                        continue;
                    }
                    if (jidRangeA[j * 2 + 2].equals("0")) {
                        continue;
                    }
                    ibuf = JidObj.cmpJid(target, jidRangeA[j * 2 + 1]);
                    if (ibuf < 2) {
                        continue;
                    }
                    ibuf = JidObj.cmpJid(target, jidRangeA[j * 2 + 2]);
                    if (ibuf > 2) {
                        continue;
                    }
                    pass = 1;
                    break;
                }
                if (pass == 0) {
                    agentRespError(sendJid, act, cmdNo, para, "The Group Jid Is Out Of Range !!!");
                    return null;
                }

                JidObj gobj = jidMap.get(target);
                if (gobj == null) {
                    gobj = cdsc.newClass("GroupJobj");
                }
                gobj.mp.put("jid", new JidData("S", target));
                gobj.mp.put("sname", new JidData("S", sname));
                gobj.mp.put("gid", new JidData("S", gid));
                gobj.jidArrayList.clear();
                //==========================================

                for (String key : map.keySet()) {
                    String[] strA = key.split("~");
                    if (strA.length == 2) {
                        value = map.get(key);
                        ArrayList<String> jids = Myjs.jsToStrArr(value);
                        if (jids == null) {
                            agentRespError(sendJid, act, cmdNo, para, "Json Array Format Error !!!");
                            return null;
                        }
                        mdsc = jdsc.mmDsc.get(strA[0]);
                        if (mdsc == null) {
                            agentRespError(sendJid, act, cmdNo, para, "Cannot Find GroupModel '" + strA[0] + "' !!!");
                            return null;
                        }
                        jidHead = mdsc.get("jidHead");
                        for (String jid : jids) {
                            ibuf = Jdsc.chkJidHead(jid, jidHead);
                            if (ibuf != 2) {
                                agentRespError(sendJid, act, cmdNo, para, jid + " Out Of Range !!!");
                                return null;
                            }
                        }
                        className = mdsc.get("className");
                        groupName = strA[1];
                        JidArray ja = new JidArray(className, groupName, 0);
                        ja.jids = jids;
                        gobj.jidArrayList.add(ja);
                    }
                }
                //==================================================
                jidMap.put(target, gobj);
                Map<String, JidObj> mjobj = jdsc.mmJobj.get("groupLs");
                mjobj.put(target, gobj);
                wjo.addJid("GroupJobj", "groups", target);
                agentMqttResp(sendJid, act, cmdNo, "OK:" + para);
                cla.reBuildTree();
                break;
            case "getRecord":

                target = map.get("target");
                if (target == null) {
                    agentRespError(sendJid, act, cmdNo, para, "No 'target' Item !!!");
                    return null;
                }
                type = map.get("type");
                if (type == null) {
                    type = "A00";
                }
                errStr = cla.respRecordToSend(sendJid, act, target, type, cmdNo, admin);
                if (errStr != null) {
                    agentRespError(sendJid, act, cmdNo, para, errStr);
                }
                break;

            default:
                agentRespError(sendJid, act, cmdNo, para, "Cannot Find This Command !!!");
                return null;

        }
        return null;

    }

    JidObj trsRedisMapToJsobj(String className, Map<String, String> map) {
        String name, value = "";
        String str;
        String[] strA;
        String[] strB;
        JidObj obj = new JidObj();
        Map<String, String> cmap = cdsc.mmCdsc.get(className);

        for (Object opKey : map.keySet()) {

            name = (String) opKey;
            value = map.get(name);
            strA = name.split("~");
            if (strA.length < 2) {
                continue;
            }
            if (strA[0].equals("O")) {
                if (strA[1].equals("jidArrayList")) {
                    if (strA.length != 3) {
                        continue;
                    }
                    JidArray ja = new JidArray();
                    ja.groupName = strA[2];
                    strB = value.split("~");
                    if (strB.length < 1) {
                        continue;
                    }
                    ja.className = strB[0];
                    for (int i = 1; i < strB.length; i++) {
                        ja.jids.add(strB[i]);
                    }
                    obj.jidArrayList.add(ja);
                }
                continue;
            }

            JidData jd = new JidData();
            jd.t = strA[0];
            jd.v = value;

            String idsc = cmap.get(strA[1]);
            if (idsc != null) {
                String[] strC = idsc.split("~");
                if (strC.length == 3) {
                    jd.setFlag(strC[2]);
                }
            }
            obj.mp.put(strA[1], jd);

        }
        return obj;
    }

}

//end class==========================================================    
class MqttAgentWinLis extends WindowAdapter {

    MqttAgent cla;

    MqttAgentWinLis(MqttAgent owner) {
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
        MqttAgent.ret_f = 0;
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
        //=================================
        cla.pnMain.setLayout(null);
        cla.pnBar.setLayout(null);
        cla.pnLeft.setLayout(null);
        cla.pnRight.setLayout(null);
        cla.pnCenter.setLayout(null);
        MyLayout.ctrA[0] = cla.pnMain;
        MyLayout.rateH = 0.92;
        MyLayout.gridLy();
        MyLayout.yst = MyLayout.yend;
        MyLayout.ctrA[0] = cla.pnBar;
        MyLayout.rateH = 1;
        MyLayout.gridLy();

        MyLayout.ctrA[0] = cla.pnLeft;
        MyLayout.rateW = 0.4;
        MyLayout.gridLy();
        MyLayout.xst = MyLayout.xend;
        MyLayout.ctrA[0] = cla.pnCenter;
        MyLayout.rateW = 0.8;
        MyLayout.gridLy();
        MyLayout.xst = MyLayout.xend;
        MyLayout.ctrA[0] = cla.pnRight;
        MyLayout.rateW = 1;
        MyLayout.gridLy();

        MyLayout.ctrA[0] = cla.sp1;
        MyLayout.rateH = -24;
        MyLayout.gridLy();
        MyLayout.yst = MyLayout.yend;
        MyLayout.ctrA[0] = cla.lbMessage;
        MyLayout.rateH = 1;
        MyLayout.gridLy();
        //==

        for (int i = 0; i < cla.btBarA.length; i++) {
            MyLayout.ctrA[i] = cla.btBarA[i];
        }
        MyLayout.eleAmt = cla.btBarA.length;
        MyLayout.xc = 8;
        MyLayout.yc = MyLayout.eleAmt / MyLayout.xc;
        if ((MyLayout.eleAmt % MyLayout.xc) != 0) {
            MyLayout.yc++;
        }
        MyLayout.gridLy();

        MyLayout.ctrA[0] = cla.lbStatus;
        MyLayout.rateH = 24;
        MyLayout.gridLy();
        MyLayout.yst = MyLayout.yend;
        //==

        for (int i = 0; i < cla.btLeftA.length; i++) {
            MyLayout.ctrA[i] = cla.btLeftA[i];
        }
        MyLayout.eleAmt = cla.btLeftA.length;
        MyLayout.xc = 2;
        MyLayout.yc = MyLayout.eleAmt / MyLayout.xc;
        if ((MyLayout.eleAmt % MyLayout.xc) != 0) {
            MyLayout.yc++;
        }
        MyLayout.ofh[5] = 6;
        MyLayout.ofh[9] = 6;
        MyLayout.gridLy();

        for (int i = 0; i < cla.btRightA.length; i++) {
            MyLayout.ctrA[i] = cla.btRightA[i];
        }
        MyLayout.eleAmt = cla.btRightA.length;
        MyLayout.xc = 1;
        MyLayout.yc = MyLayout.eleAmt / MyLayout.xc;
        if ((MyLayout.eleAmt % MyLayout.xc) != 0) {
            MyLayout.yc++;
        }
        MyLayout.gridLy();

    }

}

class MqttAgentMsLis extends MouseAdapter {

    int enkey_f;
    MqttAgent cla;

    MqttAgentMsLis(MqttAgent owner) {
        cla = owner;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int index;
        if (enkey_f != 1) {
            return;
        }
        index = Integer.parseInt(e.getComponent().getName());
        switch (index >> 8) {
            case 0:
                cla.btBarFunc(index & 255);
                break;
            case 1:
                cla.btLeftFunc(index & 255);
                break;
            case 2:
                cla.btRightFunc(index & 255);
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

//20 msec
class MqttAgentTm1 implements ActionListener {

    String str;
    MqttAgent cla;
    int secBaseTime = 0;
    int minBaseTime = 0;
    int autoConeectMqttDelay = 2;//sec
    int checkRedisDelay = 1;//sec

    MqttAgentTm1(MqttAgent owner) {
        cla = owner;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Color co;
        String str;
        String strA[];
        co = Color.GRAY;
        if (++secBaseTime >= 50) {
            secBaseTime = 0;
            if (++minBaseTime >= 60) {
                minBaseTime = 0;
                int status = cla.agentMqttTick();
                if (status >= 1) {
                    autoConeectMqttDelay = 1;
                }
            }
            if (autoConeectMqttDelay != 0) {
                if (cla.redis_ok_f == 1) {
                    autoConeectMqttDelay--;
                    if (autoConeectMqttDelay == 0) {
                        cla.agentMqttConnect();
                    }
                }
            }

            if (checkRedisDelay != 0) {
                checkRedisDelay--;
                if (checkRedisDelay == 0) {
                    cla.checkRedisConnect();
                }
            }

            Set<String> keySet = cla.connectMap.keySet();
            for (String key : keySet) {
                String connectTimeS = cla.connectMap.get(key);
                if (connectTimeS == null) {
                    continue;
                }
                int connectTime = Lib.str2int(connectTimeS, 0);
                connectTime++;
                cla.connectMap.put(key, "" + connectTime);
                if (connectTime >= cla.deviceClearConnectTime) {
                    cla.connectMap.remove(key);
                }
            }
            /*
            Map<String, JidObj> agentMap = cla.jdsc.mmJobj.get("agentLs");
            if (agentMap != null) {
                for (String jid : agentMap.keySet()) {
                    JidObj agentJobj = agentMap.get(jid);
                    ArrayList<String> webJids = agentJobj.getGroupJids("webLs");
                    if (webJids == null) {
                        continue;
                    }
                    for (int k = 0; k < webJids.size(); k++) {
                        String webJid = webJids.get(k);
                        JidObj webJobj = cla.jidMap.get(webJid);
                        if (webJobj == null) {
                            continue;
                        }
                        String connectTimeS = webJobj.getv("connectTime");
                        if (connectTimeS == null) {
                            continue;
                        }
                        int connectTime = Lib.str2int(connectTimeS, 0);
                        connectTime++;
                        webJobj.edit("connectTime", "" + connectTime);
                        if (connectTime >= cla.webClearConnectTime) {
                            agentJobj.delGroupJid("webLs", webJid);
                            break;
                        }
                    }

                }
            }
             */
        }

        if (cla.showStatusTime > 0) {
            cla.showStatusTime--;
            if (cla.showStatusTime == 0) {
                cla.lbStatus.setText("");
            }
        }

        co = Color.GRAY;
        if (cla.redis_ok_f == 1) {
            co = Color.green;
        }
        if (!cla.btLeftA[0].getBackground().equals(co)) {
            cla.btLeftA[0].setBackground(co);
        }

        co = Color.GRAY;
        if (cla.myMqtt.connected == 1) {
            co = Color.green;
        }
        if (!cla.btLeftA[2].getBackground().equals(co)) {
            cla.btLeftA[2].setBackground(co);
        }

        co = Color.GRAY;
        if (cla.webMqtt.connected == 1) {
            co = Color.green;
        }
        if (!cla.btLeftA[3].getBackground().equals(co)) {
            cla.btLeftA[3].setBackground(co);
        }

        co = Color.GRAY;
        if (cla.deviceMqtt.connected == 1) {
            co = Color.green;
        }
        if (!cla.btLeftA[4].getBackground().equals(co)) {
            cla.btLeftA[4].setBackground(co);
        }

        for (int i = 0; i < 8; i++) {
            if (cla.funcMode == i) {
                co = Color.green;
            } else {
                co = Color.LIGHT_GRAY;
            }
            if (!cla.btLeftA[i + 12].getBackground().equals(co)) {
                cla.btLeftA[i + 12].setBackground(co);
            }
        }

        switch (cla.funcMode) {
            case 0:
                cla.eta.showFuncKey();
                break;
        }

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
        str = Lib.buttonTextLine2("Total Line\n" + cla.viewLineCount, "blue", "10px");
        if (!cla.btRightA[0].getText().equals(str)) {
            if (cla.viewLineCount > GB.viewMaxLines) {
                cla.viewLineCount = 0;
                cla.viewLineChars = 0;
                cla.tp1.setText("");
            }
            if (cla.viewTrace_f == 1) {
                JScrollBar vertical = cla.sp1.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            }
            cla.btRightA[0].setText(str);
        }
        co = cla.coBt;
        if (cla.viewTrace_f == 1) {
            co = Color.green;
        }
        if (!cla.btRightA[2].getBackground().equals(co)) {
            cla.btRightA[2].setBackground(co);
        }
        //==
        co = cla.coBt;
        if (cla.disShowTx_f == 1) {
            co = Color.green;
        }
        if (!cla.btRightA[3].getBackground().equals(co)) {
            cla.btRightA[3].setBackground(co);
        }
        //==
        co = cla.coBt;
        if (cla.disShowRx_f == 1) {
            co = Color.green;
        }
        if (!cla.btRightA[4].getBackground().equals(co)) {
            cla.btRightA[4].setBackground(co);
        }
        //==
        co = cla.coBt;
        if (cla.disShowAgent_f == 1) {
            co = Color.green;
        }
        if (!cla.btRightA[5].getBackground().equals(co)) {
            cla.btRightA[5].setBackground(co);
        }
        //==
        co = cla.coBt;
        if (cla.disShowWeb_f == 1) {
            co = Color.green;
        }
        if (!cla.btRightA[6].getBackground().equals(co)) {
            cla.btRightA[6].setBackground(co);
        }
        //==
        co = cla.coBt;
        if (cla.disShowDevice_f == 1) {
            co = Color.green;
        }
        if (!cla.btRightA[7].getBackground().equals(co)) {
            cla.btRightA[7].setBackground(co);
        }
        //==

    }

}

class MqttAgentTd1 extends Thread {

    MqttAgent cla;

    MqttAgentTd1(MqttAgent owner) {
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

//============================================
class JidArray {

    String className = "";
    String groupName = "";
    ArrayList<String> jids = new ArrayList<>();

    JidArray() {
    }

    JidArray(String _className, String _groupName, int _amount) {
        className = _className;
        groupName = _groupName;
        for (int i = 0; i < _amount; i++) {
            jids.add("J00000000000000");
        }
    }
}

//type:U:undefined, S:string, L:long, D:Double,B:byte,O:object, SA,LA,DA,BA...... 
//f:R0:recode 0:non record, 1:Record 1000, 
//f:D0:disp type 0:none trans, 1:trans to oct; 2:trans to hex
class JidData {

    String v = "";
    String t = "U";
    String f = ""; //recode
    int record = 0;
    int dispType = 0;

    JidData() {

    }

    JidData(String _type, String _ss) {
        v = _ss;
        t = _type;
    }

    JidData(String _type, String _ss, String _flag) {
        v = _ss;
        t = _type;
        f = _flag;
        char[] cha = f.toCharArray();
        int value = 0;
        char type = '0';
        for (int i = 0; i < cha.length; i++) {
            if (cha[i] <= '9' && cha[i] >= '0') {
                value = value * 10 + Integer.parseInt("" + cha[i]);
                int sett = 0;
                if (i == (cha.length - 1)) {
                    sett = 1;
                } else {
                    if (cha[i + 1] > '9' || cha[i] < '0') {
                        sett = 1;
                    }
                }
                if (sett == 1) {
                    switch (type) {
                        case 'R':
                            record = value;
                            break;
                        case 'D':
                            dispType = value;
                            break;
                    }
                    value = 0;
                }
            } else {
                type = cha[i];
            }
        }
    }

    void setFlag(String flag) {
        char[] cha = flag.toCharArray();
        int value = 0;
        char type = '0';
        for (int i = 0; i < cha.length; i++) {
            if (cha[i] <= '9' && cha[i] >= '0') {
                value = value * 10 + Integer.parseInt("" + cha[i]);
                int sett = 0;
                if (i == (cha.length - 1)) {
                    sett = 1;
                } else {
                    if (cha[i + 1] > '9' || cha[i] < '0') {
                        sett = 1;
                    }
                }
                if (sett == 1) {
                    switch (type) {
                        case 'R':
                            record = value;
                            break;
                        case 'D':
                            dispType = value;
                            break;
                    }
                    value = 0;
                }
            } else {
                type = cha[i];
            }
        }
    }

    String getStrValue() {
        int ii;
        switch (t) {
            case "S":
                return v;
            case "I":
                ii = Lib.str2Number(v);
                if (ii != 1) {
                    return "_errorData";
                }
                if (dispType == 2) {
                    return Lib.intToHexStr(Lib.valueInt);
                }
                return "" + Lib.valueInt;
            case "L":
                ii = Lib.str2Number(v);
                if (ii == 1 || ii == 2) {
                    if (dispType == 2) {
                        return Lib.longToHexStr(Lib.valueLong);
                    }
                    return "" + Lib.valueLong;

                }
                return "_errorData";
            case "B":
                ii = Lib.str2Number(v);
                if (ii != 1) {
                    return "_errorData";
                }
                if (Lib.valueInt > 255) {
                    return "_errorData";
                }
                if (dispType == 2) {
                    return Lib.byteToHexStr(Lib.valueInt);
                }
                return "" + Lib.valueInt;
            case "D":
                ii = Lib.str2Number(v);
                if (ii == 0) {
                    return "_errorData";
                }
                return "" + Lib.valueDouble;
        }
        return "_noneType";

    }

    //return error
    static int checkType(String tt, String vv) {
        int ii;
        switch (tt) {
            case "U":
                return 0;
            case "S":
                return 0;
            case "L":
                ii = Lib.str2Number(vv);
                if (ii == 1 || ii == 2) {
                    return 0;
                }
                return 1;
            case "B":
                ii = Lib.str2Number(vv);
                if (ii != 1) {
                    return 1;
                }
                if (Lib.valueInt > 255) {
                    return 1;
                }
                return 0;
            case "D":
                ii = Lib.str2Number(vv);
                if (ii == 0) {
                    return 1;
                }
                return 0;
        }
        return 0;

    }

}

class JidObj {

    Map<String, JidData> mp = new HashMap();
    ArrayList<JidArray> jidArrayList = new ArrayList<>();

    JidObj() {
        mp.put("nid", new JidData("S", "JidObject"));
        mp.put("jid", new JidData("S", ""));
        mp.put("gid", new JidData("S", ""));
        mp.put("sname", new JidData("S", ""));
        mp.put("dsc", new JidData("S", ""));
        mp.put("editTime", new JidData("L", "0"));
    }

    //return errFlag
    //chkType=  0: full jid;
    //          1: first part jid
    //          2: jid range 
    static int chkJidString(String jidStr, int chkType) {
        String str;
        str = jidStr.trim();
        if (chkType == 2) {
            String[] strA = str.split("~");
            if (strA.length == 2) {
                if (JidObj.chkJidString(strA[0], 1) == 1) {
                    return 1;
                }
                if (JidObj.chkJidString(strA[1], 1) == 1) {
                    return 1;
                }
                return 0;
            } else {
                chkType = 1;
            }
        }

        if (str.length() == 0) {
            return 1;
        }
        if (chkType == 0) {
            if (str.length() != 15) {
                return 1;
            }
        }
        if (str.charAt(0) != 'J') {
            return 1;
        }
        for (int i = 1; i > str.length(); i++) {
            if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
                continue;
            }
            if (str.charAt(i) >= 'A' && str.charAt(i) <= 'F') {
                continue;
            }
            return 1;
        }
        return 0;
    }

    JidArray getJa(String groupName) {
        for (int i = 0; i < jidArrayList.size(); i++) {
            if (jidArrayList.get(i).groupName.equals(groupName)) {
                return jidArrayList.get(i);
            }
        }
        return null;
    }

    void editNewGroup(String className, String groupName) {
        JidArray ja;
        for (int i = 0; i < jidArrayList.size(); i++) {
            ja = jidArrayList.get(i);
            if (ja.groupName.equals(groupName)) {
                ja.className = className;
                ja.jids.clear();
                return;
            }
        }
        ja = new JidArray();
        ja.className = className;
        ja.groupName = groupName;
        jidArrayList.add(ja);
    }

    void delGroup(String groupName) {
        for (int i = 0; i < jidArrayList.size(); i++) {
            if (jidArrayList.get(i).groupName.equals(groupName)) {
                jidArrayList.remove(i);
                break;
            }
        }
    }

    void delGroupJid(String groupName, String jid) {
        JidArray ja;
        for (int i = 0; i < jidArrayList.size(); i++) {
            ja = jidArrayList.get(i);
            if (ja.groupName.equals(groupName)) {
                for (int j = 0; j < ja.jids.size(); j++) {
                    if (ja.jids.get(i).equals(jid)) {
                        ja.jids.remove(j);
                        return;
                    }
                }
                return;
            }
        }
    }

    ArrayList<String> getGroupJids(String groupName) {
        JidArray ja;
        for (int i = 0; i < jidArrayList.size(); i++) {
            ja = jidArrayList.get(i);
            if (ja.groupName.equals(groupName)) {
                return ja.jids;
            }
        }
        return null;
    }

    String getGroupClassName(String groupName) {
        JidArray ja;
        for (int i = 0; i < jidArrayList.size(); i++) {
            ja = jidArrayList.get(i);
            if (ja.groupName.equals(groupName)) {
                return ja.className;
            }
        }
        return null;
    }

    void addJid(String className, String groupName, String jid) {
        JidArray ja;
        for (int i = 0; i < jidArrayList.size(); i++) {
            ja = jidArrayList.get(i);
            if (ja.groupName.equals(groupName)) {
                for (int j = 0; j < ja.jids.size(); j++) {
                    if (ja.jids.get(j).equals(jid)) {
                        return;
                    }
                }
                ja.jids.add(jid);
                return;
            }
        }
        ja = new JidArray(className, groupName, 0);
        ja.jids.add(jid);
        jidArrayList.add(ja);
    }

    void put(String key, String type, String value) {
        JidData jd;
        jd = mp.get(key);
        if (jd != null) {
            jd.t = type;
            jd.v = value;
            return;
        }
        mp.put(key, new JidData(type, value));
    }

    boolean edit(String key, String value) {
        JidData jd;
        jd = mp.get(key);
        if (jd != null) {
            jd.v = value;
            return true;
        }
        return false;
    }

    String getv(String key) {
        JidData jd = mp.get(key);
        if (jd == null) {
            return null;
        }
        return jd.v;
    }

    String getf(String key) {
        JidData jd = mp.get(key);
        if (jd == null) {
            return null;
        }
        return jd.f;
    }

    int getRecord(String key) {
        JidData jd = mp.get(key);
        if (jd == null) {
            return 0;
        }
        return jd.record;
    }

    int getDispType(String key) {
        JidData jd = mp.get(key);
        if (jd == null) {
            return 0;
        }
        return jd.dispType;
    }

    int setv(String key, String value) {
        JidData jd = mp.get(key);
        if (jd == null) {
            return 0;
        }
        jd.v = value;
        java.util.Date now = new java.util.Date();
        mp.put("editTime", new JidData("L", "" + now.getTime()));
        return 1;
    }

    String geto(String key) {
        JidData jd = mp.get(key);
        if (jd == null) {
            return null;
        }
        if (jd.t.equals("S")) {
            return "\"" + jd.v + "\"";
        }
        return jd.v;
    }

    String gett(String key) {
        JidData jd = mp.get(key);
        if (jd == null) {
            return null;
        }
        return jd.t;
    }

    String newGroupById(String groupName, String jid) {
        JidArray ja;
        for (int i = 0; i < jidArrayList.size(); i++) {
            ja = jidArrayList.get(i);
            if (ja.groupName.equals(groupName)) {
                return null;
            }
        }

        return null;
    }

    //return 0:can not compare
    //return 1:jid1 < jid2
    //return 2:jid1 = jid2
    //return 3:jid1 > jid2
    static int cmpJid(String jid1, String jid2) {
        int len1 = jid1.length();
        int len2 = jid2.length();
        int len = len1;
        if (len1 > len2) {
            len = len2;
        }
        for (int i = 0; i < len; i++) {
            if (jid1.charAt(i) > jid2.charAt(i)) {
                return 3;
            }
            if (jid1.charAt(i) < jid2.charAt(i)) {
                return 1;
            }
        }
        return 2;
    }

    static String addInt2Jid(String jidStr, int addValue, int len) {
        String str = "";
        char[] cha = jidStr.toCharArray();
        String endStr = "";
        int stInx = cha.length - len;
        for (int i = 0; i < len; i++) {
            endStr += cha[stInx + i];
        }
        int base = Lib.str2int(endStr, 0);
        base += addValue;
        String addStr = "" + base;
        char[] chb = addStr.toCharArray();
        for (int i = 0; i < chb.length; i++) {
            cha[cha.length - i - 1] = chb[chb.length - i - 1];
        }
        str = new String(cha);;
        return str;
    }

    static int isJid(String jidStr) {
        int len = jidStr.length();
        char ch;
        if (len != 15) {
            return 1;
        }
        if (jidStr.charAt(0) != 'J') {
            return 1;
        }
        for (int i = 1; i < len; i++) {
            ch = jidStr.charAt(i);
            if (ch >= '0' && ch <= '9') {
                continue;
            }
            if (ch >= 'A' && ch <= 'F') {
                continue;
            }
            return 1;
        }
        return 0;
    }

    //type.0 0 delete agents and webs
    //type.21 0 show key in,1:show groupNumber,2 show jid,3 show gid
    static String trimPath(String fullPath, int typeFlag) {
        String path = "";
        String[] strB;
        String[] strC;
        String[] strD;
        String[] strA = fullPath.split("\\.");
        int first = 1;
        for (int i = 0; i < strA.length; i++) {
            strB = strA[i].split("#");
            if (strB.length == 1) {
                if (first == 0) {
                    path += ".";
                }
                path += strB[0];
            }
            if (strB.length != 2) {
                return "";
            }
            strC = strB[1].split("\\{");
            if (strC.length != 2) {
                return "";
            }
            strD = strC[1].split("\\}");

            if (first == 0) {
                path += ".";
            }
            first = 0;
            if ((typeFlag & 1) != 0) {
                if (strB[0].equals("agents")) {
                    first = 1;
                    continue;
                }
                if (strB[0].equals("webs")) {
                    first = 1;
                    continue;
                }
            }
            if (((typeFlag >> 1) & 3) == 0) {
                path += strB[0] + "#" + strC[0];
                continue;
            }
            if (((typeFlag >> 1) & 3) == 1) {
                path += strB[0] + "#" + strC[0];
                continue;
            }
            if (((typeFlag >> 1) & 3) == 2) {
                path += strB[0] + "#" + strD[0];
                continue;
            }
            if (((typeFlag >> 1) & 3) == 3) {
                String gid = MqttAgent.scla.jidMap.get(strD[0]).getv("gid");
                if (gid == null) {
                    path += strB[0] + "#" + strC[0];
                } else {
                    path += strB[0] + "#" + gid;
                }
                continue;
            }

        }
        return path;

    }

    static String getJidobjValue(String jidStr, String type, int deepCnt) {
        String[] strA;
        strA = jidStr.split("\\.");
        String value;
        String path;
        JidObj jobj;

        jobj = MqttAgent.scla.jidMap.get(jidStr);
        if (jobj == null) {
            return "\"null\"";
        }
        Myjs mj = new Myjs();
        mj.stObj();
        for (String key : jobj.mp.keySet()) {
            switch (type.charAt(2)) {
                case '0':
                    if (key.equals("jid")) {
                        continue;
                    }
                    if (key.equals("nid")) {
                        continue;
                    }
                    if (key.equals("gid")) {
                        continue;
                    }
                    if (key.equals("sname")) {
                        continue;
                    }
                    if (key.equals("dsc")) {
                        continue;
                    }

                    if (key.equals("connectTime")) {
                        continue;
                    }

                    break;
                case '1':
                    if (key.equals("dsc")) {
                        continue;
                    }
                    if (key.equals("sname")) {
                        continue;
                    }
                    if (key.equals("nid")) {
                        continue;
                    }
                    if (key.equals("connectTime")) {
                        continue;
                    }
                    break;
                case '2':
                    if (key.equals("dsc")) {
                        continue;
                    }
                    if (key.equals("connectTime")) {
                        continue;
                    }
                    break;
                case '3':
                    break;

            }
            value = jobj.geto(key);

            if (key.equals("editTime")) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String strDate = dateFormat.format(Long.parseLong(value));
                value = "\"" + strDate + "\"";
            }

            mj.addKeyVobj(key, value);
        }
        String jid;
        for (int i = 0; i < jobj.jidArrayList.size(); i++) {
            JidArray ja = jobj.jidArrayList.get(i);
            for (int j = 0; j < ja.jids.size(); j++) {
                jid = ja.jids.get(j);
                deepCnt++;
                if (deepCnt > 20) {
                    break;
                }
                value = getJidobjValue(jid, type, deepCnt);
                deepCnt--;
                path = ja.groupName + "#" + j;
                if (type.charAt(0) == 'B') {
                    path = ja.groupName + "#" + j;
                }
                if (type.charAt(0) == 'C') {
                    path = ja.groupName + "#" + jid;
                }
                if (type.charAt(0) == 'D') {
                    JidObj jo = MqttAgent.scla.jidMap.get(jid);
                    if (jo == null) {
                        path = ja.groupName + "#" + j;
                    } else {
                        path = ja.groupName + "#" + jo.getv("gid");
                    }
                }
                mj.addKeyVobj(path, value);
            }
        }
        mj.end();
        return mj.jstr;

    }

    static String getJidobjRecord(String jidStr, String type, int deepCnt) {
        String[] strA;
        strA = jidStr.split("\\.");
        String value;
        String path;
        JidObj jobj = null;

        jobj = MqttAgent.scla.jidMap.get(jidStr);
        if (jobj == null) {
            return "\"null\"";
        }
        Myjs mj = new Myjs();
        mj.stObj();
        for (String key : jobj.mp.keySet()) {
            int record = jobj.getRecord(key);
            if (record > 0) {
                value = JidObj.getRedisRecord(jobj, key, type);
                mj.addKeyVobj(key, value);
            }
        }
        String jid;
        for (int i = 0; i < jobj.jidArrayList.size(); i++) {
            JidArray ja = jobj.jidArrayList.get(i);
            for (int j = 0; j < ja.jids.size(); j++) {
                jid = ja.jids.get(j);
                deepCnt++;
                if (deepCnt > 20) {
                    break;
                }
                value = getJidobjValue(jid, type, deepCnt);
                deepCnt--;
                path = ja.groupName + "#" + j;
                if (type.charAt(0) == 'B') {
                    path = ja.groupName + "#" + j;
                }
                if (type.charAt(0) == 'C') {
                    path = ja.groupName + "#" + jid;
                }
                if (type.charAt(0) == 'D') {
                    JidObj jo = MqttAgent.scla.jidMap.get(jid);
                    if (jo == null) {
                        path = ja.groupName + "#" + j;
                    } else {
                        path = ja.groupName + "#" + jo.getv("gid");
                    }
                }
                mj.addKeyVobj(path, value);
            }
        }
        mj.end();
        return mj.jstr;

    }

    static String getValue(String keyin, String jidStr, String type) {
        String[] strA;
        strA = jidStr.split("\\.");
        String value;
        String path;
        int showType = 0;
        JidObj jobj;

        path = MqttAgent.scla.pathTreeMap.get(strA[0]);
        if (type.charAt(0) == 'A') {
            showType = 0;
        }
        if (type.charAt(0) == 'B') {
            showType = 1;
        }
        if (type.charAt(0) == 'C') {
            showType = 2;
        }
        if (type.charAt(0) == 'D') {
            showType = 3;
        }

        if (showType == 0) {
            path = keyin;
        } else {
            path = trimPath(path, 1 + (showType << 1)) + "." + strA[1];
        }

        jobj = MqttAgent.scla.jidMap.get(strA[0]);
        if (jobj == null) {
            value = "\"null\"";
            return value;
        }

        if (strA.length == 1) {
            value = getJidobjValue(strA[0], type, 0);
            if (value == null) {
                value = "\"null\"";
            }
            if (strA[0].equals("editTime")) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String strDate = dateFormat.format(Long.parseLong(value));
                strDate = "\"" + strDate + "\"";
                return strDate;
            }
            return value;
        }
        if (strA.length == 2) {
            value = MqttAgent.scla.jidMap.get(strA[0]).geto(strA[1]);

            if (value == null) {
                if (strA[1].equals("classDsc")) {
                    value = "\"null\"";
                    String nid = MqttAgent.scla.jidMap.get(strA[0]).geto("nid");
                    if (nid != null) {
                        String className = nid.substring(1, nid.length() - 1);;
                        Map<String, String> dscMap = MqttAgent.scla.cdsc.mmCdsc.get(className);
                        String dsc = dscMap.get("classDsc");
                        if (dsc != null) {
                            value = "\"" + dsc + "\"";
                        }
                    }
                } else {
                    value = "\"null\"";
                }
            }
            if (strA[1].equals("editTime")) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String strDate = dateFormat.format(Long.parseLong(value));
                strDate = "\"" + strDate + "\"";
                return strDate;
            }
            return value;
        }
        return null;
    }

    static String getRedisRecord(JidObj jobj, String item, String type) {
        int start = 0;
        int end = 10;
        String[] strA = type.split("~");
        if (strA.length == 1) {
            if (type.equals("ALL")) {
                start = 0;
                end = -1;
            }
            if (type.charAt(0) == 'L') {
                type = type.substring(1, type.length());
                end = Lib.str2int(type, 10);
            }
            if (type.charAt(0) == 'S') {
                type = type.substring(1, type.length());
                String[] strB = type.split("L");
                if (strB.length == 2) {
                    start = Lib.str2int(strB[0], 0);
                    end = Lib.str2int(strB[1], 10);
                    end = start + end;
                }
            }
        }
        if (strA.length == 2) {

        }

        String key = "list";
        key += "~" + "jidValue";
        key += "~" + jobj.getv("jid");
        key += "~" + item;
        key += "<~>" + start;
        key += "<~>" + end;
        KvRedis.actOne("getList", key.split("<~>"), null);
        String value = KvRedis.strList.toString();
        return value;
    }

    static String getRecord(String keyin, String jidStr, String type) {
        String[] strA;
        strA = jidStr.split("\\.");
        String value;
        String path;
        int showType = 0;
        JidObj jobj;

        path = MqttAgent.scla.pathTreeMap.get(strA[0]);
        if (type.charAt(0) == 'A') {
            showType = 0;
        }
        if (type.charAt(0) == 'B') {
            showType = 1;
        }
        if (type.charAt(0) == 'C') {
            showType = 2;
        }
        if (type.charAt(0) == 'D') {
            showType = 3;
        }

        if (showType == 0) {
            path = keyin;
        } else {
            path = trimPath(path, 1 + (showType << 1)) + "." + strA[1];
        }

        jobj = MqttAgent.scla.jidMap.get(strA[0]);
        if (jobj == null) {
            value = "\"null\"";
            return value;
        }

        if (strA.length == 1) {
            value = getJidobjRecord(strA[0], type, 0);
            if (value == null) {
                value = "\"null\"";
            }
            return value;
        }
        if (strA.length == 2) {
            int record = jobj.getRecord(strA[1]);
            if (record == 0) {
                value = "\"" + "Null" + "\"";
                return value;
            }
            value = JidObj.getRedisRecord(jobj, strA[1], type);
            return value;
        }
        return null;
    }

    static Map<String, String> trsJsonToMap(String sendJid, String istr, String _para) {
        JSONObject js;
        String kstr;
        String[] strA;
        Map<String, String> map = new HashMap();
        try {
            js = new JSONObject(istr);
            Iterator<String> it = js.keys();
            while (it.hasNext()) {
                kstr = it.next();
                strA = kstr.split("\\.");
                if (strA.length < 0) {

                }
                map.put(sendJid + "<~>" + kstr, js.get(kstr).toString());
            }
            return map;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    static String chkJidInJid(String jid, String chkJid, String path, int deepCnt) {
        String retStr;
        JidObj jobj = MqttAgent.scla.jidMap.get(jid);
        JidObj chkObj = MqttAgent.scla.jidMap.get(chkJid);
        if (jobj == null) {
            return null;
        }
        if (chkObj == null) {
            return null;
        }
        if (jobj.equals(chkJid)) {
            return path;
        }
        for (int i = 0; i < jobj.jidArrayList.size(); i++) {
            JidArray jd = jobj.jidArrayList.get(i);
            for (int j = 0; j < jd.jids.size(); j++) {
                if (jd.jids.get(j).equals(chkJid)) {
                    return path + "." + jd.groupName + "#" + j;
                }
                deepCnt++;
                if (deepCnt >= 20) {
                    return null;
                }
                retStr = JidObj.chkJidInJid(jd.jids.get(j), chkJid, path + "." + jd.groupName + "#" + j, deepCnt);
                deepCnt--;
                if (retStr != null) {
                    return retStr;
                }

            }

        }
        return null;
    }

    static void getJidInJid(String jid, String chkJid, ArrayList<String> retStrA, String path, int deepCnt) {
        JidObj jobj = MqttAgent.scla.jidMap.get(jid);
        JidObj chkObj = MqttAgent.scla.jidMap.get(chkJid);
        if (jobj == null) {
            return;
        }
        if (chkObj == null) {
            return;
        }
        if (jid.equals(chkJid)) {
            retStrA.add(path);
            return;
        }
        for (int i = 0; i < jobj.jidArrayList.size(); i++) {
            JidArray jd = jobj.jidArrayList.get(i);
            for (int j = 0; j < jd.jids.size(); j++) {
                if (jd.jids.get(j).equals(chkJid)) {
                    retStrA.add(path + "." + jd.groupName + "#" + j);
                    break;
                }
                deepCnt++;
                if (deepCnt >= 20) {
                    return;
                }
                JidObj.getJidInJid(jd.jids.get(j), chkJid, retStrA, path + "." + jd.groupName + "#" + j, deepCnt);
                deepCnt--;

            }

        }
        return;
    }

    //self
    //J403F0000000000.flags
    //#0.flags
    //relays#0.flags
    static String chkJidstrInJid(String jid, String jidStr) {
        if (isJid(jid) != 0) {
            return null;
        }
        if (jidStr.equals("self")) {
            return jid;
        }
        JidObj jobj = MqttAgent.scla.jidMap.get(jid);
        if (jobj == null) {
            return null;
        }
        if (jidStr.equals(jid)) {
            return jid;
        }
        String[] strA;
        String[] strB;
        String str;
        String actJid = jid;
        strA = jidStr.split("\\.");
        String item = null;
        String baseJid = jid;
        String groupName;
        for (int m = 0; m < strA.length; m++) {
            if (JidObj.isJid(strA[m]) == 0) {
                if (m == 0) {
                    if (chkJidInJid(jid, strA[m], jid, 0) == null) {
                        return null;
                    }
                }
                baseJid = strA[m];
                item = null;
                continue;
            }
            if (strA[m].equals("self")) {
                baseJid = jid;
                item = null;
                continue;
            }
            strB = strA[m].split("#");
            if (strB.length == 1) {
                item = strB[0];
                break;
            }
            if (strB.length == 2) {

                jobj = MqttAgent.scla.jidMap.get(baseJid);
                if (jobj == null) {
                    return null;
                }
                baseJid = null;
                int len = jobj.jidArrayList.size();
                if (len == 0) {
                    return null;
                }
                groupName = strB[0];
                if (strB[0].length() == 0) {
                    groupName = jobj.jidArrayList.get(0).groupName;
                }
                if (JidObj.isJid(strB[1]) == 0) {
                    baseJid = strB[1];
                    item = null;
                    continue;
                }
                for (int i = 0; i < jobj.jidArrayList.size(); i++) {
                    JidArray ja = jobj.jidArrayList.get(i);
                    if (!ja.groupName.equals(groupName)) {
                        continue;
                    }
                    int yes = 0;
                    for (int k = 0; k < ja.jids.size(); k++) {
                        if (strB[1].equals("" + k)) {
                            baseJid = ja.jids.get(k);
                            item = null;
                            yes = 1;
                            break;
                        }
                        JidObj jo = MqttAgent.scla.jidMap.get(ja.jids.get(k));
                        if (jo == null) {
                            continue;
                        }
                        if (jo.getv("gid").equals(strB[1])) {
                            baseJid = ja.jids.get(k);
                            item = null;
                            yes = 1;
                            break;
                        }

                    }
                    if (yes == 1) {
                        break;
                    }
                }

            }

        }
        if (baseJid == null) {
            return null;
        }
        if (item == null) {
            return baseJid;
        }
        return baseJid + "." + item;
    }

}

class ClassDsc {

    Map<String, Map<String, String>> mmCdsc = new HashMap();
    MqttAgent cla;

    ClassDsc(MqttAgent _cla) {
        cla = _cla;
        String name;
        Map<String, String> mss;
        //===================================
        name = "AgentObj";
        mss = new HashMap();
        mss.put("classDsc", "this is AgentObj classDsc");
        mss.put("jidArrayList~webs", "WebJobj");
        mmCdsc.put(name, mss);
        //==================================
        name = "WebJobj";
        mss = new HashMap();
        mss.put("classDsc", "this is WebObj classDsc");
        mss.put("jidArrayList~groups", "GroupJobj");
        mss.put("connectTime", "L~0");
        mmCdsc.put(name, mss);
        //==================================
        name = "GroupJobj";
        mss = new HashMap();
        mss.put("classDsc", "this is GroupObj classDsc");
        mss.put("connectTime", "L~0");
        mss.put("txType", "S~A00");
        mmCdsc.put(name, mss);
        //==================================
        name = "TruckJobj";
        mss = new HashMap();
        mss.put("classDsc", "this is TruckObj classDsc");
        mss.put("jidArrayList~relays", "RelayJobj");
        mss.put("jidArrayList~tyres", "TyreJobj");
        mss.put("flags", "I~0~D2");
        mss.put("truckType", "S");
        mss.put("gpsData", "S");
        mss.put("tyreLayout", "L~0~D2");

        mmCdsc.put(name, mss);
        //==================================
        name = "RelayJobj";
        mss = new HashMap();
        mss.put("classDsc", "this is RelayObj classDsc");
        mss.put("jidArrayList~tyres", "TyreJobj");
        mss.put("flags", "I");
        mss.put("id", "I~0");
        mss.put("tyreLayout", "I~0~D2");
        mss.put("lowPressureSet", "I~0~D2");
        mss.put("highPressureSet", "I~0~D2");
        mss.put("highTemperatureSet", "I~0~D2");
        mmCdsc.put(name, mss);
        //==================================
        name = "TyreJobj";
        mss = new HashMap();
        mss.put("classDsc", "this is TyreObj classDsc");
        mss.put("tpmsData", "I~0~R1D2");
        mss.put("testString", "S~0~R1");
        mss.put("id", "I~0");
        mmCdsc.put(name, mss);
        //==================================
    }

    void delete(String className) {
        mmCdsc.remove(className);
    }

    JidObj newClass(String className) {
        Map<String, String> mCdsc = mmCdsc.get(className);
        if (mCdsc == null) {
            return null;
        }
        String valueStr;
        String value;
        String groupName;
        String sonClassName;
        String[] strA;
        JidData jdata;
        JidObj jobj = new JidObj();
        jobj.mp.put("nid", new JidData("S", className));
        JidArray jidArray;
        for (String key : mCdsc.keySet()) {
            if (key.equals("classDsc")) {
                continue;
            }
            strA = key.split("~");
            if (strA.length == 2 && strA[0].equals("jidArrayList")) {
                groupName = key.substring(strA[0].length() + 1);
                sonClassName = mCdsc.get(key);
                jidArray = new JidArray(sonClassName, groupName, 0);
                jobj.jidArrayList.add(jidArray);
                continue;
            }
            if (strA.length == 1) {
                valueStr = mCdsc.get(key);
                if (valueStr == null) {
                    continue;
                }
                strA = valueStr.split("~");
                if (strA.length == 1) {
                    jdata = new JidData(strA[0], "");
                    jobj.mp.put(key, jdata);
                    continue;
                }
                if (strA.length == 2) {
                    jdata = new JidData(strA[0], strA[1]);
                    jobj.mp.put(key, jdata);
                    continue;
                }
                if (strA.length >= 3) {
                    int stlen = strA[0].length() + 1;
                    int endlen = valueStr.length() - (strA[strA.length - 1].length() + 1);
                    jdata = new JidData(strA[0], valueStr.substring(stlen, endlen), strA[2]);
                    jobj.mp.put(key, jdata);
                    continue;
                }

            }
        }
        return jobj;
    }

}

final class Jdsc {

    Map<String, Map<String, String>> mmDsc = new HashMap();
    Map<String, Map<String, JidObj>> mmJobj = new HashMap();
    MqttAgent cla;

    Jdsc(MqttAgent _cla) {
        cla = _cla;
        Map<String, String> map;
        String name;
        //===================
        name = "agentLs";
        map = new HashMap();
        map.put("className", "AgentJobj");
        map.put("name", name);
        map.put("sname", "Agent List");
        map.put("dsc", "this is the agentLs dsc");
        map.put("jidHead", "J003");
        map.put("son~0", "WebJobj~webs~J010");
        addMdsc(name, map);
        //==
        name = "webLs";
        map = new HashMap();
        map.put("className", "WebJobj");
        map.put("name", name);
        map.put("dsc", "this is the webLs dsc");
        map.put("sname", "Web List");
        map.put("jidHead", "J010");
        map.put("son~0", "GroupJobj~groups~J100~J400");
        addMdsc(name, map);
        //==
        name = "groupLs";
        map = new HashMap();
        map.put("className", "GroupJobj");
        map.put("name", name);
        map.put("sname", "Group List");
        map.put("dsc", "this is the groupLs dsc");
        map.put("jidHead", "J100~J400");
        map.put("son~0", "GroupJobj~groups~J100~J400");
        map.put("son~1", "TruckJobj~trucks~J403");
        map.put("son~2", "RelayJobj~relays~J402");
        map.put("son~3", "TyreJobj~tyres~J401");
        addMdsc(name, map);
        //==
        name = "truckLs";
        map = new HashMap();
        map.put("className", "TruckJobj");
        map.put("name", name);
        map.put("sname", "Truck List");
        map.put("dsc", "this is the truckLs dsc");
        map.put("jidHead", "J403");
        map.put("son~0", "RelayJobj~relays~J402");
        addMdsc(name, map);
        //==
        name = "relayLs";
        map = new HashMap();
        map.put("className", "RelayJobj");
        map.put("name", name);
        map.put("sname", "Relay List");
        map.put("dsc", "this is the relayLs dsc");
        map.put("jidHead", "J402");
        map.put("son~0", "TyreJobj~tyres~J401");
        addMdsc(name, map);
        //==
        name = "tyreLs";
        map = new HashMap();
        map.put("className", "TyreJobj");
        map.put("name", name);
        map.put("sname", "Tyre List");
        map.put("dsc", "this is the tyreLs dsc");
        map.put("jidHead", "J401");
        addMdsc(name, map);
        //==
    }

    void deleteJid(String jid) {
        for (String name : mmJobj.keySet()) {
            Map<String, JidObj> objLsMap = mmJobj.get(name);
            objLsMap.remove(jid);
        }
        cla.jidMap.remove(jid);

    }

    void newEditJidToAgent(JidObj jobj) {
        String jid = jobj.getv("jid");
        String dscName;
        ArrayList<Map<String, String>> am = getDscs(jid);
        for (int i = 0; i < am.size(); i++) {
            Map<String, String> mp = am.get(i);
            dscName = mp.get("name");
            JidObj oldObj = cla.jidMap.get(jid);
            cla.jidMap.put(jid, jobj);
            Map<String, JidObj> mJobj = mmJobj.get(dscName);
            mJobj.put(jid, jobj);
            continue;
        }
    }

    void addMdsc(String name, Map<String, String> map) {
        mmDsc.put(name, map);
        Map<String, JidObj> jidMap = new HashMap();
        mmJobj.put(name, jidMap);
    }

    void delete(String name) {
        mmDsc.remove(name);
        mmJobj.remove(name);
    }

    String mmDscGet(String dscName, String key) {
        Map<String, String> map = mmDsc.get(dscName);
        if (map == null) {
            return null;
        }
        String value = map.get(key);
        return value;
    }

    Map<String, JidObj> getSjmByName(String dscName) {
        return mmJobj.get(dscName);
    }

    ArrayList<Map<String, JidObj>> getSjmsByJid(String jid) {
        int ibuf;
        String jidHead;
        ArrayList<Map<String, JidObj>> am = new ArrayList();
        for (String dscName : mmDsc.keySet()) {
            Map<String, String> mDsc = mmDsc.get(dscName);
            jidHead = mDsc.get("jidHead");
            ibuf = Jdsc.chkJidHead(jid, jidHead);
            if (ibuf != 2) {
                continue;
            }
            am.add(mmJobj.get(dscName));
        }
        return am;
    }

    String getClassName(String jid) {
        int ibuf;
        String jidHead;
        for (String dscName : mmDsc.keySet()) {
            Map<String, String> mDsc = mmDsc.get(dscName);
            jidHead = mDsc.get("jidHead");
            ibuf = Jdsc.chkJidHead(jid, jidHead);
            if (ibuf != 2) {
                continue;
            }
            return mDsc.get("className");
        }
        return null;
    }

    static int chkJidHead(String jid, String jidHead) {
        String[] strA;
        int ibuf;
        strA = jidHead.split("~");
        if (strA.length == 2) {
            ibuf = JidObj.cmpJid(jid, strA[0]);
            if (ibuf <= 1) {
                return ibuf;
            }
            ibuf = JidObj.cmpJid(jid, strA[1]);
            if (ibuf >= 3) {
                return ibuf;
            }
            return 2;
        }
        if (strA.length == 1) {
            ibuf = JidObj.cmpJid(jid, strA[0]);
            return ibuf;
        }
        return 0;
    }

    Map<String, String> getFirstDsc(String jid) {
        String[] strA;
        int ibuf;
        for (String key : mmDsc.keySet()) {
            Map<String, String> mp = mmDsc.get(key);
            String jidHead = mp.get("jidHead");
            strA = jidHead.split("~");
            if (strA.length == 2) {
                ibuf = JidObj.cmpJid(jid, strA[0]);
                if (ibuf <= 1) {
                    continue;
                }
                ibuf = JidObj.cmpJid(jid, strA[1]);
                if (ibuf >= 3) {
                    continue;
                }
                return mp;
            }
            if (strA.length == 1) {
                ibuf = JidObj.cmpJid(jid, strA[0]);
                if (ibuf != 2) {
                    continue;
                }
                return mp;
            }
        }
        return null;
    }

    ArrayList<Map<String, String>> getDscs(String jid) {
        String[] strA;
        int ibuf;
        ArrayList<Map<String, String>> am = new ArrayList();

        for (String key : mmDsc.keySet()) {
            Map<String, String> mp = mmDsc.get(key);
            String jidHead = mp.get("jidHead");
            strA = jidHead.split("~");
            if (strA.length == 2) {
                ibuf = JidObj.cmpJid(jid, strA[0]);
                if (ibuf <= 1) {
                    continue;
                }
                ibuf = JidObj.cmpJid(jid, strA[1]);
                if (ibuf >= 3) {
                    continue;
                }
                am.add(mp);
                continue;
            }
            if (strA.length == 1) {
                ibuf = JidObj.cmpJid(jid, strA[0]);
                if (ibuf != 2) {
                    continue;
                }
                am.add(mp);
                continue;
            }
        }
        return am;
    }

}
