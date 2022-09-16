package base3;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import redis.clients.jedis.Jedis;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashMap;
import org.json.JSONObject;

public class EmuTpmsAiot {

    static final int AGENT_AMT = 1;
    static final int WEB_AMT = 3;
    static final int TPMS_GROUP_AMT = 3;
    static final int TRUCKS_AMT = 32;
    static final int RELAYS_AMT = TRUCKS_AMT * 3;
    static final int TYRE_AMT = RELAYS_AMT * 8;

    MqttAgent mcla;
    int selectIdType = 0;
    int fromIndex = 0;
    int toIndex = 0;
    int testCnt = 0;
    int testInx = 0;
    ArrayList<String> jsClassNameLs = new ArrayList();
    ArrayList<Object> jsClassObjectLs = new ArrayList();

    static EmuTpmsAiot scla;

    public EmuTpmsAiot(MqttAgent _mcla) {
        mcla = _mcla;
        scla = this;

    }

    public void init() {
        initAgent();
        readAllFromRedis();
    }

    void showFuncKey() {
        int i;
        Color co;
        String str;
        String[] strA = new String[28];
        for (i = 0; i < 28; i++) {
            strA[i] = "";
        }
        strA[0] = "Database Clear";
        strA[1] = "Database Init";
        strA[2] = "Database Set";
        strA[3] = "Database Read";
        strA[4] = "Edit JidObject";
        strA[5] = "Add Object";
        strA[6] = "Test Jid Command";
        strA[7] = "SelectIdType";
        strA[8] = "TestInx + : " + testInx;
        strA[9] = "TestInx - : " + testInx;

        for (i = 0; i < 28; i++) {
            str = strA[i];
            if (!mcla.btLeftA[i + 20].getText().equals(str)) {
                mcla.btLeftA[i + 20].setText(str);
            }
        }
    }

    void initAgent() {
        String jid, gid, sname;
        Map<String, JidObj> agentLs = mcla.jdsc.mmJobj.get("agentLs");
        agentLs.clear();
        for (int i = 0; i < 1; i++) {
            JidObj obj = mcla.cdsc.newClass("AgentObj");
            jid = "J003F0000" + String.format("%06d", i);
            gid = "Agent" + String.format("%06d", i);
            sname = "Agent-" + String.format("%06d", i);
            obj.mp.put("jid", new JidData("S", jid));
            obj.mp.put("gid", new JidData("S", gid));
            obj.mp.put("sname", new JidData("S", sname));
            mcla.jidMap.put(jid, obj);
            agentLs.put(jid, obj);
        }
    }

    void initWeb() {
        String jid, gid, sname;
        Map<String, JidObj> webLs = mcla.jdsc.mmJobj.get("webLs");
        webLs.clear();
        for (int i = 0; i < WEB_AMT; i++) {
            JidObj obj = mcla.cdsc.newClass("WebJobj");
            jid = "J010F0000" + String.format("%06d", i);
            gid = "Web" + String.format("%06d", i);
            sname = "Web-" + String.format("%06d", i);
            newWeb(jid, gid, sname);
        }
    }

    JidObj newWeb(String jid, String gid, String sname) {
        Map<String, JidObj> webLs = mcla.jdsc.mmJobj.get("webLs");
        JidObj obj = mcla.cdsc.newClass("WebJobj");
        obj.mp.put("jid", new JidData("S", jid));
        obj.mp.put("gid", new JidData("S", gid));
        obj.mp.put("sname", new JidData("S", sname));
        mcla.jidMap.put(jid, obj);
        webLs.put(jid, obj);
        return obj;
    }

    void initTpmsGroup() {
        int i;
        String jid, gid, sname;
        Map<String, JidObj> groupLs = mcla.jdsc.mmJobj.get("groupLs");
        groupLs.clear();

        for (i = 0; i < TPMS_GROUP_AMT; i++) {
            JidObj obj = mcla.cdsc.newClass("GroupJobj");
            jid = "J100F0000" + String.format("%06d", i);
            gid = "Group" + String.format("%06d", i);
            sname = "Group-" + String.format("%06d", i);
            obj.mp.put("jid", new JidData("S", jid));
            obj.mp.put("gid", new JidData("S", gid));
            obj.mp.put("sname", new JidData("S", sname));
            mcla.jidMap.put(jid, obj);
            groupLs.put(jid, obj);
        }
    }

    void initTruck() {
        int i;
        String jid, gid, sname;
        Map<String, JidObj> truckLs = mcla.jdsc.mmJobj.get("truckLs");
        truckLs.clear();
        for (i = 0; i < TRUCKS_AMT; i++) {
            JidObj obj = mcla.cdsc.newClass("TruckJobj");
            jid = "J403F0000" + String.format("%06d", i);
            gid = "Truck" + String.format("%06d", i);
            sname = "Truck-" + String.format("%06d", i);
            obj.mp.put("jid", new JidData("S", jid));
            obj.mp.put("gid", new JidData("S", gid));
            obj.mp.put("sname", new JidData("S", sname));
            //
            obj.mp.put("flags", new JidData("L", "0x00000000"));
            obj.mp.put("truckType", new JidData("S", "truck"));
            obj.mp.put("gpsData", new JidData("S", ""));
            mcla.jidMap.put(jid, obj);
            truckLs.put(jid, obj);
        }
    }

    void initRelay() {
        int i;
        String jid, gid, sname;
        Map<String, JidObj> relayLs = mcla.jdsc.mmJobj.get("relayLs");
        relayLs.clear();
        for (i = 0; i < RELAYS_AMT; i++) {
            JidObj obj = mcla.cdsc.newClass("RelayJobj");
            jid = "J402F0000" + String.format("%06d", i);
            gid = "Relay" + String.format("%06d", i);
            sname = "Relay-" + String.format("%06d", i);
            obj.mp.put("jid", new JidData("S", jid));
            obj.mp.put("gid", new JidData("S", gid));
            obj.mp.put("sname", new JidData("S", sname));
            //
            obj.mp.put("flags", new JidData("L", "0x00000000"));
            obj.mp.put("tyreLayout", new JidData("L", "0x8100c3c3"));
            obj.mp.put("lowPressureSet", new JidData("L", "26"));
            obj.mp.put("highPressureSet", new JidData("L", "45"));
            obj.mp.put("highTemperatureSet", new JidData("L", "70"));
            mcla.jidMap.put(jid, obj);
            relayLs.put(jid, obj);
        }
    }

    void initTyre() {
        int i;
        String jid, gid, sname;
        Map<String, JidObj> tyreLs = mcla.jdsc.mmJobj.get("tyreLs");
        tyreLs.clear();
        for (i = 0; i < TYRE_AMT; i++) {
            JidObj obj = mcla.cdsc.newClass("TyreJobj");
            jid = "J401F0000" + String.format("%06d", i);
            gid = "Tyre" + String.format("%06d", i);
            sname = "Tyre-" + String.format("%06d", i);
            obj.mp.put("jid", new JidData("S", jid));
            obj.mp.put("gid", new JidData("S", gid));
            obj.mp.put("sname", new JidData("S", sname));
            obj.mp.put("tpmsData", new JidData("L", "0x00004120"));
            mcla.jidMap.put(jid, obj);
            tyreLs.put(jid, obj);
        }
    }

