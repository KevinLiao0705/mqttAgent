package base3;

//import javafx.scene.layout.Region;

import javax.swing.JComponent;






public class MyLayout
{
    static public JComponent[] ctrA=new JComponent[256];
    //static public Region[] ctrA=new Region[256];
    static public int eleAmt=1;
    static public int xc=1,yc=1;
    static public int tm=2,bm=2,lm=2,rm=2;
    static public int xm=2,ym=2;
    static public int xst=0,yst=0,xend=0,yend=0;
    static public int iw=0,ih=0;
    static public int fw=0,fh=0;
    static public int ixst=0,iyst=0;
    static public int xmax=0,ymax=0;
    static public double rateW=0,rateH=0;
    static public int xcen=0,ycen=0;
    
    static public int[] ofw=new int[32]; 
    static public int[] ofh=new int[32]; 
            
    //if (rateW<0) iw=fw*rateW
    //if (rateH<0) ih=fh*rateH
    //if(iw==0) auto range iw;
    //if(ih==0) auto range ih;
    //if(iw==-1) use original iw;
    //if(ih==-1) use original ih;
     
    public static boolean gridLy() 
    {
        int i,j,k,m,n;
        int x,y;
        int wr,hr;
        double xf,yf,xrf,yrf;
        int iwbuf=0,ihbuf=0;
        int lmb,rmb,tmb,bmb,xmb,ymb;
        lmb=lm;rmb=rm;tmb=tm;bmb=bm;xmb=xm;ymb=ym;
        int ofwb=0;
        int ofhb=0;
        
        for(i=0;i<ofw.length;i++)
            ofwb+=ofw[i]&255;
        for(i=0;i<ofh.length;i++)
            ofhb+=ofh[i]&255;
        bmb+=ofhb;
        rmb+=ofwb;
        int firstInx=-1;
        for(i=0;i<ctrA.length;i++){
            if(ctrA[i]!=null){
                firstInx=i;
                break;
            }
        }
        if(firstInx<0)
            return false;
        if(fw==0)
          fw=ctrA[firstInx].getParent().getWidth();
        if(fh==0)
          fh=ctrA[firstInx].getParent().getHeight();
        
        
        
        if(eleAmt<1)
            return false;
        if(xc<1)
            return false;
        if(yc<1)
            return false;
        //================================
        if(rateW>0)
        {
            iwbuf=fw-xst-lmb-rmb;
            if(rateW>1)
                iw=(int)(rateW);
            else
                iw=(int)(iwbuf*rateW);
            if(iw>iwbuf)
                iw=iwbuf;
            iw=(iw-xm*(xc-1))/xc;
            if(iw==0) iw=1;
        }
        if(rateW<0){
            iwbuf=fw-xst-lmb-rmb;
            iw=(int)(iwbuf+(int)rateW);
            if(iw>iwbuf)
                iw=iwbuf;
            iw=(iw-xm*(xc-1))/xc;
            if(iw==0) iw=1;
            
        }
        
        
        if(rateH>0)
        {
            ihbuf=fh-yst-tmb-bmb;
            if(rateH>1)
                ih=(int)(rateH);
            else
                ih=(int)(ihbuf*rateH);
            if(ih>ihbuf)
                ih=ihbuf;
            ih=(ih-ym*(yc-1))/yc;
            if(ih==0) ih=1;
        }
        if(rateH<0){
            ihbuf=fh-yst-tmb-bmb;
            ih=(int)(ihbuf+(int)rateH);
            if(ih>ihbuf)
                ih=ihbuf;
            ih=(ih-ym*(yc-1))/yc;
            if(ih==0) ih=1;
            
        }
        
        
        
        //================================
        xrf=0;
        
        if(iw==0)
        {
            iw=(fw-xst-lmb-rmb-(xc-1)*xmb)/xc;
            wr=(fw-xst-lmb-rmb-(xc-1)*xmb)%xc;
            xrf=(double)wr/xc;
        }
        else
        {
          if(xcen==1)
          {
            lmb=(fw-xst-iw*xc-xmb*(xc-1))/2;
            rmb=lmb;
            wr=(fw-xst-lmb-rmb-(xc-1)*xmb)%xc;
            xrf=(double)wr/xc;
          }
        }

        yrf=0;
        if(ih==0)
        {
            ih=(fh-yst-tmb-bmb-(yc-1)*ymb)/yc;
            hr=(fh-yst-tmb-bmb-(yc-1)*ymb)%yc;
            yrf=(double)hr/yc;
        }
        else
        {
          if(ycen==1)
          {
            tmb=(fh-yst-ih*yc-ymb*(yc-1))/2;
            bmb=tmb;
            hr=(fh-yst-tmb-bmb-(yc-1)*ymb)%yc;
            yrf=(double)hr/yc;
          }

        }
        //================================
        yend=0;
        y=yst+tmb;
        k=0;
        yf=0;
        BK0:
        for(i=iyst;i<yc;i++)
        {
            n=0;
            xf=0;
            if(yf>=1)
            {
                yf-=1;
                n=1;
            }
            x=xst+lmb;
            xend=0;
            ihbuf=ih;
            for(j=ixst;j<xc;j++)
            {
                m=0;
                if(xf>=1)
                {
                    xf-=1;
                    m=1;
                }
                iwbuf=iw;
                if(iw==-1)
                    iwbuf=ctrA[k].getWidth()+1;
                if(ih==-1)
                    ihbuf=ctrA[k].getHeight()+1;
                if(ctrA[k]!=null)
                    ctrA[k].setBounds(x, y, iwbuf, ihbuf);
                xend=x+iwbuf;
                x+=(xmb+iwbuf+m);
                x+=ofw[j];
                xf+=xrf;
                k++;
            }
            yend=y+ihbuf;
            y+=(ymb+ihbuf+n);
            y+=ofh[i];
            yf+=yrf;
            if(xend>xmax)
                xmax=xend;

        }
        if(yend>ymax)
            ymax=yend;
        eleAmt=1;
        xc=1;
        yc=1;
        xst=0;
        yst=0;
        iw=0;
        ih=0;
        ixst=0;
        iyst=0;
        rateW=0;
        rateH=0;
        fw=0;
        fh=0;
        xcen=0;
        ycen=0;
        tm=2;bm=2;lm=2;rm=2;
        xm=2;ym=2;
        for(i=0;i<ofh.length;i++)
            ofh[i]=0;
        for(i=0;i<ofw.length;i++)
            ofw[i]=0;
        
        for(i=0;i<ctrA.length;i++)
            ctrA[i]=null;
        return true;
        
    }         
    
    
    
    
}