/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.util
 *
 *    Filename:    Constants.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月27日 下午4:52:17
 *
 *    Revision:
 *
 *    2017年2月27日 下午4:52:17
 *
 *****************************************************************/
package com.blemobi.payment.util;


/**
 * @ClassName Constants
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年2月27日 下午4:52:17
 * @version 1.0.0
 */
public final class Constants {
    public enum HTMLSTS{
        SUCCESS("success"),FAILED("failed");
        private String value;
        private HTMLSTS(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    
    public enum RESPSTS{
        SUCCESS("0000");
        private String value;
        private RESPSTS(String value) {
            this.value = value; 
        }
        public String getValue() {
            return value;
        }
    }
    
    public enum RONGYUN_ORD_STS{
        SUCCESS("1");
        private String value;
        private RONGYUN_ORD_STS(String value) {
            this.value = value; 
        }
        public String getValue() {
            return value;
        }
    }
}