    //==========================================================================
    int getJsobjlsFromRedis(String className, String modelGroupName, int thd) {
        String[] strA;
        String hashKey;
        KvRedis.anaJsobjName = className;
        try {
            //Class MyClass = Class.forName("base3." + className);
            String key = "set~jid~" + className + "~" + modelGroupName;
            if (!KvRedis.actOne("getSet", key.split("<~>"), null)) {
                return 1;
            }
            strA = KvRedis.strSet.toArray(new String[KvRedis.strSet.size()]);
            //===================================================================
            KvRedis.pipeAct("connect", null, null);
            for (int i = 0; i < strA.length; i++) {
                hashKey = "hash~jid~" + strA[i];
                KvRedis.pipeAct("getHashAll", hashKey.split("<~>"), null);
            }
            if (thd != 0) {
                KvRedis.pipeActTh("execute", null, null);
            } else {
                KvRedis.pipeAct("execute", null, null);
                KvRedis.chkPipe();
            }
            return 0;
        } catch (Exception ex) {
            Logger.getLogger(EmuTpmsAiot.class.getName()).log(Level.SEVERE, null, ex);
            return 2;
        }
    }

    int getGroupModelFromRedis(String className, Object anaObject, int thd) {
        String[] strA;
        String hashKey;
        KvRedis.anaClassName = className;
        KvRedis.anaOperate = "addGroupModelToMMap";
        KvRedis.anaObject = anaObject;
        try {
            //Class MyClass = Class.forName("base3." + className);
            String key = "set~groupModel";
            if (!KvRedis.actOne("getSet", key.split("<~>"), null)) {
                return 1;
            }
            strA = KvRedis.strSet.toArray(new String[KvRedis.strSet.size()]);
            //===================================================================
            KvRedis.pipeAct("connect", null, null);
            for (int i = 0; i < strA.length; i++) {
                hashKey = "hash~groupModel~" + strA[i];
                KvRedis.pipeAct("getHashAll", hashKey.split("<~>"), null);
            }
            if (thd != 0) {
                KvRedis.pipeActTh("execute", null, null);
            } else {
                KvRedis.pipeAct("execute", null, null);
                KvRedis.chkPipe();
            }
            return 0;
        } catch (Exception ex) {
            Logger.getLogger(EmuTpmsAiot.class.getName()).log(Level.SEVERE, null, ex);
            return 2;
        }
    }

    int getJidClassFromRedis(String className, Object anaObject, int thd) {
        String[] strA;
        String hashKey;
        KvRedis.anaClassName = className;
        KvRedis.anaOperate = "addJidClassToMMap";
        KvRedis.anaObject = anaObject;
        try {
            //Class MyClass = Class.forName("base3." + className);
            String key = "set~jidClass";
            if (!KvRedis.actOne("getSet", key.split("<~>"), null)) {
                return 1;
            }
            strA = KvRedis.strSet.toArray(new String[KvRedis.strSet.size()]);
            //===================================================================
            KvRedis.pipeAct("connect", null, null);
            for (int i = 0; i < strA.length; i++) {
                hashKey = "hash~jidClass~" + strA[i];
                KvRedis.pipeAct("getHashAll", hashKey.split("<~>"), null);
            }
            if (thd != 0) {
                KvRedis.pipeActTh("execute", null, null);
            } else {
                KvRedis.pipeAct("execute", null, null);
                KvRedis.chkPipe();
            }
            return 0;
        } catch (Exception ex) {
            Logger.getLogger(EmuTpmsAiot.class.getName()).log(Level.SEVERE, null, ex);
            return 2;
        }
    }

