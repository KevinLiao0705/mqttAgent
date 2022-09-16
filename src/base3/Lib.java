package base3;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.json.JSONException;
import org.json.JSONObject;

public class Lib {

    static int error_f = 0;
    static int valueInt = 0;
    static long valueLong = 0;
    static float valueFloat = 0;
    static double valueDouble = 0;
    static int valueType = 0;//0: none, 1: int, 2: long, 3:float, 4:double

    static String retstr;
    static char[] asciiTbl = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static int reti;
    static float retf;
    static String rets;
    static ArrayList<String> retsal = new ArrayList();
    static int[] retia;
    static float[] retfa;

    static String getJsobjItem(Object obj, String itemType, String itemName) {
        Field[] fields = null;
        String type, cla, name, value = "";
        String className;
        String[] strA;
        Object vobj;
        String str;
        strA = obj.getClass().getName().split("\\.");
        className = strA[strA.length - 1];

        try {
            Class MyClass = Class.forName("base3." + className);
            for (int m = 0; m < 2; m++) {
                if (m == 0) {
                    fields = MyClass.getSuperclass().getDeclaredFields();
                }
                if (m == 1) {
                    fields = MyClass.getDeclaredFields();
                }
                for (int i = 0; i < fields.length; i++) {
                    strA = fields[i].getType().getTypeName().split("\\.");
                    type = strA[strA.length - 1];
                    strA = fields[i].getDeclaringClass().getTypeName().split("\\.");
                    cla = strA[strA.length - 1];
                    name = fields[i].getName();
                    if (!name.equals(itemName)) {
                        continue;
                    }
                    return fields[i].get(obj).toString();
                }
            }
            return null;

        } catch (Exception ex) {
            return null;
        }

    }

    static String trsObjToKvstr(String type, Object obj) {
        String retStr = "";
        switch (type) {
            case "String":
            case "int":
            case "long":
            case "double":
            case "char":
                return obj.toString();
            case "int[]":
                int[] iobj = (int[]) obj;
                for (int i = 0; i < iobj.length; i++) {
                    if (i != 0) {
                        retStr += "~";
                    }
                    retStr += iobj[i];
                }
                break;
            case "long[]":
                long[] lobj = (long[]) obj;
                for (int i = 0; i < lobj.length; i++) {
                    if (i != 0) {
                        retStr += "~";
                    }
                    retStr += lobj[i];
                }
                break;
            case "dounle[]":
                double[] dobj = (double[]) obj;
                for (int i = 0; i < dobj.length; i++) {
                    if (i != 0) {
                        retStr += "~";
                    }
                    retStr += dobj[i];
                }
                break;
            default:
                return null;
        }
        return retStr;

    }

    static void setField(Object obj, Field field, String type, String itemValue) throws IllegalArgumentException, IllegalAccessException {
        String[] strA;
        switch (type) {
            case "String":
                field.set(obj, itemValue);
                break;
            case "int":
                field.set(obj, Integer.parseInt(itemValue));
                break;
            case "long":
                field.set(obj, Long.parseLong(itemValue));
                break;
            case "double":
                field.set(obj, Double.parseDouble(itemValue));
                break;
            case "char":
                field.set(obj, itemValue.charAt(0));
                break;
            case "int[]":
                strA = itemValue.split("~");
                int[] ivalue = new int[strA.length];
                for (int i = 0; i < strA.length; i++) {
                    ivalue[i] = Integer.parseInt(strA[i]);
                }
                field.set(obj, ivalue);
                break;
            case "long[]":
                strA = itemValue.split("~");
                long[] lvalue = new long[strA.length];
                for (int i = 0; i < strA.length; i++) {
                    lvalue[i] = Integer.parseInt(strA[i]);
                }
                field.set(obj, lvalue);
                break;
            case "double[]":
                strA = itemValue.split("~");
                double[] dvalue = new double[strA.length];
                for (int i = 0; i < strA.length; i++) {
                    dvalue[i] = Integer.parseInt(strA[i]);
                }
                field.set(obj, dvalue);
                break;
        }

    }

    static int editJidobjItem(JidObj obj, String dataType, String itemName, String itemValue) {
        JidData jidData = obj.mp.get(itemName);
        if (jidData == null) {
            return 0;
        }
        switch (dataType) {
            case "U":
                jidData.v = "";
                break;
            case "B":
            case "L":
            case "D":
            case "S":
                jidData.v = itemValue;
                break;
            default:
                jidData.v = itemValue;
                break;
        }
        obj.mp.put(itemName, jidData);
        return 1;
    }

    static int editNewJidobjItem(JidObj obj, String dataType, String itemName, String itemValue) {
        JidData jidData = new JidData();
        switch (dataType) {
            case "U":
                jidData.v = "";
                break;
            case "B":
            case "L":
            case "D":
            case "S":
                jidData.v = itemValue;
                break;
            default:
                jidData.v = itemValue;
                break;
        }
        obj.mp.put(itemName, jidData);
        return 1;
    }

    static int xxeditJsobjItem(Object obj, String itemType, String itemName, String itemValue) {

        Field[] fields = null;
        String type, cla, name, value = "";
        String className;
        String[] strA;
        Object vobj;
        String str;
        strA = obj.getClass().getName().split("\\.");
        className = strA[strA.length - 1];

        try {
            Class MyClass = Class.forName("base3." + className);
            for (int m = 0; m < 2; m++) {
                if (m == 0) {
                    fields = MyClass.getSuperclass().getDeclaredFields();
                }
                if (m == 1) {
                    fields = MyClass.getDeclaredFields();
                }
                for (int i = 0; i < fields.length; i++) {
                    strA = fields[i].getType().getTypeName().split("\\.");
                    type = strA[strA.length - 1];
                    strA = fields[i].getDeclaringClass().getTypeName().split("\\.");
                    cla = strA[strA.length - 1];
                    name = fields[i].getName();
                    if (!name.equals(itemName)) {
                        continue;
                    }
                    Lib.setField(obj, fields[i], type, itemValue);
                    return 1;
                }
            }
            return 0;

        } catch (Exception ex) {
            return 0;
        }

    }

