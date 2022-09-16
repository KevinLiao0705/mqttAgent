package base3;

import com.fazecast.jSerialComm.SerialPort;
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
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
//import gnu.io.CommPortIdentifier;
//import gnu.io.NoSuchPortException;
//import gnu.io.SerialPort;
import java.awt.List;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.util.Date;
import java.util.Calendar;

public class UartTest extends javax.swing.JDialog {

//=====================================
// 0: char, 1: uchar, 2: int8_t, 3: uint8_t, 4: int16_t, 5: uint16_t, 
// 6: int, 7: uint, 8: float, 9: double, 10: long 
    String[] dscTypeA = {
        "char",
        "uchar",
        "int8",
        "uint8",
        "int16",
        "uint16",
        "int32",
        "uint32",
        "float",
        "double",
        "long",
        "bit",
        "obj"
    };

    String[] eobj_dscA = {
        "0~1~   0~   4~_char~'a'",
        "1~1~   0~   5~_uchar~'b'",
        "2~1~   0~   6~_int8_t~-25",
        "3~1~   0~   7~_uint8_t~134",
        "6~4~   0~   8~_int32_t~-256",
        "7~4~   0~  12~_uint32_t~4294967200",
        "8~4~   0~  16~_float~1.234",
        "9~8~   0~  24~_double~2.468",
        "0~1~  12~  32~_charA~12345",
        "1~1~  12~  44~_ucharA~abcde",
        "2~1~   4~  56~_int8_tA~[-25, 119, 23, -4]",
        "3~1~   4~  60~_uint8_tA~[134, 233, 1, 222]",
        "6~4~   4~  64~_int32_tA~[-256, -111111, 3276888, 26777]",
        "7~4~   4~  80~_uint32_tA~[4294967200, 2222, 44555, 66666]",
        "8~4~   4~  96~_floatA~[1.234, 2.444, 6.7888, 6.789]",
        "9~8~   4~ 112~_doubleA~[2.468, 3,  6.56, 4344.555]",
        "0~1~  10~ 144~sockComBoudrate~115200",
        "0~1~   8~ 154~sockComParity~None",
        "3~1~   0~ 162~ntp_enable_f~0",
        "3~1~   0~ 163~mqtt_enable_f~0",
        "0~1~  32~ 164~softAp_ssid~kevin ESP8266",
        "0~1~  32~ 196~softAp_password~0000",
        "0~1~  32~ 228~wifi_ssid~kevin ASUS",
        "0~1~  32~ 260~wifi_password~0000",
        "6~4~   0~ 292~wifiReconnectTime~10000",
        "6~4~   0~ 296~wifiCheckStatusTime~1000",
        "3~1~   0~ 300~wifiConnectTimeOut_wifiMode~21",
        "3~1~   0~ 301~wifiConnectBreak_wifiMode~21",
        "3~1~   0~ 302~powerOn_wifiMode~21",
        "3~1~   0~ 303~wifiStartWithScan_f~1",
        "3~1~   0~ 304~serverEnable_f~1",
        "0~1~  32~ 305~ntpServerAddress~pool.ntp.org",
        "6~4~   0~ 340~ntpSyncPeriod~60",
        "3~1~   0~ 344~ntpTimeZone~8",
        "3~1~   0~ 345~ntpEnable_f~1",
        "0~1~  32~ 346~mqttServer~118.163.89.29",
        "6~4~   0~ 380~mqttPort~1883",
        "0~1~  32~ 384~mqttUserName~kevin",
        "0~1~  32~ 416~mqttPassword~xcdswe32@",
        "0~1~  32~ 448~mqttClientId~Kevin_esp8266",
        "6~4~   0~ 480~mqttConnectTrig_dly_k~10000",
        "3~1~   0~ 484~mqttConnectAfterTimeSync_f~1"
    };

    String[] gobj_dscA = {
        "3~1~   0~   0~wifi_mod~0",
        "3~1~   0~   1~wifi_connected_f~0",
        "3~1~   0~   2~mqtt_connected_f~0",
        "3~1~   0~   3~timeSynced_f~0",
        "3~1~   0~   4~printOutMode~2"
    };

    String[] f24obj_dscA = {
        "0~1~  16~   0~fatherId~12345",
        "0~1~  16~  16~deviceId~12345",
        "0~1~  32~  32~objectId~12345",
        "0~1~  32~  64~nameIdId~12345",
        "2~1~   0~ 128~INT8~0~255~0",
        "3~1~   0~ 129~UINT8~0~100~1",
        "4~2~   0~ 130~INT16~0",
        "5~2~   0~ 132~UINT16~0",
        "6~4~   0~ 134~INT32~0",
        "7~4~   0~ 138~UINT32~0",
        "8~4~   0~ 142~FLOAT~0",
        "9~8~   0~ 146~DOUBLE~0",};

    String[] r24obj_dscA = {
        "4~2~   0~   0~INT16~123",
        "5~2~   0~   2~UINT16~456",};

    String[] jsobj_dscA = {
        "7~0~0~0~nid~0",
        "7~0~0~1~jid~0",
        "7~0~0~2~fid~0",
        "7~0~0~3~gid~0",
        "7~0~0~4~dirty~0",
        "7~0~0~5~sname~0",
        "7~0~0~6~dsc~0",
        //=========================
        "7~0~0~7~carType~0",
        "7~0~0~8~switchAble~0",
        "7~0~0~9~segCount~0",
        "7~0~0~10~segDirty~0",
        "7~0~0~11~segJidA~0",};

//=====================================
    static public int ret_i;
    static public int ret_f = 0;
    static public String retStr = "";
    static UartTest scla;

    String title_str = "title_str";
    int fullScr_f = 1;
    int frameOn_f = 1;
    int winW = 1620;
    int winH = 1040;
    int firstShow_f = 0;
    Timer tm1 = null;
    Timer tm2 = null;
    int td1_run_f = 0;
    UartTestTd1 td1 = null;
    int td1_destroy_f = 0;
    PopWinTd popWinTd = null;

    Container cp;
    JButton[] bta1 = new JButton[16];   //bar button
    JButton[] bta2 = new JButton[40];   //left button
    JButton[] bta3 = new JButton[20];   //right button
    JButton[] bta4 = new JButton[2];   //pnTx panel

    JPanel pnMain, pnBar, pnLeft, pnRight, pnRightR, pnRightL, pnTx;
    JTextPane tp1;
    JScrollPane sp1;

    SimpleAttributeSet tp1FontSet = new SimpleAttributeSet();

    Color coBt;
    JTextField tf1;

    JLabel lbStatus;

    //==============
    int comSeted_f = 0;
    int comConnected_f = 0;

    //SerialPort serialPort;
    String uartTxType = "Text";//Text/Message/Pack A/Pack B

    int viewList_f = 0;

    CommPortReceiver cpr;
    char[] uartRxBuffer = new char[1024];
    int uartRxIndex = 0;
    //int uartRxStart_f = 0;
    int viewTrace_f = 0;
    //int uartRxLineCount = 0;
    //int uartMaxLineCount = 100;
    int viewLineChars = 0;
    int viewLineCount = 0;

    //int uartMaxLineChars = 100;
    String uartRxViewType = "Normal";//Normal/Hex
    char[] drawCharBuffer = new char[1024];
    int drawCharIndex = 0;
    int onDrawChar_f = 0;
    int sockUartGetting_f = 0;

    Csocket csock1;
    Ssocket ssk;
    SockSelector ssEcho;

    Csocket uartSock = null;
    RspHandler nioClientRxHandler;
    NioClient client;
    int nioServerStart_f = 0;

    int uartSockConnect_f = 0;
    //int sockUartStart_f = 0;
    int sockComOn_f = 0;
    int sockComSeted_f = 0;
    int sockComConnected_f = 0;

    int txMqttTest_cnt = 0;
    //==================================
    MyEncDec medClient = new MyEncDec();
    String[] cmdKeyNameA = new String[32];
    String[] cmdKeyValueA = new String[32];
    int cmdKeyLen = 0;
    int cmdKeyHandle = 10000;
    //==================================
    MyEncDec medUart = new MyEncDec();
    String[] uartKeyNameA = new String[32];
    String[] uartKeyValueA = new String[32];
    int uartKeyLen = 0;
    int uartKeyHandle = 10000;
    //====================================

    byte[] uartTxBuf = new byte[8192];
    int uartTxBuf_len;

    String statusStr = "";
    Color statusColor = Color.black;
    ArrayList<RespData> cmdRspdList = new ArrayList<>();
    ArrayList<RespData> uartRspdList = new ArrayList<>();

    int showStatusTime = 0;
    int showMesbox_f = 0;
    String showMesbox_text = "";
    String showMesbox_type = "";

    UartTestMsLis mslis;

    SerialPort serialPort;
    CommPortSender serialOut;

    int sepPanelCount = 0;
    //=====================
    int popWinTd_destroy_f;
    String popWinTd_mod;
    String popWinTd_act;
    String[] popWinTd_strPara = new String[10];
    int[] popWinTd_iPara = new int[10];
    String uartRspActStr = "";
    //=====================

    //static MyLayout ly=new MyLayout();
    public UartTest(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        UartTest cla = this;
        scla = cla;
        if (cla.frameOn_f == 0) {
            cla.setUndecorated(true);
        }
        cla.setBounds(-100, -100, 0, 0);
        csock1 = new Csocket();
    }

    public void sskRx() {
        this.insertDocument("Server RX: ", Color.BLACK);
        // public 
        String dataIn = new String(ssk.inbuf, 0, ssk.inbuf_len, StandardCharsets.UTF_8);
        dataIn += "\n";
        this.insertDocument(dataIn, Color.BLACK);
        //String s = new String(ssk.inbuf, StandardCharsets.UTF_8);        
        ssk.txretStr("Server Back \n");

    }

    public void sskRx(byte[] bytes, int len) {
        this.insertDocument("Server RX: ", Color.BLACK);
        this.insertDocument(new String(bytes, StandardCharsets.UTF_8), Color.BLACK);
    }

