/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base3;

import java.util.ArrayList;

/**
 *
 * @author kevin
 */
public class MyData {
    static String para0;
    String name = "";
    String desc = "";
    String type = "string";//string,int,long,double,stringArray,intArray,longArray,doubleArray
    Object value = null;
    ArrayList<Object>enu=null;
    int enuW=1000;
    int enuYc=0;
    int enuXc=0;
    double min = 0;
    double max = -1;;
    int check = 0;  
    /**
        * 0: no check 
        * 1: 0~255 
        * 2: 0~65535
        * 3:0~0xffffffff
        * 4:min~max
        * 5:ip
        * 6:fullJid
        * 7:partJid
        * 8:unnone string
        * 9:jidHeadRang
        * 10:partJid or none
        * 11:long
    */
    int edit = 1;
    int passw = 0;
    double nameWRate = 0;
    
    //=================
    int error_f=0;
    String valueStr="";        
    
    //;
    static public boolean isFirstNameChar(char ch) {
        if (ch >= 'a' && ch <= 'z') {
            return true;
        }
        if (ch >= 'A' && ch <= 'Z') {
            return true;
        }
        if (ch == '_') {
            return true;
        }
        return false;
    }

    static public boolean isNameChar(char ch) {
        if (ch >= '0' && ch <= '9') {
            return true;
        }
        if (ch >= 'a' && ch <= 'z') {
            return true;
        }
        if (ch >= 'A' && ch <= 'Z') {
            return true;
        }
        if (ch == '_') {
            return true;
        }
        return false;
    }

    public int chkLegal(String _str) {
        valueStr = _str;
        error_f = 0;
        switch (check) {
            case 0:
                return error_f;
            case 1://0<=x<=155
                valueStr = _str.trim();
                Lib.chkStrIsNumber(valueStr, 2);
                if (Lib.error_f != 0) {
                    error_f=1;
                    return error_f;
                }
                if (Lib.valueLong < 0 || Lib.valueLong > 255) {
                    error_f = 1;
                }
                return error_f;
            case 2:
                valueStr = _str.trim();
                Lib.chkStrIsNumber(valueStr, 2);
                if (Lib.error_f != 0) {
                    error_f=1;
                    return error_f;
                }
                if (Lib.valueLong < 0 || Lib.valueLong > 0xffff) {
                   error_f = 1;
                }
                return error_f;
            case 3:
                valueStr = _str.trim();
                Lib.chkStrIsNumber(valueStr, 2);
                if (Lib.error_f != 0) {
                    error_f=1;
                    return error_f;
                }
                if (Lib.valueLong < 0 || Lib.valueLong >0xffffffffL) {
                    error_f=1;
                    return error_f;
                }
                return error_f;
            case 4:     
                valueStr = _str.trim();
                Lib.chkStrIsNumber(valueStr, 3);
                if (Lib.error_f != 0) {
                    error_f=1;
                    return error_f;
                }
                if (Lib.valueLong < min || Lib.valueLong > max) {
                    error_f=1;
                    return error_f;
                }
                return error_f;
            case 5:
                valueStr = _str.trim();
                Lib.chkStrIsIp(valueStr);
                if (Lib.error_f != 0) {
                    error_f=1;
                }
                return error_f;
            case 6://Jid
                valueStr = _str.trim();
                if (JidObj.chkJidString(valueStr, 0) == 1) {
                    error_f=1;
                }
                return error_f;
            case 7://first part jid
                valueStr = _str.trim();
                if (JidObj.chkJidString(valueStr, 1) == 1) {
                    error_f=1;
                }
                return error_f;
            case 8://none illegel
                valueStr = _str.trim();
                if(valueStr.length()==0)
                    error_f=1;
                return error_f;
            case 9://jidhead range
                valueStr = _str.trim();
                if (JidObj.chkJidString(valueStr, 2) == 1) {
                    error_f=1;
                }
                return error_f;
            case 10://part jid or none
                valueStr = _str.trim();
                if(valueStr.equals("0"))
                    return error_f;
                if (JidObj.chkJidString(valueStr, 1) == 1) {
                    error_f=1;
                }
                return error_f;
            case 11://long
                valueStr = _str.trim();
                Lib.chkStrIsNumber(valueStr, 2);
                if (Lib.error_f != 0) {
                    error_f=1;
                    return error_f;
                }
                return error_f;
                
                
                
                
                
        }
        return error_f;
    }
    
    
    static public int setKeyValue(String key, Object value, String valueType, MyData md) {
        switch (key) {
            case "name":
                md.name = (String)value;
                break;
            case "enu":
                md.enu = (ArrayList<Object>)value;
                break;
            case "type":
                md.type = (String)value;
                break;
            case "value":
                if (md.type == null) {
                    md.type = valueType;
                }
                md.value = value;
                if (md.type.equals("int")) {
                    md.value = Integer.parseInt((String)value);
                    break;
                }
                if (md.type.equals("long")) {
                    md.value = Long.parseLong((String)value);
                    break;
                }
                if (md.type.equals("double")) {
                    md.value = Double.parseDouble((String)value);
                    break;
                }
                if (md.type.equals("string")) {
                    md.value = value;
                    break;
                }
                if (md.type.equals("array")) {
                    break;
                }
                if (md.type.equals("stringArray")) {
                    md.value=value;
                    break;
                }
                if (md.type.equals("intArray")) {
                    md.value=value;
                    break;
                }
                if (md.type.equals("longArray")) {
                    md.value=value;
                    break;
                }
                if (md.type.equals("doubleArray")) {
                    md.value=value;
                    break;
                }
                return -1;
            case "desc":
                md.desc = (String)value;
                break;
            case "min":
                if (valueType.equals("string")) {
                    return -1;
                }
                md.min = Double.parseDouble((String)value);
                break;
            case "max":
                if (valueType.equals("string")) {
                    return -1;
                }
                md.max = Double.parseDouble((String)value);
                break;
            case "enuXc":
                if (valueType.equals("string")) {
                    return -1;
                }
                md.enuXc = Integer.parseInt((String)value);
                break;
            case "check":
                if (valueType.equals("string")) {
                    return -1;
                }
                md.check = Integer.parseInt((String)value);
                break;
            case "edit":
                if (valueType.equals("string")) {
                    return -1;
                }
                md.edit = Integer.parseInt((String)value);
                break;
            case "passw":
                if (valueType.equals("string")) {
                    return -1;
                }
                md.passw = Integer.parseInt((String)value);
                break;
            case "nameWRate":
                md.nameWRate = (Double)value;
                break;

        }
        return 0;
    }

