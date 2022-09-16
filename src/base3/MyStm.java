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
public class MyStm {

    int inx;
    int spcChar_f;
    byte[] rdata = new byte[4096];
    byte[] rbuf = new byte[4096];
    byte[] tdata = new byte[4096];
    byte[] tbuf = new byte[4096];
    int rxin_f;
    int rbuf_byte;
    int tbuf_byte;
    int rxlen;
    int rxInx;
    int txlen;

    public MyStm() {

    }

    void dec_mystm(byte indata) {
        rbuf_byte = 1;
        rbuf[0] = indata;
        dec_mystm();
    }

    void dec_mystm() {
        int i, j, k;
        int len;
        int chksum0, chksum1;
        for (i = 0; i < rbuf_byte; i++) {
            if (rbuf[i] == (byte) 0xEA) {
                inx = 0;
                spcChar_f = 0;
                continue;
            }
            if (rbuf[i] == (byte) 0xEC) {
                spcChar_f = 1;
                continue;
            }
            if (rbuf[i] != (byte) 0xEB) {
                if (inx < rdata.length) {
                    if (spcChar_f == 1) {
                        rdata[inx] = (byte) (rbuf[i] ^ 0xAB);
                    } else {
                        rdata[inx] = rbuf[i];
                    }
                    spcChar_f = 0;
                    inx++;
                    if (inx >= 500) {
                        spcChar_f = 0;
                    }
                }
                continue;
            }
            spcChar_f = 0;
            
                
            len = rdata[0];
            if(len>100){
                len+=0;
            }
            if (rdata[0] == 0) {
                len = inx - 3;
            }
            k = 1;
            if (rdata[0] == (byte) 0xf0) {
                len = inx - 2;
                k = 0;
            }
            chksum0 = 0xab;
            chksum1 = 0;
            for (j = 0; j < len; j++) {
                chksum0 ^= rdata[j + k];
                chksum1 += rdata[j + k];
            }
            if (((chksum0 ^ rdata[j + k]) & 0xff) != 0) {
                continue;
            }
            j++;
            if (((chksum1 ^ rdata[j + k]) & 0xff) != 0) {
                continue;
            }
            rxlen = len;
            rxInx = k;
            rxin_f = 1;
            //mstp->fptr();
        }
    }

    void encmst(byte uch, int enc) {
        if (enc != 0) {
            if (uch == (byte) 0xEA || uch == (byte) 0xEB || uch == (byte) 0xEC) {
                tdata[txlen++] = (byte) 0xEC;
                tdata[txlen++] = (byte) (uch ^ (byte) 0xAB);
                return;
            }
            tdata[txlen++] = uch;
            return;
        }
        tdata[txlen++] = uch;
    }

    void enc_mystm() {
        int i, j;
        int len;
        int chksum0, chksum1;
        txlen = 0;
        encmst((byte) 0xEA, 0);
        encmst((byte) tbuf_byte, 1);
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
    
    

}