    public byte[] serverRx(byte[] bytes, int len, SocketChannel socket) {
        SocketAddress ss;
        String address;
        try {
            ss = socket.getLocalAddress();
            address = ss.toString();
        } catch (IOException ex) {
            Logger.getLogger(UartTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.insertDocument("NioServer RX: ", Color.GRAY);
        this.insertDocument(new String(bytes, StandardCharsets.UTF_8), Color.blue);
        this.insertDocument("NioServer TX: ", Color.GRAY);
        String retStr = "NioServer Return: 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ\n";
        this.insertDocument(retStr, Color.red);
        return retStr.getBytes();

    }

    String bytes2String(byte[] bts, int len, String sep) {
        String str = "";
        for (int i = 0; i < len; i++) {
            if (viewLineChars != 0) {
                str += sep;
                viewLineChars += sep.length();
            }
            str += Lib.asciiTbl[(bts[i] >> 4) & 15];
            str += Lib.asciiTbl[bts[i] & 15];
            viewLineChars += 2;
            if (viewLineChars >= GB.viewMaxLineChars) {
                str += "\n";
                viewLineChars = 0;
                viewLineCount++;
            }
        }
        return str;
    }

    int jsonrx_itemInx = 0;
    int[] jsonrx_keyStart = new int[32];
    int[] jsonrx_keyEnd = new int[32];
    int[] jsonrx_valueStart = new int[32];
    int[] jsonrx_valueEnd = new int[32];
    int[] jsonrx_dataType = new int[32];

    int anaJson(byte[] bts, int len) {
        int i;
        int buf_f;
        char chn;
        int jsonrx_step = 1;
        int nest = 0;
        jsonrx_itemInx = 0;
        int jsonrx_valueAlt_f = 0;
        int decHex_f = 0;
        for (i = 0; i < len; i++) {
            chn = (char) (bts[i] & 255);
            switch (jsonrx_step) {
                case 1: //check {
                    if (chn == '{') {
                        jsonrx_step++;
                        jsonrx_itemInx = 0;
                        break;
                    }
                    if (chn == ' ' || chn == '\n' || chn == '\t') {
                        break;
                    }
                    return -1;
                case 2: //chk key start "\""
                    if (chn == '"' || chn == '\'') {
                        if (jsonrx_itemInx >= 32) {
                            return -1;
                        }
                        jsonrx_keyStart[jsonrx_itemInx] = i;
                        jsonrx_step++;
                        break;
                    }
                    if (chn == ' ' || chn == '\n' || chn == '\t') {
                        break;
                    }
                    return -1;
                case 3: //chk key end"\""
                    if (chn == '"' || chn == '\'') {
                        jsonrx_keyEnd[jsonrx_itemInx] = i;
                        jsonrx_step++;
                        break;
                    }
                    if (chn >= 0x20) {
                        break;
                    }
                    return -1;
                case 4: //chk :
                    if (chn == ' ' || chn == '\n' || chn == '\t') {
                        break;
                    }
                    if (chn == ':') {
                        jsonrx_step++;
                        break;
                    }
                    return -1;
                case 5: //chk value start
                    if (chn == ' ' || chn == '\n' || chn == '\t') {
                        break;
                    }
                    if (chn == '"' || chn == '\'') {
                        jsonrx_valueStart[jsonrx_itemInx] = i;
                        jsonrx_dataType[jsonrx_itemInx] = 0;
                        jsonrx_valueAlt_f = 0;
                        jsonrx_step++;
                        break;
                    }
                    if (chn >= '0' && chn <= '9') {
                        jsonrx_valueStart[jsonrx_itemInx] = i;
                        jsonrx_dataType[jsonrx_itemInx] = 1;
                        jsonrx_step++;
                        decHex_f = 0;
                        char chNext = (char) (bts[i + 1] & 255);
                        if (chNext == 'x' || chNext == 'X') {
                            decHex_f = 1;
                        }
                        break;
                    }
                    if (chn == '-') {
                        jsonrx_valueStart[jsonrx_itemInx] = i;
                        jsonrx_dataType[jsonrx_itemInx] = 1;
                        jsonrx_step++;
                        break;
                    }
                    if (chn == '[') {
                        jsonrx_valueStart[jsonrx_itemInx] = i;
                        jsonrx_dataType[jsonrx_itemInx] = 2;
                        jsonrx_step++;
                        nest = 1;
                        break;
                    }
                    if (chn == '{') {
                        jsonrx_valueStart[jsonrx_itemInx] = i;
                        jsonrx_dataType[jsonrx_itemInx] = 3;
                        jsonrx_step++;
                        nest = 1;
                        break;
                    }
                    return -1;
                case 6:                                     //chk value
                    if (jsonrx_dataType[jsonrx_itemInx] == 0) //string
                    {
                        if (chn == '"' || chn == '\'') {
                            if (jsonrx_valueAlt_f == 1) {
                                jsonrx_valueAlt_f = 0;
                                break;
                            } else {
                                jsonrx_valueEnd[jsonrx_itemInx] = i;
                                jsonrx_itemInx++;
                                jsonrx_step++;
                                break;
                            }
                        }
                        if (chn == '\\') {
                            jsonrx_valueAlt_f = 1;
                            break;
                        }
                        jsonrx_valueAlt_f = 0;
                        if (chn == ' ' || chn == '\n' || chn == '\t') {
                            break;
                        }
                        if (chn < 20) {
                            return -1;
                        }
                        break;
                    }

                    if (jsonrx_dataType[jsonrx_itemInx] == 1) //number
                    {
                        if (chn >= '0' && chn <= '9') {
                            break;
                        }
                        if (chn == '.') {
                            break;
                        }
                        if (decHex_f == 1) {
                            if (chn == 'x') {
                                break;
                            }
                            if (chn == 'X') {
                                break;
                            }
                            if (chn >= 'a' && chn <= 'f') {
                                break;
                            }
                            if (chn >= 'A' && chn <= 'F') {
                                break;
                            }
                        }

                        buf_f = 0;
                        if (chn == ' ' || chn == '\n' || chn == '\t') {
                            buf_f = 1;
                        }
                        if (chn == ',') {
                            buf_f = 2;
                        }
                        if (chn == '}') {
                            buf_f = 3;
                        }
                        if (buf_f != 0) {
                            jsonrx_valueEnd[jsonrx_itemInx] = i - 1;
                            jsonrx_itemInx++;
                            if (buf_f == 1) {
                                jsonrx_step++;
                            }
                            if (buf_f == 2) {
                                jsonrx_step = 2;
                                break;
                            }
                            if (buf_f == 3) {
                                return jsonrx_itemInx;
                            }
                        }
                        return -1;
                    }
                    if (jsonrx_dataType[jsonrx_itemInx] == 2) //array
                    {
                        if (chn == '[') {
                            nest++;
                            break;
                        }
                        if (chn == ']') {
                            nest--;
                            if (nest == 0) {
                                jsonrx_valueEnd[jsonrx_itemInx] = i;
                                jsonrx_itemInx++;
                                jsonrx_step++;
                                break;
                            }
                        }
                        break;
                    }
                    if (jsonrx_dataType[jsonrx_itemInx] == 3) //obj
                    {
                        if (chn == '{') {
                            nest++;
                            break;
                        }
                        if (chn == '}') {
                            nest--;
                            if (nest == 0) {
                                jsonrx_valueEnd[jsonrx_itemInx] = i;
                                jsonrx_itemInx++;
                                jsonrx_step++;
                                break;
                            }
                        }
                        break;
                    }
                    return -1;

                case 7: //check next
                    if (chn == ' ' || chn == '\n' || chn == '\t') {
                        break;
                    }
                    if (chn == '}') {
                        return jsonrx_itemInx;
                    }
                    if (chn == ',') {
                        jsonrx_step = 2;
                        break;
                    }
                    return -1;
            }
        }
        return -1;
    }

    String texts2String(byte[] bts, int len) {
        String str = "";
        char ch;

        for (int i = 0; i < len; i++) {
            ch = (char) bts[i];
            /*
            if (ch == 0) {
                continue;
            }
             */
            viewLineChars++;
            if (bts[i] == '\n') {
                str += ch;
                viewLineChars = 0;
                viewLineCount++;
                continue;
            }
            if (bts[i] == '\r') {
                continue;
            }
            if (ch < 0x20) {
                ch = '?';
            }
            str += ch;
            if (viewLineChars >= GB.viewMaxLineChars) {
                str += "\n";
                viewLineChars = 0;
                viewLineCount++;
            }
        }
        return str;
    }

    public void clientRx(byte[] bytes, int len) {

        /*    
        if (viewLineCount >= GB.viewMaxLines) {
            tp1.setText("");
            viewLineCount = 0;
            viewLineChars = 0;
        }
         */
        if (GB.sockComEncorder.equals("Byte")) {
            if (viewList_f == 1) {
                this.insertDocument(bytes2String(bytes, len, " "), Color.blue);
            }
        }
        if (GB.sockComEncorder.equals("Text")) {
            if (viewList_f == 1) {
                this.insertDocument(texts2String(bytes, len), Color.blue);
            }
        }
        if (GB.sockComEncorder.equals("KVENC")) {
            medClient.myDec(bytes, len);
            //decsrx(bytes, len);

        }
    }

    public void ssRx(ByteBuffer bb) {
        String dataIn = "Server RX: " + new String(bb.array(), 0, bb.position(), StandardCharsets.UTF_8);
        //String dataIn=new String(Arrays.copyOfRange(bb.array(), 0, bb.limit()));
        dataIn += "\n";
        this.insertDocument(dataIn, Color.BLACK);
        //String s = new String(ssk.inbuf, StandardCharsets.UTF_8);        
        //ssk.txretStr("Server Back \n");

    }

    public void create() {
        final UartTest cla = this;
        //==========================================
        cla.setTitle("UartTest");
        Font myFont = new Font("Serif", Font.BOLD, 24);
        cla.addWindowListener(new UartTestWinLis(cla));
        cla.addWindowListener(new UartTestWinLis(cla));

        cla.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ev) {
                cla.windowShow();
            }
        });

        mslis = new UartTestMsLis(cla);
        //===============================================
        medClient.rxClass = new MyEncDecRx() {
            @Override
            public void rxprg(MyEncDec med) {
                cla.clientMedRx(med);
            }
        };
        //===============================================
        medUart.rxClass = new MyEncDecRx() {
            @Override
            public void rxprg(MyEncDec med) {
                cla.uartMedRx(med);
            }
        };
        //===============================================
        //uartSock = new Csocket();
        //cla.uartSockConnect_f = 0;
        //========================================================
        /*
        ExecutorService svc = Executors.newSingleThreadExecutor();
        ssEcho = new SockSelector();
        ssEcho.rxClass= new SockSelectorRx(){
            @Override
            public void rxprg(ByteBuffer bb) {
                cla.ssRx(bb);
            }
            
            
        };
        svc.execute(ssEcho);
         */
        //====================================================
        try {
            EchoWorker worker = new EchoWorker();
            worker.nioServerRx = new NioServerRx() {
                @Override
                public byte[] rxprg(byte[] bytes, int len, SocketChannel socket) {
                    return cla.serverRx(bytes, len, socket);
                }
            };
            new Thread(worker).start();
            new Thread(new NioServer("127.0.0.1", 1234, worker)).start();
            nioServerStart_f = 1;
        } catch (IOException e) {
            Base3.log.error(e.toString());
        }
        //====================================================

        nioClientRxHandler = new RspHandler();
        nioClientRxHandler.nioClentRx = new NioClientRx() {
            @Override
            public void rxprg(byte[] bytes, int len) {
                cla.clientRx(bytes, len);
            }
        };
        try {
            client = new NioClient("127.0.0.1", 1234);
            Thread t = new Thread(client);
            t.setDaemon(true);
            t.start();
            //handler.waitForResponse();
        } catch (Exception ea) {
            ea.printStackTrace();
        }

        /*
        ssk = new Ssocket();
        ssk.format = 0;
        ssk.readTimeOut = 100;//unit 10ms
        ssk.create(1234);
        ssk.sskRx = new SskRx() {
            @Override
            public void sskRx(int format) {
                cla.sskRx();
            }
        };
        ssk.start();
         */
        //=============================================
        cp = cla.getContentPane();
        cp.setLayout(null);
        cp.setBackground(Color.CYAN);
        pnMain = new JPanel();
        pnBar = new JPanel();
        pnLeft = new JPanel();
        pnRight = new JPanel();
        pnRightR = new JPanel();
        pnRightL = new JPanel();
        pnTx = new JPanel();

        pnLeft.setBackground(Color.CYAN);
        pnRight.setBackground(Color.CYAN);
        pnRightR.setBackground(Color.CYAN);
        pnRightL.setBackground(Color.CYAN);
        pnTx.setBackground(Color.CYAN);

        cp.add(pnMain);
        cp.add(pnBar);
        pnMain.add(pnLeft);
        pnMain.add(pnRight);
        pnRight.add(pnRightR);
        pnRight.add(pnRightL);
        pnRightL.add(pnTx);

        tp1 = new JTextPane();
        sp1 = new JScrollPane(cla.tp1);
        pnRightL.add(cla.sp1);

        tp1.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
        StyleConstants.setFontSize(tp1FontSet, 12);//设置字体大小

        lbStatus = new JLabel();
        lbStatus.setBackground(Color.yellow);
        lbStatus.setOpaque(true);
        lbStatus.setText("");
        pnRightL.add(cla.lbStatus);

        //insertDocument("eqweqwe", Color.RED);
        tf1 = new JTextField();
        pnTx.add(cla.tf1);

        //===============================================
        int i;
        for (i = 0; i < cla.bta1.length; i++) {
            bta1[i] = new JButton();
            bta1[i].setFont(myFont);
            bta1[i].setName(Integer.toString(0 * 256 + i));
            bta1[i].addMouseListener(mslis);
            bta1[i].setFocusable(false);
            bta1[i].setVisible(true);
            bta1[i].setText("" + i);
            pnBar.add(bta1[i]);
        }
        bta1[15].setText("ESC");
        bta1[14].setText("getTime");
        coBt = bta1[0].getBackground();
        for (i = 0; i < cla.bta2.length; i++) {
            bta2[i] = new JButton();
            bta2[i].setFont(myFont);
            bta2[i].setName(Integer.toString(1 * 256 + i));
            bta2[i].addMouseListener(mslis);
            bta2[i].setFocusable(false);
            bta2[i].setVisible(true);
            bta2[i].setText("" + i);
            pnLeft.add(bta2[i]);
        }
        //===========================
        for (i = 0; i < cla.bta3.length; i++) {
            bta3[i] = new JButton();
            bta3[i].setFont(new Font("Serif", Font.BOLD, 14));
            bta3[i].setName(Integer.toString(2 * 256 + i));
            bta3[i].addMouseListener(mslis);
            bta3[i].setFocusable(false);
            bta3[i].setVisible(true);
            bta3[i].setText("" + i);
            pnRightR.add(bta3[i]);
        }
        bta3[7].setBackground(Color.white);
        bta3[13].setBackground(Color.white);
        //===========================
        for (i = 0; i < cla.bta4.length; i++) {
            bta4[i] = new JButton();

            bta4[i].setFont(new Font("Serif", Font.BOLD, 14));
            bta4[i].setName(Integer.toString(3 * 256 + i));
            bta4[i].addMouseListener(mslis);
            bta4[i].setFocusable(false);
            bta4[i].setOpaque(false);
            pnTx.add(bta4[i]);
        }

        if (cla.tm1 == null) {
            cla.tm1 = new Timer(10, new UartTestTm1(cla));  //about 30ms 
            cla.tm1.start();
        }
        if (cla.tm2 == null) {
            cla.tm2 = new Timer(20, new UartTestTm2(cla));
            cla.tm2.start();
        }
        if (cla.td1 == null) {
            cla.td1 = new UartTestTd1(cla);
            cla.td1_run_f = 1;
            cla.td1_destroy_f = 0;
            cla.td1.start();
        }

