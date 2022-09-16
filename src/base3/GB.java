package base3;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

 

public class GB
{
    static String version = "1.0";
    public static final int MAX_PARA_LEN = 1024;
    //================================================
    static int os=-1;    //0
    
    static int linuxWin_f=1;    //0
    static int setPass_f=1;     //0
    static int userExit_f=1;    //0
    static int cursorOff_f=0;   //1
    static int fullScreen_f=0;
    static int remote_f=1;
    static int showDebug_f=1;
    static String mqttAgentAdminPass="16020039";
    static String mqttAgentNewModelPass="16020039";
    static String mqttAgentNewClassPass="16020039";
    
    
    //===================================
    static int winFrame_bm=40;
    static int winFrame_bm1=-12;//28; //fullscreen no frame 
    static int winFrame_bm2=50;  //fullscreen fram on
    static int winDialog_bm2=32;  //fullscreen fram on
    
    
    static int winFrame_wm1=0; //fullscreen-frameOn, win10: 0
    static int winFrame_hm1=63; //fullscreen-frameOn, win10: 63
    static int winFrame_wm2=0; //fullscreen-frameOff win10: 0
    static int winFrame_hm2=-7; //fullscreen-frameOff win10: -7
    static int winFrame_wm3=16; //notfullscreen-frameOn, win10:16
    static int winFrame_hm3=39; //notfullscreen-frameOn, win10: 39
    static int winFrame_th3=32; //notfullscreen-frameOn,toolbar height win10: 32
    static int winFrame_wm4=0; //notfullscreen-frameOn, win10: 0
    static int winFrame_hm4=-2; //notfullscreen-frameOn, win10: -2
    static int winFrame_th4=40; //notfullscreen-frameOn,toolbar height win10: 40
    
    static int winDialog_wm1=-14; //fullscreen-frameOn, win10: 0
    static int winDialog_hm1=33; //fullscreen-frameOn, win10: 63
    static int winDialog_xm1=-7; //fullscreen-frameOn, win10: 0
    static int winDialog_ym1=0; //fullscreen-frameOn, win10: 0
    static int winDialog_wc1=16; //fullscreen-frameOn, win10: 0
    static int winDialog_hc1=39; //fullscreen-frameOn, win10: 0
    
    static int winDialog_wm2=0; //fullscreen-frameOn, win10: 0
    static int winDialog_hm2=0; //fullscreen-frameOn, win10: 63
    static int winDialog_xm2=0; //fullscreen-frameOn, win10: 0
    static int winDialog_ym2=0; //fullscreen-frameOn, win10: 0
    static int winDialog_wc2=0; //fullscreen-frameOn, win10: 0
    static int winDialog_hc2=0; //fullscreen-frameOn, win10: 0
    
    static int winDialog_wm3=0; //notfullscreen-frameOn, win10:16
    static int winDialog_hm3=33; //notfullscreen-frameOn, win10: 39
    static int winDialog_wc3=16; //fullscreen-frameOn, win10: 0
    static int winDialog_hc3=39; //fullscreen-frameOn, win10: 0

    static int winDialog_wm4=0; //notfullscreen-frameOn, win10:16
    static int winDialog_hm4=40; //notfullscreen-frameOn, win10: 39
    static int winDialog_wc4=0; //fullscreen-frameOn, win10: 0
    static int winDialog_hc4=0; //fullscreen-frameOn, win10: 0



    
    
    
    
    static int winDialog_th3=32; //notfullscreen-frameOn,toolbar height win10: 32
    static int winFrame_ch=32; //center buttom margin
    static int winFrame_ch1=32; //center buttom margin
    
    static String comPort = "COM1";
    static String comBoudrate = "115200";
    static String comParity = "None";//None/Even/Odd   
    static String comEncorder = "Byte";//Byte/Text/KVENC   
    
    static String sockIp = "127.0.0.1";
    static int sockPort = 2468;
    static String sockComPort = "COM1";
    static String sockComBoudrate = "115200";
    static String sockComParity = "None";//None/Even/Odd   
    static String sockComEncorder = "Byte";//Byte/Text/KVENC   
    
    static int showBytes_len = 30;
    
    
    static int viewMaxLineChars=50;
    static int viewMaxLines=200;
    static String viewType="Text";//Byte/Text/KVENC   
    static String viewPack="Both";//Both,Uart,Command
    
    
    //=====================================
    static String setPassword="1234";
    static String exitPassword="123456789";
    
    static int syssec_xor=0xa3;
    static int syssec_f=0;
    static String syssec="123-125-222-456-111-123";
    
    
    
    static String set_localNet_Inf="192.168.0.168~255.255.255.0~192.168.0.1~192.168.0.222";
    static String set_localNet_ip="192.168.0.168";
    static String set_localNet_mask="255.255.255.0";
    static String set_localNet_gateway="192.168.0.1";
    static String set_localNet_dns="192.168.0.222";
    
