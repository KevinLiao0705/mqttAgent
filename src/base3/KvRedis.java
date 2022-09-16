/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base3;

import static base3.KvRedis.thd;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 *
 * @author Administrator
 */
public class KvRedis {

    //public static String host = "localhost";
    //public static int port = 6379;
    //=====================================
    //public static String host = "103.44.220.55";
    //public static int port = 19346;
    //public static String passw = "1234";
    //=======================================
    public static String host = "118.163.89.29";
    public static String passw = "";
    public static int port = 16479;
    public static int libIndex = 1;
    //=======================================
    public static int timeout = 3000;//msec
    public static String actStr;
    public static String errStr;
    public static String actAllStr;
    public static long actLine;
    public static long actAllLine = 0;
    public static String valueStr;
    public static int errCode;
    public static Set<String> strSet;
    public static List<String> strList;
    public static Map<String, String> map;
    public static Jedis jedis;
    public static Pipeline pipe;
    public static AnaResponse anaResponse;

    public static ArrayList<Map<String, Response<Map<String, String>>>> rspMapLs = new ArrayList();
    public static ArrayList<Map<String, Response<Set<String>>>> rspSetLs = new ArrayList();
    public static ArrayList<Map<String, Response<List<String>>>> rspListLs = new ArrayList();
    public static ArrayList<Map<String, Response<Long>>> rspLongLs = new ArrayList();
    public static ArrayList<Map<String, Response<String>>> rspStringLs = new ArrayList();
    public static Set<String> rspSet;
    public static List<String> rspList;
    public static Map<String, String> rspMap;
    public static Long rspLong;
    public static String rspString;

    public static Thread thd;
    public static int thd_run_f = 0;
    public static String op;
    public static String[] strA;
    public static Object objB;

    public static String anaClassName;
    public static String anaOperate;
    public static String anaJsobjName;
    public static Object anaObject;