        if (cla.popWinTd == null) {
            cla.popWinTd = new PopWinTd(cla);
            cla.popWinTd_act = "";
            cla.popWinTd_destroy_f = 0;
            cla.popWinTd.start();
        }

    }

    public void insertDocument(String text, Color textColor)//根据传入的颜色及文字，将文字插入文本域
    {
        StyleConstants.setForeground(tp1FontSet, textColor);//设置文字颜色
        Document doc = tp1.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), text, tp1FontSet);//插入文字
        } catch (BadLocationException e) {
        }
    }

    public String[] listComPort() {
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] result = new String[ports.length];
        for (int i = 0; i < ports.length; i++) {
            result[i] = ports[i].getSystemPortName();
        }
        return result;

        /*
        Enumeration comIdentifiersEn = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier portId = null;
        ArrayList<String> list = new ArrayList<>();

        while (comIdentifiersEn.hasMoreElements()) {
            portId = (CommPortIdentifier) comIdentifiersEn.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                list.add(portId.getName());
            }
        }
         */
        //new ListAvailablePorts().list();           
    }

    public void closeUart() {
        if (serialPort != null) {
            cpr.terminate();
            try {
                sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(UartTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
            serialPort.removeDataListener();
            //logger.debug("Going to close the port...");
            boolean result = serialPort.closePort();
            //logger.debug("Port closed? {}", result);
            serialPort = null;
            comSeted_f = 0;
            viewList_f = 0;
        }
        /*
        if (serialPort != null) {
            cpr.terminate();
            try {
                sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(UartTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            //serialPort.close();
            serialPort = null;
            comSeted_f = 0;
            viewList_f = 0;

        }
         */
    }

    public void flushDrawBuffer() {
        //String str;
        if (drawCharIndex == 0) {
            return;
        }
        String str = "";
        if (uartRxViewType.equals("Normal")) {
            str = new String(drawCharBuffer, 0, drawCharIndex);
        } else {
            char[] charA = new char[drawCharIndex * 3];
            int index = 0;
            for (int i = 0; i < drawCharIndex; i++) {
                charA[index++] = Lib.asciiTbl[(drawCharBuffer[i] >> 4) & 15];
                charA[index++] = Lib.asciiTbl[(drawCharBuffer[i]) & 15];
                if (drawCharBuffer[i] == '\n') {
                    charA[index++] = '\n';
                }
            }
            str = new String(charA, 0, index);
        }

        insertDocument(str, Color.BLACK);
        drawCharIndex = 0;
    }

    public void drawChar(char ch) {
        //insertDocument("" + ch, Color.BLACK);

        onDrawChar_f = 1;
        drawCharBuffer[drawCharIndex] = ch;
        drawCharIndex++;
        if (drawCharIndex > 1000) {
            flushDrawBuffer();
        }
        onDrawChar_f = 0;

    }

    public void onUartReveive(byte[] bytes, int len) {

        if (GB.comEncorder.equals("Byte")) {
            if (viewList_f == 1) {
                this.insertDocument(bytes2String(bytes, len, " "), Color.blue);
            }
        }
        if (GB.comEncorder.equals("Text")) {
            if (viewList_f == 1) {
                this.insertDocument(texts2String(bytes, len), Color.blue);
            }
        }
        if (GB.comEncorder.equals("KVENC")) {
            medUart.myDec(bytes, len);
            //decsrx(bytes, len);

        }

        /*
        byte b;
        for (int i = 0; i < len; i++) {
            b = ba[i];
            if (viewList_f != 0) {
                //insertDocument("" + (char) (b & 255), Color.BLACK);
                drawChar((char) (b & 255));
                viewLineChars++;
                if ((b & 255) == '\n') {
                    viewLineCount++;
                    viewLineChars = 0;
                }
                if (GB.viewMaxLineChars > 10) {
                    if (viewLineChars > GB.viewMaxLineChars || viewLineChars > 1000) {
                        //insertDocument("\n", Color.BLACK);
                        drawChar('\n');
                        viewLineCount++;
                        viewLineChars = 0;
                    }
                }
            }
        }
         */
    }

    public void onUartStreamClosed() {
        flushDrawBuffer();
        /*
        if(uartRxIndex==0)
            return;
        uartRxBuffer[uartRxIndex] = 0;;
        insertDocument("" + uartRxBuffer, Color.BLACK);
        uartRxIndex = 0;
         */

    }

    private boolean isSerialPort(SerialPort sp) {
        String portName = sp.getSystemPortName().toLowerCase();
        String portDesc = sp.getDescriptivePortName().toLowerCase();
        return ((GB.os == 2) && portName.startsWith("cu") && portName.contains("usbserial")
                || (GB.os == 2) && portName.startsWith("cu.hc-0")
                || // Bluetooth uart on Mac
                (GB.os == 0) && portDesc.contains("serial")
                || (GB.os == 0) && portDesc.contains("uart")
                || (GB.os == 0) && portDesc.contains("hc-0")
                || // Bluetooth uart on Win
                (GB.os == 1) && portDesc.contains("usb") && portDesc.contains("serial")
                || (GB.os == 1) && portDesc.contains("hc-0")
                || // Bluetooth uart on Linux?
                portDesc.contains("pmsensor"));   // TODO make the name configurable (custom name for BT HC-05/HC-06 or even normal serial)
        /*
            return (SystemUtils.IS_OS_MAC_OSX && portName.startsWith("cu") && portName.contains("usbserial") ||
            SystemUtils.IS_OS_MAC_OSX && portName.startsWith("cu.hc-0") ||  // Bluetooth uart on Mac
            SystemUtils.IS_OS_WINDOWS && portDesc.contains("serial") ||
            SystemUtils.IS_OS_WINDOWS && portDesc.contains("hc-0") || // Bluetooth uart on Win
            SystemUtils.IS_OS_LINUX && portDesc.contains("usb") && portDesc.contains("serial") || 
            SystemUtils.IS_OS_LINUX && portDesc.contains("hc-0") || // Bluetooth uart on Linux?
            portDesc.contains("pmsensor")   // TODO make the name configurable (custom name for BT HC-05/HC-06 or even normal serial)
            );
         */
    }

    public boolean openUart(String portName, String Parity, int boudrate) {
        UartTest cla = this;
        String comName;
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            //logger.warn("No serial ports available!");
            return false;
        }
        //logger.debug("Got {} serial ports available", ports.length);
        int portToUse = -1;

        for (int i = 0; i < ports.length; i++) {
            SerialPort sp = ports[i];
            //logger.debug("\t- {}, {}", sp.getSystemPortName(), sp.getDescriptivePortName());
            comName = sp.getSystemPortName();//.toLowerCase();
            if (comName.equals(portName)) {
                portToUse = i;
                break;
            }
        }
        if (portToUse < 0) {
            //logger.warn("No this port on this system!");
            return false;
        }
        int parity = SerialPort.NO_PARITY;
        if (Parity.equals("Even")) {
            parity = SerialPort.EVEN_PARITY;
        }
        if (Parity.equals("Odd")) {
            parity = SerialPort.ODD_PARITY;
        }
        serialPort = ports[portToUse];
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        serialPort.setComPortParameters(boudrate, 8, SerialPort.ONE_STOP_BIT, parity);
        //serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        //serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        //logger.debug("Going to open the port...");
        boolean result = serialPort.openPort();
        if (result) {
            serialOut = new CommPortSender();
            serialOut.setWriterStream(serialPort.getOutputStream());
            // setup serial port reader
            cpr = new CommPortReceiver(serialPort.getInputStream());
            cpr.start();
            comSeted_f = 1;
            viewList_f = 1;
        } else {
            comSeted_f = 0;
            viewList_f = 0;
        }
        return result;
        //logger.debug("Port opened? {}", result);

        /*
        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            if (portIdentifier.isCurrentlyOwned()) {
                System.out.println("Port in use!");
            } else {
                // points who owns the port and connection timeout
                serialPort = (SerialPort) portIdentifier.open(portName, 2000);
                // setup connection parameters
                int parity = SerialPort.PARITY_NONE;
                if (Parity.equals("Even")) {
                    parity = SerialPort.PARITY_EVEN;
                }
                if (Parity.equals("Odd")) {
                    parity = SerialPort.PARITY_ODD;
                }
                serialPort.setSerialPortParams(
                        boudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, parity);
                // setup serial port writer
                CommPortSender.setWriterStream(serialPort.getOutputStream());
                // setup serial port reader
                cla.cpr = new CommPortReceiver(serialPort.getInputStream());
                cpr.start();
                comSeted_f = 1;
                viewList_f = 1;
            }
        } catch (Exception ex) {
            Message mes1 = new Message(null, true);
            mes1.keyType_i = 0;
            mes1.mesType_i = 1;
            mes1.title_str = "Open Uart Error !!!";
            mes1.create();
            mes1.setVisible(true);
        }
         */
    }

    void clientTx(String address, int port, String outStr, int msTimeOut) {
        Socket client = new Socket();
        InetSocketAddress isa = new InetSocketAddress(address, port);
        try {
            client.connect(isa, msTimeOut);
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            // 送出字串
            out.write(outStr.getBytes());
            out.flush();
            out.close();
            out = null;
            client.close();
            client = null;
        } catch (java.io.IOException e) {
            System.out.println("Socket連線有問題 !");
            System.out.println("IOException :" + e.toString());
        }
    }
    //void clientGet(String address, int port, String outStr, int msTimeOut) {
    int socketError_f = 0;
    int socketFinished_f = 0;
    String socketError;

    //getMode data/html
    void clientGet(String address, int port, String outStr, String getMode) {
        socketError_f = 0;
        socketError = "";
        Socket clientSocket = new Socket();
        try {
            clientSocket.connect(new InetSocketAddress(address, port), 1000);
            clientSocket.setSoTimeout(1000);
        } catch (IOException ex) {
            socketFinished_f = 1;
            socketError_f = 1;
            socketError = "Connect Error To" + address + " " + port;
            return;
        }

        try {

            InputStream is = clientSocket.getInputStream();
            BufferedOutputStream os = new BufferedOutputStream(clientSocket.getOutputStream());
            switch (getMode) {
                case "data":
                    os.write("GET / HTTP/1.0\n\n".getBytes());
                    os.flush();
                    os.close();
                    break;
                case "html":
                    PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
                    pw.println("GET / HTTP/1.0");
                    pw.println();
                    pw.flush();
                    break;
            }

            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                String output = new String(buffer, 0, read);
                insertDocument(output, Color.red);
                System.out.print(output);
                System.out.flush();
            };
        } catch (IOException ex) {
            insertDocument("Read Socket Error !!!\n", Color.red);
        }
    }

    void uartMedRx(MyEncDec med) {
        int i, j;
        String key;
        String strValue;
        int type = 0; //0 for string,1 for int, 2 for float, 3 for array 4 for object 
        int st;
        int end;
        if (med.rxin_type == '0')//dirt
        {
            if (viewList_f == 1) {
                if (GB.viewPack.contains("0")) {
                    this.insertDocument(texts2String(med.rdata, med.rxin_byte), new Color(0, 100, 0));
                }
            }
        }
        if (med.rxin_type == '1')//system
        {
            if (viewList_f == 1) {
                if (GB.viewPack.contains("1")) {
                    this.insertDocument(texts2String(med.rdata, med.rxin_byte), new Color(0, 100, 0));
                }
            }
        }
        if (med.rxin_type == 'B')//json A:cmd B:uart
        {
            if (med.rxin_byte <= 2) {
                return;
            }
            if (anaJson(med.rdata, med.rxin_byte) <= 0) {
                return;
            }
            for (i = 0; i < jsonrx_itemInx; i++) {
                st = jsonrx_keyStart[i] + 1;
                end = jsonrx_keyEnd[i] - 1;
                key = "";
                for (j = st; j <= end; j++) {
                    key += (char) med.rdata[j];
                }
                st = jsonrx_valueStart[i];
                end = jsonrx_valueEnd[i];
                if ((char) med.rdata[st] == '"') {
                    st += 1;
                    end -= 1;
                    type = 0;
                } else if ((char) med.rdata[st] == '[') {
                    st += 1;
                    end -= 1;
                    type = 3;
                } else if ((char) med.rdata[st] == '{') {
                    st += 1;
                    end -= 1;
                    type = 4;
                } else {
                    type = 1;
                }
                strValue = "";
                for (j = st; j <= end; j++) {
                    strValue += (char) med.rdata[j];
                    if (type == 1) {
                        if ((char) med.rdata[j] == '.') {
                            type = 2;
                        }
                    }
                }
                if (med.rxin_type == 'B') {
                    anaUartKeyValue(key, strValue, type);
                }

            }

            if (viewList_f == 1) {
                if (GB.viewPack.contains("B")) {
                    if (med.rxin_type == 'B') {
                        this.insertDocument(texts2String(med.rdata, med.rxin_byte), Color.black);
                    }
                }
            }
        }

    }

    void clientMedRx(MyEncDec med) {
        int i, j;
        String key;
        String strValue;
        int type = 0; //0 for string,1 for int, 2 for float
        int st;
        int end;
        if (med.rxin_type == '0')//dirt
        {
            if (viewList_f == 1) {
                if (GB.viewPack.contains("0")) {
                    this.insertDocument(texts2String(med.rdata, med.rxin_byte), new Color(0, 100, 0));
                }
            }
        }
        if (med.rxin_type >= 'A' && med.rxin_type <= 'B')//json A:cmd B:uart
        {
            if (med.rxin_byte <= 2) {
                return;
            }
            if (anaJson(med.rdata, med.rxin_byte) <= 0) {
                return;
            }
            for (i = 0; i < jsonrx_itemInx; i++) {
                st = jsonrx_keyStart[i] + 1;
                end = jsonrx_keyEnd[i] - 1;
                key = "";
                for (j = st; j <= end; j++) {
                    key += (char) med.rdata[j];
                }
                st = jsonrx_valueStart[i];
                end = jsonrx_valueEnd[i];
                if ((char) med.rdata[st] == '"') {
                    st += 1;
                    end -= 1;
                    type = 0;
                } else {
                    type = 1;
                }
                strValue = "";
                for (j = st; j <= end; j++) {
                    strValue += (char) med.rdata[j];
                    if (type == 1) {
                        if ((char) med.rdata[j] == '.') {
                            type = 2;
                        }
                    }
                }
                if (med.rxin_type == 'A') {
                    anaCmdKeyValue(key, strValue, type);
                }
                if (med.rxin_type == 'B') {
                    anaUartKeyValue(key, strValue, type);
                }

            }

            if (viewList_f == 1) {
                if (GB.viewPack.contains("A")) {
                    if (med.rxin_type == 'A') {
                        this.insertDocument(texts2String(med.rdata, med.rxin_byte), Color.blue);
                    }
                }
                if (GB.viewPack.contains("B")) {
                    if (med.rxin_type == 'B') {
                        this.insertDocument(texts2String(med.rdata, med.rxin_byte), Color.black);
                    }
                }
            }
        }

    }

    void anaCmdKeyValue(String key, String value, int type) {
        String hd = "";
        if (key.charAt(6) == '!') {
            hd += key.charAt(1);
            hd += key.charAt(2);
            hd += key.charAt(3);
            hd += key.charAt(4);
            hd += key.charAt(5);
            chkCmdKeyResp(Integer.parseInt(hd), (byte) key.charAt(7), value, type);
        }

    }

    void anaUartKeyValue(String key, String value, int type) {
        String hd = "";
        if (key.charAt(6) == '!') {
            hd += key.charAt(1);
            hd += key.charAt(2);
            hd += key.charAt(3);
            hd += key.charAt(4);
            hd += key.charAt(5);
            chkUartKeyResp(Integer.parseInt(hd), (byte) key.charAt(7), value, type);
        }

    }

    //===================================================
    int addCmdKey(String keyName, String keyValue, char act) {
        int i;
        String str = "";
        if (cmdKeyLen >= 32) {
            return 0;
        }
        str += act;
        str += cmdKeyHandle + "~";
        cmdKeyNameA[cmdKeyLen] = str + keyName;
        cmdKeyValueA[cmdKeyLen] = keyValue;
        cmdKeyLen++;
        return 1;
    }

    int addCmdKeyValue(String keyName, int keyValue, char act) {
        return addCmdKey(keyName, "" + keyValue, act);
    }

    int addCmdKeyString(String keyName, String keyValue, char act) {
        return addCmdKey(keyName, "\"" + keyValue + "\"", act);
    }

    int addUartKey(String keyName, String keyValue, char act) {
        int i;
        String str = "";
        if (uartKeyLen >= 32) {
            return 0;
        }
        str += act;
        str += uartKeyHandle + "~";
        uartKeyNameA[uartKeyLen] = str + keyName;
        uartKeyValueA[uartKeyLen] = keyValue;
        uartKeyLen++;
        return 1;
    }

    int addUartKeyValue(String keyName, int keyValue, char act) {
        return addUartKey(keyName, "" + keyValue, act);
    }

    int addUartKeyValue(String keyName, String keyValue, char act) {
        return addUartKey(keyName, keyValue, act);
    }

    int addUartKeyString(String keyName, String keyValue, char act) {
        return addUartKey(keyName, "\"" + keyValue + "\"", act);
    }
    //===================================================

    void incCmdKeyHandle() {
        if (++cmdKeyHandle > 99999) {
            cmdKeyHandle = 10000;
        }
    }

    void incUartKeyHandle() {
        if (++uartKeyHandle > 99999) {
            uartKeyHandle = 10000;
        }
    }

    void addCmdKeyRsp(int handle, int rspType, int waitTime, String action) {
        synchronized (cmdRspdList) {
            if (cmdRspdList.size() >= 32) {
                cmdRspdList.clear();
            }
            cmdRspdList.add(new RespData(handle, rspType, waitTime, action));
        }
    }

    void addUartKeyRsp(int handle, int rspType, int waitTime, String action) {
        synchronized (uartRspdList) {
            if (uartRspdList.size() >= 32) {
                uartRspdList.clear();
            }
            uartRspdList.add(new RespData(handle, rspType, waitTime, action));
        }
    }

    void chkCmdKeyRspTout() {
        synchronized (cmdRspdList) {
            int len = cmdRspdList.size();
            for (int i = 0; i < len; i++) {
                RespData rspd = cmdRspdList.get(i);
                rspd.time++;
                if (rspd.time >= rspd.waitTime) {
                    if (rspd.type == 1 || rspd.type == 2) {
                        statusColor = Color.red;
                        statusStr = rspd.action + " Time Out !!! ";
                        cmdRspdList.remove(i);
                        showStatusTime = 50;
                        return;
                    } else {
                        showMesbox_f = 1;
                        showMesbox_text = rspd.action + " Time Out !!! ";
                        showMesbox_type = "error";
                        cmdRspdList.remove(i);
                        return;
                    }
                }
            }
        }
    }

    void chkUartKeyRspTout() {
        synchronized (uartRspdList) {
            int len = uartRspdList.size();
            for (int i = 0; i < len; i++) {
                RespData rspd = uartRspdList.get(i);
                rspd.time++;
                if (rspd.time >= rspd.waitTime) {
                    if (rspd.type == 1 || rspd.type == 2) {
                        statusColor = Color.red;
                        statusStr = rspd.action + " Time Out !!! ";
                        uartRspdList.remove(i);
                        showStatusTime = 50;
                        return;
                    } else {
                        showMesbox_f = 1;
                        showMesbox_text = rspd.action + " Time Out !!! ";
                        showMesbox_type = "error";
                        uartRspdList.remove(i);
                        return;
                    }
                }
            }
        }
    }

    //rType=='0' ok
    //rType=='1' error
    void chkCmdKeyResp(int hd, byte rType, String value, int vType) {
        synchronized (cmdRspdList) {
            int len = cmdRspdList.size();
            int ibuf;
            for (int i = 0; i < len; i++) {
                RespData rspd = cmdRspdList.get(i);
                if (rspd.handle == hd) {
                    ibuf = rspd.type;
                    if (rType == (byte) '1') {//error
                        ibuf += 10;
                    }

                    switch (rspd.action) {
                        case "Open Socket Com:":
                            if (ibuf >= 10) {
                                sockComSeted_f = 0;
                            } else {
                                sockComSeted_f = 1;
                            }
                            break;
                        case "Close Socket Com:":
                            if (ibuf < 10) {
                                sockComSeted_f = 0;
                            }
                            break;

                    }

                    switch (ibuf) {
                        case 0:     //none
                            break;
                        case 1:     //mes error
                            break;
                        case 2:     //mes ok,mes error
                        case 4:     //mes ok,box err
                            statusColor = Color.green;
                            statusStr = rspd.action + " ok";
                            showStatusTime = 50;
                            break;
                        case 3:     //box err,
                            break;
                        case 5:     //box ok,box err
                            showMesbox_type = "ok";
                            showMesbox_text = rspd.action + " ok";

                            showMesbox_f = 1;
                            break;
                        //=============================    
                        case 11:     //mes error
                        case 12:     //mes ok,mes error
                            statusColor = Color.red;
                            statusStr = rspd.action + " " + value;
                            showStatusTime = 50;
                            break;
                        case 13:     //box err,
                        case 14:     //mes ok,box err
                        case 15:     //box ok,box err
                            showMesbox_type = "error";
                            showMesbox_text = rspd.action + " error !!!";

                            showMesbox_f = 1;
                            break;

                    }
                    cmdRspdList.remove(i);
                    return;
                }
            }
        }
    }

    //rType=='0' ok
    //rType=='1' error
    void chkUartKeyResp(int hd, byte rType, String value, int vType) {
        String[] strA;
        synchronized (uartRspdList) {
            int len = uartRspdList.size();
            int ibuf;
            for (int i = 0; i < len; i++) {
                RespData rspd = uartRspdList.get(i);
                if (rspd.handle == hd) {
                    ibuf = rspd.type;
                    if (rType == (byte) '1') {//error
                        ibuf += 10;
                    }

                    switch (rspd.action) {
                        case "Open Socket Com:":
                            break;
                        case "Close Socket Com:":
                            break;
                        case "getKvTable:":
                            if (vType == 3) {
                                popWinTd_act = "getKvTable";
                                popWinTd_strPara[1] = value;
                            }
                            break;
                        case "getKvData:":
                            if (vType == 3) {
                                popWinTd_act = "getKvData";
                                popWinTd_strPara[3] = value;
                            }
                            break;
                        case "getKvTableCount:":
                            if (vType == 3) {
                                popWinTd_act = "getKvTableCount";
                                popWinTd_strPara[3] = value;
                            }
                            break;

                        case "getJsobj:":
                            if (vType == 3) {
                                popWinTd_act = "getJsobj";
                                popWinTd_strPara[0] = value;
                            }
                            break;
                        case "Get Value:":
                            if (uartRspActStr.equals("getJsobjValue")) {
                                if (popWinTd_mod.equals("getJsobj")) {
                                    break;
                                }
                                if (popWinTd_mod.equals("setJsobj")) {
                                    break;
                                }
                            }
                            break;
                        case "getJstblData:":
                            if (vType == 3) {
                                if (uartRspActStr.equals("getSonobjCount")) {
                                    popWinTd_act = "getSonobjCount";
                                    popWinTd_strPara[3] = value;
                                    break;
                                }
                                if (uartRspActStr.equals("getSonobj")) {

                                    strA = value.split(",");
                                    if (strA.length < 3) {
                                        break;
                                    }
                                    if (Lib.str2Number(strA[1]) == 0) {
                                        break;
                                    }
                                    int ist = strA[0].length() + strA[1].length() + 2;
                                    String nowValue = Lib.str2edit(value.substring(ist));
                                    popWinTd_strPara[0] = nowValue;
                                    strA = popWinTd_strPara[1].split("~");
                                    if (strA.length < 2) {
                                        return;
                                    }
                                    incUartKeyHandle();
                                    addUartKeyString("getJsobj", nowValue + "~" + strA[strA.length - 1], 'a');
                                    addUartKeyRsp(uartKeyHandle, 3, 50, "getJsobj:");
                                    break;
                                }
                                if (popWinTd_mod.equals("getJsobj")) {
                                    popWinTd_act = "getJsobjData";
                                }
                                if (popWinTd_mod.equals("setJsobj")) {
                                    popWinTd_act = "setJsobjData";
                                }
                                popWinTd_strPara[3] = value;
                            }
                            break;

                    }
                    uartRspActStr = "";
                    switch (ibuf) {
                        case 0:     //none
                            break;
                        case 1:     //mes error
                            break;
                        case 2:     //mes ok,mes error
                        case 4:     //mes ok,box err
                            statusColor = Color.green;
                            statusStr = rspd.action + " ok";
                            showStatusTime = 50;
                            break;
                        case 3:     //box err,
                            break;
                        case 5:     //box ok,box err
                            showMesbox_type = "ok";
                            showMesbox_text = rspd.action + " ok";

                            showMesbox_f = 1;
                            break;
                        //=============================    
                        case 11:     //mes error
                        case 12:     //mes ok,mes error
                            statusColor = Color.red;
                            statusStr = rspd.action + " " + value;
                            showStatusTime = 50;
                            break;
                        case 13:     //box err,
                        case 14:     //mes ok,box err
                        case 15:     //box ok,box err
                            showMesbox_type = "error";
                            showMesbox_text = rspd.action + " error !!!";

                            showMesbox_f = 1;
                            break;

                    }
                    uartRspdList.remove(i);
                    return;
                }
            }
        }
    }

    void windowShow() {
        UartTest cla = this;
        Rectangle r = new Rectangle();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (cla.firstShow_f == 0) {
            ///Rectangle rn = cla.getBounds();
            //cla.winH = rn.height;
            //cla.winW = rn.width;
            //======================================================

            if (cla.fullScr_f == 1) {
                r.width = screenSize.width - GB.winDialog_wm1;;
                r.height = screenSize.height - GB.winDialog_hm1;
                r.x = GB.winDialog_xm1;;
                r.y = GB.winDialog_ym1;;;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width, r.height);
            } else {
                r.width = cla.winW;
                r.height = cla.winH;
                r.x = (screenSize.width - r.width) / 2;
                r.y = (screenSize.height - r.height - GB.winDialog_th3) / 2;
                cla.setBounds(r);
                cla.cp.setBounds(0, 0, r.width - GB.winDialog_wm3, r.height - GB.winDialog_hm3);
            }
        }
        cla.firstShow_f = 1;

        //=================================
        cla.pnMain.setLayout(null);
        cla.pnBar.setLayout(null);
        cla.pnLeft.setLayout(null);
        cla.pnRight.setLayout(null);
        cla.pnRightL.setLayout(null);
        cla.pnRightR.setLayout(null);
        cla.pnTx.setLayout(null);
        //==
        MyLayout.ctrA[0] = cla.pnMain;
        MyLayout.rateH = 0.9;
        MyLayout.gridLy();
        MyLayout.yst = MyLayout.yend;
        MyLayout.ctrA[0] = cla.pnBar;
        MyLayout.rateH = 1;
        MyLayout.gridLy();
        //==
        MyLayout.ctrA[0] = cla.pnLeft;
        MyLayout.rateW = 0.3;
        MyLayout.gridLy();
        MyLayout.xst = MyLayout.xend;
        MyLayout.ctrA[0] = cla.pnRight;
        MyLayout.rateH = 1;
        MyLayout.gridLy();
        //==
        MyLayout.ctrA[0] = cla.pnRightL;
        MyLayout.rateW = 0.85;
        MyLayout.gridLy();
        MyLayout.xst = MyLayout.xend;
        MyLayout.ctrA[0] = cla.pnRightR;
        MyLayout.gridLy();
        //==
        MyLayout.ctrA[0] = cla.sp1;
        MyLayout.rateH = -60;
        MyLayout.gridLy();
        MyLayout.yst = MyLayout.yend;
        MyLayout.ctrA[0] = cla.lbStatus;
        MyLayout.rateH = -36;
        MyLayout.gridLy();
        MyLayout.yst = MyLayout.yend;
        MyLayout.rateH = 1;
        MyLayout.ctrA[0] = cla.pnTx;
        MyLayout.gridLy();
        //==

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
        //==
        for (int i = 0; i < cla.bta2.length; i++) {
            MyLayout.ctrA[i] = cla.bta2[i];
        }
        MyLayout.eleAmt = cla.bta2.length;
        MyLayout.xc = 2;
        MyLayout.yc = MyLayout.eleAmt / MyLayout.xc;
        MyLayout.ofh[3] = 16;

        if ((MyLayout.eleAmt % MyLayout.xc) != 0) {
            MyLayout.yc++;
        }
        MyLayout.gridLy();
        //==
        for (int i = 0; i < cla.bta3.length; i++) {
            MyLayout.ctrA[i] = cla.bta3[i];
        }
        MyLayout.eleAmt = cla.bta3.length;
        MyLayout.xc = 1;
        MyLayout.ofh[2] = 10;
        MyLayout.ofh[5] = 10;
        MyLayout.ofh[13] = 10;
        MyLayout.yc = MyLayout.eleAmt / MyLayout.xc;
        if ((MyLayout.eleAmt % MyLayout.xc) != 0) {
            MyLayout.yc++;
        }
        MyLayout.gridLy();
        //==

        MyLayout.ctrA[0] = cla.tf1;
        MyLayout.rateW = 0.8;
        MyLayout.tm = 0;
        MyLayout.bm = 0;
        MyLayout.gridLy();

        MyLayout.xst = MyLayout.xend;
        for (int i = 0; i < cla.bta4.length; i++) {
            MyLayout.ctrA[i] = cla.bta4[i];
        }
        MyLayout.eleAmt = cla.bta4.length;
        MyLayout.xc = MyLayout.eleAmt;
        MyLayout.tm = 0;
        MyLayout.bm = 0;
        MyLayout.gridLy();
        //==

    }

    String getDscInf(String[] dscA, String name) {
        String[] strB;
        String str = "";
        for (int i = 0; i < dscA.length; i++) {
            strB = dscA[i].split("~");
            if (strB[4].equals(name)) {
                str += strB[0];
                str += "~" + strB[1];
                str += "~" + strB[2];
                str += "~" + strB[3];
                return str;
            }
        }
        return null;
    }
}

