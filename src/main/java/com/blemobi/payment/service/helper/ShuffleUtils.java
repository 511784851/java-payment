/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.service.helper
 *
 *    Filename:    ShuffleUtils.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月22日 下午4:21:25
 *
 *    Revision:
 *
 *    2017年3月22日 下午4:21:25
 *
 *****************************************************************/
package com.blemobi.payment.service.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.blemobi.library.cache.UserBaseCache;
import com.blemobi.sep.probuf.AccountProtos.PUserBase;
import com.blemobi.sep.probuf.PaymentProtos.PUserBaseGiftEx;

import lombok.extern.log4j.Log4j;

/**
 * @ClassName ShuffleUtils
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月22日 下午4:21:25
 * @version 1.0.0
 */
@Log4j
public final class ShuffleUtils {
    public static List<PUserBaseGiftEx> shuffle(List<String> usrList, Integer cnt){
        List<PUserBaseGiftEx> winList = new ArrayList<PUserBaseGiftEx>();
        for (int idx = 0; idx < cnt; idx++) {
            Random r = new Random();
            int win = r.nextInt(usrList.size());
            String uidAndLoc = usrList.get(win);
            String[] ulArr = uidAndLoc.split("_");
            String uid = ulArr[0];
            String locCd = "";
            if (ulArr.length < 2) {
                locCd = "na;";
            } else {
                locCd = ulArr[1];
            }
            PUserBase userBase = null;
            try {
                userBase = UserBaseCache.get(uid);
            } catch (IOException e) {
                log.error("uuid:[" + uid + "]在缓存中没有找到");
                throw new RuntimeException("用户没有找到");
            }
            PUserBaseGiftEx.Builder builder = PUserBaseGiftEx.newBuilder();
            builder.setInfo(userBase);
            builder.setRegion(locCd);
            usrList.remove(win);
            winList.add(builder.build());
        }
        return winList;
    }
}