    ArrayList<JidObj> getJsobjlsJidFromRedisSet(String className, String lsName) {
        String[] strA;
        try {
            Class MyClass = Class.forName("base3." + className);
            String key = "set~jid~" + className + "~" + lsName;
            if (!KvRedis.actOne("getSet", key.split("<~>"), null)) {
                return null;
            }
            ArrayList<JidObj> als = new ArrayList();
            strA = KvRedis.strSet.toArray(new String[KvRedis.strSet.size()]);

            for (int i = 0; i < strA.length; i++) {
                JidObj obj = new JidObj();
                obj.mp.put("nid", new JidData("S", className));
                obj.mp.put("jid", new JidData("S", strA[i]));
                als.add(obj);
            }
            return als;

        } catch (Exception ex) {
            Logger.getLogger(EmuTpmsAiot.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    boolean setJsobjlsToRedis(Map<String, JidObj> objLs, String className, String lsName, int pipe) {
        if (!setJsobjlsJidToRedisSet(objLs, "set~jid~" + className + "~" + lsName, pipe)) {
            return false;
        }
        if (!setJsobjlsToRedisHash(objLs, pipe)) {
            return false;
        }
        return true;
    }

    boolean setJidClassToRedis(String jidClassName, Map<String, String> cdscMap, int pipe) {
        if (!addJidClassToRedisSet(jidClassName, pipe)) {
            return false;
        }
        if (!setJidClassToRedisHash(jidClassName, cdscMap, pipe)) {
            return false;
        }
        return true;
    }

    boolean setGroupModelToRedis(String groupModelName, Map<String, String> dscMap, int pipe) {
        if (!addGroupModelToRedisSet(groupModelName, pipe)) {
            return false;
        }
        if (!setGroupModelToRedisHash(groupModelName, dscMap, pipe)) {
            return false;
        }
        return true;
    }

    boolean addJsobjToRedis(String className, String groupModelName, JidObj jobj, int pipe) {
        String jid = jobj.getv("jid");
        if (!addJidToRedisSet(jid, "set~jid~" + className + "~" + groupModelName, pipe)) {
            return false;
        }
        if (!setJsobjToRedisHash(jobj, "hash~jid~" + jid, pipe)) {
            return false;
        }
        return true;
    }

    boolean addJidToRedisSet(String jid, String key, int pipe) {
        int i;
        String value = jid;
        if (value.length() == 0) {
            return true;
        }
        if (pipe == 0) {
            return KvRedis.actOne("addSet", key.split("<~>"), value.split("~"));
        }
        KvRedis.pipeAct("addSet", key.split("<~>"), value.split("~"));
        return true;

    }

    boolean addGroupModelToRedisSet(String groupModelName, int pipe) {
        int i;
        String key = "set~groupModel";
        if (pipe == 0) {
            return KvRedis.actOne("addSet", key.split("<~>"), groupModelName.split("~"));
        }
        KvRedis.pipeAct("addSet", key.split("<~>"), groupModelName.split("~"));
        return true;

    }

    boolean addJidClassToRedisSet(String jidClassName, int pipe) {
        int i;
        String key = "set~jidClass";
        if (pipe == 0) {
            return KvRedis.actOne("addSet", key.split("<~>"), jidClassName.split("~"));
        }
        KvRedis.pipeAct("addSet", key.split("<~>"), jidClassName.split("~"));
        return true;

    }

    boolean setJidClassToRedisHash(String jidClassName, Map<String, String> cdscMap, int pipe) {
        int i;
        String key = "hash~jidClass~" + jidClassName;
        if (pipe == 0) {
            return KvRedis.actOne("setHashAll", key.split("<~>"), cdscMap);
        }
        KvRedis.pipeAct("setHashAll", key.split("<~>"), cdscMap);
        return true;

    }

    boolean setGroupModelToRedisHash(String groupModelName, Map<String, String> dscMap, int pipe) {
        int i;
        String key = "hash~groupModel~" + groupModelName;
        if (pipe == 0) {
            return KvRedis.actOne("setHashAll", key.split("<~>"), dscMap);
        }
        KvRedis.pipeAct("setHashAll", key.split("<~>"), dscMap);
        return true;

    }

    boolean setJsobjlsJidToRedisSet(Map<String, JidObj> objLs, String key, int pipe) {
        int i;
        String value = "";
        String jid = "";
        int inx = 0;
        for (String id : objLs.keySet()) {
            JidObj obj = objLs.get(id);
            if (inx != 0) {
                value += "~";
            }
            value += obj.mp.get("jid").v;
            inx++;
        }
        if (value.length() == 0) {
            return true;
        }
        if (pipe == 0) {
            return KvRedis.actOne("setSet", key.split("<~>"), value.split("~"));
        }
        KvRedis.pipeAct("setSet", key.split("<~>"), value.split("~"));
        return true;

    }

    boolean setJsobjlsToRedisHash(Map<String, JidObj> objLs, int pipe) {
        int i;
        String value = "";
        String jid = "";
        for (String id : objLs.keySet()) {
            JidObj obj = objLs.get(id);
            String hashKey = "hash~jid~" + obj.mp.get("jid").v;
            if (!setJsobjToRedisHash(obj, hashKey, pipe)) {
                return false;
            }
        }
        return true;
    }

    boolean setJsobjToRedisHash(JidObj obj, String hashKey, int pipe) {
        int i, j;
        String name, value, type;
        Map<String, String> map = new HashMap();
        for (Object opKey : obj.mp.keySet()) {
            name = (String) opKey;
            value = obj.mp.get(name).v;
            name = obj.mp.get(name).t + "~" + name;
            map.put(name, value);
        }
        for (i = 0; i < obj.jidArrayList.size(); i++) {
            JidArray ja = obj.jidArrayList.get(i);
            name = "O~jidArrayList~" + ja.groupName;
            value = ja.className;
            for (j = 0; j < ja.jids.size(); j++) {
                value += "~" + ja.jids.get(j);
            }
            map.put(name, value);
        }
        if (pipe == 0) {
            return KvRedis.actOne("setHashAll", new String[]{hashKey}, map);
        }
        KvRedis.pipeAct("setHashAll", new String[]{hashKey}, map);
        return true;
    }

    boolean setJidobjItemToRedisHash(String hashKey, String itemName, String itemValue, int pipe) {
        int i;
        if (pipe == 0) {
            return KvRedis.actOne("setHashKeyValue", new String[]{hashKey, itemName, itemValue}, null);
        }
        KvRedis.pipeAct("setHashKeyValue", new String[]{hashKey, itemName, itemValue}, null);
        return true;
    }

    //==========================================================================
    void readJidlsFromRedis(String modelGroupName) {
        Map<String, JidObj> jidObjls = mcla.jdsc.mmJobj.get(modelGroupName);
        String className = mcla.jdsc.mmDscGet(modelGroupName, "className");
        if (jidObjls != null) {
            KvRedis.anaObject = jidObjls;
            getJsobjlsFromRedis(className, modelGroupName, 0);
        }
    }

    //return error
    void readAllFromRedis() {
        getGroupModelFromRedis("EmuTpmsAiot", mcla.jdsc, 0);
        getJidClassFromRedis("EmuTpmsAiot", mcla.cdsc, 0);
        KvRedis.anaClassName = "EmuTpmsAiot";
        KvRedis.anaOperate = "addDataToJsobjls";
        for (String name : mcla.jdsc.mmDsc.keySet()) {
            readJidlsFromRedis(name);
        }
    }

    int setAllToRedis() {
        return 0;
    }

    int editJsobjls(String title, Map<String, JidObj> objls) {
        Select sel1;
        final LoopSelect lsel1;
        String[] strA;
        String[] strB;
        String key;
        String value;
        Set<String> ss;
        String str;
        Object nowObj;

        lsel1 = new LoopSelect(null, true);
        lsel1.xc = 4;
        lsel1.yc = 9;
        lsel1.winW = 1600;
        SelData sd = new SelData();
        sd.actObject = objls;
        sd.actObjectType = 0;
        sd.title = title;
        for (String jid : objls.keySet()) {
            if (selectIdType == 0) {
                sd.slst.add("{ " + objls.get(jid).mp.get("jid").v + " }");
                sd.mapKeys.add(objls.get(jid).mp.get("jid").v);
            }
            if (selectIdType == 1) {
                sd.slst.add("{ " + objls.get(jid).mp.get("gid").v + " }");
                sd.mapKeys.add(objls.get(jid).mp.get("jid").v);
            }
            if (selectIdType == 2) {
                sd.slst.add("{ " + objls.get(jid).mp.get("sname").v + " }");
                sd.mapKeys.add(objls.get(jid).mp.get("jid").v);
            }
        }
        //Collections.sort(sd.slst);
        lsel1.selDataLs.add(sd);
        lsel1.create();

        lsel1.lse = new LoopSelectEnter() {
            @Override
            public int enter(int reti, String rets) {
                String str;
                String[] strA;
                String regType = "";
                String regName = "";
                String strValue = "";

                if (reti < 0) {
                    return 0;
                }
                SelData nowSd = lsel1.selDataLs.get(lsel1.selDataLs.size() - 1);
                String mapKey = nowSd.mapKeys.get(reti);
                if (nowSd.actObjectType == 0) { //objectList
                    Map<String, JidObj> nowObjectList = (Map<String, JidObj>) nowSd.actObject;
                    JidObj nowObj = nowObjectList.get(mapKey);
                    if (nowObj == null) {
                        return 1;
                    }
                    SelData sd = new SelData();
                    sd.title = nowObj.getv("jid");
                    sd.title += "-" + nowObj.getv("gid");

                    for (Object opKey : nowObj.mp.keySet()) {
                        str = (String) opKey;
                        sd.mapKeys.add(str);
                        sd.slst.add(str);
                    }

                    for (int i = 0; i < nowObj.jidArrayList.size(); i++) {
                        JidArray ja = nowObj.jidArrayList.get(i);
                        sd.mapKeys.add(ja.groupName);
                        sd.slst.add("[" + ja.groupName + "]");

                    }
                    sd.actObject = nowObj;
                    sd.actObjectType = 1;   //object
                    lsel1.selDataLs.add(sd);
                    return 1;

                }
                if (nowSd.actObjectType == 1) { //object
                    JidObj nowObj = (JidObj) nowSd.actObject;
                    String className = nowObj.getv("nid");
                    Map<String, String> cdsc = mcla.cdsc.mmCdsc.get(className);
                    if (rets.charAt(0) == '[') {
                        String groupName = nowSd.mapKeys.get(reti);
                        for (int i = 0; i < nowObj.jidArrayList.size(); i++) {
                            if (nowObj.jidArrayList.get(i).groupName.equals(groupName)) {
                                JidArray ja = nowObj.jidArrayList.get(i);
                                Map<String, JidObj> objls = new HashMap();
                                SelData sd = new SelData();
                                for (int j = 0; j < ja.jids.size(); j++) {
                                    String jid = ja.jids.get(j);
                                    JidObj jidobj = mcla.jidMap.get(jid);
                                    objls.put(jid, jidobj);
                                    sd.slst.add("{ " + jid + " }");
                                    sd.mapKeys.add(jid);
                                }
                                sd.title = rets;
                                sd.actObject = objls;
                                sd.actObjectType = 0;
                                lsel1.selDataLs.add(sd);

                            }
                        }
                        return 1;
                    }

                    if (rets.charAt(0) != '[') {
                        SetPanel spn1;
                        spn1 = new SetPanel(null, true);
                        spn1.winW = 600;
                        spn1.title_str = rets;

                        Object regValue = null;
                        JidData jidData = nowObj.mp.get(rets);
                        regType = "" + jidData.t;
                        regName = rets;
                        regValue = jidData.v;
                        if (regName.equals("editTime")) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String strDate = dateFormat.format(Long.parseLong((String) regValue));
                            spn1.list.add(new MyData("edit:0", regValue.toString() + " " + strDate));
                            spn1.onlyView_f = 1;
                        } else {
                            /*
                            String rdsc=cdsc.get(regName);
                            if(rdsc!=null){
                                String[] strB=rdsc.split("~");
                                if(strB.length==3){
                                    jidData.setFlag(strB[2]);
                                }
                            }
                             */
                            String tstr = "";
                            switch (regType) {
                                case "I":
                                    tstr += "check:3";
                                    break;
                                case "L":
                                    tstr += "check:11";
                                    break;
                            }
                            spn1.list.add(new MyData(tstr, jidData.getStrValue()));
                        }

                        spn1.nameWideRate = 0.01;
                        spn1.create();
                        spn1.setVisible(true);
                        if (SetPanel.ret_f == 0) {
                            return 1;
                        }
                        strValue = spn1.tfa1[4 * 0 + 1].getText();
                        int errf = JidData.checkType(regType, strValue);
                        if (errf == 1) {
                            Message.warnBox("Input Error !!!");
                            return 1;
                        }

                        Lib.editJidobjItem(nowObj, regType, regName, spn1.tfa1[4 * 0 + 1].getText());
                        boolean bf;
                        JidObj jobj = (JidObj) nowObj;
                        String jid = nowObj.mp.get("jid").v;
                        String value = nowObj.mp.get(regName).v;
                        bf = setJidobjItemToRedisHash("hash~jid~" + jid, regType + "~" + regName, value, 0);
                        if (bf) {
                            mcla.setStatus("Save OK.", 1);
                        } else {
                            mcla.setStatus("Save Error !!!", 2);
                        }

                        Map<String, String> mapResp = mcla.chkJidInConnect(jid+"."+regName, "");
                        {
                            for (String key : mapResp.keySet()) {
                                mcla.agentMqttVobj(key.split("~")[0], "setData", "" + mcla.agentTxCmdNo, mapResp.get(key), value);
                                mcla.agentTxCmdNo++;
                            }
                        }
                        
                        
                        
                        return 1;

                    }

                }
                return 0;

            }

        };

        lsel1.setVisible(true);
        lsel1.retf = 0;
        if (lsel1.retf == 0) {
            return 0;
        }

        for (int i = 0; i < lsel1.count; i++) {
            str = "";
            JidObj jidObj = objls.get(i);
            if (selectIdType == 0) {
                str = jidObj.mp.get("jid").v;
            }
            if (selectIdType == 1) {
                str = jidObj.mp.get("gid").v;
            }
            if (selectIdType == 2) {
                str = jidObj.mp.get("sname").v;
            }
            jidObj = objls.get(i);
            if (str.equals(Select.retStr)) {
                sel1 = new Select(null, true);
                sel1.title_str = "Select " + str;
                sel1.count = jidObj.mp.keySet().size();
                sel1.xc = 4;
                sel1.yc = 9;
                sel1.winW = 1000;

                ArrayList<String> strLs = new ArrayList();
                for (Object opKey : jidObj.mp.keySet()) {
                    str = (String) opKey;
                    strLs.add(str);
                    sel1.selstr.add(str);
                }

                for (i = 0; i < jidObj.jidArrayList.size(); i++) {
                    JidArray ja = jidObj.jidArrayList.get(i);
                    str = "[ " + ja.groupName + " ]";
                    strLs.add(ja.groupName);
                    sel1.selstr.add(str);
                }
                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    return 0;
                }
                str = strLs.get(Select.ret_i);
                String regType = "strA[0]";
                String regName = "strA[1]";
                if (regName.equals("jidArrayList")) {

                }
                //============
                SetPanel spn1;
                spn1 = new SetPanel(null, true);
                spn1.winW = 600;
                spn1.title_str = Select.retStr;
                //Object regValue = jidobj.get(str);
                //spn1.list.add(new MyData("", (String) regValue));
                spn1.nameWideRate = 0.01;
                spn1.create();
                spn1.setVisible(true);
                if (SetPanel.ret_f == 0) {
                    return 0;
                }
                //str = Lib.getJsobjItem(jsobjls.get(i), regType, regName);
                //Lib.editJsobjItem(jsobjls.get(i), regType, regName, spn1.tfa1[4 * 0 + 1].getText());
                return 1;
            }
        }

        return 0;
    }

    int selectIdType() {
        Select sel1;
        sel1 = new Select(null, true);
        sel1.title_str = "Select Type";
        sel1.count = 3;
        sel1.xc = 1;
        sel1.yc = 3;
        sel1.winW = 500;
        sel1.selstr.add("JID");
        sel1.selstr.add("GID");
        sel1.selstr.add("SNAME");
        sel1.create();
        sel1.setVisible(true);
        if (Select.ret_f == 0) {
            return 0;
        }
        selectIdType = Select.ret_i;
        return 1;
    }

    void addJidToJsobj(String title, String toDscName, String fromDscStr) {
        Map<String, JidObj> fromObjLs = null;
        Map<String, JidObj> toObjLs = null;
        String fromObjName = null;
        String toObjName = null;
        String fromSname = null;
        String toSname = null;
        Map<String, String> jdscMap;

        jdscMap = mcla.jdsc.mmDsc.get(toDscName);
        if (jdscMap != null) {
            toObjName = jdscMap.get("className");
            toSname = jdscMap.get("sname");
            toObjLs = mcla.jdsc.mmJobj.get(toDscName);
        }
        if (toObjLs == null) {
            return;
        }
        if (toObjName == null) {
            return;
        }

        boolean bf;
        LoopSelect lsel1;

        lsel1 = new LoopSelect(null, true);
        lsel1.xc = 4;
        lsel1.yc = 9;
        lsel1.winW = 1600;
        SelData sd = new SelData();
        //sd.actObject = mcla.jdsc.mmJobj.get(toDscName);
        //sd.actObjectType = 0;
        sd.title = title;
        for (String jid : toObjLs.keySet()) {
            JidObj jobj = toObjLs.get(jid);
            if (selectIdType == 0) {
                sd.slst.add(jobj.getv("jid"));
                sd.mapKeys.add(jobj.getv("jid"));
            }
            if (selectIdType == 1) {
                sd.slst.add(jobj.getv("gid"));
                sd.mapKeys.add(jobj.getv("jid"));
            }
            if (selectIdType == 2) {
                sd.slst.add(jobj.getv("sname"));
                sd.mapKeys.add(jobj.getv("jid"));
            }

        }

        lsel1.selDataLs.add(sd);
        lsel1.create();
        lsel1.setVisible(true);
        if (lsel1.retf == 0) {
            return;
        }
        //================================
        //toIndex = lsel1.reti;
        //=============================================
        String jid = sd.mapKeys.get(lsel1.reti);
        JidObj toObj = toObjLs.get(jid);

        int jaryInx = 0;
        JidArray jary = null;
        int exist = 0;

        String[] strA = fromDscStr.split("~");
        if (strA.length < 2) {
            return;
        }

        for (jaryInx = 0; jaryInx < toObj.jidArrayList.size(); jaryInx++) {
            jary = toObj.jidArrayList.get(jaryInx);
            if (jary.groupName.equals(strA[1])) {
                exist = 1;
                break;
            }
        }
        if (exist == 0) {
            jary = new JidArray(strA[0], strA[1], 0);
            toObj.jidArrayList.add(jary);
            jaryInx = 0;
        }
        //============================================
        Insert ins1 = new Insert(null, true);
        ins1.title_str = toSname + "." + toObj.mp.get("jid").v + "  <======>  " + fromDscStr;
        ins1.winW = 1600;
        ins1.xc = 2;
        ins1.yc = 20;
        for (int i = 0; i < jary.jids.size(); i++) {
            ins1.selstrA.add(jary.jids.get(i));
        }
        String jidHead = "";
        if (strA.length == 3) {
            jidHead = strA[2];
        }
        if (strA.length == 4) {
            jidHead = strA[2] + "~" + strA[3];
        }

        for (String jidKey : mcla.jidMap.keySet()) {
            int ibuf = Jdsc.chkJidHead(jidKey, jidHead);
            if (ibuf == 2) {
                ins1.selstrB.add(jidKey);
            }
        }
        /*
    
        
        
        for (int i = 0; i < fromObjLs.size(); i++) {
            JidObj tobj = fromObjLs.get(i);
            int breakf = 0;
            for (int j = 0; j < jary.jids.size(); j++) {
                if (jary.jids.get(j).equals(tobj.mp.get("jid").v)) {
                    breakf = 1;
                }
            }
            if (breakf == 0) {
                ins1.selstrB.add(tobj.mp.get("jid").v);
            }
        }
         */
        ins1.create();
        ins1.setVisible(true);
        if (ins1.retf == 0) {
            return;
        }
        jary.jids.clear();
        for (int i = 0; i < ins1.selstrA.size(); i++) {
            jary.jids.add(ins1.selstrA.get(i));
        }
        //============================================
        String value = jary.className;
        for (int i = 0; i < jary.jids.size(); i++) {
            value += "~" + jary.jids.get(i);
        }
        bf = setJidobjItemToRedisHash("hash~jid~" + toObj.mp.get("jid").v, "O~jidArrayList~" + jary.groupName, value, 0);
        if (bf) {
            mcla.setStatus("Save OK.", 1);
        } else {
            mcla.setStatus("Save Error !!!", 2);
        }
        return;
    }

    /*
    
    void addJidToJsobj(String title, Object fromObjLs, Object toObjLs, String fromClassName, String toGroupName) {
        ArrayList<JidObj> fromls = (ArrayList<JidObj>) fromObjLs;
        ArrayList<JidObj> tols = (ArrayList<JidObj>) toObjLs;
        boolean bf;
        LoopSelect lsel1;

        lsel1 = new LoopSelect(null, true);
        lsel1.xc = 4;
        lsel1.yc = 9;
        lsel1.winW = 1600;
        SelData sd = new SelData();
        sd.actObject = mcla.groupLs;
        sd.actObjectType = 0;
        sd.title = title;
        for (int i = 0; i < tols.size(); i++) {
            if (selectIdType == 0) {
                sd.slst.add(tols.get(i).mp.get("jid").v);
            }
            if (selectIdType == 1) {
                sd.slst.add(tols.get(i).mp.get("gid").v);
            }
            if (selectIdType == 2) {
                sd.slst.add(tols.get(i).mp.get("sname").v);
            }
        }
        lsel1.selDataLs.add(sd);
        lsel1.create();
        lsel1.setVisible(true);
        if (lsel1.retf == 0) {
            return;
        }
        //================================
        toIndex = lsel1.reti;
        //=============================================
        JidObj gobj = tols.get(toIndex);

        int jaryInx = 0;
        JidArray jary = null;
        for (jaryInx = 0; jaryInx < gobj.jidArrayList.size(); jaryInx++) {
            jary = gobj.jidArrayList.get(jaryInx);
            if (jary.groupName.equals(toGroupName)) {
                break;
            }
        }
        if (jaryInx == gobj.jidArrayList.size()) {
            jary = new JidArray(fromClassName, toGroupName, 0);
            gobj.jidArrayList.add(jary);
            jaryInx = 0;
        }
        //============================================
        Insert ins1 = new Insert(null, true);
        ins1.title_str = toGroupName + "  <======>  " + fromClassName;
        ins1.winW = 1600;
        ins1.xc = 2;
        ins1.yc = 20;
        for (int i = 0; i < jary.jids.size(); i++) {
            ins1.selstrA.add(jary.jids.get(i));
        }
        for (int i = 0; i < fromls.size(); i++) {
            JidObj tobj = fromls.get(i);
            int breakf = 0;
            for (int j = 0; j < jary.jids.size(); j++) {
                if (jary.jids.get(j).equals(tobj.mp.get("jid").v)) {
                    breakf = 1;
                }
            }
            if (breakf == 0) {
                ins1.selstrB.add(tobj.mp.get("jid").v);
            }
        }
        ins1.create();
        ins1.setVisible(true);
        if (ins1.retf == 0) {
            return;
        }
        jary.jids.clear();
        for (int i = 0; i < ins1.selstrA.size(); i++) {
            jary.jids.add(ins1.selstrA.get(i));
        }
        //============================================
        String value = jary.name;
        for (int i = 0; i < jary.jids.size(); i++) {
            value += "~" + jary.jids.get(i);
        }
        bf = setJidobjItemToRedisHash("hash~jid~" + gobj.mp.get("jid").v, "O~jidArrayList~" + jary.groupName, value, 0);
        if (bf) {
            mcla.setStatus("Save OK.", 1);
        } else {
            mcla.setStatus("Save Error !!!", 2);
        }
        return;
    }
     */
    void funcKeyAct(int index) {
        Select sel1;
        String[] strA;
        String key;
        String value;
        Set<String> ss;
        String str;
        boolean bf;
        switch (index) {
            case 0:
                sel1 = new Select(null, true);
                sel1.title_str = "Redis Clear";
                sel1.count = 5;
                sel1.xc = 1;
                sel1.yc = 9;
                sel1.winW = 1000;
                sel1.selstr.add("Clear GroupJobj List");
                sel1.selstr.add("Clear TruckJobj List");
                sel1.selstr.add("Clear RelayJobj List");
                sel1.selstr.add("Clear TyreJobj List");
                sel1.selstr.add("Clear All TPMS Object List");

                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    break;
                }
                switch (Select.ret_i) {
                    case 0:
                        mcla.jdsc.mmJobj.get("groupLs").clear();
                        break;
                    case 1:
                        mcla.jdsc.mmJobj.get("truckLs").clear();
                        break;
                    case 2:
                        mcla.jdsc.mmJobj.get("relayLs").clear();
                        break;
                    case 3:
                        mcla.jdsc.mmJobj.get("tyreLs").clear();
                        break;
                    case 4:
                        mcla.jdsc.mmJobj.get("agentLs").clear();
                        mcla.jdsc.mmJobj.get("webLs").clear();
                        mcla.jdsc.mmJobj.get("groupLs").clear();
                        mcla.jdsc.mmJobj.get("truckLs").clear();
                        mcla.jdsc.mmJobj.get("relayLs").clear();
                        mcla.jdsc.mmJobj.get("tyreLs").clear();
                        break;
                }
                break;
            case 1:
                sel1 = new Select(null, true);
                sel1.title_str = "Redis Init";
                sel1.count = 6;
                sel1.xc = 1;
                sel1.yc = 9;
                sel1.winW = 1000;
                sel1.selstr.add("Init AgentJobj List");
                sel1.selstr.add("Init GroupJobj List");
                sel1.selstr.add("Init TruckJobj List");
                sel1.selstr.add("Init RelayJobj List");
                sel1.selstr.add("Init TyreJobj List");
                sel1.selstr.add("Init All TPMS Object List");

                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    break;
                }
                switch (Select.ret_i) {
                    case 0:
                        initAgent();
                        break;
                    case 1:
                        initTpmsGroup();
                        break;
                    case 2:
                        initTruck();
                        break;
                    case 3:
                        initRelay();
                        break;
                    case 4:
                        initTyre();
                        break;
                    case 5:
                        initAgent();
                        initWeb();
                        //initTpmsGroup();
                        initTruck();
                        initRelay();
                        initTyre();
                        break;
                }
                break;
            case 2:
                sel1 = new Select(null, true);
                sel1.title_str = "Redis Set";
                sel1.count = 5;
                sel1.xc = 1;
                sel1.yc = 9;
                sel1.winW = 1000;

                sel1.selstr.add("Set GroupJobj List To Redis");
                sel1.selstr.add("Set TruckJobj List To Redis");
                sel1.selstr.add("Set RelayJobj List To Redis");
                sel1.selstr.add("Set TyreJobj List To Redis");
                sel1.selstr.add("Set All TPMS Object List To Redis");

                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    break;
                }
                if (KvRedis.thd_run_f != 0) {
                    mcla.setStatus("Redis is Busy", 2);
                    break;
                }
                mcla.setStatus("Redis Oprating......", 0);
                KvRedis.anaClassName = "EmuTpmsAiot";
                KvRedis.anaOperate = "";
                switch (Select.ret_i) {
                    case 0:
                        KvRedis.pipeAct("connect", null, null);
                        setJsobjlsToRedis(mcla.jdsc.mmJobj.get("groupLs"), "GroupJobj", "groupLs", 1);
                        KvRedis.pipeActTh("execute", null, null);
                        break;
                    case 1:
                        KvRedis.pipeAct("connect", null, null);
                        setJsobjlsToRedis(mcla.jdsc.mmJobj.get("truckLs"), "TruckJobj", "truckLs", 1);
                        KvRedis.pipeActTh("execute", null, null);
                        break;
                    case 2:
                        KvRedis.pipeAct("connect", null, null);
                        setJsobjlsToRedis(mcla.jdsc.mmJobj.get("relayLs"), "RelayJobj", "relayLs", 1);
                        KvRedis.pipeActTh("execute", null, null);
                        break;
                    case 3:
                        KvRedis.pipeAct("connect", null, null);
                        setJsobjlsToRedis(mcla.jdsc.mmJobj.get("tyreLs"), "TyreJobj", "tyreLs", 1);
                        KvRedis.pipeActTh("execute", null, null);
                        break;
                    case 4:
                        KvRedis.pipeAct("connect", null, null);
                        setJsobjlsToRedis(mcla.jdsc.mmJobj.get("agentLs"), "AgentJobj", "agentLs", 1);
                        setJsobjlsToRedis(mcla.jdsc.mmJobj.get("webLs"), "WebJobj", "webLs", 1);
                        setJsobjlsToRedis(mcla.jdsc.mmJobj.get("groupLs"), "GroupJobj", "groupLs", 1);
                        setJsobjlsToRedis(mcla.jdsc.mmJobj.get("truckLs"), "TruckJobj", "truckLs", 1);
                        setJsobjlsToRedis(mcla.jdsc.mmJobj.get("relayLs"), "RelayJobj", "relayLs", 1);
                        setJsobjlsToRedis(mcla.jdsc.mmJobj.get("tyreLs"), "TyreJobj", "tyreLs", 1);
                        KvRedis.pipeActTh("execute", null, null);
                        break;
                }
                break;
            case 3:
                sel1 = new Select(null, true);
                sel1.title_str = "Redis Read";
                sel1.count = 5;
                sel1.xc = 1;
                sel1.yc = 9;
                sel1.winW = 1000;