class UartTestWinLis extends WindowAdapter {

    UartTest cla;

    UartTestWinLis(UartTest owner) {
        cla = owner;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        UartTest.ret_f = 0;
        //cla.windowShow();
    }
}
//%1

class UartTestMsLis extends MouseAdapter {

    int enkey_f;
    UartTest cla;

    UartTestMsLis(UartTest owner) {
        cla = owner;
    }

    public void actSetPanel0(int index) {
        String str;
        Select sel1;
        Numpad kpad;
        SetPanel spn1;
        String[] strA, strB;
        String[] dscA = null;
        String objName = "";
        switch (index) {
            case 1 * 256 + 0:
            case 1 * 256 + 1:
            case 1 * 256 + 2:
            case 1 * 256 + 3:
            case 1 * 256 + 4:
            case 1 * 256 + 5:
            case 1 * 256 + 6:
            case 1 * 256 + 7:
                cla.sepPanelCount = index % 256;
                for (int i = 8; i < 40; i++) {
                    cla.bta2[i].setText("");
                }
                break;
            case 1 * 256 + 8:
                cla.incCmdKeyHandle();
                cla.addCmdKeyValue("testSuckCmdResp", 0, 'a');
                cla.addCmdKeyRsp(cla.cmdKeyHandle, 2, 50, "Open Socket Com:");
                break;
            case 1 * 256 + 9:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("testUartCmdResp", 0, 'a');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Test Uart Respond:");
                break;
            case 1 * 256 + 10:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("printEobj", 0, 'a');
                break;
            case 1 * 256 + 11:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("printGobj", 0, 'a');
                break;

            case 1 * 256 + 12:
            case 1 * 256 + 13:
                if ((index & 255) == 12) {
                    dscA = cla.eobj_dscA;
                    objName = "eobj";
                }
                if ((index & 255) == 13) {
                    dscA = cla.gobj_dscA;
                    objName = "gobj";
                }
                if (cla.comSeted_f == 0) {
                    break;
                }
                sel1 = new Select(null, true);
                sel1.title_str = "Setect Register Name";
                sel1.count = dscA.length;
                sel1.xc = 2;
                sel1.yc = 9;
                sel1.winW = 1000;
                for (int i = 0; i < dscA.length; i++) {
                    strA = dscA[i].split("~");
                    if (strA.length != 6) {
                        continue;
                    }
                    sel1.selstr.add(strA[4]);
                }
                sel1.create();
                for (;;) {
                    sel1.setVisible(true);

                    int noType;
                    String regType = "";
                    String regName = "";
                    String regValue;
                    int array_cnt = 0;
                    if (Select.ret_f != 1) {
                        break;
                    }
                    if (Select.ret_f == 1) {
                        for (int i = 0; i < dscA.length; i++) {
                            strA = dscA[i].split("~");
                            if (strA.length != 6) {
                                continue;
                            }
                            if (Select.retStr.equals(strA[4])) {

                                spn1 = new SetPanel(null, true);
                                spn1.winW = 1000;
                                spn1.title_str = strA[4];
                                regName = strA[4];
                                str = "name:Type;edit:0;";
                                noType = Integer.parseInt(strA[0]);
                                regType = cla.dscTypeA[noType];
                                array_cnt = Integer.parseInt(strA[2].trim());
                                if (array_cnt > 0) {
                                    spn1.list.add(new MyData(str, regType + " Array"));
                                } else {
                                    spn1.list.add(new MyData(str, regType));
                                }
                                if (strA[5].charAt(0) == '"' || strA[5].charAt(0) == '\'' || strA[5].charAt(0) == '[') {
                                    strA[5] = strA[5].substring(1, strA[5].length() - 1);
                                }
                                str = "name:Init;edit:0;";
                                spn1.list.add(new MyData(str, strA[5]));
                                str = "name:Value;edit:1;";
                                spn1.list.add(new MyData(str, strA[5]));
                                spn1.nameWideRate = 0.2;
                                spn1.create();
                                spn1.setVisible(true);
                                if (SetPanel.ret_f == 1) {
                                    cla.incUartKeyHandle();
                                    regValue = spn1.tfa1[4 * 2 + 1].getText();
                                    if (noType <= 1) {
                                        cla.addUartKeyString(objName + "." + regName, regValue, 'w');
                                        cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Set Value:");
                                    } else {
                                        if (array_cnt > 0) {
                                            regValue = "[" + regValue + "]";
                                        }
                                        cla.addUartKeyValue(objName + "." + regName, regValue, 'w');
                                        cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Set Value:");
                                    }
                                }
                                break;
                            }

                        }

                    }
                }
                break;

            case 1 * 256 + 14:
            case 1 * 256 + 15:
                if ((index & 255) == 14) {
                    dscA = cla.eobj_dscA;
                    objName = "eobj";
                }
                if ((index & 255) == 15) {
                    dscA = cla.gobj_dscA;
                    objName = "gobj";
                }
                if (cla.comSeted_f == 0) {
                    break;
                }
                sel1 = new Select(null, true);
                sel1.title_str = "Setect Register Name";
                sel1.count = dscA.length;
                sel1.xc = 2;
                sel1.yc = 9;
                sel1.winW = 1000;
                for (int i = 0; i < dscA.length; i++) {
                    strA = dscA[i].split("~");
                    if (strA.length != 6) {
                        continue;
                    }
                    sel1.selstr.add(strA[4]);
                    
                }
                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 1) {
                    cla.incUartKeyHandle();
                    cla.addUartKeyValue(objName + "." + Select.retStr, 0, 'r');
                    cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Get Value:");
                }
                break;
            case 1 * 256 + 16:
                if (cla.comSeted_f == 0) {
                    break;
                }
                cla.incUartKeyHandle();
                cla.addUartKeyValue("mcuRestart", 0, 'a');
                break;

            case 1 * 256 + 17:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("gobj" + "." + "printOutMode", "2", 'w');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Set Value:");
                break;
            case 1 * 256 + 18:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("getWifiScanData", 0, 'a');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getWifiScanData:");
                break;
            case 1 * 256 + 19:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("gobj" + "." + "printOutMode", "0", 'w');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Set Value:");
                break;

            case 1 * 256 + 20:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("saveAllEeprom", 0, 'a');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "saveAllEeprom:");
                break;
            case 1 * 256 + 21:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("getWifiScanData", 0, 'a');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Get Wifi Scan Data:");
                break;

        }

    }

    public void actSetPanel1(int index) {
        String str;
        String[] strA, strB;
        String[] dscA = null;
        String objName = "";
        Select sel1;
        SetPanel spn1;
        switch (index) {
            case 1 * 256 + 8:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("mqttStart", 0, 'a');
                break;
            case 1 * 256 + 9:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("testUartCmdResp", 0, 'a');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Test Uart Respond:");
                break;
            case 1 * 256 + 10:
                cla.incUartKeyHandle();
                str = "testValue " + cla.txMqttTest_cnt++;
                cla.addUartKeyString("txMqtt~js2320-1b.testKey", str, 'a');
                break;
            case 1 * 256 + 11:
                cla.incUartKeyHandle();
                cla.addUartKeyValue("mqttStop", 0, 'a');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "mqttStop:");
                break;
            case 1 * 256 + 12:
            case 1 * 256 + 14:
                if ((index & 255) == 12) {
                    dscA = cla.f24obj_dscA;
                    objName = "f24obj";
                }
                if ((index & 255) == 14) {
                    dscA = cla.r24obj_dscA;
                    objName = "r24obj";
                }
                sel1 = new Select(null, true);
                sel1.title_str = "Setect Register Name";
                sel1.count = dscA.length;
                sel1.xc = 2;
                sel1.yc = 9;
                sel1.winW = 1000;
                for (int i = 0; i < dscA.length; i++) {
                    strA = dscA[i].split("~");
                    if (strA.length < 6) {
                        continue;
                    }
                    sel1.selstr.add(strA[4]);

                }
                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 1) {
                    cla.incUartKeyHandle();
                    cla.addUartKeyValue(objName + "." + cla.getDscInf(dscA, Select.retStr), 0, 'R');
                    cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Get Value:");
                }
                break;
            case 1 * 256 + 16:
                cla.incUartKeyHandle();
                cla.addUartKeyString("getJsobj", "000000000000000~0", 'a');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getJsobj:");
                cla.popWinTd_mod = "getJsobj";
                break;
            case 1 * 256 + 17:
                cla.incUartKeyHandle();
                cla.addUartKeyString("getJsobj", "000000000000000~0", 'a');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getJsobj:");
                cla.popWinTd_mod = "setJsobj";
                break;

            case 1 * 256 + 18:
                str = "this~0";
                cla.incUartKeyHandle();
                cla.addUartKeyString("getKvTable", str, 'a');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getKvTable:");
                cla.popWinTd_mod = "getKvData";
                cla.popWinTd_strPara[0] = str;
                break;
            case 1 * 256 + 19:
                str = "this~0";
                cla.incUartKeyHandle();
                cla.addUartKeyString("getKvTable", str, 'a');
                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getKvTable:");
                cla.popWinTd_mod = "setKvData";
                cla.popWinTd_strPara[0] = str;
                break;

            case 1 * 256 + 13:
            case 1 * 256 + 15:
                if ((index & 255) == 13) {
                    dscA = cla.f24obj_dscA;
                    objName = "f24obj";
                }
                if ((index & 255) == 15) {
                    dscA = cla.r24obj_dscA;
                    objName = "r24obj";
                }
                if (cla.comSeted_f == 0) {
                    break;
                }
                sel1 = new Select(null, true);
                sel1.title_str = "Setect Register Name";
                sel1.count = dscA.length;
                sel1.xc = 2;
                sel1.yc = 9;
                sel1.winW = 1000;
                for (int i = 0; i < dscA.length; i++) {
                    strA = dscA[i].split("~");
                    if (strA.length < 6) {
                        continue;
                    }
                    sel1.selstr.add(strA[4]);
                }
                sel1.create();
                for (;;) {
                    sel1.setVisible(true);

                    int noType;
                    String regType = "";
                    String regName = "";
                    String regValue;
                    int array_cnt = 0;
                    if (Select.ret_f != 1) {
                        break;
                    }
                    if (Select.ret_f == 1) {
                        for (int i = 0; i < dscA.length; i++) {
                            strA = dscA[i].split("~");
                            if (strA.length < 6) {
                                continue;
                            }
                            if (Select.retStr.equals(strA[4])) {
                                spn1 = new SetPanel(null, true);
                                spn1.winW = 1000;
                                spn1.title_str = strA[4];
                                regName = strA[4];
                                str = "name:Type;edit:0;";
                                noType = Integer.parseInt(strA[0]);
                                regType = cla.dscTypeA[noType];
                                array_cnt = Integer.parseInt(strA[2].trim());
                                if (array_cnt > 0) {
                                    spn1.list.add(new MyData(str, regType + " Array"));
                                } else {
                                    spn1.list.add(new MyData(str, regType));
                                }
                                if (strA[5].charAt(0) == '"' || strA[5].charAt(0) == '\'' || strA[5].charAt(0) == '[') {
                                    strA[5] = strA[5].substring(1, strA[5].length() - 1);
                                }
                                str = "name:Init;edit:0;";
                                spn1.list.add(new MyData(str, strA[5]));
                                str = "name:Value;edit:1;";
                                spn1.list.add(new MyData(str, strA[5]));
                                spn1.nameWideRate = 0.2;
                                spn1.create();
                                spn1.setVisible(true);
                                String outStr = "";
                                if (SetPanel.ret_f == 1) {
                                    cla.incUartKeyHandle();
                                    regValue = spn1.tfa1[4 * 2 + 1].getText();
                                    outStr = Lib.trsKvData(regValue, dscA[i]);
                                    if (Lib.error_f == 1) {
                                        Message mes1 = new Message(null, true);
                                        mes1.keyType_i = 0;
                                        mes1.mesType_i = 1;
                                        mes1.title_str = "Data Input Error !!!";
                                        mes1.create();
                                        mes1.setVisible(true);
                                        break;
                                    }
                                    cla.incUartKeyHandle();
                                    cla.addUartKeyValue(objName + "." + cla.getDscInf(dscA, Select.retStr), outStr, 'W');
                                    cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Set Value:");

                                }

                                break;
                            }
                        }

                    }
                }
                break;

        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        String str;
        Select sel1;
        Numpad kpad;
        SetPanel spn1;
        String[] strA, strB;
        String[] dscA = null;
        String objName = "";
        int index;
        if (enkey_f != 1) {
            return;
        }
        index = Integer.parseInt(e.getComponent().getName());

        if (cla.sepPanelCount == 0) {
            actSetPanel0(index);
        } else if (cla.sepPanelCount == 1) {
            actSetPanel1(index);
        } else {
        }

        switch (index) {
            case 0 * 256 + 15:
                cla.dispose();
                UartTest.ret_f = 0;
                break;
            case 0 * 256 + 14:
                cla.statusStr = "Time: ";
                Date dt = new Date();
                dt.getTime();
                cla.statusStr += dt.getTime();
                break;

            case 1 * 256 + 0:
            case 1 * 256 + 1:
            case 1 * 256 + 2:
            case 1 * 256 + 3:
            case 1 * 256 + 4:
            case 1 * 256 + 5:
            case 1 * 256 + 6:
            case 1 * 256 + 7:
                cla.sepPanelCount = index % 256;
                for (int i = 8; i < 40; i++) {
                    cla.bta2[i].setText("");
                }
                break;
            case 2 * 256 + 0:
                spn1 = new SetPanel(null, true);
                spn1.winW = 600;
                spn1.title_str = "Set Real Com Port";
                str = "name:COM Port;edit:0;";
                str += "enu:COM1~COM2~COM3~COM4~COM5~COM6~COM7~COM8~COM9~COM10~COM11~COM12~COM13~COM14~COM15~COM16~COM17~COM18~COM19~COM20";
                spn1.list.add(new MyData(str, GB.comPort));
                str = "name:Boudrate;";
                str += "enu:2400~4800~9600~19200~38400~57600~115200~250000~1000000";
                spn1.list.add(new MyData(str, GB.comBoudrate));
                str = "name:Parity;edit:0;";
                str += "enu:None~Even~Odd";
                spn1.list.add(new MyData(str, GB.comParity));
                str = "name:Encorder;edit:0;";
                str += "enu:Byte~Text~KVENC";
                spn1.list.add(new MyData(str, GB.comEncorder));
                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 1) {
                    GB.comPort = spn1.tfa1[4 * 0 + 1].getText();
                    GB.comBoudrate = spn1.tfa1[4 * 1 + 1].getText();
                    GB.comParity = spn1.tfa1[4 * 2 + 1].getText();
                    GB.comEncorder = spn1.tfa1[4 * 3 + 1].getText();
                    Base3.editNewDb("comPort", GB.comPort);
                    Base3.editNewDb("comBoudrate", GB.comBoudrate);
                    Base3.editNewDb("comParity", GB.comParity);
                    Base3.editNewDb("comEncorder", GB.comEncorder);
                }
                break;
            case 2 * 256 + 1:

                if (!cla.openUart(GB.comPort, GB.comParity, Integer.parseInt(GB.comBoudrate))) {
                    Message mes1 = new Message(null, true);
                    mes1.keyType_i = 0;
                    mes1.mesType_i = 1;
                    mes1.title_str = "Open Uart Error !!!";
                    mes1.create();
                    mes1.setVisible(true);
                }
                break;
            case 2 * 256 + 2:
                cla.closeUart();
                //cla.listComPort();
                break;

            case 2 * 256 + 3:
                //SetPanel spn1;
                spn1 = new SetPanel(null, true);
                spn1.winW = 600;
                spn1.title_str = "Set Cocket Com";
                str = "Socket IP";
                spn1.list.add(new MyData(str, GB.sockIp));
                str = "Socket Port";
                spn1.list.add(new MyData(str, GB.sockPort));
                str = "name:COM Port;edit:0;";
                str += "enu:COM1~COM2~COM3~COM4~COM5~COM6~COM7~COM8~COM9~COM10~COM11~COM12~COM13~COM14~COM15~COM16~COM17~COM18~COM19~COM20";
                spn1.list.add(new MyData(str, GB.sockComPort));
                str = "name:Boudrate;";
                str += "enu:2400~4800~9600~19200~38400~57600~115200~250000~1000000";
                spn1.list.add(new MyData(str, GB.sockComBoudrate));
                str = "name:Parity;edit:0;";
                str += "enu:None~Even~Odd";
                spn1.list.add(new MyData(str, GB.sockComParity));
                str = "name:Encorder;edit:0;";
                str += "enu:Byte~Text~KVENC";
                spn1.list.add(new MyData(str, GB.sockComEncorder));
                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 1) {
                    GB.sockIp = spn1.tfa1[4 * 0 + 1].getText();
                    GB.sockPort = Lib.str2int(spn1.tfa1[4 * 1 + 1].getText(), 2468);
                    GB.sockComPort = spn1.tfa1[4 * 2 + 1].getText();
                    GB.sockComBoudrate = spn1.tfa1[4 * 3 + 1].getText();
                    GB.sockComParity = spn1.tfa1[4 * 4 + 1].getText();
                    GB.sockComEncorder = spn1.tfa1[4 * 5 + 1].getText();
                    Base3.editNewDb("sockIp", GB.sockIp);
                    Base3.editNewDb("sockPort", "" + GB.sockPort);
                    Base3.editNewDb("sockComPort", GB.sockComPort);
                    Base3.editNewDb("sockComBoudrate", GB.sockComBoudrate);
                    Base3.editNewDb("sockComParity", GB.sockComParity);
                    Base3.editNewDb("sockComEncorder", GB.sockComEncorder);
                }
                break;

            case 2 * 256 + 4:
                if (cla.sockComOn_f == 0) {
                    cla.sockComOn_f = 1;
                    cla.sockComSeted_f = 0;
                    cla.sockComConnected_f = 0;
                    cla.viewList_f = 1;
                    //========================================    
                    cla.incCmdKeyHandle();
                    cla.addCmdKeyString("sockComPort", GB.sockComPort, 'w');
                    cla.addCmdKeyString("sockComBoudrate", GB.sockComBoudrate, 'w');
                    cla.addCmdKeyString("sockComParity", GB.sockComParity, 'w');
                    cla.addCmdKeyValue("resetSockCom", 0, 'a');
                    cla.addCmdKeyRsp(cla.cmdKeyHandle, 2, 50, "Open Socket Com:");

                } else {
                    cla.sockComOn_f = 0;
                    cla.sockComSeted_f = 0;
                    cla.sockComConnected_f = 0;
                }
                break;
            case 2 * 256 + 5:
                if (cla.sockComSeted_f == 1) {
                    cla.incCmdKeyHandle();
                    cla.addCmdKeyValue("closeSockCom", 0, 'a');
                    cla.addCmdKeyRsp(cla.cmdKeyHandle, 2, 50, "Close Socket Com:");
                } else {
                    cla.incCmdKeyHandle();
                    cla.addCmdKeyString("sockComPort", GB.sockComPort, 'w');
                    cla.addCmdKeyString("sockComBoudrate", GB.sockComBoudrate, 'w');
                    cla.addCmdKeyString("sockComParity", GB.sockComParity, 'w');
                    cla.addCmdKeyValue("resetSockCom", 0, 'a');
                    cla.addCmdKeyRsp(cla.cmdKeyHandle, 2, 50, "Open Socket Com:");

                }
                break;
            case 2 * 256 + 6:
                //SetPanel spn1;
                spn1 = new SetPanel(null, true);
                spn1.winW = 600;
                spn1.title_str = "Set View Parameter";
                str = "Max Chars In A Line";
                spn1.list.add(new MyData(str, GB.viewMaxLineChars));
                str = "Max Lines";
                spn1.list.add(new MyData(str, GB.viewMaxLines));
                str = "name:View Type;edit:0;";
                str += "enu:Text~Hex~Dec";
                spn1.list.add(new MyData(str, GB.viewType));
                str = "name:View Pack";
                spn1.list.add(new MyData(str, GB.viewPack));

                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 1) {
                    GB.viewMaxLineChars = Lib.str2int(spn1.tfa1[4 * 0 + 1].getText(), 100, 1000, 10);
                    GB.viewMaxLines = Lib.str2int(spn1.tfa1[4 * 1 + 1].getText(), 200, 1000, 10);
                    GB.viewType = spn1.tfa1[4 * 2 + 1].getText();
                    GB.viewPack = spn1.tfa1[4 * 3 + 1].getText();
                    Base3.editNewDb("viewMaxLineChars", "" + GB.viewMaxLineChars);
                    Base3.editNewDb("viewMaxLines", "" + GB.viewMaxLines);
                    Base3.editNewDb("viewType", "" + GB.viewType);
                    Base3.editNewDb("viewPack", "" + GB.viewPack);
                }
                break;

            case 2 * 256 + 8:
                cla.viewList_f ^= 1;
                break;
            case 2 * 256 + 9:
                cla.viewTrace_f ^= 1;
                break;
            case 2 * 256 + 10:
                cla.tp1.setText("");
                cla.viewLineCount = 0;
                cla.viewLineChars = 0;
                break;
            case 2 * 256 + 11:
                JScrollBar vertical = cla.sp1.getVerticalScrollBar();
                vertical.setValue(0);
                break;
            case 2 * 256 + 12:
                vertical = cla.sp1.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
                break;

            case 2 * 256 + 15:
                str = "NioClent Send Block 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ\n";
                cla.csock1.sendDataBlock(str.getBytes(), str.length());
                break;
            case 2 * 256 + 16:
                try {
                    cla.client.hostAddress = "127.0.0.1";
                    cla.client.port = 1234;
                    cla.insertDocument("NioClient TX: ", Color.BLACK);
                    str = "NioClent Send Unbloak 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ\n";
                    cla.insertDocument(str, Color.red);
                    cla.client.send(str.getBytes(), cla.nioClientRxHandler);
                } catch (IOException ex) {
                    Logger.getLogger(UartTestMsLis.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 2 * 256 + 17:
                break;
            case 2 * 256 + 18:
                break;

            case 3 * 256 + 0:
                sel1 = new Select(null, true);
                sel1.title_str = "Select Tx Type";
                sel1.count = 4;
                sel1.xc = 1;
                sel1.winW = 300;
                    sel1.selstr.add("Text");
                    sel1.selstr.add("Message");
                    sel1.selstr.add("Pack A");
                    sel1.selstr.add("Pack B");
                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 1) {
                    cla.uartTxType = Select.retStr;
                }
                break;
            case 3 * 256 + 1:
                str = "";
                str = cla.tf1.getText();
                switch (cla.uartTxType) {
                    case "Text":
                        break;
                    case "Message":
                        str = str + "\n";
                        break;
                    case "Pack A":
                        break;
                    case "Pack B":
                        break;
                }
                if (str.length() > 0) {
                    cla.insertDocument(str, Color.red);
                    if (cla.comSeted_f == 1) {
                        cla.serialOut.send(str.getBytes(), 0, str.length());
                    }
                }
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

class UartTestTm2 implements ActionListener {

    UartTest cla;

    UartTestTm2(UartTest owner) {
        cla = owner;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (cla.showMesbox_f == 1) {
            cla.showMesbox_f = 0;
            Message mes1 = new Message(null, true);
            mes1.keyType_i = 0;
            if (cla.showMesbox_type.equals("ok")) {
                mes1.mesType_i = 0;
            } else {
                mes1.mesType_i = 2;
            }
            mes1.title_str = cla.showMesbox_text;
            mes1.create();
            mes1.setVisible(true);
        }

    }
}

class UartTestTm1 implements ActionListener {

    String str;
    UartTest cla;
    int testCnt = 0;
    int sockConnectTimer = 0;
    int uartConnectTimer = 0;

    UartTestTm1(UartTest owner) {
        cla = owner;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Color co;
        String str;

        cla.chkCmdKeyRspTout();
        cla.chkUartKeyRspTout();
        if (++testCnt > 100) {
            testCnt = 0;
            //Base3.log.debug(""+testCnt);
        }
        if (cla.showStatusTime > 0) {
            cla.showStatusTime--;
            if (cla.showStatusTime == 0) {
                cla.statusStr = "";
            }
        }
        if (cla.comSeted_f == 1) {
            if (++uartConnectTimer > 1) {
                uartConnectTimer = 0;
                if (cla.uartKeyLen > 0) {
                    cla.medUart.tdata_inx = 0;
                    cla.medUart.cmd2Json(cla.uartKeyNameA, cla.uartKeyValueA, cla.uartKeyLen, 'B');
                    if (cla.medUart.tdata_byte > 0) {
                        cla.serialOut.send(cla.medUart.tdata, 0, cla.medUart.tdata_byte);
                    }
                    cla.uartKeyLen = 0;
                }
            }
        }
        if (++sockConnectTimer > 1) {
            sockConnectTimer = 0;
            if (cla.client.ready_f == 1 && cla.sockComOn_f == 1) {
                cla.medClient.tdata_byte = 0;
                cla.medClient.tdata_inx = 0;
                cla.medClient.cmd2Json(cla.cmdKeyNameA, cla.cmdKeyValueA, cla.cmdKeyLen, 'A');
                cla.medClient.cmd2Json(cla.uartKeyNameA, cla.uartKeyValueA, cla.uartKeyLen, '0');
                cla.cmdKeyLen = 0;
                cla.uartKeyLen = 0;
                if (cla.medClient.tdata_byte > 0) {
                    byte[] sendBuf = new byte[cla.medClient.tdata_byte];
                    System.arraycopy(cla.medClient.tdata, 0, sendBuf, 0, cla.medClient.tdata_byte);
                    cla.client.hostAddress = GB.sockIp;
                    cla.client.port = GB.sockPort;
                    try {
                        cla.client.send(sendBuf, cla.nioClientRxHandler);
                    } catch (IOException ex) {
                        Logger.getLogger(UartTestTm1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        if (cla.onDrawChar_f == 0) {
            //cla.flushDrawBuffer();
        }
        str = Lib.textToHtml("Real Comport\n" + GB.comPort, "blue");
        if (!cla.bta3[0].getText().equals(str)) {
            cla.bta3[0].setText(str);
        }
        co = Color.GRAY;
        if (cla.comSeted_f == 1) {
            co = Color.yellow;
            if (cla.comConnected_f == 1) {
                co = Color.green;
            }
        }
        if (!cla.bta3[0].getBackground().equals(co)) {
            cla.bta3[0].setBackground(co);
        }
        str = "Open COM";
        if (!cla.bta3[1].getText().equals(str)) {
            cla.bta3[1].setText(str);
        }
        str = "Close COM";
        if (!cla.bta3[2].getText().equals(str)) {
            cla.bta3[2].setText(str);
        }
        //===================
        str = "SockUart";
        str = Lib.textToHtml("Socket Comport\n" + GB.sockComPort, "blue");
        if (!cla.bta3[3].getText().equals(str)) {
            cla.bta3[3].setText(str);
        }
        co = Color.GRAY;
        if (cla.sockComOn_f == 1) {
            co = Color.ORANGE;
            if (cla.sockComSeted_f == 1) {
                co = Color.YELLOW;
                if (cla.sockComConnected_f == 1) {
                    co = Color.green;
                }
            }
        }
        if (!cla.bta3[3].getBackground().equals(co)) {
            cla.bta3[3].setBackground(co);
        }

        if (cla.sockComOn_f == 1) {
            str = Lib.textToHtml("Socket\n" + "ON", "blue");
        } else {
            str = Lib.textToHtml("Socket\n" + "OFF", "blue");
        }
        if (!cla.bta3[4].getText().equals(str)) {
            cla.bta3[4].setText(str);
        }
        //=======================================================
        if (cla.sockComSeted_f == 1) {
            str = Lib.textToHtml("Socket Com\n" + "Open", "blue");
        } else {
            str = Lib.textToHtml("Socket Com\n" + "Close", "blue");
        }
        //========================================================
        if (!cla.bta3[5].getText().equals(str)) {
            cla.bta3[5].setText(str);
        }
        //=============================

        str = "View Set";
        if (!cla.bta3[6].getText().equals(str)) {
            cla.bta3[6].setText(str);
        }

        str = Lib.textToHtml("Total Line\n" + cla.viewLineCount, "blue");
        if (!cla.bta3[7].getText().equals(str)) {
            if (cla.viewLineCount > GB.viewMaxLines) {
                cla.viewLineCount = 0;
                cla.viewLineChars = 0;
                cla.tp1.setText("");
            }
            if (cla.viewTrace_f == 1) {
                JScrollBar vertical = cla.sp1.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            }
            cla.bta3[7].setText(str);
        }

        str = "Stop List";
        if (cla.viewList_f == 1) {
            str = "Start List";
        }
        if (!cla.bta3[8].getText().equals(str)) {
            cla.bta3[8].setText(str);
        }
        co = cla.coBt;
        if (cla.viewList_f == 1) {
            co = Color.green;
        }
        if (!cla.bta3[8].getBackground().equals(co)) {
            cla.bta3[8].setBackground(co);
        }

        str = "Trace";
        if (!cla.bta3[9].getText().equals(str)) {
            cla.bta3[9].setText(str);
        }
        co = cla.coBt;
        if (cla.viewTrace_f == 1) {
            co = Color.green;
        }
        if (!cla.bta3[9].getBackground().equals(co)) {
            cla.bta3[9].setBackground(co);
        }

        str = "Clear";
        if (!cla.bta3[10].getText().equals(str)) {
            cla.bta3[10].setText(str);
        }

        str = "Top";
        if (!cla.bta3[11].getText().equals(str)) {
            cla.bta3[11].setText(str);
        }
        str = "Bottom";
        if (!cla.bta3[12].getText().equals(str)) {
            cla.bta3[12].setText(str);
        }
        str = Lib.textToHtml("ErrPack Count\n" + cla.medClient.errPackCount, "blue");
        if (!cla.bta3[13].getText().equals(str)) {
            cla.bta3[13].setText(str);
        }

        str = "Server";
        if (!cla.bta3[14].getText().equals(str)) {
            cla.bta3[14].setText(str);
        }
        co = cla.coBt;
        if (cla.nioServerStart_f == 1) {
            co = Color.green;
        }
        if (!cla.bta3[14].getBackground().equals(co)) {
            cla.bta3[14].setBackground(co);
        }

        str = "Client Sent0";
        if (!cla.bta3[15].getText().equals(str)) {
            cla.bta3[15].setText(str);
        }

        str = "Client Sent1";
        if (!cla.bta3[16].getText().equals(str)) {
            cla.bta3[16].setText(str);
        }

        //===========================================================
        str = Lib.textToHtml("Type\n" + cla.uartTxType, "blue");
        if (!cla.bta4[0].getText().equals(str)) {
            cla.bta4[0].setText(str);
        }
        str = "TX";
        if (!cla.bta4[1].getText().equals(str)) {
            cla.bta4[1].setText(str);
        }

        str = cla.statusStr;
        if (!cla.lbStatus.getText().equals(str)) {
            cla.lbStatus.setText(str);
        }
        co = cla.statusColor;
        if (!cla.lbStatus.getForeground().equals(co)) {
            cla.lbStatus.setForeground(co);
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
        //================================================
        for (int i = 0; i < 8; i++) {
            if (cla.sepPanelCount == i) {
                co = Color.green;
            } else {
                co = Color.LIGHT_GRAY;
            }
            if (!cla.bta2[i].getBackground().equals(co)) {
                cla.bta2[i].setBackground(co);
            }
        }
        str = "ESP8266";
        if (!cla.bta2[0].getText().equals(str)) {
            cla.bta2[0].setText(str);
        }
        str = "js236-01b";
        if (!cla.bta2[1].getText().equals(str)) {
            cla.bta2[1].setText(str);
        }

        if (cla.sepPanelCount == 1) {
            str = "mqttStart";
            if (!cla.bta2[8].getText().equals(str)) {
                cla.bta2[8].setText(str);
            }
            str = "testUartcmdResp";
            if (!cla.bta2[9].getText().equals(str)) {
                cla.bta2[9].setText(str);
            }
            str = "txMqttTest";
            if (!cla.bta2[10].getText().equals(str)) {
                cla.bta2[10].setText(str);
            }
            str = "mqttStop";
            if (!cla.bta2[11].getText().equals(str)) {
                cla.bta2[11].setText(str);
            }
            str = "rF24obj";
            if (!cla.bta2[12].getText().equals(str)) {
                cla.bta2[12].setText(str);
            }
            str = "wF24obj";
            if (!cla.bta2[13].getText().equals(str)) {
                cla.bta2[13].setText(str);
            }
            str = "rR24obj";
            if (!cla.bta2[14].getText().equals(str)) {
                cla.bta2[14].setText(str);
            }
            str = "wR24obj";
            if (!cla.bta2[15].getText().equals(str)) {
                cla.bta2[15].setText(str);
            }
            str = "getJsobj";
            if (!cla.bta2[16].getText().equals(str)) {
                cla.bta2[16].setText(str);
            }
            str = "setJsobj";
            if (!cla.bta2[17].getText().equals(str)) {
                cla.bta2[17].setText(str);
            }
            str = "getKvData";
            if (!cla.bta2[18].getText().equals(str)) {
                cla.bta2[18].setText(str);
            }
            str = "setKvData";
            if (!cla.bta2[19].getText().equals(str)) {
                cla.bta2[19].setText(str);
            }

        }
        if (cla.sepPanelCount == 0) {
            str = "testSuckCmdResp";
            if (!cla.bta2[8].getText().equals(str)) {
                cla.bta2[8].setText(str);
            }
            str = "testUartCmdResp";
            if (!cla.bta2[9].getText().equals(str)) {
                cla.bta2[9].setText(str);
            }
            str = "printEobj";
            if (!cla.bta2[10].getText().equals(str)) {
                cla.bta2[10].setText(str);
            }
            str = "printGobj";
            if (!cla.bta2[11].getText().equals(str)) {
                cla.bta2[11].setText(str);
            }
            str = "setEobj";
            if (!cla.bta2[12].getText().equals(str)) {
                cla.bta2[12].setText(str);
            }
            str = "setGobj";
            if (!cla.bta2[13].getText().equals(str)) {
                cla.bta2[13].setText(str);
            }
            str = "getEobj";
            if (!cla.bta2[14].getText().equals(str)) {
                cla.bta2[14].setText(str);
            }
            str = "getGobj";
            if (!cla.bta2[15].getText().equals(str)) {
                cla.bta2[15].setText(str);
            }
            str = "mcuRestart";
            if (!cla.bta2[16].getText().equals(str)) {
                cla.bta2[16].setText(str);
            }

            str = "enableSysPrint";
            if (!cla.bta2[17].getText().equals(str)) {
                cla.bta2[17].setText(str);
            }
            str = "getWifiScanData";
            if (!cla.bta2[18].getText().equals(str)) {
                cla.bta2[18].setText(str);
            }
            str = "disableSysPrint";
            if (!cla.bta2[19].getText().equals(str)) {
                cla.bta2[19].setText(str);
            }
            str = "saveAllEeprom";
            if (!cla.bta2[20].getText().equals(str)) {
                cla.bta2[20].setText(str);
            }
            str = "getWifiScanData";
            if (!cla.bta2[21].getText().equals(str)) {
                cla.bta2[21].setText(str);
            }
        }

    }
}

class PopWinTd extends Thread {

    UartTest cla;

    PopWinTd(UartTest owner) {
        cla = owner;
    }

    @Override
    public void run() {
        String[] strA;
        String str = "";
        Select sel1;
        int ibuf;
        SetPanel spn1;
        String regName;
        int noType;
        String regType;
        int regLen;
        int array_cnt;
        for (;;) {
            if (cla.popWinTd_destroy_f == 1) {
                break;
            }
            switch (cla.popWinTd_act) {
                case "":
                    break;
                case "getKvTable":
                    Lib.jsobjToStringArray(cla.popWinTd_strPara[1]);
                    sel1 = new Select(null, true);
                    sel1.title_str = "Setect Register Name";
                    sel1.count = Lib.retsal.size() - 1;
                    sel1.xc = 2;
                    sel1.yc = 9;
                    sel1.winW = 1000;
                    for (int k = 0; k < sel1.count; k++) {
                        sel1.selstr.add(Lib.retsal.get(k + 1));
                    }
                    sel1.create();
                    sel1.setVisible(true);
                    if (Select.ret_f == 1) {
                        strA = Select.retStr.split("_");
                        if (strA.length >= 2 && strA[0].equals("KVD")) {
                            str = "AMT" + Select.retStr.substring(3);
                            cla.incUartKeyHandle();
                            cla.addUartKeyString("getKvData", cla.popWinTd_strPara[0] + "." + str, 'a');
                            cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getKvTableCount:");
                            cla.uartRspActStr = "getKvTableCount";
                            cla.popWinTd_strPara[2] = Select.retStr;
                            break;

                            //str = "AMT" + Select.retStr.substring(3);
                            //cla.incUartKeyHandle();
                            //cla.addUartKeyString("getJstblData", cla.popWinTd_strPara[0] + "." + str, 'a');
                            //cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getJstblData:");
                            //cla.uartRspActStr = "getJstblData";
                            //cla.popWinTd_strPara[1] = Select.retStr;
                            //cla.uartRspActStr = "getSonobjCount";
                            //break;
                        } else {
                            if (cla.popWinTd_mod.equals("getKvData")) {
                                cla.incUartKeyHandle();
                                cla.addUartKeyString("getKvData", cla.popWinTd_strPara[0] + "." + Select.retStr, 'a');
                                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getKvData:");
                                cla.uartRspActStr = "getKvData";
                                cla.popWinTd_strPara[2] = Select.retStr;
                                break;
                            }
                            if (cla.popWinTd_mod.equals("setKvData")) {
                                cla.incUartKeyHandle();
                                cla.addUartKeyString("getKvData", cla.popWinTd_strPara[0] + "." + Select.retStr, 'a');
                                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getKvData:");
                                cla.uartRspActStr = "getKvData";
                                cla.popWinTd_strPara[2] = Select.retStr;
                                break;
                            }
                        }
                    }
                    break;

                case "getKvData":
                    strA = cla.popWinTd_strPara[3].split(",");
                    if (strA.length < 3) {
                        break;
                    }
                    if (Lib.str2Number(strA[1]) == 0) {
                        break;
                    }
                    int ist = strA[0].length() + strA[1].length() + 2;
                    String nowValue = Lib.str2edit(cla.popWinTd_strPara[3].substring(ist));
                    String jid = Lib.str2edit(strA[0]);

                    spn1 = new SetPanel(null, true);
                    spn1.winW = 1000;
                    spn1.title_str = cla.popWinTd_strPara[2];
                    regName = cla.popWinTd_strPara[2];
                    str = "name:Type;edit:0;";
                    noType = (Lib.valueInt >> 24) & 255;
                    regType = cla.dscTypeA[noType];
                    regLen = (Lib.valueInt >> 16) & 255;
                    array_cnt = Lib.valueInt & 0xfff;
                    if (array_cnt > 0) {
                        spn1.list.add(new MyData(str, regType + " Array"));
                    } else {
                        spn1.list.add(new MyData(str, regType));
                    }
                    str = "name:Now Value;edit:0;";
                    spn1.list.add(new MyData(str, nowValue));

                    if (cla.popWinTd_mod.equals("getKvData")) {
                        spn1.nameWideRate = 0.2;
                        spn1.keyCnt = 1;
                        spn1.create();
                        spn1.setVisible(true);
                        break;
                    }
                    str = "name:Set;edit:1;";
                    spn1.list.add(new MyData(str, nowValue));
                    spn1.nameWideRate = 0.2;
                    spn1.create();
                    spn1.selectAll = 2;
                    spn1.setVisible(true);
                    String outStr = "";
                    String regValue = "";
                    if (SetPanel.ret_f == 1) {
                        regValue = spn1.tfa1[4 * 2 + 1].getText();
                        outStr = Lib.trsKvData(regValue, "" + noType + "~" + regLen + "~" + array_cnt);
                        if (Lib.error_f == 1) {
                            Message mes1 = new Message(null, true);
                            mes1.keyType_i = 0;
                            mes1.mesType_i = 1;
                            mes1.title_str = "Data Input Error !!!";
                            mes1.create();
                            mes1.setVisible(true);
                            break;
                        }
                        cla.incUartKeyHandle();
                        cla.addUartKeyValue("setJsobjValue~" + jid + "." + regName, outStr, 'a');
                        cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "setJsobjValue:");

                    }
                    break;

                case "getKvTableCount":
                    strA = cla.popWinTd_strPara[3].split(",");
                    if (strA.length < 3) {
                        break;
                    }

                    if (Lib.chkStrIsNumber(strA[2], 2)) {
                        sel1 = new Select(null, true);
                        sel1.title_str = "Setect Son Object";
                        sel1.count = Lib.valueInt;
                        sel1.xc = 2;
                        sel1.yc = 9;
                        sel1.winW = 1000;
                        for (int k = 0; k < sel1.count; k++) {
                            sel1.selstr.add(cla.popWinTd_strPara[2] + "~" + k);
                        }
                        sel1.create();
                        sel1.setVisible(true);
                        if (Select.ret_f == 1) {
                            str = cla.popWinTd_strPara[0] + "." + Select.retStr;
                            cla.incUartKeyHandle();
                            cla.addUartKeyString("getKvTable", str, 'a');
                            cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getKvTable:");
                            cla.popWinTd_strPara[0] = str;

                            break;

                        }
                    }

                    break;

                case "getJsobj":
                    Lib.jsobjToStringArray(cla.popWinTd_strPara[0]);
                    cla.popWinTd_strPara[0] = Lib.retsal.get(0);
                    sel1 = new Select(null, true);
                    sel1.title_str = "Setect Register Name";
                    sel1.count = Lib.retsal.size() - 1;
                    sel1.xc = 2;
                    sel1.yc = 9;
                    sel1.winW = 1000;
                    for (int k = 0; k < sel1.count; k++) {
                        sel1.selstr.add(Lib.retsal.get(k + 1));
                    }
                    sel1.create();
                    sel1.setVisible(true);
                    if (Select.ret_f == 1) {
                        if (Select.retStr.charAt(0) == '.') {
                            //str = "jsobj.COUNT_" + Select.retStr.substring(1);
                            //cla.popWinTd_strPara[0] = "jsobj." + Select.retStr;
                            //cla.incUartKeyHandle();
                            //cla.addUartKeyValue(str, 0, 'R');
                            //cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Get Value:");

                            str = "COUNT_" + Select.retStr.substring(1);
                            cla.incUartKeyHandle();
                            cla.addUartKeyString("getJstblData", cla.popWinTd_strPara[0] + "." + str, 'a');
                            cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getJstblData:");
                            //cla.uartRspActStr = "getJstblData";
                            cla.popWinTd_strPara[1] = Select.retStr;
                            cla.uartRspActStr = "getSonobjCount";
                            break;

                        } else {
                            if (cla.popWinTd_mod.equals("getJsobj")) {
                                cla.incUartKeyHandle();
                                cla.addUartKeyString("getJstblData", cla.popWinTd_strPara[0] + "." + Select.retStr, 'a');
                                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getJstblData:");
                                cla.uartRspActStr = "getJstblData";
                                cla.popWinTd_strPara[1] = Select.retStr;
                                break;
                                //cla.incUartKeyHandle();
                                //cla.addUartKeyValue("jsobj" + "." + Select.retStr, 0, 'R');
                                //cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Get Value:");
                                //cla.uartRspActStr = "getJsobjValue";
                                //cla.popWinTd_strPara[1] = Select.retStr;
                            }
                            if (cla.popWinTd_mod.equals("setJsobj")) {
                                cla.incUartKeyHandle();
                                cla.addUartKeyString("getJstblData", cla.popWinTd_strPara[0] + "." + Select.retStr, 'a');
                                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getJstblData:");
                                cla.uartRspActStr = "getJstblData";
                                cla.popWinTd_strPara[1] = Select.retStr;
                                break;
                            }

                            /*
                            if (cla.popWinTd_mod.equals("getJsobj")) {
                                cla.incUartKeyHandle();
                                cla.addUartKeyValue("jsobj" + "." + Select.retStr, 0, 'R');
                                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "Get Value:");
                            }
                            if (cla.popWinTd_mod.equals("setJsobj")) {
                                cla.incUartKeyHandle();
                                cla.addUartKeyString("getJsobjDataType", Select.retStr, 'a');
                                cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getJsobjDataType:");
                                cla.popWinTd_strPara[2] = Select.retStr;
                            }
                             */
                        }
                    }
                    break;
                case "getJsobjData":
                case "setJsobjData":
                    strA = cla.popWinTd_strPara[3].split(",");
                    if (strA.length < 3) {
                        break;
                    }
                    if (Lib.str2Number(strA[1]) == 0) {
                        break;
                    }
                    ist = strA[0].length() + strA[1].length() + 2;
                    nowValue = Lib.str2edit(cla.popWinTd_strPara[3].substring(ist));
                    jid = Lib.str2edit(strA[0]);

                    spn1 = new SetPanel(null, true);
                    spn1.winW = 1000;
                    spn1.title_str = cla.popWinTd_strPara[1];
                    regName = cla.popWinTd_strPara[1];
                    str = "name:Type;edit:0;";
                    noType = (Lib.valueInt >> 24) & 255;
                    regType = cla.dscTypeA[noType];
                    regLen = (Lib.valueInt >> 16) & 255;
                    array_cnt = Lib.valueInt & 0xfff;
                    if (array_cnt > 0) {
                        spn1.list.add(new MyData(str, regType + " Array"));
                    } else {
                        spn1.list.add(new MyData(str, regType));
                    }
                    str = "name:Now Value;edit:0;";
                    spn1.list.add(new MyData(str, nowValue));

                    if (cla.popWinTd_act.equals("getJsobjData")) {
                        spn1.nameWideRate = 0.2;
                        spn1.keyCnt = 1;
                        spn1.create();
                        spn1.setVisible(true);
                        break;
                    }
                    str = "name:Set;edit:1;";
                    spn1.list.add(new MyData(str, nowValue));
                    spn1.nameWideRate = 0.2;
                    spn1.create();
                    spn1.selectAll = 2;
                    spn1.setVisible(true);
                    outStr = "";
                    regValue = "";
                    if (SetPanel.ret_f == 1) {
                        regValue = spn1.tfa1[4 * 2 + 1].getText();
                        outStr = Lib.trsKvData(regValue, "" + noType + "~" + regLen + "~" + array_cnt);
                        if (Lib.error_f == 1) {
                            Message mes1 = new Message(null, true);
                            mes1.keyType_i = 0;
                            mes1.mesType_i = 1;
                            mes1.title_str = "Data Input Error !!!";
                            mes1.create();
                            mes1.setVisible(true);
                            break;
                        }
                        cla.incUartKeyHandle();
                        cla.addUartKeyValue("setJsobjValue~" + jid + "." + regName, outStr, 'a');
                        cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "setJsobjValue:");

                    }

                    break;

                case "getSonobjCount":
                    strA = cla.popWinTd_strPara[3].split(",");
                    if (strA.length < 3) {
                        break;
                    }

                    if (Lib.chkStrIsNumber(strA[2], 2)) {
                        sel1 = new Select(null, true);
                        sel1.title_str = "Setect Sonobj";
                        sel1.count = Lib.valueInt;
                        sel1.xc = 2;
                        sel1.yc = 9;
                        sel1.winW = 1000;
                        for (int k = 0; k < sel1.count; k++) {
                            sel1.selstr.add(cla.popWinTd_strPara[1] + "~" + k);
                        }
                        sel1.create();
                        sel1.setVisible(true);
                        if (Select.ret_f == 1) {
                            str = Select.retStr;
                            cla.incUartKeyHandle();
                            cla.addUartKeyString("getJstblData", cla.popWinTd_strPara[0] + "." + str, 'a');
                            cla.addUartKeyRsp(cla.uartKeyHandle, 3, 50, "getJstblData:");
                            cla.popWinTd_strPara[1] = Select.retStr;
                            cla.uartRspActStr = "getSonobj";
                            break;

                        }
                    }

                    break;

            }
            cla.popWinTd_act = "";
            Lib.thSleep(10);
        }
    }

}

class UartTestTd1 extends Thread {

    UartTest cla;

    UartTestTd1(UartTest owner) {
        cla = owner;
    }

    @Override
    public void run() { // override Thread's run()
        //Test cla=Test.thisCla;
        for (;;) {
            if (cla.td1_destroy_f == 1) {
                break;
            }
            if (cla.td1_run_f == 1) {

            }
            Lib.thSleep(10);
        }
    }
}

//type 
//0 no resp;
//1 just error message
//2 ok,error message
//3 just error window
//4 ok error window
class RespData {

    int handle;
    int type;
    int time;
    int waitTime;
    String action;

    RespData(int hd, int tp, int wt, String act) {
        handle = hd;
        type = tp;
        waitTime = wt;
        action = act;
        time = 0;
    }
}