    //=========================================================================
    static public boolean pipeAct(String op, String[] strA, Object objB) {
        String rspKey;
        Set<String> setListTmp;
        Iterator<String> it;
        Map<String, Response<String>> mapString;
        Map<String, Response<Long>> mapLong;
        Map<String, Response<Set<String>>> mapSet;
        Map<String, Response<List<String>>> mapList;
        Map<String, Response<Map<String, String>>> mapMap;
        try {
            switch (op) {
                case "connect":
                    GB.redisServerStatus = "";
                    KvRedis.errCode = 0;
                    KvRedis.errStr = null;
                    KvRedis.actStr = null;
                    KvRedis.valueStr = null;

                    KvRedis.rspStringLs.clear();
                    KvRedis.rspLongLs.clear();
                    KvRedis.rspSetLs.clear();
                    KvRedis.rspMapLs.clear();

                    KvRedis.jedis = new Jedis(KvRedis.host, KvRedis.port, KvRedis.timeout);
                    if (KvRedis.passw.length() > 0) {
                        jedis.auth(passw);
                    }
                    KvRedis.pipe = KvRedis.jedis.pipelined();
                    if (KvRedis.libIndex != 0) {
                        rspKey = op + "<>" + KvRedis.libIndex;
                        mapString = new HashMap();
                        mapString.put(rspKey, KvRedis.pipe.select(KvRedis.libIndex));
                        KvRedis.rspStringLs.add(mapString);
                    }
                    break;
                case "selectDb"://test error
                    rspKey = op + "<>" + strA[0];
                    mapString = new HashMap();
                    mapString.put(rspKey, KvRedis.pipe.select(Integer.parseInt(strA[0])));
                    KvRedis.rspStringLs.add(mapString);
                    break;
                case "getKeyType"://out none,string,list,set,zset,hash,stream
                    rspKey = op + "<>" + strA[0];
                    mapString = new HashMap();
                    mapString.put(rspKey, KvRedis.pipe.type(strA[0]));
                    KvRedis.rspStringLs.add(mapString);
                    break;
                case "chkExistkeys":
                    rspKey = op + "<>" + strA[0] + "<>...";
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.exists(strA));
                    KvRedis.rspLongLs.add(mapLong);
                    break;
                case "renameKey":
                    rspKey = op + "<>" + strA[0] + "<>...";
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.renamenx(strA[0], strA[1]));
                    KvRedis.rspLongLs.add(mapLong);
                    break;
                case "delKey":
                    rspKey = op + "<>" + strA[0];
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.del(strA[0]));
                    KvRedis.rspLongLs.add(mapLong);
                    break;
                case "delKeysFilter":
                    setListTmp = KvRedis.jedis.keys(strA[0]);
                    it = setListTmp.iterator();
                    String getKey;
                    while (it.hasNext()) {
                        getKey = it.next();
                        if (Lib.compareString(getKey, strA[0]) == 1) {
                            rspKey = op + "<>" + strA[0];
                            mapLong = new HashMap();
                            mapLong.put(rspKey, KvRedis.pipe.del(getKey));
                            KvRedis.rspLongLs.add(mapLong);
                        }
                    }
                    break;
                case "delKeys":
                    rspKey = op + "<>" + strA[0] + "<>...";
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.del(strA));
                    KvRedis.rspLongLs.add(mapLong);
                    break;
                case "getKeys"://can use '*' to present all char
                    rspKey = op + "<>" + strA[0];
                    mapSet = new HashMap();
                    mapSet.put(rspKey, KvRedis.pipe.keys(strA[0]));
                    KvRedis.rspSetLs.add(mapSet);
                    break;
                case "getKeySize":
                    rspKey = op;
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.dbSize());
                    KvRedis.rspLongLs.add(mapLong);
                    break;
                case "setKeyValue":
                    rspKey = op + "<>" + strA[0] + "<>...";
                    mapString = new HashMap();
                    mapString.put(rspKey, KvRedis.pipe.set(strA[0], strA[1]));
                    KvRedis.rspStringLs.add(mapString);
                    break;
                case "getKeyValue":
                    rspKey = op + "<>" + strA[0];
                    mapString = new HashMap();
                    mapString.put(rspKey, KvRedis.pipe.get(strA[0]));
                    KvRedis.rspStringLs.add(mapString);
                    break;
                case "setHashKeyValue":
                    rspKey = op + "<>" + strA[0] + "<>" + strA[1];
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.hset(strA[0], strA[1], strA[2]));
                    KvRedis.rspLongLs.add(mapLong);
                    break;
                case "setHashAll":
                    Map<String, String> map = (Map<String, String>) objB;
                    rspKey = op + "<>" + strA[0];
                    mapString = new HashMap();
                    mapString.put(rspKey, KvRedis.pipe.hmset(strA[0], map));
                    KvRedis.rspStringLs.add(mapString);
                    break;
                case "getHashKeyValue":
                    rspKey = op + "<>" + strA[0] + op + "<>" + strA[1];
                    mapString = new HashMap();
                    mapString.put(rspKey, KvRedis.pipe.hget(strA[0], strA[1]));
                    KvRedis.rspStringLs.add(mapString);
                    break;
                case "getHashAll":
                    rspKey = op + "<>" + strA[0];
                    mapMap = new HashMap();
                    mapMap.put(rspKey, KvRedis.pipe.hgetAll(strA[0]));
                    KvRedis.rspMapLs.add(mapMap);
                    break;
                case "delHashAll":
                    rspKey = op + "<>" + strA[0];
                    mapSet = new HashMap();
                    mapSet.put(rspKey, KvRedis.pipe.hkeys(strA[0]));
                    KvRedis.rspSetLs.add(mapSet);
                    break;
                case "delHashItem":
                    rspKey = op + "<>" + strA[0] + "<>" + strA[1];
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.hdel(strA[0], strA[1]));
                    KvRedis.rspLongLs.add(mapLong);
                    break;
                case "getHashItemNames":
                    rspKey = op + "<>" + strA[0];
                    mapSet = new HashMap();
                    mapSet.put(rspKey, KvRedis.pipe.hkeys(strA[0]));
                    KvRedis.rspSetLs.add(mapSet);
                    break;
                case "getHashItemNamesFilter":
                    rspKey = op + "<>" + strA[0];
                    mapSet = new HashMap();
                    mapSet.put(rspKey, KvRedis.pipe.hkeys(strA[0]));
                    KvRedis.rspSetLs.add(mapSet);
                    break;
                case "setSet":
                    KvRedis.pipe.del(strA[0]);
                    rspKey = op + "<>" + strA[0];
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.sadd(strA[0], (String[]) objB));
                    KvRedis.rspLongLs.add(mapLong);
                    break;
                case "addSet":
                    rspKey = op + "<>" + strA[0];
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.sadd(strA[0], (String[]) objB));
                    KvRedis.rspLongLs.add(mapLong);
                    break;
                case "delSet":
                    rspKey = op + "<>" + strA[0];
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.srem(strA[0], (String[]) objB));
                    KvRedis.rspLongLs.add(mapLong);
                    break;
                case "getSet":
                    rspKey = op + "<>" + strA[0];
                    mapSet = new HashMap();
                    mapSet.put(rspKey, KvRedis.pipe.smembers(strA[0]));
                    KvRedis.rspSetLs.add(mapSet);
                    break;
                case "trimList":
                    long[] ia = (long[]) objB;
                    rspKey = op + "<>" + strA[0];
                    mapString = new HashMap();
                    mapString.put(rspKey, KvRedis.pipe.ltrim(strA[0], ia[0], ia[1]));
                    KvRedis.rspStringLs.add(mapString);
                    break;
                case "pushList":
                    rspKey = op + "<>" + strA[0];
                    mapLong = new HashMap();
                    mapLong.put(rspKey, KvRedis.pipe.lpush(strA[0], (String[]) objB));
                    KvRedis.rspLongLs.add(mapLong);
                    break;

                case "popList":
                    rspKey = op + "<>" + strA[0];
                    mapString = new HashMap();
                    mapString.put(rspKey, KvRedis.pipe.lpop(strA[0]));
                    KvRedis.rspStringLs.add(mapString);
                    break;

                case "getList":
                    rspKey = op + "<>" + strA[0];
                    mapList = new HashMap();
                    mapList.put(rspKey, KvRedis.pipe.lrange(strA[0], Integer.parseInt(strA[1]), Integer.parseInt(strA[2])));
                    KvRedis.rspListLs.add(mapList);
                    break;

                case "execute":
                    KvRedis.pipe.sync();
                    KvRedis.jedis.disconnect();
                    KvRedis.jedis = null;
                    KvRedis.pipe.close();
                    break;
            }
        } catch (Exception ex) {
            KvRedis.actStr = ex.getMessage();
            KvRedis.errCode = 1;
            GB.redisServerStatus = "redis act: " + op + " Error!!!";
            return false;
        }
        return true;
    }

    static public void actOneTh(String op, String[] strA, Object objB) {
        KvRedis.op = op;
        KvRedis.strA = strA;
        KvRedis.objB = objB;
        if (KvRedis.thd_run_f == 1) {
            KvRedis.ana("processBusy", "");
            return;
        }
        KvRedis.thd = new Thread(new Runnable() {
            public void run() {
                KvRedis.thd_run_f = 1;
                KvRedis.actOne(KvRedis.op, KvRedis.strA, KvRedis.objB);
                //KvRedis.ansResponse.ana("processDone", "");
                KvRedis.ana("processDone", "");
                KvRedis.thd_run_f = 0;
            }
        });
        thd.start();
    }

    static public void pipeActTh(String op, String[] strA, Object objB) {
        KvRedis.op = op;
        KvRedis.strA = strA;
        KvRedis.objB = objB;
        if (KvRedis.thd_run_f == 1) {
            KvRedis.ana("processBusy", "");
            return;
        }
        KvRedis.thd = new Thread(new Runnable() {
            public void run() {
                KvRedis.thd_run_f = 1;
                KvRedis.pipeAct(KvRedis.op, KvRedis.strA, KvRedis.objB);
                if (KvRedis.errCode != 0) {
                    KvRedis.ana("processError", "");
                } else {
                    KvRedis.ana("processDone", "");
                }
                KvRedis.thd_run_f = 0;
            }
        });
        thd.start();
    }

    static public boolean actOne(String op, String[] strA, Object objB) {
        KvRedis.act("connect", null, null);
        if (KvRedis.errCode != 0) {
            return false;
        }
        KvRedis.act(op, strA, objB);
        KvRedis.act("disConnect", null, null);
        if (KvRedis.errCode != 0) {
            return false;
        }
        return true;
    }

    static public boolean act(String op, String[] strA, Object objB) {
        Iterator<String> it;
        Set<String> setListTmp;
        String getKey;
        String sbuf;
        String rspKey;
        try {
            KvRedis.actLine = 0;
            if (!op.equals("connect")) {
                if (KvRedis.jedis == null) {
                    KvRedis.actStr = "Must Connect First!!!";
                    KvRedis.errCode = 3;
                    return false;
                }
            }
            switch (op) {
                case "connect":
                    KvRedis.errCode = 0;
                    KvRedis.actStr = null;
                    KvRedis.valueStr = null;
                    KvRedis.jedis = new Jedis(KvRedis.host, KvRedis.port, KvRedis.timeout);
                    if (KvRedis.passw.length() > 0) {
                        KvRedis.jedis.auth(passw);
                    }
                    actStr = KvRedis.jedis.select(KvRedis.libIndex);
                    if (!actStr.equals("OK")) {
                        KvRedis.errCode = 3;
                        KvRedis.errStr = "Set Database Lib Error !!!";
                    }
                    break;
                case "disConnect":
                    KvRedis.jedis.disconnect();
                    KvRedis.jedis = null;
                    break;
                case "selectDb"://test error
                    KvRedis.actStr = KvRedis.jedis.select(Integer.parseInt(strA[0]));
                    break;
                case "getKeyType"://out none,string,list,set,zset,hash,stream
                    KvRedis.actStr = KvRedis.jedis.type(strA[0]);
                    break;
                case "chkExistkeys":
                    KvRedis.actLine = KvRedis.jedis.exists(strA);
                    KvRedis.actAllLine += KvRedis.actLine;
                    if (KvRedis.actLine != strA.length) {
                        KvRedis.errCode = 3;
                        KvRedis.errStr = "Some Redis Key Is Not Exist !!!";
                    }
                    break;
                case "renameKey":
                    KvRedis.actLine = KvRedis.jedis.renamenx(strA[0], strA[1]);
                    KvRedis.actAllLine += KvRedis.actLine;
                    if (KvRedis.actLine == 0) {
                        KvRedis.errCode = 3;
                        KvRedis.errStr = "The New Key Name Exist In The Database !!!";
                    }
                    break;
                case "delKey":
                    KvRedis.actLine = KvRedis.jedis.del(strA[0]);
                    KvRedis.actAllLine += KvRedis.actLine;
                    if (KvRedis.actLine == 0) {
                        KvRedis.errCode = 3;
                        KvRedis.errStr = "The Del KeyName Does Not Exist !!!";
                    }
                    break;
                case "delKeys":
                    KvRedis.actLine = KvRedis.jedis.del(strA);
                    KvRedis.actAllLine += KvRedis.actLine;
                    if (KvRedis.actLine != strA.length) {
                        KvRedis.errCode = 3;
                        KvRedis.errStr = "Some Del KeyNames Does Not Exist !!!";
                    }
                    break;
                case "delKeysFilter":
                    setListTmp = KvRedis.jedis.keys(strA[0]);
                    it = setListTmp.iterator();
                    ArrayList<String> keys = new ArrayList();
                    while (it.hasNext()) {
                        getKey = it.next();
                        if (Lib.compareString(getKey, strA[0]) == 1) {
                            keys.add(getKey);

                            /*
                            KvRedis.actLine = KvRedis.jedis.del(getKey.split("<~>"));
                            KvRedis.actAllLine += KvRedis.actLine;
                            if(KvRedis.actLine==0){
                                KvRedis.errCode = 3;
                                KvRedis.errStr="Sone Del KeyNames Does Not Exist !!!";
                            } 
                             */
                        }
                    }
                    String[] delKeys = Arrays.asList(keys.toArray()).toArray(new String[0]);
                    KvRedis.actLine = 0;
                    if (delKeys.length != 0) {
                        KvRedis.actLine = KvRedis.jedis.del(delKeys);
                        KvRedis.actAllLine += KvRedis.actLine;
                        if (KvRedis.actLine != delKeys.length) {
                            KvRedis.errCode = 3;
                            KvRedis.errStr = "Some Del KeyNames Does Not Exist !!!";
                        }
                    }
                    KvRedis.actStr = op + " OK, " + KvRedis.actLine + " lines.";
                    break;
                case "getKeys"://can use '*' to present all char
                    KvRedis.strSet = KvRedis.jedis.keys(strA[0]);
                    break;
                case "getKeySize"://can use '*' to present all char
                    KvRedis.actLine = KvRedis.jedis.dbSize();
                    KvRedis.actAllLine += KvRedis.actLine;
                    break;
                case "setKeyValue":
                    KvRedis.actStr = KvRedis.jedis.set(strA[0], strA[1]);
                    break;
                case "getKeyValue":
                    KvRedis.valueStr = KvRedis.jedis.get(strA[0]);
                    break;
                case "setHashKeyValue":
                    KvRedis.actLine = KvRedis.jedis.hset(strA[0], strA[1], strA[2]);
                    KvRedis.actAllLine += KvRedis.actLine;
                    break;
                case "setHashAll":
                    Map<String, String> map = (Map<String, String>) objB;
                    KvRedis.actStr = KvRedis.jedis.hmset(strA[0], map);
                    break;
                case "getHashKeyValue":
                    KvRedis.valueStr = KvRedis.jedis.hget(strA[0], strA[1]);
                    break;
                case "getHashAll":
                    KvRedis.map = KvRedis.jedis.hgetAll(strA[0]);
                    if (KvRedis.map == null) {
                        KvRedis.actStr = "Cannot get this key";
                        KvRedis.errCode = 2;
                    }
                    break;
                case "delHashAll":
                    KvRedis.strSet = KvRedis.jedis.hkeys(strA[0]);
                    it = KvRedis.strSet.iterator();
                    while (it.hasNext()) {
                        sbuf = it.next();
                        KvRedis.jedis.hdel(strA[0], sbuf);
                    }
                    break;
                case "delHashItem":
                    KvRedis.actLine = KvRedis.jedis.hdel(strA[0], strA[1]);
                    KvRedis.actAllLine += KvRedis.actLine;
                    break;
                case "getHashItemNames":
                    KvRedis.strSet = KvRedis.jedis.hkeys(strA[0]);
                    break;
                case "getHashItemNamesFilter":
                    setListTmp = KvRedis.jedis.hkeys(strA[0]);
                    it = setListTmp.iterator();
                    KvRedis.strSet = new HashSet<String>();
                    while (it.hasNext()) {
                        getKey = it.next();
                        if (Lib.compareString(getKey, strA[0]) == 1) {
                            KvRedis.strSet.add(getKey);
                        }
                    }
                    break;
                case "setSet":
                    KvRedis.jedis.del(strA[0]);
                    KvRedis.actLine += KvRedis.jedis.sadd(strA[0], (String[]) objB);
                    KvRedis.actAllLine += KvRedis.actLine;
                    KvRedis.actStr = op + " OK, " + KvRedis.actLine + " members.";
                    KvRedis.actAllStr = op + " OK, " + KvRedis.actAllLine + " members.";
                    break;
                case "addSet":
                    KvRedis.actLine += KvRedis.jedis.sadd(strA[0], (String[]) objB);
                    KvRedis.actAllLine += KvRedis.actLine;
                    KvRedis.actStr = op + " OK, " + KvRedis.actLine + " members.";
                    KvRedis.actAllStr = op + " OK, " + KvRedis.actAllLine + " members.";
                    break;
                case "delSet":
                    KvRedis.actLine += KvRedis.jedis.srem(strA[0], (String[]) objB);
                    KvRedis.actAllLine += KvRedis.actLine;
                    KvRedis.actStr = op + " OK, " + KvRedis.actLine + " members.";
                    KvRedis.actAllStr = op + " OK, " + KvRedis.actAllLine + " members.";
                    break;
                case "getSet":
                    KvRedis.strSet = KvRedis.jedis.smembers(strA[0]);
                    break;
                case "trimList":
                    long[] ia = (long[]) objB;
                    KvRedis.actStr = KvRedis.jedis.ltrim(strA[0], ia[0], ia[1]);
                    break;
                case "pushList":
                    KvRedis.actLine += KvRedis.jedis.lpush(strA[0], (String[]) objB);
                    KvRedis.actAllLine += KvRedis.actLine;
                    break;
                case "popList":
                    KvRedis.valueStr += KvRedis.jedis.lpop(strA[0]);
                    break;
                case "getList":
                    KvRedis.strList = KvRedis.jedis.lrange(strA[0], Integer.parseInt(strA[1]), Integer.parseInt(strA[2]));
                    break;

                default:
                    KvRedis.actStr = "Redis No This Command";
                    KvRedis.errCode = 2;
                    break;
            }

            return KvRedis.errCode == 0;
        } catch (Exception e) {
            KvRedis.actStr = e.getMessage();
            KvRedis.errCode = 1;
            GB.redisServerStatus = "redis act: " + op + " Error!!!";
            return false;
        }

    }

    //连接本地的 Redis 服务
    
    
    static public boolean testConnect() {
        try {
            KvRedis.jedis = new Jedis(KvRedis.host, KvRedis.port, KvRedis.timeout);
            if (KvRedis.passw.length() > 0) {
                KvRedis.jedis.auth(passw);
            }
            KvRedis.actStr = "Redis Connect Test Ping " + jedis.ping();
            KvRedis.jedis.disconnect();
            KvRedis.errCode = 0;
            return true;
        } catch (Exception e) {
            KvRedis.actStr = e.getMessage();
            KvRedis.errCode = 1;
            System.err.println(e.getMessage());
            return false;
        }

    }

    static public void chkPipe() {
        Map<String, Response<Set<String>>> mapSet;
        for (int i = 0; i < KvRedis.rspSetLs.size(); i++) {
            mapSet = KvRedis.rspSetLs.get(i);
            for (int j = 0; j < mapSet.size(); j++) {
                for (String opKey : mapSet.keySet()) {
                    KvRedis.rspSet = mapSet.get(opKey).get();
                    KvRedis.ana(opKey, "Set");
                }
            }
        }
        Map<String, Response<Map<String, String>>> mapMap;
        for (int i = 0; i < KvRedis.rspMapLs.size(); i++) {
            mapMap = KvRedis.rspMapLs.get(i);
            for (int j = 0; j < mapMap.size(); j++) {
                for (String opKey : mapMap.keySet()) {
                    KvRedis.rspMap = mapMap.get(opKey).get();
                    KvRedis.ana(opKey, "Map");
                }
            }
        }
        Map<String, Response<Long>> mapLong;
        for (int i = 0; i < KvRedis.rspLongLs.size(); i++) {
            mapLong = KvRedis.rspLongLs.get(i);
            for (int j = 0; j < mapLong.size(); j++) {
                for (String opKey : mapLong.keySet()) {
                    KvRedis.rspLong = mapLong.get(opKey).get();
                    KvRedis.ana(opKey, "Long");
                }
            }
        }

        Map<String, Response<String>> mapString;
        for (int i = 0; i < KvRedis.rspStringLs.size(); i++) {
            mapString = KvRedis.rspStringLs.get(i);
            for (int j = 0; j < mapString.size(); j++) {
                for (String opKey : mapString.keySet()) {
                    KvRedis.rspString = mapString.get(opKey).get();
                    KvRedis.ana(opKey, "String");
                }
            }
        }

    }

    static public void ana(String opKey, String type) {

        if (KvRedis.anaResponse != null) {
            KvRedis.anaResponse.ana(opKey, type);
        }

        String strA[];
        strA = opKey.split("<>");

        if (strA[0].equals("setHashKeyValue")) {
            if (KvRedis.rspLong != 0) {
                KvRedis.errStr = "Save Redis Error->" + opKey;
            }
        }

        if (KvRedis.anaClassName.equals("EmuTpmsAiot")) {
            if (strA[0].equals("processBusy")) {
                MqttAgent.scla.setStatus("Redis is Busy !!", 2);
                return;
            }
            if (strA[0].equals("processError")) {
                MqttAgent.scla.setStatus("Redis Process Error !!!", 2);
                return;
            }
            if (strA[0].equals("processDone")) {
                MqttAgent.scla.setStatus("Redis Process OK.", 1);
                switch (KvRedis.anaOperate) {
                    case "":
                        switch (opKey) {
                            case "":
                                break;
                        }
                        break;
                }
            }
            if (strA[0].equals("getHashAll")) {
                if (KvRedis.anaOperate.equals("addDataToJsobjls")) {
                    JidObj obj = EmuTpmsAiot.scla.mcla.trsRedisMapToJsobj(KvRedis.anaJsobjName, KvRedis.rspMap);
                    //JidObj obj=Lib.trsRedisMapToJsobj(KvRedis.anaJsobjName, KvRedis.rspMap);
                    if (obj != null) {
                        String jid = obj.mp.get("jid").v;
                        EmuTpmsAiot.scla.mcla.jidMap.put(jid, obj);
                        Map<String, JidObj> groupModelMap = (Map<String, JidObj>) KvRedis.anaObject;
                        groupModelMap.put(jid, obj);
                    }
                }

                if (KvRedis.anaOperate.equals("addGroupModelToMMap")) {
                    Jdsc jdsc = (Jdsc) KvRedis.anaObject;
                    String name = KvRedis.rspMap.get("name");
                    jdsc.addMdsc(name, KvRedis.rspMap);
                }

                if (KvRedis.anaOperate.equals("addJidClassToMMap")) {

                    ClassDsc cdsc = (ClassDsc) KvRedis.anaObject;
                    String[] strB = strA[1].split("~");
                    if (strB.length == 3) {
                        cdsc.mmCdsc.put(strB[2], KvRedis.rspMap);
                    }
                }

            }

        }

    }

}

abstract class AnaResponse {

    public abstract void ana(String opKey, String type);
}
/*
        Redis.anaResponse = new AnaResponse() {
            @Override
            public void ana() {
            }
        };
 */