    static String getFatherTree(String fatherJid, String sonJid, Map<String, Map<String, String>> mapTable, int deepInx, String tree) {
        if (deepInx > 10) {
            return null;
        }
        String str;
        String[] strA;
        String[] strB;
        int i, j;
        String mapTableKey = sonJid.substring(0, 4);
        Map<String, String> mapFather;
        mapFather = mapTable.get(mapTableKey);
        if (mapFather == null) {
            return null;
        }
        str = mapFather.get(sonJid);
        strA = str.split("<>");
        for (i = 0; i < strA.length; i++) {
            strB = strA[i].split("~");
            if (strB[0].equals(fatherJid)) {
                if (tree.length() > 0) {
                    return strA[i] + "<>" + tree;
                } else {
                    return strA[i];
                }
            } else {
                String newTree = "";
                if (tree.length() > 0) {
                    newTree = strA[i] + "<>" + tree;
                } else {
                    newTree = strA[i];
                }
                String retStr = Lib.getFatherTree(fatherJid, strB[0], mapTable, deepInx + 1, newTree);
                if (retStr != null) {
                    return retStr;
                }

            }
        }
        return null;
    }

    static boolean chkFather(String fatherJid, String sonJid, Map<String, Map<String, String>> mapTable, int deepInx) {
        if (deepInx > 10) {
            return false;
        }
        String str;
        String[] strA;
        String[] strB;
        int i, j;
        String mapTableKey = sonJid.substring(0, 4);
        Map<String, String> mapFather;
        mapFather = mapTable.get(mapTableKey);
        if (mapFather == null) {
            return false;
        }
        str = mapFather.get(sonJid);
        strA = str.split("<>");
        for (i = 0; i < strA.length; i++) {
            strB = strA[i].split("~");
            if (strB[0].equals(fatherJid)) {
                return true;
            } else if (Lib.chkFather(fatherJid, strB[0], mapTable, deepInx + 1)) {
                return true;
            }
        }
        return false;
    }

    /*
    static void genFatherMap(Object _rootLs, Map<String, Map<String, String>> mapMap) {
        String str;
        String mapKey;
        String mapValue;
        String preMapValue;
        String mapTable;
        //Map<String,Map<String,String>> mapMap=new HashMap();
        ArrayList<JidObj> root = (ArrayList<JidObj>) _rootLs;

        for (int i = 0; i < root.size(); i++) {
            JidObj obj = root.get(i);
            for (int j = 0; j < obj.jidArrayList.size(); j++) {
                JidArray jobj = obj.jidArrayList.get(j);
                str = obj.jid + "~" + jobj.name + "~";
                for (int k = 0; k < jobj.jids.size(); k++) {
                    mapKey = jobj.jids.get(k);
                    mapValue = str + k;
                    mapTable = mapKey.substring(0, 4);
                    if (!mapMap.containsKey(mapTable)) {
                        Map<String, String> map = new HashMap();
                        mapMap.put(mapTable, map);
                    }
                    Map<String, String> map = mapMap.get(mapTable);
                    preMapValue = "";
                    if (map.containsKey(mapKey)) {
                        preMapValue = map.get(mapKey) + "<>";
                    }
                    map.put(mapKey, preMapValue + mapValue);
                }
            }

        }

    }
     */
 /*
    static Object trsMapToJsobj(String className, Map<String, String> map) {
        Field[] fields = null;
        String type, cla, name, value = "";
        String[] strA;
        Object vobj;
        String str;

        try {
            Class MyClass = Class.forName("base3." + className);
            JidObject obj = (JidObject) MyClass.newInstance();

            for (int m = 0; m < 2; m++) {
                if (m == 0) {
                    fields = MyClass.getSuperclass().getDeclaredFields();
                }
                if (m == 1) {
                    fields = MyClass.getDeclaredFields();
                }
                for (int i = 0; i < fields.length; i++) {
                    strA = fields[i].getType().getTypeName().split("\\.");
                    type = strA[strA.length - 1];
                    strA = fields[i].getDeclaringClass().getTypeName().split("\\.");
                    cla = strA[strA.length - 1];
                    name = fields[i].getName();
                    if (name.equals("jidArrayList")) {
                        ArrayList<JidArray> jalst = new ArrayList();
                        for (int j = 0;; j++) {
                            str = map.get("String" + "<>" + name + "<>" + j);
                            if (str == null) {
                                break;
                            }
                            strA = str.split("~");
                            if (strA.length < 2) {
                                continue;
                            }
                            JidArray jary = new JidArray(strA[0], Integer.parseInt(strA[1]));
                            for (int k = 2; k < strA.length; k++) {
                                jary.jids.set(k - 2, strA[k]);
                            }
                            jalst.add(jary);
                        }
                        fields[i].set(obj, jalst);
                        continue;
                    }
                    str = map.get(type + "<>" + name);
                    if (str == null) {
                        continue;
                    }
                    Lib.setField(obj, fields[i], type, str);
                }
            }
            return obj;

        } catch (Exception ex) {
            return null;
        }

    }

    static Map<String, String> trsJsobjToMap(Object obj) {
        Class objClass = obj.getClass();
        Field[] fields = null;
        String type, cla, name, value = "";
        String[] strA;
        Object vobj;
        Map<String, String> map = new HashMap();
        for (int m = 0; m < 2; m++) {
            if (m == 0) {
                fields = objClass.getSuperclass().getDeclaredFields();
            }
            if (m == 1) {
                fields = objClass.getDeclaredFields();
            }
            for (int i = 0; i < fields.length; i++) {
                strA = fields[i].getType().getTypeName().split("\\.");
                type = strA[strA.length - 1];
                strA = fields[i].getDeclaringClass().getTypeName().split("\\.");
                cla = strA[strA.length - 1];
                name = fields[i].getName();
                try {
                    if (name.equals("jidArrayList")) {
                        vobj = fields[i].get(obj);
                        ArrayList<JidArray> jidArrayList = (ArrayList<JidArray>) vobj;
                        value = "";
                        for (int j = 0; j < jidArrayList.size(); j++) {
                            JidArray jary = jidArrayList.get(j);
                            type = "String";
                            value = jary.name;
                            value += "~" + jary.jids.size();
                            for (int k = 0; k < jary.jids.size(); k++) {
                                value += "~";
                                value += jary.jids.get(k);
                            }
                            map.put(type + "<>" + name + "<>" + j, value);
                        }
                        continue;
                    }
                    vobj = fields[i].get(obj);
                    if (vobj != null) {
                        String kvstr = Lib.trsObjToKvstr(type, vobj);
                        if (kvstr != null) {
                            map.put(type + "<>" + name, kvstr);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Lib.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return map;
    }

    static Object getJsobjByName(Object obj, String _name) {
        Class objClass = obj.getClass();
        Field[] fields;
        String[] strA;
        String type, cla, name, value = "";
        Object vobj;

        fields = objClass.getSuperclass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            name = fields[i].getName();
            if (name.equals(_name)) {
                try {
                    vobj = fields[i].get(obj);
                    return vobj;
                } catch (Exception ex) {
                    return null;
                }
            }
        }
        fields = objClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            name = fields[i].getName();
            if (name.equals(_name)) {
                try {
                    vobj = fields[i].get(obj);
                    return vobj;
                } catch (Exception ex) {
                    return null;
                }
            }
        }
        return null;
    }

     */

