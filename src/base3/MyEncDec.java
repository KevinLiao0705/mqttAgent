/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base3;

/**
 *
 * @author Administrator
 */
public class MyEncDec {

    //======================
    int tdata_chksum;
    byte tdata_packCount;

    int TBUF_MAXLEN = 8192;
    byte[] tbuf = new byte[TBUF_MAXLEN];
    int TDATA_MAXLEN = 8192;
    byte[] tdata = new byte[4096];
    int tdata_inx;
    int tdata_byte;
    //======================
    int RBUF_MAXLEN = 8192;
    byte[] rbuf = new byte[RBUF_MAXLEN];
    int RDATA_MAXLEN = 8192;
    byte[] rdata = new byte[RDATA_MAXLEN];
    int rdata_start_f;
    int rdata_alt_f;
    int rdata_chksum;
    byte rdata_prech0;
    byte rdata_prech1;
    int rdata_step;
    int rdata_packCount;
    int rdata_nextPackCount;
    int rdata_inx;
    //==
    int errPackCount = -1;
    int rxin_f;
    int rxin_byte;
    byte rxin_type;
    //======================
    byte MYENC_ST = 0x01;
    byte MYENC_END = 0x04;
    byte MYENC_ALT = 0x1a;
    MyEncDecRx rxClass;

    public MyEncDec() {

    }

    public void myEncStart(byte type) {
        tdata_chksum = 0;
        //tdata_inx = 0;
        tdata[tdata_inx++] = MYENC_ST;
        myEncData(type);
        tdata_packCount++;
        myEncData(tdata_packCount);
    }

    public void myEncData(byte uch) {
        tdata_chksum += uch & 255;
        if (uch == MYENC_ST || uch == MYENC_END || uch == MYENC_ALT) {
            tdata[tdata_inx++] = MYENC_ALT;
            uch ^= 0xea;
        }
        tdata[tdata_inx++] = uch;
    }

    public void myEncData(byte[] ucha, int len) {
        byte uch;
        for (int i = 0; i < len; i++) {
            uch = ucha[i];
            tdata_chksum += uch & 255;
            if (uch == MYENC_ST || uch == MYENC_END || uch == MYENC_ALT) {
                tdata[tdata_inx++] = MYENC_ALT;
                uch ^= 0xea;
            }
            tdata[tdata_inx++] = uch;
        }
    }

    void myEncEnd() {
        int ibuf = tdata_chksum;
        myEncData((byte) ((ibuf >> 8) & 255));
        myEncData((byte) (ibuf & 255));
        tdata[tdata_inx++] = MYENC_END;
        tdata_byte = tdata_inx;
    }

    void myEncPack(byte[] inbuf, int len, byte type) {
        int i;
        myEncStart(type);
        for (i = 0; i < len; i++) {
            myEncData(inbuf[i]);
        }
        myEncEnd();
    }

    void myDec(byte[] inbuf, int len) {
        int i;
        byte chn;
        int load_f;

        for (i = 0; i < len; i++) {
            chn = inbuf[i];
            if (chn == MYENC_ST) {
                rxin_f = 0;
                rdata_alt_f = 0;
                rdata_start_f = 1;
                rdata_chksum = 0;
                rdata_prech0 = 0;
                rdata_prech1 = 0;
                rdata_step = 0;
                rdata_inx = 0;
                continue;
            }
            if (rdata_start_f == 0) {
                continue;
            }
            if (chn == MYENC_END) {
                rdata_start_f = 0;
                rdata_chksum -= rdata_prech0 & 255;
                rdata_chksum -= rdata_prech1 & 255;
                if ((rdata_chksum & 255) != (rdata_prech0 & 255)) {
                    continue;
                }
                if (((rdata_chksum >> 8) & 255) != (rdata_prech1 & 255)) {
                    continue;
                }
                //decsrx_prg();
                if (rdata_packCount != rdata_nextPackCount) {
                    if (rdata_nextPackCount == -1) {
                        errPackCount = 0;
                    } else {
                        errPackCount++;
                    }
                }
                rdata_nextPackCount = (rdata_packCount + 1) & 255;

                rxin_byte = rdata_inx - 2;
                if (rxin_byte > 0) {
                    rxin_f = 1;
                    rxClass.rxprg(this);
                }
                continue;
            }
            if (chn == 0x1a) {
                rdata_alt_f = 1;
                continue;
            }
            if (rdata_alt_f == 1) {
                chn ^= 0xea;
            }
            rdata_alt_f = 0;
            rdata_prech1 = rdata_prech0;
            rdata_prech0 = chn;
            rdata_chksum += chn & 255;

            if (rdata_step == 0) {
                rxin_type = chn;
                rdata_step++;
                rdata_inx = 0;
                continue;
            }
            if (rdata_step == 1) {
                rdata_packCount = chn & 255;
                rdata_step++;
                rdata_inx = 0;
                continue;
            }

            rdata[rdata_inx++] = (byte) chn;
            if (rdata_inx >= RDATA_MAXLEN) {
                rdata_start_f = 0;
            }

        }
    }

    int cmd2Json(String[] keyNameA, String[] keyValueA,int keyLen,char packName) {
        int i;
        String str = "";
        str += "{";
        for (i = 0; i < keyLen; i++) {
            if (i != 0) {
                str += ',';
            }
            str += "\"";
            str += keyNameA[i];
            str += "\":";
            str += keyValueA[i];
        }
        str += "}";
        myEncStart((byte) packName); //json-cmd
        myEncData(str.getBytes(),str.length());
        myEncEnd(); //uart
        return keyLen;
    }

    /*
    void enc_myPack() {
        int i, j;
        int len;
        int chksum0, chksum1;
        txlen = 0;
        encmst((byte) 0xEA, 0);
        chksum0 = 0xAB;
        chksum1 = 0;
        for (i = 0; i < tbuf_byte; i++) {
            encmst(tbuf[i], 1);
            chksum0 ^= tbuf[i];
            chksum1 += tbuf[i];
        }
        encmst((byte) (chksum0 & 255), 1);
        encmst((byte) (chksum1 & 255), 1);
        encmst((byte) 0xEB, 0);
    }
     */
}

abstract class MyEncDecRx {

    public abstract void rxprg(MyEncDec med);
}