    static String real_localNet_Inf="192.168.0.168~255.255.255.0~192.168.0.1~192.168.0.222";
    static String real_localNet_ip="192.168.0.168";
    static String real_localNet_mask="255.255.255.0";
    static String real_localNet_gateway="192.168.0.1";
    static String real_localNet_dns="192.168.0.222";
    


    //=====================================
    //=====================================
    static String ntp_path="";
    static String ntp_dns="";
    static String rasp_user_name="pi";
    static String rasp_password="raspberry";
    //==============================================================================
    static String setdata_xml="./setdata.xml";
    static String setdata_db="./setdata.db";
    //static String interfaces="./interfaces";
    //==============================================================================
    //static String setdata_xml="/kevin/java/roip_web/build/web/setdata.xml";
    //static String setdata_db="/kevin/java/roip_web/build/web/setdata.db";
    //static String interfaces="./interfaces";
    //===============================================================================
    //static String setdata_xml="c:/apache-tomcat-9.0.13/webapps/HelloWeb/setdata.xml";
    //static String setdata_db="c:/apache-tomcat-9.0.13/webapps/HelloWeb/setdata.db";
    //static String interfaces="./interfaces";
    //===============================================================================
    //pi zero use
    //static String setdata_xml="/var/lib/tomcat8/webapps/HelloWeb/setdata.xml";
    //static String setdata_db="/var/lib/tomcat8/webapps/HelloWeb/setdata.db";
    static String interfaces="/etc/network/interfaces";
    //static String linphone_address="127.0.0.1";
    //============================================================================
    static String mqttServerAddr="118.163.89.29";//118.163.89.29
    static String mqttServerPort="1883";//1883
    static String mqttServerUserName="kevin";//krvin
    static String mqttServerPassword="xcdswe32@";//xcdswe32@
    static String mqttQoS="2";//2
    static String agentMqttClientId="mqttAgent";//mqttAgent
    
    static String webMqttClientId="mqttWeb";//mqttWeb
    static String[] agentMqttSubA=new String[6];
    static String[] webMqttSubA=new String[6];
    static String webJid="J010F0000000000";

    static String deviceMqttClientId="mqttDeviceText";//mqttWeb
    static String[] deviceMqttSubA=new String[6];
    static String deviceJid="J403F0000000000";

    
    //============================================================================
    static String redisServerAddr="118.163.89.29";//118.163.89.29
    static String redisServerPort="16479";//16479
    static String redisServerUsername="kevin";//
    static String redisServerPassword="1234";//
    //============================================================================
    static String[] webGroupPass=new String[6];
    
    
    
    static String[] paraName=new String[MAX_PARA_LEN] ;
    static String[] paraValue=new String[MAX_PARA_LEN] ;
    
    

    static Color coBt;
    
    
    static Color blue0     = new Color(0, 0, 255);
    static Color green0     = new Color(0, 96, 0);
    static Color red0     = new Color(255, 0, 0);
    static Color blue1     = new Color(80, 80, 255);
    static Color green1     = new Color(80, 128, 80);
    static Color red1     = new Color(255, 80, 80);
    static Color black     = new Color(0, 0, 0);
    static Color white     = new Color(255, 255, 255);
    static Color gray0     = new Color(64, 64, 64);
    static Color gray1     = new Color(128, 128, 128);
    static Color gray2     = new Color(196, 196, 196);
    
    //================================================
    static String redisServerStatus="";
    