    static int search(String str, String st, String end) {
        int sti, endi;
        sti = str.indexOf(st);
        if (sti < 0) {
            return -1;
        }
        endi = str.indexOf(end, sti + st.length());
        if (endi < 0) {
            return -1;
        }
        retstr = str.substring(sti + st.length(), endi);
        return 1;
    }

    static int searchEnd(String str, String st, String end) {
        int sti, endi;
        sti = str.indexOf(st);
        if (sti < 0) {
            return -1;
        }
        endi = str.indexOf(end, sti + st.length());
        if (endi < 0) {
            endi = str.length();
        }
        retstr = str.substring(sti + st.length(), endi);
        return 1;
    }

    static int fsearchEnd(String fileName, String st, String end) {
        File f = new File(fileName);
        if (!f.exists()) {
            return -1;
        }
        if (f.isDirectory()) {
            return -1;
        }
        FileReader fr;
        BufferedReader br;
        String[] fields;
        String tmp;
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            while ((tmp = br.readLine()) != null) {
                if (searchEnd(tmp, st, end) == 1) {
                    fr.close();
                    br.close();
                    return 1;
                }
            }
            fr.close();
            br.close();
            return 0;
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return -1;
    }

    static int wrInterfaces(String ip, String mask, String gateway) {
        String fname;
        String bstr;
        fname = GB.interfaces;
        int debug_i = 0;
        if (debug_i == 1) {
            return 0;
        }

        try {
            FileWriter fw = new FileWriter(fname);
            fw.write("auto lo\n");
            fw.write("iface lo inet loopback\n");
            fw.write("\n");
            fw.write("auto eth0\n");
            //if(GB.ip_type==1)
            //  fw.write("iface eth0 inet dhcp\n");
            //else
            fw.write("iface eth0 inet static\n");

            bstr = "address " + ip + "\n";
            fw.write(bstr);
            bstr = "netmask " + mask + "\n";
            fw.write(bstr);
            bstr = "gateway " + gateway + "\n";
            fw.write(bstr);
            fw.flush();
            fw.close();
            return 1;
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return 0;
    }

    static String rdInterfaces(String cmpstr) {
        String fnameInterfaces = GB.interfaces;
        File f = new File(fnameInterfaces);
        if (f.exists() && !f.isDirectory()) {
            FileReader fr;
            BufferedReader br;
            String[] fields;
            String tmp;
            String str;

            try {
                fr = new FileReader(fnameInterfaces);
                br = new BufferedReader(fr);
                while ((tmp = br.readLine()) != null) {
                    if (tmp.contains(cmpstr)) {
                        str = tmp.trim();
                        fields = str.split("[ ]+");
                        return fields[1];
                    }
                }
                fr.close();
                br.close();
                return null;
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            }

        } else {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
            }
        }
        return null;

    }

    static public int ping(String ip, int wait_tim) {
        int i = 0;
        try {
            if (InetAddress.getByName(ip).isReachable(wait_tim)) {
                return 0;
            } else {
                return -1;
            }
        } catch (UnknownHostException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return -1;
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return -1;
        }
    }

    //Ok return 0:
    //else return 1;
    public static final int ping(String hostname) {
        try {
            if (GB.linuxWin_f == 1) //n=tx count w=wait time
            {
                //return Runtime.getRuntime().exec("ping -n 1 -w 1000 " + hostname).waitFor();  //windows
                return ping(hostname, 1000);
            }
            if (GB.linuxWin_f == 0) {
                return Runtime.getRuntime().exec("ping -c 1 " + hostname).waitFor();  //linux
            }
            return 1;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void thSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
        }
    }