    static public ArrayList<Object> anaArray(String aryStr) {
        int asciiOnly_f = 0;
        char[] cha = aryStr.toCharArray();
        int i, j, k;
        //====================
        String valueType = "noneArray";
        String keyStr = "";
        String valueStr = "";
        String arrayStr = "";
        //======================            
        //ArrayList<String> strList = new ArrayList<>();
        //ArrayList<Integer> intList = new ArrayList<>();
        //ArrayList<Double> doubleList = new ArrayList<>();
        ArrayList<Object> objList = new ArrayList<>();
        //objectList.add(123);
        
        char[] valueCha = new char[4096];
        int valueCha_len = 0;
        int step = 0;
        int first_f = 0;

        for (i = 0; i < cha.length; i++) {
            switch (step) {
                case 0://find "["
                    if (cha[i] == '[') {
                        valueCha_len=0;
                        step++;
                        break;
                    }
                    if (cha[i] == ' ' || cha[i] == '\t') {
                        break;
                    }
                    return null;
                case 1://find content start
                    if (cha[i] == '"' || cha[i] == '\'') {
                        if (valueType == "noneArray") {
                            valueType = "stringArray";
                        } else if (!valueType.equals("stringArray")) {
                            return null;
                        }
                        valueCha_len=0;
                        step++;
                        break;
                    }
                    if (cha[i] >= '0' && cha[i] <= '9') {
                        if (valueType == "noneArray") {
                            valueType = "intArray";
                        } else if (valueType.equals("string")) {
                            return null;
                        }
                        valueCha_len=0;
                        valueCha[valueCha_len++] = cha[i];
                        step++;
                        break;
                    }
                    if (cha[i] == ' ' || cha[i] == '\t') {
                        break;
                    }
                    if (cha[i] == ']') {
                        step=100;
                        break;
                    }
                    return null;
                case 2://find cintent end
                    if (valueType.equals("stringArray")) {
                        if (cha[i] == '"' || cha[i] == '\'') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            objList.add(valueStr);
                            step++;
                            break;
                        }
                        valueCha[valueCha_len++] = cha[i];
                        break;
                    }
                    if (valueType.equals("intArray")) {
                        if (cha[i] >= '0' && cha[i] <= '9') {
                            valueCha[valueCha_len++] = cha[i];
                            break;
                        }
                        if (cha[i] == '.') {
                            valueType = "doubleArray";
                            valueCha[valueCha_len++] = cha[i];
                            break;
                        }
                        if (cha[i] == ' ' || cha[i] == '\t') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            objList.add(Integer.parseInt(valueStr));
                            step++;
                            break;
                        }
                        if (cha[i] == ',') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            objList.add(Integer.parseInt(valueStr));
                            i--;
                            step++;
                            break;
                        }
                        if (cha[i] == ']') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            objList.add(Integer.parseInt(valueStr));
                            i--;
                            step++;
                            break;
                        }
                    }
                    
                    
                    if (valueType.equals("doubleArray")) {
                        if (cha[i] >= '0' && cha[i] <= '9') {
                            valueCha[valueCha_len++] = cha[i];
                            break;
                        }
                        if (cha[i] == '.') {
                            return null;
                        }
                        if (cha[i] == ' ' || cha[i] == '\t') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            objList.add(Double.parseDouble(valueStr));
                            step++;
                            break;
                        }
                        if (cha[i] == ',') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            objList.add(Double.parseDouble(valueStr));
                            i--;
                            step++;
                            break;
                        }
                        if (cha[i] == ']') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            objList.add(Double.parseDouble(valueStr));
                            i--;
                            step++;
                            break;
                        }
                    }
                    return null;
                case 3:
                        if (cha[i] == ',') {
                            step=1;
                            break;
                        }    
                        if (cha[i] == ']') {
                            step=100;
                            i--;
                            break;
                        }    
                        if (cha[i] == ' ' || cha[i] == '\t') {
                            break;
                        }
                    break;
                case 100:
                      MyData.para0=valueType;  
                      return objList;  
                    
            }

        }

        return null;
    }

    static public MyData anaJson(String jsonStr) {
        int asciiOnly_f = 0;
        char[] cha = jsonStr.toCharArray();
        int i, j, k;
        //====================
        String valueType = "";
        String keyStr = "";
        String valueStr = "";
        String arrayStr = "";
        //======================            
        char[] keyCha = new char[256];
        int keyCha_len = 0;
        char[] valueCha = new char[4096];
        int valueCha_len = 0;
        int jsonStep = 0;
        MyData md = new MyData();

        for (i = 0; i < cha.length; i++) {
            switch (jsonStep) {
                case 0: //check json start '{'
                    if (cha[i] == '{') {
                        jsonStep++;
                        break;
                    }
                    if (cha[i] == ' ' || cha[i] == '\t') {
                        break;
                    }
                    return null;
                case 1: //check key start '"'
                    if (cha[i] == '"' || cha[i] == '\'') {
                        jsonStep++;
                        break;
                    }
                    if (cha[i] == ' ' || cha[i] == '\t') {
                        break;
                    }
                    return null;
                case 2: //check key name start 'x'
                    if (isFirstNameChar(cha[i])) {
                        keyCha_len = 0;
                        keyCha[keyCha_len++] = cha[i];
                        jsonStep++;
                        break;
                    }
                    return null;
                case 3: //check key name end 'x'
                    if (isNameChar(cha[i])) {
                        keyCha[keyCha_len++] = cha[i];
                        break;
                    }
                    if (cha[i] == '"' || cha[i] == '\'') {
                        keyStr = new String(keyCha, 0, keyCha_len);
                        jsonStep++;
                        break;
                    }
                    return null;
                case 4://check colone
                    if (cha[i] == ':') {
                        jsonStep++;
                        break;
                    }
                    if (cha[i] == ' ' || cha[i] == '\t') {
                        break;
                    }
                    return null;
                case 5://check value first sym
                    if (cha[i] == '"' || cha[i] == '\'') {
                        valueType = "string";
                        valueCha_len = 0;
                        jsonStep++;
                        break;
                    }
                    if (cha[i] == '[') {
                        valueType = "array";
                        valueCha_len = 0;
                        valueCha[valueCha_len++] = cha[i];
                        jsonStep++;
                        break;
                    }

                    if (cha[i] >= '0' && cha[i] <= '9') {
                        valueType = "int";
                        valueCha_len = 0;
                        valueCha[valueCha_len++] = cha[i];
                        jsonStep++;
                        break;
                    }
                    if (cha[i] == ' ' || cha[i] == '\t') {
                        break;
                    }
                    return null;
                case 6://check value end 
                    if (valueType.equals("string")) {
                        if (cha[i] == '"' || cha[i] == '\'') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            setKeyValue(keyStr, valueStr, valueType, md);
                            jsonStep++;
                            break;
                        }
                        if (asciiOnly_f == 0) {
                            valueCha[valueCha_len++] = cha[i];
                            break;
                        }

                        if (cha[i] >= 0x20 && cha[i] <= 0x7e) {
                            valueCha[valueCha_len++] = cha[i];
                            break;
                        }
                        return null;
                    }
                    if (valueType.equals("int") || valueType.equals("double") || valueType.equals("long")) {
                        if (cha[i] >= '0' && cha[i] <= '9') {
                            valueCha[valueCha_len++] = cha[i];
                            break;
                        }
                        if (cha[i] == '.') {
                            if (valueType.equals("double")) {
                                return null;
                            }
                            valueType = "double";
                            valueCha[valueCha_len++] = cha[i];
                            break;
                        }
                        if (cha[i] == ' ' || cha[i] == '\t') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            setKeyValue(keyStr, valueStr, valueType, md);
                            jsonStep++;
                            break;
                        }
                        if (cha[i] == '}') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            setKeyValue(keyStr, valueStr, valueType, md);
                            jsonStep = 100;
                            break;
                        }
                        if (cha[i] == ',') {
                            valueStr = new String(valueCha, 0, valueCha_len);
                            setKeyValue(keyStr, valueStr, valueType, md);
                            i--;
                            jsonStep++;
                            break;
                        }
                        return null;
                    }
                    if (valueType.equals("array")) {
                        if (cha[i] == ']') {
                            valueCha[valueCha_len++] = cha[i];
                            valueStr = new String(valueCha, 0, valueCha_len);
                            Object obj=anaArray(valueStr);
                            if(md.value==null)
                                return null;
                            setKeyValue(keyStr, obj, MyData.para0, md);
                            jsonStep++;
                            break;
                        }
                        if (asciiOnly_f == 0) {
                            valueCha[valueCha_len++] = cha[i];
                            break;
                        }
                        if (cha[i] >= 0x20 && cha[i] <= 0x7e) {
                            valueCha[valueCha_len++] = cha[i];
                            break;
                        }
                        return null;
                    }

                    return null;
                case 7:// check , or }
                    if (cha[i] == ',') {
                        jsonStep++;
                        break;
                    }
                    if (cha[i] == '}') {
                        jsonStep = 100;
                        break;
                    }
                    if (cha[i] == ' ' || cha[i] == '\t') {
                        break;
                    }
                    return null;
                case 8://check next key sym:
                    if (cha[i] == '}') {
                        jsonStep = 100;
                        break;
                    }
                    if (cha[i] == '"' || cha[i] == '\'') {
                        jsonStep = 2;
                        break;
                    }
                    if (cha[i] == ' ' || cha[i] == '\t') {
                        break;
                    }
                    return null;
                case 100:
                    if (cha[i] == ' ' || cha[i] == '\t') {
                        break;
                    }
                    return null;

            }

        }

        return md;

    }

    public MyData() {
    }
    public MyData(String jsonStr) {
        anaJson(jsonStr);
    }

    public MyData(String nstr, int iv) {
        name=nstr;
        value=iv;
        type="int";
    }    
    public MyData(String nstr, double dv) {
        name=nstr;
        value=dv;
        type="double";
    }    
    public MyData(String dataStr, String valueStr) {
        String[] strA;
        String[] strB;
        String[] strC;
        value = "";
        strA = dataStr.split(";");

        if (strA.length == 1) {
            if(strA[0].split(":").length==1){
                name = dataStr;
                value = valueStr;
                return;
            }
        }
        value=valueStr;
        for (int i = 0; i < strA.length; i++) {
            strB = strA[i].split(":");
            if (strB.length != 2) {
                continue;
            }
            switch (strB[0]) {
                case "value":
                    value = strB[1];
                    break;
                case "name":
                    name = strB[1];
                    break;
                case "desc":
                    desc = strB[1];
                    break;
                case "type":
                    type = strB[1];
                    switch (type) {
                        case "doubleArray":
                        case "intArray":
                        case "stringArray":
                        case "string":
                            value = valueStr;
                            break;
                        case "int":
                            value = Lib.str2int(valueStr);
                            break;
                        case "double":
                            value = Lib.str2double(valueStr);
                            break;

                    }
                    break;
                case "enu":
                    strC = strB[1].split("~");
                    if(strC.length==0)
                        break;
                    enu=new ArrayList<>();
                    for (int j = 0; j < strC.length; j++) {
                        enu.add(strC[j]);
                    }
                    break;
                case "min":
                    min = Lib.str2double(strB[1]);
                    break;
                case "max":
                    min = Lib.str2double(strB[1]);
                    break;
                case "check":
                    check = Lib.str2int(strB[1]);
                    break;
                case "edit":
                    edit = Lib.str2int(strB[1]);
                    break;
                case "enuXc":
                    enuXc = Lib.str2int(strB[1]);
                    break;
                case "passw":
                    passw = Lib.str2int(strB[1]);
                    break;
                case "nameWRate":
                    nameWRate = Lib.str2double(strB[1]);;
            }
        }

    }
    
}