    static int paraLen=0;
    static String ret_str;
    
    
    static void initGB() {
        agentMqttSubA[0]="#";
        agentMqttSubA[1]="";
        agentMqttSubA[2]="";
        agentMqttSubA[3]="";
        agentMqttSubA[4]="";
        agentMqttSubA[5]="";
        
        webMqttSubA[0]="#";
        webMqttSubA[1]="";
        webMqttSubA[2]="";
        webMqttSubA[3]="";
        webMqttSubA[4]="";
        webMqttSubA[5]="";
        
        deviceMqttSubA[0]="#";
        deviceMqttSubA[1]="";
        deviceMqttSubA[2]="";
        deviceMqttSubA[3]="";
        deviceMqttSubA[4]="";
        deviceMqttSubA[5]="";
        
        
        webGroupPass[0]="J010F0000000000~J010F~J010F~0~0~0~0~0~0";
        webGroupPass[1]="J010F0000000000~J010F~J010F~0~0~0~0~0~0";
        webGroupPass[2]="J010F0000000000~J010F~J010F~0~0~0~0~0~0";
        webGroupPass[3]="J010F0000000000~J010F~J010F~0~0~0~0~0~0";
        webGroupPass[4]="J010F0000000000~J010F~J010F~0~0~0~0~0~0";
        webGroupPass[5]="J010F0000000000~J010F~J010F~0~0~0~0~0~0";
        
        
    }

public static void loadPara2Form() {

        Class type;
        Object obj;
        int i;
        String str;
        String[] strA;
        String[][] strAA;

        //java.lang.reflect.Field[] f3 = cla.getClass().getDeclaredFields();
        java.lang.reflect.Field[] f3 = GB.class.getDeclaredFields();
        for (i = 0; i < f3.length; i++) {
            f3[i].setAccessible(true);
            try {
                obj = f3[i].get(GB.class);

                if (obj instanceof String[][]) {
                    str = f3[i].getName();
                    strAA = (String[][]) obj;
                    for (int j = 0; j < paraLen; j++) {
                        String[] sbufA;
                        sbufA = paraName[j].split("~");
                        if (sbufA.length == 3) {
                            if (str.equals(sbufA[0])) {
                                byte[] bytes = new byte[paraValue[j].length()];
                                String str1 = paraValue[j];
                                for (int m = 0; m < str1.length(); m++) {
                                    bytes[m] = (byte) str1.charAt(m);
                                }
                                strAA[Integer.parseInt(sbufA[1])][Integer.parseInt(sbufA[2])] = new String(bytes, Charset.forName("UTF-8"));
                            }
                        }
                    }
                }else if (obj instanceof String[]) {
                    str = f3[i].getName();
                    strA = (String[]) obj;
                    for (int j = 0; j < paraLen; j++) {
                        String[] sbufA;
                        sbufA = paraName[j].split("~");
                        if (sbufA.length == 2) {
                            if (str.equals(sbufA[0])) {
                                byte[] bytes = new byte[paraValue[j].length()];
                                String str1 = paraValue[j];
                                for (int m = 0; m < str1.length(); m++) {
                                    bytes[m] = (byte) str1.charAt(m);
                                }
                                strA[Integer.parseInt(sbufA[1])] = new String(bytes, Charset.forName("UTF-8"));
                            }
                        }
                    }
                } else if (obj instanceof int[]) {
                    str = f3[i].getName();
                    int[] intA = (int[]) obj;
                    for (int j = 0; j < paraLen; j++) {
                        String[] sbufA;
                        sbufA = paraName[j].split("~");
                        if (sbufA.length == 2) {
                            if (str.equals(sbufA[0])) {
                                intA[Integer.parseInt(sbufA[1])] = Integer.parseInt(paraValue[j]);
                            }
                        }
                    }
                } else if (obj instanceof String) {
                    str = f3[i].getName();

                    for (int j = 0; j < paraLen; j++) {
                        if (str.equals(paraName[j])) {
                            byte[] bytes = new byte[paraValue[j].length()];
                            String str1 = paraValue[j];
                            for (int m = 0; m < str1.length(); m++) {
                                bytes[m] = (byte) str1.charAt(m);
                            }
                            f3[i].set(GB.class, new String(bytes, Charset.forName("UTF-8")));
                        }
                    }
                } else if (obj instanceof Integer) {
                    str = f3[i].getName();
                    for (int j = 0; j < paraLen; j++) {
                        if (str.equals(paraName[j])) {
                            f3[i].set(GB.class, Integer.parseInt(paraValue[j]));
                        }
                    }
                } else {

                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(GB.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    static void clrPara() {
        for (int i = 0; i < GB.paraLen; i++) {
            GB.paraName[i] = null;
            GB.paraValue[i] = null;
            GB.paraLen = 0;
        }
    }

    static int newPara(String name, String value) {
        if (paraLen >= MAX_PARA_LEN) {
            return 0;
        }
        paraName[paraLen] = name;
        paraValue[paraLen] = value;
        paraLen++;
        return 1;
    }

    static int editPara(String name, String value) {
        int i;
        for (i = 0; i < paraLen; i++) {
            if (paraName[i].equals(name)) {
                paraValue[i] = value;
                return 1;
            }
        }
        return 0;
    }

    static int editNewPara(String name, String value) {
        if (editPara(name, value) == 0) {
            return newPara(name, value);
        }
        return 1;
    }

    static String getPara(String name) {
        int i;
        for (i = 0; i < paraLen; i++) {
            if (paraName[i].equals(name)) {
                return paraValue[i];
            }
        }
        return null;
    }

    static int deletePara(String name) {
        int i;
        for (i = 0; i < paraLen; i++) {
            if (paraName[i].equals(name)) {
                i++;
                for (; i < paraLen; i++) {
                    paraName[i - 1] = paraName[i];
                    paraValue[i - 1] = paraValue[i];
                }
                paraLen--;
                return 1;
            }
        }
        return 0;
    }

}