    public static void exe(String exestr) {
        try {
            Process process = Runtime.getRuntime().exec(exestr);
        } catch (IOException ex) {
            Logger.getLogger(Lib.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    public static boolean chkStr2int(String str) {
        try {
            int ibuf = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean chkStrIsIp(String _str) {
        try {
            String str;
            Lib.error_f = 1;
            str = _str.trim();
            String[] strA = str.split("\\.");
            if (strA.length != 4) {
                return false;
            }
            for (int i = 0; i < 4; i++) {
                if (Lib.str2int(strA[i], -1, 255, 0) == -1) {
                    return false;
                }
            }
            Lib.error_f = 0;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String chkStrLegal(String _str, int check, double min, double max) {
        String retStr = _str;
        Lib.error_f = 0;
        switch (check) {
            case 0:
                return retStr;
            case 1:
                retStr = _str.trim();
                Lib.chkStrIsNumber(retStr, 2);
                if (Lib.error_f != 0) {
                    return retStr;
                }
                if (Lib.valueLong < 0 || Lib.valueLong > 255) {
                    Lib.error_f = 1;
                }
                return retStr;
            case 2:
                retStr = _str.trim();
                Lib.chkStrIsNumber(retStr, 2);
                if (Lib.error_f != 0) {
                    return retStr;
                }
                if (Lib.valueLong < 0 || Lib.valueLong > 0xffff) {
                    Lib.error_f = 1;
                }
                return retStr;
            case 3:
                retStr = _str.trim();
                Lib.chkStrIsNumber(retStr, 2);
                if (Lib.error_f != 0) {
                    return retStr;
                }
                if (Lib.valueLong < 0 || Lib.valueLong > 0xffffffff) {
                    Lib.error_f = 1;
                }
                return retStr;
            case 4:
                retStr = _str.trim();
                Lib.chkStrIsNumber(retStr, 3);
                if (Lib.error_f != 0) {
                    return retStr;
                }
                if (Lib.valueLong < min || Lib.valueLong > max) {
                    Lib.error_f = 1;
                }
                return retStr;
            case 5:
                retStr = _str.trim();
                Lib.chkStrIsIp(retStr);
                return retStr;
            case 6://Jid
                retStr = _str.trim();
                if (retStr.equals("0")) {
                    return retStr;
                }
                if (JidObj.chkJidString(retStr, 0) == 1) {
                    Lib.error_f = 1;
                    return retStr;
                }
                return retStr;
            case 7://Jid head
                retStr = _str.trim();
                if (retStr.equals("0")) {
                    return retStr;
                }
                if (JidObj.chkJidString(retStr, 1) == 1) {
                    Lib.error_f = 1;
                    return retStr;
                }
                return retStr;
        }
        Lib.error_f = 0;
        return retstr;
    }

    public static int str2int(String str) {
        return Integer.parseInt(str);
    }

    public static float str2float(String str) {
        return Float.parseFloat(str);
    }

    public static double str2double(String str) {
        return Double.parseDouble(str);
    }

    public static int str2int(String str, int default_i) {
        try {
            Lib.error_f = 0;
            int ibuf = Integer.parseInt(str);
            return ibuf;
        } catch (NumberFormatException e) {
            Lib.error_f = 1;
            return default_i;
        }
    }

    public static int str2int(String str, int default_i, int max, int min) {
        try {
            Lib.error_f = 0;
            int ibuf = Integer.parseInt(str);
            if (ibuf > max) {
                Lib.error_f = 1;
                return default_i;
            }
            if (ibuf < min) {
                Lib.error_f = 1;
                return default_i;
            }
            Lib.error_f = 0;
            return ibuf;
        } catch (NumberFormatException e) {
            Lib.error_f = 1;
            return default_i;
        }
    }

    
    public static String byteToHexStr(int value) {
        String str = "0x";
        str += Lib.asciiTbl[(value >> 4) & 15];
        str += Lib.asciiTbl[(value >> 0) & 15];
        return str;
    }
    
    public static String intToHexStr(int value) {
        String str = "0x";
        int inx = 32;
        int bypass = 1;
        for (int i = 0; i < 8; i++) {
            inx -= 4;
            if (((value >> inx) & 15) != 0) {
                bypass = 0;
            }
            if (bypass == 0 || i >= 6) {
                str += Lib.asciiTbl[(value >> inx) & 15];
            }
        }
        return str;
    }

    public static String longToHexStr(long value) {
        String str = "0x";
        int inx = 64;
        int bypass = 1;
        for (int i = 0; i < 16; i++) {
            inx -= 4;
            if (((value >> inx) & 15) != 0) {
                bypass = 0;
            }
            if (bypass == 0 || i >= 14) {
                str += Lib.asciiTbl[(int) (value >> inx) & 15];
            }
        }
        return str;
    }

    public static String str2edit(String instr) {
        String str = instr.trim();
        int buf = 0;
        if (str.charAt(0) == '"') {
            buf = 1;
        }
        if (str.charAt(0) == '[') {
            buf = 1;
        }
        if (buf == 0) {
            return instr;
        } else {
            return instr.substring(1, instr.length() - 1);
        }
    }

    //flag.0 check signed
    //flag.1 check hex
    //fkag.2 check float
    public static String trsKvData(String input, String kvDataFormat) {
        String outStr = "";
        String inStr = input.trim();
        String init = null;
        String max = null;
        String min = null;
        int imax = 0x7fffffff;
        int imin = 0x80000001;
        int ftype;
        int intBuf;
        long longBuf;
        Lib.error_f = 1;

        String[] strA = kvDataFormat.split("~");
        if (strA.length < 3) {
            return outStr;
        }
        int dtype = Lib.str2intE(strA[0], 11, 0);
        if (Lib.error_f == 1) {
            return outStr;
        }
        int dlen = Lib.str2intE(strA[1], 8, 1);
        if (Lib.error_f == 1) {
            return outStr;
        }
        int arrayCnt = Lib.str2intE(strA[2], 65535, 0);
        if (Lib.error_f == 1) {
            return outStr;
        }
        switch (dtype) {
            case 0:
            case 1:
                if (arrayCnt == 0) {
                    if (inStr.length() > 1) {
                        return outStr;
                    }
                }
                outStr = "\"" + inStr + "\"";
                Lib.error_f = 0;
                return outStr;
            case 2:
                imax = 127;
                imin = -127;
                ftype = 0x03;
                break;
            case 3:
                imax = 255;
                imin = 0;
                ftype = 0x02;
                break;
            case 4:
                imax = 32767;
                imin = -32767;
                ftype = 0x03;
                break;
            case 5:
            case 11:
                imax = 65535;
                imin = 0;
                ftype = 0x02;
                break;
            case 6:
                imax = 0x7fffffff;
                imin = 0x80000001;
                ftype = 0x03;
                break;
            case 7:
                imax = 0xffffffff;
                imin = 0;
                ftype = 0x02;
                break;
            case 8:
            case 9:
                imax = 0;
                imin = 1;
                ftype = 0x05;
                break;
            case 10:
                imax = 0;
                imin = 1;
                ftype = 0x02;
                break;
            default:
                return outStr;

        }
        if (strA.length >= 7) {
            imax = str2intE(strA[6]);
            if (Lib.error_f != 0) {
                return outStr;
            }
        }
        if (strA.length >= 8) {
            imin = str2intE(strA[7]);
            if (Lib.error_f != 0) {
                return outStr;
            }
        }
        if (arrayCnt == 0) {
            if (!Lib.chkStrIsNumber(inStr, ftype)) {
                return outStr;
            }
            if (imax >= imin) {
                if (Lib.valueDouble > imax) {
                    Lib.error_f = 1;
                    return outStr;
                }
                if (Lib.valueDouble < imin) {
                    Lib.error_f = 1;
                    return outStr;
                }
            }
            Lib.error_f = 0;
            if (dtype >= 2 && dtype <= 7) {
                return inStr;
            }
            if (dtype == 8) { //float
                intBuf = Float.floatToIntBits((float) Lib.valueDouble);
                return Lib.intToHexStr(intBuf);
            }
            if (dtype == 9) { //double
                longBuf = Double.doubleToLongBits(Lib.valueDouble);
                return Lib.longToHexStr(longBuf);
            }
            if (dtype == 10) { //long
                return Lib.longToHexStr(Lib.valueLong);
            }
            if (dtype == 11) { //long
                return Lib.intToHexStr(Lib.valueInt);
            }

        }

        String[] strB = inStr.split(",");
        if (strB.length == 0) {
            return outStr;
        }
        inStr = "";
        for (int i = 0; i < strB.length; i++) {
            if (i != 0) {
                inStr += ",";
            }
            if (!Lib.chkStrIsNumber(strB[i], ftype)) {
                return outStr;
            }
            if (imax >= imin) {
                if (Lib.valueDouble > imax) {
                    Lib.error_f = 1;
                    return outStr;
                }
                if (Lib.valueDouble < imin) {
                    Lib.error_f = 1;
                    return outStr;
                }
            }

            if (dtype >= 2 && dtype <= 7) {
                inStr += strB[i];
            }
            if (dtype == 8) { //float
                intBuf = Float.floatToIntBits((float) Lib.valueDouble);
                inStr += Lib.intToHexStr(intBuf);
            }
            if (dtype == 9) { //double
                longBuf = Double.doubleToLongBits(Lib.valueDouble);
                inStr += Lib.longToHexStr(longBuf);
            }
            if (dtype == 10) { //long
                longBuf = Double.doubleToLongBits(Lib.valueLong);
                inStr += Lib.longToHexStr(longBuf);
            }
            if (dtype == 11) { //long
                inStr += Lib.intToHexStr(Lib.valueInt);
            }

        }
        Lib.error_f = 0;
        return "[" + inStr + "]";

    }

    //flag.0 check signed
    //flag.1 check hex
    //fkag.2 check float
    public static boolean chkStrIsNumber(String inStr, int bflag) {
        double dvalue = 0.0;
        long lvalue = 0;
        String str = inStr.trim();
        byte[] btA = str.getBytes();
        if ((bflag & 2) != 0) {
            if (btA.length >= 3 && btA[0] == '0') {
                if (btA[1] == 'x' || btA[1] == 'X') {
                    for (int i = 2; i < btA.length; i++) {
                        if (btA[i] >= '0' && btA[i] <= '9') {
                            lvalue *= 16;
                            lvalue += btA[i] - '0';
                            continue;
                        }
                        if (btA[i] >= 'a' && btA[i] <= 'f') {
                            lvalue *= 16;
                            lvalue += btA[i] - 'a' + 10;
                            continue;
                        }
                        if (btA[i] >= 'A' && btA[i] <= 'F') {
                            lvalue *= 16;
                            lvalue += btA[i] - 'A' + 10;
                            continue;
                        }
                        Lib.error_f = 1;
                        return false;
                    }
                    Lib.valueDouble = lvalue;
                    Lib.valueLong = lvalue;
                    Lib.valueInt = (int) (lvalue);
                    Lib.error_f = 0;
                    return true;
                }
            }
        }

        int minus = 1;
        int pointCnt = -100;
        for (int i = 0; i < btA.length; i++) {
            if ((bflag & 1) != 0) {
                if (btA[i] == '-' && i == 0) {
                    minus = -1;
                    continue;
                }
            }
            if ((bflag & 4) != 0) {
                if (btA[i] == '.' && i != 0) {
                    pointCnt = 0;
                    continue;
                }
            }
            if (btA[i] >= '0' && btA[i] <= '9') {
                lvalue *= 10;
                lvalue += btA[i] - '0';
                pointCnt++;
                continue;
            }
            Lib.error_f = 1;
            return false;
        }
        dvalue = lvalue;
        while (pointCnt > 0) {
            pointCnt--;
            dvalue = dvalue / 10;
            lvalue = lvalue / 10;
        }
        Lib.valueDouble = dvalue * minus;
        Lib.valueLong = lvalue * minus;
        Lib.valueInt = (int) (lvalue * minus);
        Lib.error_f = 0;
        return true;
    }

    //valueType 0:error 1:int 2:long 4:double
    public static int str2Number(String inStr) {
        double dvalue = 0.0;
        long lvalue = 0;

        String str = inStr.trim();
        byte[] btA = str.getBytes();

        if (btA.length >= 3 && btA[0] == '0') {
            if (btA[1] == 'x' || btA[1] == 'X') {
                for (int i = 2; i < btA.length; i++) {
                    if (btA[i] >= '0' && btA[i] <= '9') {
                        lvalue *= 16;
                        lvalue += btA[i] - '0';
                        continue;
                    }
                    if (btA[i] >= 'a' && btA[i] <= 'f') {
                        lvalue *= 16;
                        lvalue += btA[i] - 'a' + 10;
                        continue;
                    }
                    if (btA[i] >= 'A' && btA[i] <= 'F') {
                        lvalue *= 16;
                        lvalue += btA[i] - 'A' + 10;
                        continue;
                    }
                    Lib.error_f = 1;
                    Lib.valueType = 0;
                    return Lib.valueType;
                }
                Lib.valueLong = lvalue;
                Lib.valueInt = (int) lvalue & 0xffffffff;
                Lib.valueType = 1;
                if ((lvalue >> 32) > 0) {
                    Lib.valueType = 2;
                }
                Lib.error_f = 0;
                return Lib.valueType;
            }
        }

        int minus = 1;
        int pointCnt = -100;
        for (int i = 0; i < btA.length; i++) {
            if (btA[i] == '-' && i == 0) {
                minus = -1;
                continue;
            }
            if (btA[i] == '.' && i != 0) {
                pointCnt = 0;
                continue;
            }
            if (btA[i] >= '0' && btA[i] <= '9') {
                lvalue *= 10;
                lvalue += btA[i] - '0';
                pointCnt++;
                continue;
            }
            Lib.error_f = 1;
            Lib.valueType = 0;
            return Lib.valueType;
        }

        if (pointCnt < 0) {
            Lib.valueDouble = lvalue;
            Lib.valueLong = lvalue;
            Lib.valueInt = (int) lvalue & 0xffffffff;
            Lib.valueType = 1;
            if ((lvalue >> 32) > 0) {
                Lib.valueType = 2;
            }
            Lib.error_f = 0;
            return Lib.valueType;
        }

        dvalue = lvalue;
        while (pointCnt > 0) {
            pointCnt--;
            dvalue = dvalue / 10;
            lvalue = lvalue / 10;
        }
        Lib.valueDouble = dvalue * minus;
        Lib.valueLong = lvalue * minus;
        Lib.valueInt = (int) (lvalue * minus);
        Lib.error_f = 0;
        Lib.valueType = 3;
        return Lib.valueType;

    }

    public static int str2intE(String str, int max, int min) {
        try {

            int ibuf = Integer.parseInt(str);
            if (ibuf > max) {
                Lib.error_f = 1;
                return 0;
            }
            if (ibuf < min) {
                Lib.error_f = 1;
                return 0;
            }
            Lib.error_f = 0;
            return ibuf;
        } catch (NumberFormatException e) {
            Lib.error_f = 0;
            return 0;
        }
    }

    public static int str2intE(String str) {
        try {
            int ibuf = Integer.parseInt(str);
            Lib.error_f = 0;
            return ibuf;
        } catch (NumberFormatException e) {
            Lib.error_f = 0;
            return 0;
        }
    }

    public static String bytes2String(byte[] bts, int len) {
        String str = "";
        for (int i = 0; i < len; i++) {
            str += Lib.asciiTbl[(bts[i] >> 4) & 15];
            str += Lib.asciiTbl[bts[i] & 15];
        }
        return str;
    }

    public static String createString(char cha, int len) {
        String str = "";
        for (int i = 0; i < len; i++) {
            str += cha;
        }
        return str;
    }

    public static int str2int(String str, int max, int min) throws Exception {
        try {
            int ibuf = Integer.parseInt(str);
            if (ibuf > max) {
                throw new Exception("The Number is Too Big !!!");
            }
            if (ibuf < min) {
                throw new Exception("The Number is Too Small !!!");
            }
            return ibuf;
        } catch (NumberFormatException e) {
            throw new Exception("The Number is Illegel !!!");
        }
    }

    public static String textToHtml(String istr, String textColor) {
        //return "<html>" + str.replaceAll("\\n", "<br>") + "</html>";       
        String[] strA = istr.split("\n");
        if (strA.length != 2) {
            return istr;
        }
        String str = "";
        str += "<html>";
        str += "<div style='overflow: hidden;white-space: nowrap;'>";
        str += strA[0];
        str += "</div>";
        str += "<div style='color:" + textColor + ";text-align:center;'>";
        str += strA[1];
        str += "</div>";
        str += "</html>";
        return str;
    }

    public static String buttonTextLine2(String istr, String textColor, String fontSize) {
        //return "<html>" + str.replaceAll("\\n", "<br>") + "</html>";       
        String[] strA = istr.split("\n");
        if (strA.length != 2) {
            return istr;
        }
        String str = "";
        str += "<html>";
        str += "<div style='overflow: hidden;white-space: nowrap;font-size:" + fontSize + ";'>";
        str += strA[0];
        str += "</div>";
        str += "<div style='color:" + textColor + ";text-align:center;font-size:" + fontSize + ";'>";
        str += strA[1];
        str += "</div>";
        str += "</html>";
        return str;
    }

    static int getOs() {
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            return 0;
        }
        if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
            return 1;
        }
        if (OS.contains("mac")) {
            return 2;
        }
        if (OS.contains("sunos")) {
            return 3;
        }
        return -1;

    }

    static public int wNtp() {
        String fname;
        String bstr;
        fname = GB.ntp_path;
        try {
            FileWriter fw = new FileWriter(fname);
            fw.write("[Time]\n");
            fw.write("ntp=" + GB.ntp_dns + "\n");
            bstr = "FallbackNTP=0.debian.pool.ntp.org 1.debian.pool.ntp.org 2.debian.pool.ntp.org 3.debian.pool.ntp.org";
            fw.write(bstr);
            fw.flush();
            fw.close();
            return 1;
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return 0;
    }

    static void moveMouse(Point p) {
        GraphicsEnvironment ge
                = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        // Search the devices for the one that draws the specified point.
        for (GraphicsDevice device : gs) {
            GraphicsConfiguration[] configurations
                    = device.getConfigurations();
            for (GraphicsConfiguration config : configurations) {
                Rectangle bounds = config.getBounds();
                if (bounds.contains(p)) {
                    // Set point to screen coordinates.
                    Point b = bounds.getLocation();
                    Point s = new Point(p.x - b.x, p.y - b.y);

                    try {
                        Robot r = new Robot(device);
                        r.mouseMove(s.x, s.y);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
        }
        // Couldn't move to the point, it may be off screen.
        return;
    }

    //input ["dfdsf","dfdsf","dfdsf",...  ]
    static int jsobjToStringArray(String jstr) {
        String str;
        String[] strB;
        String[] strC;
        String[] strA = jstr.split(",");
        Lib.retsal.clear();
        ArrayList<String> arrayList = new ArrayList();
        int len = 0;
        for (int i = 0; i < strA.length; i++) {
            if (strA[i].charAt(0) != '\"') {
                return 0;
            }
            if (strA[i].charAt(strA[i].length() - 1) != '\"') {
                return 0;
            }
            str = strA[i].substring(1, strA[i].length() - 1);
            arrayList.add(str);
        }
        Lib.retsal = arrayList;
        return 1;
    }

    static String[] sepCppIns(String str) {
        String[] strA = new String[4];
        int len;
        len = str.length();
        int i;
        char ch;
        int anaStep = 0;
        int anaStart = 0;
        int anaEnd = 0;
        String itemStr;
        String regType = "";
        String regName = "";
        String arrayCnt = "";
        String regValue = "";

        int chknect_f = 0;
        int bypassSpace_f = 1;

        for (i = 0; i < len; i++) {
            ch = str.charAt(i);
            if (bypassSpace_f == 1) {
                if (ch == ' ') {
                    continue;
                }
                bypassSpace_f = 0;
                anaStart = i;
            }
            switch (anaStep) {
                case 0:
                    if (ch == ' ') {
                        itemStr = str.substring(anaStart, i);
                        if (itemStr.equals("unsigned")) {
                            regType = itemStr + " ";
                            bypassSpace_f = 1;
                            continue;
                        }
                        regType += itemStr;
                        bypassSpace_f = 1;
                        anaStep++;
                    }
                    break;
                case 1:
                    if (ch >= 'a' && ch <= 'z') {
                        continue;
                    }
                    if (ch >= 'A' && ch <= 'Z') {
                        continue;
                    }
                    if (ch == '_') {
                        continue;
                    }
                    if (i != anaStart) {
                        if (ch >= '0' && ch <= '9') {
                            continue;
                        }
                    }
                    if (ch == ' ' || ch == '[' || ch == ';' || ch == '=') {
                        regName = str.substring(anaStart, i);
                        i--;
                        bypassSpace_f = 1;
                        anaStep++;
                        continue;
                    }
                    return null;
                case 2:
                    if (ch == '[') {
                        bypassSpace_f = 1;
                        anaStep = 3;
                        continue;
                    }
                    if (ch == ';') {
                        bypassSpace_f = 1;
                        anaStep = 20;
                        i--;
                        continue;
                    }
                    if (ch == '=') {
                        bypassSpace_f = 1;
                        anaStep = 10;
                        continue;
                    }
                case 3:
                    if (ch == ' ' || ch == ']') {
                        arrayCnt = str.substring(anaStart, i);
                        if (ch == ']') {
                            i--;
                        }
                        bypassSpace_f = 1;
                        anaStep++;
                        continue;
                    }
                    if (ch >= '0' && ch <= '9') {
                        continue;
                    }
                    return null;
                case 4:
                    if (ch == ']') {
                        bypassSpace_f = 1;
                        anaStep++;
                        continue;
                    }
                    return null;
                case 5:
                    if (ch == ';') {
                        bypassSpace_f = 1;
                        anaStep = 20;
                        i--;
                        continue;
                    }
                    if (ch == '=') {
                        bypassSpace_f = 1;
                        anaStep = 10;
                        continue;
                    }
                    return null;
                case 10:
                    if (ch >= '0' && ch <= '9') {
                        anaStep = 11;
                        continue;
                    }
                    if (ch == '-') {
                        anaStep = 11;
                        continue;
                    }
                    if (ch == '"') {
                        anaStep = 12;
                        continue;
                    }
                    if (ch == '{') {
                        anaStep = 13;
                        continue;
                    }
                    if (ch == '\'') {
                        anaStep = 14;
                        continue;
                    }
                    return null;
                case 11:
                    if (ch == ' ' || ch == ';') {
                        regValue = str.substring(anaStart, i);
                        bypassSpace_f = 1;
                        anaStep = 20;
                        if (ch == ';') {
                            i--;
                        }
                        continue;
                    }
                    if (ch >= '0' && ch <= '9') {
                        continue;
                    }
                    if (ch >= '.') {
                        continue;
                    }
                    return null;
                case 12:
                    if (ch == '"') {
                        regValue = str.substring(anaStart + 1, i);
                        bypassSpace_f = 1;
                        anaStep = 20;
                        continue;
                    }
                    continue;
                case 13:
                    if (ch == '}') {
                        regValue = str.substring(anaStart, i + 1);
                        bypassSpace_f = 1;
                        anaStep = 20;
                        continue;
                    }
                    continue;
                case 14:
                    if (ch == '\'') {
                        regValue = str.substring(anaStart, i + 1);
                        bypassSpace_f = 1;
                        anaStep = 20;
                        continue;
                    }
                    continue;
                case 20:
                    if (ch == ';') {
                        strA[0] = regType;
                        strA[1] = regName;
                        strA[2] = arrayCnt;
                        strA[3] = regValue;
                        return strA;
                    }
                    return null;
            }

        }
        return null;

    }

    //return 1:ok, 0 error
    public static int compareString(String orgStr, String cmpStr) {
        int olen = orgStr.length();
        int clen = cmpStr.length();
        int i, j, k, ibuf;
        String sbuf, tbuf;
        ibuf = cmpStr.indexOf('*');
        if (ibuf == -1) {
            if (orgStr.equals(cmpStr)) {
                return 1;
            }
            return 0;
        }
        if (ibuf == 0) {    //first *
            if (clen == 1) //cmpStr="*"
            {
                return 1;
            }
            if (cmpStr.charAt(clen - 1) == '*') {     //comStr="*xxx*"
                sbuf = cmpStr.substring(1, clen - 1);
                if (orgStr.indexOf(sbuf) == -1) {
                    return 0;
                }
                return 1;
            }
            sbuf = cmpStr.substring(1);

            int slen = sbuf.length();
            for (i = 0; i < slen; i++) {
                if (sbuf.charAt(slen - 1 - i) != orgStr.charAt(olen - 1 - i)) {
                    return 0;
                }
            }
            return 1;
        }
        if (ibuf == clen - 1) {   //end *
            sbuf = cmpStr.substring(0, clen - 1);
            ibuf = orgStr.indexOf(sbuf);
            if (ibuf != 0) {
                return 0;
            }
            return 1;
        }
        sbuf = cmpStr.substring(0, ibuf);
        tbuf = cmpStr.substring(ibuf + 1, clen);
        ibuf = orgStr.indexOf(sbuf);
        if (ibuf != 0) {
            return 0;
        }
        int tlen = tbuf.length();
        for (i = 0; i < tlen; i++) {
            if (tbuf.charAt(tlen - 1 - i) != orgStr.charAt(olen - 1 - i)) {
                return 0;
            }
        }
        if (sbuf.length() + tbuf.length() > olen) {
            return 0;
        }
        return 1;
    }

    public static void putJos(JSONObject jo, String key, Object value) {
        try {
            //jo.accumulate(key, value);  //if exist trans to array
            jo.put(key, value);//添加元素
            //jo.append(key, value);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public static int[] getBounds(int fullScr_f,int frameOn_f,int winW,int winH) {
        int[] ia=new int[8];
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //======================================================
        if (fullScr_f == 1) {
            if (frameOn_f == 1) {
                ia[0] = GB.winDialog_xm1;
                ia[1] = GB.winDialog_ym1;;
                ia[2] = screenSize.width - GB.winDialog_wm1;
                ia[3] = screenSize.height - GB.winDialog_hm1;
                ia[4] = 0;
                ia[5] = 0;
                ia[6] = ia[2] - GB.winDialog_wc1;
                ia[7] = ia[3] - GB.winDialog_hc1;
            } else {
                ia[0] = GB.winDialog_xm2;
                ia[1] = GB.winDialog_ym2;;
                ia[2] = screenSize.width - GB.winDialog_wm2;
                ia[3] = screenSize.height - GB.winDialog_hm2;
                ia[4] = 0;
                ia[5] = 0;
                ia[6] = ia[2] - GB.winDialog_wc2;
                ia[7] = ia[3] - GB.winDialog_hc2;
            }
        } else {
            if (frameOn_f == 1) {
                ia[2] = winW;
                ia[3] = winH;
                ia[0] = (screenSize.width - GB.winDialog_wm3 - ia[2]) / 2;
                ia[1] = (screenSize.height - GB.winDialog_hm3 - ia[3]) / 2;
                ia[4] = 0;
                ia[5] = 0;
                ia[6] = ia[2] - GB.winDialog_wc3;
                ia[7] = ia[3] - GB.winDialog_hc3;
            } else {
                ia[2] = winW;
                ia[3] = winH;
                ia[0] = (screenSize.width - GB.winDialog_wm4 - ia[2]) / 2;
                ia[1] = (screenSize.height - GB.winDialog_hm4 - ia[3]) / 2;
                ia[4] = 0;
                ia[5] = 0;
                ia[6] = ia[2] - GB.winDialog_wc4;
                ia[7] = ia[3] - GB.winDialog_hc4;
            }
        }
        return ia;
    }
    
    public static String setMyData(MyData initData) {
        String str;
        SetPanel spn1;
        spn1 = new SetPanel(null, true);
        spn1.winW = 1000;
        spn1.fullScr_f=1;
        spn1.nameWideRate = 0.3;
        spn1.title_str = "New Data";
        str = "name:name;edit:1;check:8";
        spn1.list.add(new MyData(str, ""));
        str = "name:enu;edit:0;type:string;enu:string~int~long~double~stringArray~intArray~longArray~doubleArray";
        spn1.list.add(new MyData(str, "string"));
        str = "name:check;edit:0;type:int;enu:0~1~2~3~4~5~6~7~8";
        spn1.list.add(new MyData(str, "0"));
        str = "name:desc;edit:1";
        spn1.list.add(new MyData(str, ""));
        str = "name:min;type:double";
        spn1.list.add(new MyData(str, "0"));
        str = "name:max;type:double";
        spn1.list.add(new MyData(str, "-1"));
        str = "name:desc;edit:1";
        spn1.list.add(new MyData(str, ""));
        str = "name:edit;edit:0;type:int;enu:0~1";
        spn1.list.add(new MyData(str, "0"));
        str = "name:passw;edit:0;type:int;enu:0~1";
        spn1.list.add(new MyData(str, "0"));
        str = "name:nameWRate;type:double";
        spn1.list.add(new MyData(str, "0"));
        spn1.create();
        spn1.setVisible(true);

        return null;
    }

}

interface StrCallback {

    public String prg(String vstr);
}

interface BytesCallback {

    public String prg(byte[] bytes, int len);
}

interface ObjCallback {

    public String prg(Object vobj);
}

interface StrObjCallback {

    public String prg(String str, Object vobj);
}

abstract class StringCbk {

    public abstract String prg(String str);
}

abstract class MapStringCbk {

    public abstract String prg(Map<String, String> map);
}

abstract class MapCbk {

    public abstract String prg(String sendJid, Map<String, String> map);
}

abstract class MapCbk1 {

    public abstract String prg(String sendJid, Map<String, String> map, String para);
}