                sel1.selstr.add("Get GroupJobj List From Redis");
                sel1.selstr.add("Get TruckJobj List From Redis");
                sel1.selstr.add("Get RelayJobj List From Redis");
                sel1.selstr.add("Get TyreJobj List From Redis");
                sel1.selstr.add("Get All TPMS Object List From Redis");

                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    break;
                }
                KvRedis.anaClassName = "EmuTpmsAiot";
                KvRedis.anaOperate = "addDataToJsobjls";
                switch (Select.ret_i) {
                    case 0:
                        mcla.jdsc.mmJobj.get("groupLs").clear();
                        KvRedis.anaObject = mcla.jdsc.getSjmByName("groupLs");
                        getJsobjlsFromRedis("GroupJobj", "groupLs", 0);
                        break;
                    case 1:
                        mcla.jdsc.mmJobj.get("truckLs").clear();
                        KvRedis.anaObject = mcla.jdsc.getSjmByName("truckLs");
                        getJsobjlsFromRedis("TruckJobj", "truckLs", 0);
                        break;
                    case 2:
                        mcla.jdsc.mmJobj.get("relayLs").clear();
                        KvRedis.anaObject = mcla.jdsc.getSjmByName("relayLs");
                        getJsobjlsFromRedis("RelayJobj", "relayLs", 0);
                        break;
                    case 3:
                        mcla.jdsc.mmJobj.get("tyreLs").clear();
                        KvRedis.anaObject = mcla.jdsc.getSjmByName("tyreLs");
                        getJsobjlsFromRedis("TyreJobj", "tyreLs", 0);
                        break;
                    case 4:
                        mcla.jdsc.mmJobj.get("agentLs").clear();
                        KvRedis.anaObject = mcla.jdsc.mmJobj.get("agentLs");
                        getJsobjlsFromRedis("AgentJobj", "agentLs", 0);
                        mcla.jdsc.mmJobj.get("webLs").clear();
                        KvRedis.anaObject = mcla.jdsc.mmJobj.get("webLs");
                        getJsobjlsFromRedis("WebJobj", "webLs", 0);
                        mcla.jdsc.mmJobj.get("groupLs").clear();
                        KvRedis.anaObject = mcla.jdsc.mmJobj.get("groupLs");
                        getJsobjlsFromRedis("GroupJobj", "groupLs", 0);
                        mcla.jdsc.mmJobj.get("truckLs").clear();
                        KvRedis.anaObject = mcla.jdsc.mmJobj.get("truckLs");
                        getJsobjlsFromRedis("TruckJobj", "truckLs", 0);
                        mcla.jdsc.mmJobj.get("relayLs").clear();
                        KvRedis.anaObject = mcla.jdsc.mmJobj.get("relayLs");
                        getJsobjlsFromRedis("RelayJobj", "relayLs", 0);
                        mcla.jdsc.mmJobj.get("tyreLs").clear();
                        KvRedis.anaObject = mcla.jdsc.mmJobj.get("tyreLs");
                        getJsobjlsFromRedis("TyreJobj", "tyreLs", 0);
                        break;

                }
                break;

            case 4:
                sel1 = new Select(null, true);
                sel1.title_str = "Edit JidObject";
                sel1.count = mcla.jdsc.mmDsc.size();
                sel1.xc = 1;
                sel1.yc = 10;
                sel1.winW = 1000;
                for (String dscName : mcla.jdsc.mmDsc.keySet()) {
                    sel1.selstr.add(dscName);
                }
                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    break;
                }
                editJsobjls("Edit " + Select.retStr, mcla.jdsc.getSjmByName(Select.retStr));
                break;

            case 5://add object
                final LoopSelect lsel1;
                String[] strB;
                Object nowObj;
                lsel1 = new LoopSelect(null, true);

                lsel1.lse = new LoopSelectEnter() {
                    @Override
                    public int enter(int reti, String rets) {
                        String[] strA;
                        strA = rets.split(" ");
                        if (strA.length != 2) {
                            return 1;
                        } else {
                            addJidToJsobj("Select To Object", strA[0], strA[1]);
                        }
                        return 0;
                    }
                };

                lsel1.xc = 2;
                lsel1.yc = 8;
                lsel1.winW = 1600;
                SelData sd = new SelData();
                sd.actObject = jsClassObjectLs;
                sd.actObjectType = 0;
                sd.title = "To Object";

                for (String opKey : mcla.jdsc.mmDsc.keySet()) {
                    Map<String, String> mDsc = mcla.jdsc.mmDsc.get(opKey);
                    String sbuf = opKey + " ";
                    for (String dscKey : mDsc.keySet()) {
                        strA = dscKey.split("~");
                        if (strA.length == 2 && strA[0].equals("son")) {
                            sd.slst.add(sbuf + mDsc.get(dscKey));
                        }
                    }
                }

                lsel1.selDataLs.add(sd);
                lsel1.create();
                lsel1.sort = 0;
                lsel1.setVisible(true);
                break;
            case 6://test commands
                Myjs mj;
                sel1 = new Select(null, true);
                sel1.title_str = "Test Commands";
                sel1.xc = 2;
                sel1.yc = 10;
                sel1.winW = 1600;
                sel1.align = 0;
                sel1.dispNo = 1;

                sel1.selstr.add("newJidClass");
                sel1.selstr.add("newGroupModel");
                sel1.selstr.add("Web: newGroup");
                sel1.selstr.add("Device: newData");
                sel1.selstr.add("Web: setData");
                sel1.selstr.add("Web: getData");
                sel1.selstr.add("Device: setData");
                sel1.selstr.add("Device: getData");
                sel1.selstr.add("Web: getRecord");

                sel1.create();
                sel1.setVisible(true);
                if (Select.ret_f == 0) {
                    break;
                }
                switch (sel1.reti) {
                    case 0:
                        test_newJidClass();
                        break;
                    case 1:
                        test_newGroupModel();
                        break;
                    case 2:
                        test_web_newGroup();
                        break;
                    case 3:
                        test_device_newData();
                        break;
                    case 4:
                        test_web_setData();
                        break;
                    case 5:
                        test_web_getData();
                        break;
                    case 6:
                        test_device_setData();
                        break;
                    case 7:
                        test_device_getData();
                        break;
                    case 8:
                        test_web_getRecord();
                        break;

                }
                break;
            case 7:
                selectIdType();
                break;
            case 8:
                testInx++;
                break;
            case 9:
                if (testInx > 0) {
                    testInx--;
                }
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

        }

    }

    void test_web_newGroup() {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.webTxCmdNo++);
        mj.addKey("commands");
        mj.stArr();
        //=====================================
        mj.stObj();
        mj.addKeyVstr("act", "newGroup");
        mj.addKeyVstr("gid", "Group1");
        mj.addKeyVstr("sname", "Group-1");
        mj.addKeyVstr("target", "J100F0000000000");
        //===============================
        mj.addKey("truckLs~trucks");
        mj.stArr();
        mj.addArrStr("J403F0000000000");
        mj.addArrStr("J403F0000000001");
        mj.addArrStr("J403F0000000002");
        mj.addArrStr("J403F0000000003");
        mj.addArrStr("J403F0000000004");
        mj.addArrStr("J403F0000000005");
        mj.addArrStr("J403F0000000006");
        mj.addArrStr("J403F0000000007");
        mj.end();
        //===============================
        mj.end();
        //=====================================

        //=====================================
        mj.stObj();
        mj.addKeyVstr("act", "newGroup");
        mj.addKeyVstr("gid", "Group2");
        mj.addKeyVstr("sname", "Group-2");
        mj.addKeyVstr("target", "J100F0000000001");

        mj.addKey("truckLs~trucks");
        mj.stArr();
        mj.addArrStr("J403F0000000008");
        mj.addArrStr("J403F0000000009");
        mj.addArrStr("J403F0000000010");
        mj.addArrStr("J403F0000000011");
        mj.addArrStr("J403F0000000012");
        mj.addArrStr("J403F0000000013");
        mj.addArrStr("J403F0000000014");
        mj.addArrStr("J403F0000000015");
        mj.end();

        mj.end();

        mj.end();//command end
        mj.end();//json end
        //mj.anaJs(mj.jstr);
        mcla.webMqttPub("mqttAgentRX/" + GB.webJid, mj.jstr);

    }

    void test_newGroupModel() {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.deviceTxCmdNo++);
        mj.addKeyVstr("act", "newGroupModel");
        mj.addKeyVstr("password", "16020039");//
        mj.addKeyVstr("target", "groupModelName");//
        mj.addKey("value");
        mj.stObj();
        mj.addKeyVstr("className", "RelayJobj");//
        mj.addKeyVstr("sname", "RelayJobj-showName");//
        mj.addKeyVstr("jidHead", "J402");//
        mj.addKeyVstr("son~0", "TyreJobj~tyres~J401");
        mj.end();
        mj.end();
        mcla.deviceMqttPub("mqttAgentRX/" + GB.deviceJid, mj.jstr);

    }

    void test_newJidClass() {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.deviceTxCmdNo++);
        mj.addKeyVstr("act", "newJidClass");
        mj.addKeyVstr("password", "16020039");//
        String className = "JidClassName";
        mj.addKeyVstr("target", className);//
        mj.addKey("value");
        mj.stObj();
        mj.addKeyVstr("testData1", "S~ABC1234");//
        mj.addKeyVstr("testData2", "S~R~1234DEF");//
        mj.addKeyVstr("jidArrayList~tyres", "TyreJobj");

        mj.end();
        mj.end();
        mcla.deviceMqttPub("mqttAgentRX/" + GB.deviceJid, mj.jstr);

    }

    void test_device_newData() {
        mcla.deviceMqttPub("mqttAgentRX/" + GB.deviceJid, test_new_truck_mj("J403F0000000000").jstr);
        mcla.deviceMqttPub("mqttAgentRX/" + GB.deviceJid, test_new_relay_mj("J402F0000000000", 0).jstr);
        mcla.deviceMqttPub("mqttAgentRX/" + GB.deviceJid, test_new_relay_mj("J402F0000000001", 4).jstr);
        mcla.deviceMqttPub("mqttAgentRX/" + GB.deviceJid, test_new_tyre_mj("J401F0000000000", 0,8).jstr);
    }

    Myjs test_new_truck_mj(String jid) {
        String str;
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.deviceTxCmdNo++);
        mj.addKeyVstr("act", "newData");
        str = "{";
        str += "'flags':0x00000000";
        str += ",'gpsData':'this is gpsData'";
        str += ",'truckType':'this is truckType'";
        str += ",'relays#0':'J402F0000000000'";
        str += ",'relays#1':'J402F0000000001'";
        str += "}";
        mj.addKeyVstr("type", "truckLs");//
        mj.addKeyVstr("target", jid);//
        mj.addKeyVobj("value", str);
        mj.end();
        return mj;
    }

    Myjs test_new_relay_mj(String jid, int index) {
        String str;
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.deviceTxCmdNo++);
        mj.addKeyVstr("act", "newData");
        str = "{";
        str += "'flags':0x00000000";
        str += ",'id':0x00000000";
        str += ",'tyreLayout':0x81808100";
        str += ",'lowPressureSet':0x1a1a1a1a";
        str += ",'highPressureSet':0x2d2d2d2d";
        str += ",'highTemperatureSet':0x46464646";
        str += ",'tyres#0':'J401F000000000" + (index + 0) + "'";
        str += ",'tyres#1':'J401F000000000" + (index + 1) + "'";
        str += ",'tyres#2':'J401F000000000" + (index + 2) + "'";
        str += ",'tyres#3':'J401F000000000" + (index + 3) + "'";
        str += "}";
        mj.addKeyVstr("type", "relayLs");//
        mj.addKeyVstr("target", jid);//
        mj.addKeyVobj("value", str);
        mj.end();
        return mj;
    }

    Myjs test_new_tyre_mj(String jid, int index, int size) {
        String str;
        int addValue=index;
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.deviceTxCmdNo++);
        mj.addKey("commands");
        mj.stArr();
        //=====================================
        for (int i = 0; i < size; i++) {
            mj.stObj();
            mj.addKeyVstr("act", "newData");
            mj.addKeyVstr("type", "tyreLs");//
            mj.addKeyVstr("target", JidObj.addInt2Jid(jid, addValue, 2));//
            str = "{";
            str += "'id':0x0000000"+i;
            str += ",'tpmsData':0x81808100";
            str += "}";
            mj.addKeyVobj("value", str);
            //========
            /*
            mj.addKey("truckLs~trucks");
            mj.stArr();
            mj.addArrStr("J403F0000000000");
            mj.addArrStr("J403F0000000000");
            mj.end();
             */
            //========
            mj.end();
            addValue++;
        }

        //=====================================
        mj.end();//command end
        mj.end();//json end
        //mj.anaJs(mj.jstr);
        return mj;

    }

    
    void test_web_getRecord() {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.webTxCmdNo++);
        mj.addKeyVstr("act", "getRecord");
        if (testInx == 0) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.tyres#0.tpmsData");
        }
        if (testInx == 1) {
            mj.addKeyVstr("type", "L3");
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.tyres#0.tpmsData");
        }
        if (testInx == 2) {
            mj.addKeyVstr("type", "S3L2");
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.tyres#0.tpmsData");
        }
        if (testInx == 3) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.tyres#0.testString");
        }
        if (testInx == 4) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.tyres#0");
        }
        mj.end();
        mcla.webMqttPub("mqttAgentRX/" + GB.webJid, mj.jstr);
    }
    
    

    void test_web_getData() {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.webTxCmdNo++);
        mj.addKeyVstr("act", "getData");
        mj.addKeyVstr("type", "A00");
        if (testInx == 0) {
            mj.addKeyVstr("target", "self");
        }
        if (testInx == 1) {
            mj.addKeyVstr("target", "J010F0000000000");
        }
        if (testInx == 2) {
            mj.addKeyVstr("target", "sname");
        }
        if (testInx == 3) {
            mj.addKeyVstr("target", "groups#0");
        }
        if (testInx == 4) {
            mj.addKeyVstr("target", "groups#0.trucks#0");
        }
        if (testInx == 5) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0");
        }
        if (testInx == 6) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.sname");
        }
        if (testInx == 7) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#1.sname");
        }
        if (testInx == 8) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.tyres#0.id");
        }
        if (testInx == 9) {
            mj.addKeyVstr("target", "groups#0.trucks#0.classDsc");
        }
        if (testInx == 10) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.tyres#0");
        }
        mj.end();
        mcla.webMqttPub("mqttAgentRX/" + GB.webJid, mj.jstr);
    }

    void test_web_setData() {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.webTxCmdNo++);
        mj.addKeyVstr("act", "setData");
        if (testInx == 0) {
            mj.addKeyVstr("target", "self");
            mj.addKeyVobj("value", "{'sname':'testString " + testCnt + "'}");
        }
        if (testInx == 1) {
            mj.addKeyVstr("target", "J010F0000000000");
            mj.addKeyVobj("value", "{'sname':'testString " + testCnt + "'}");
        }
        if (testInx == 2) {
            mj.addKeyVstr("target", "sname");
            mj.addKeyVobj("value", "'testString " + testCnt + "'");
        }
        if (testInx == 3) {
            mj.addKeyVstr("target", "groups#0");
            mj.addKeyVobj("value", "{'sname':'testString " + testCnt + "'}");
        }
        if (testInx == 4) {
            mj.addKeyVstr("target", "groups#0.trucks#0");
            mj.addKeyVobj("value", "{'sname':'testString " + testCnt + "'}");
        }
        if (testInx == 5) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0");
            mj.addKeyVobj("value", "{'sname':'testString " + testCnt + "'}");
        }
        if (testInx == 6) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.sname");
            mj.addKeyVobj("value", "'testString " + testCnt + "'");
        }
        if (testInx == 7) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#1.sname");
            mj.addKeyVobj("value", "'testString " + testCnt + "'");
        }
        if (testInx == 8) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.tyres#0.id");
            mj.addKeyVobj("value", "'" + testCnt + "'");
        }
        if (testInx == 9) {
            mj.addKeyVstr("target", "groups#0.trucks#0.classDsc");
            mj.addKeyVobj("value", "'testString " + testCnt + "'");
        }
        if (testInx == 10) {
            mj.addKeyVstr("target", "groups#0.trucks#0.relays#0.tyres#0");
            mj.addKeyVobj("value", "{'tpmsData':0x12345678,'testString':'xyz'}");
        }
        testCnt++;
        mj.end();
        mcla.webMqttPub("mqttAgentRX/" + GB.webJid, mj.jstr);
    }
    
    
    
    
    void test_device_setData() {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.deviceTxCmdNo++);
        mj.addKeyVstr("act", "setData");
        if (testInx == 0) {
            mj.addKeyVstr("target", "self");
            mj.addKeyVobj("value", "{'sname':'testString " + testCnt + "'}");
        }
        if (testInx == 1) {
            mj.addKeyVstr("target", "J403F0000000000");
            mj.addKeyVobj("value", "{'sname':'testString " + testCnt + "'}");
        }
        if (testInx == 2) {
            mj.addKeyVstr("target", "sname");
            mj.addKeyVobj("value", "'testString " + testCnt + "'");
        }
        if (testInx == 3) {
            mj.addKeyVstr("target", "relays#0");
            mj.addKeyVobj("value", "{'sname':'testString " + testCnt + "'}");
        }
        if (testInx == 4) {
            mj.addKeyVstr("target", "relays#0.sname");
            mj.addKeyVobj("value", "'testString " + testCnt + "'");
        }
        if (testInx == 5) {
            mj.addKeyVstr("target", "relays#1.sname");
            mj.addKeyVobj("value", "'testString " + testCnt + "'");
        }
        if (testInx == 6) {
            mj.addKeyVstr("target", "relays#0.tyres#0.id");
            mj.addKeyVobj("value", "'" + testCnt + "'");
        }
        if (testInx == 7) {
            mj.addKeyVstr("target", "relays#0.classDsc");
            mj.addKeyVobj("value", "'" + testCnt + "'");
        }
        if (testInx == 8) {
            mj.addKeyVstr("target", "relays#0.tyres#0.tpmsData");
            mj.addKeyVobj("value", "" + 0x12345678 + "");
        }
        if (testInx == 9) {
            mj.addKeyVstr("target", "relays#0.tyres#0.testString");
            mj.addKeyVobj("value", "\"" + "abcd efg" + "\"");
        }
        
        
        testCnt++;
        mj.end();
        mcla.deviceMqttPub("mqttAgentRX/" + GB.deviceJid, mj.jstr);
    }

    void test_device_getData() {
        Myjs mj = new Myjs();
        mj.stObj();
        mj.addKeyVobj("cmdNo", mcla.deviceTxCmdNo++);
        mj.addKeyVstr("act", "getData");
        mj.addKeyVstr("type", "A00");
        if (testInx == 0) {
            mj.addKeyVstr("target", "self");
        }
        if (testInx == 1) {
            mj.addKeyVstr("target", "J403F0000000000");
        }
        if (testInx == 2) {
            mj.addKeyVstr("target", "sname");
        }
        if (testInx == 3) {
            mj.addKeyVstr("target", "relays#0");
        }
        if (testInx == 4) {
            mj.addKeyVstr("target", "relays#0.sname");
        }
        if (testInx == 5) {
            mj.addKeyVstr("target", "relays#1.sname");
        }
        if (testInx == 6) {
            mj.addKeyVstr("target", "relays#0.tyres#0.id");
        }
        if (testInx == 7) {
            mj.addKeyVstr("target", "relays#0.classDsc");
        }
        if (testInx == 8) {
            mj.addKeyVstr("target", "relays#0.tyres#0.tpmsData");
        }
        if (testInx == 9) {
            mj.addKeyVstr("target", "relays#0.tyres#0.testString");
        }
        mj.end();
        mcla.deviceMqttPub("mqttAgentRX/" + GB.deviceJid, mj.jstr);
    }

}

class ttt {

    int uu;

}
