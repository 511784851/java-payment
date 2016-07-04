/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.6.24 : Database - pay_data
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`pay_data` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `pay_data`;

/*Table structure for table `pay_order` */

DROP TABLE IF EXISTS `pay_order`;

CREATE TABLE `pay_order` (
  `id` varchar(50) NOT NULL,
  `uuid` varchar(50) DEFAULT NULL COMMENT '用户UUID',
  `bank_type` varchar(10) DEFAULT NULL COMMENT '支付渠道（WX-微信,ZFB-支付宝）',
  `name` varchar(200) DEFAULT NULL COMMENT '商品名称',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `amount` decimal(18,2) DEFAULT NULL COMMENT '支付金额',
  `app_ip` varchar(20) DEFAULT NULL COMMENT '客户IP',
  `fee_type` varchar(3) DEFAULT NULL COMMENT '币种（1-人民币）',
  `pay_statu` varchar(10) DEFAULT NULL COMMENT '支付状态（0-支付中，1-支付成功，2-支付失败）',
  `create_date` timestamp NULL DEFAULT NULL COMMENT '支付发起时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `pay_result` */

DROP TABLE IF EXISTS `pay_result`;

CREATE TABLE `pay_result` (
  `id` varchar(50) NOT NULL,
  `openid` varchar(30) DEFAULT NULL COMMENT '用户在商户appid下的唯一标识',
  `trade_type` varchar(10) DEFAULT NULL COMMENT ' 支付渠道（WX-微信,ZFB-支付宝）',
  `bank_type` varchar(20) DEFAULT NULL COMMENT '付款银行',
  `amount` decimal(18,2) DEFAULT NULL COMMENT '实际支付金额（单位：元）',
  `fee_type` varchar(10) DEFAULT NULL COMMENT '货币种类(1-人民币)',
  `transaction_id` varchar(50) DEFAULT NULL COMMENT '微信支付订单号',
  `order_no` varchar(50) DEFAULT NULL COMMENT '商户系统的订单号',
  `pay_statu` varchar(10) DEFAULT NULL COMMENT '支付结果（SUCCESS/FAIL）',
  `err_code` varchar(50) DEFAULT NULL COMMENT '支付失败代码',
  `err_code_des` varchar(200) DEFAULT NULL COMMENT '支付失败原因描述',
  `time_end` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '支付完成时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `pay_result` */

/*Table structure for table `wx_bank` */

DROP TABLE IF EXISTS `wx_bank`;

CREATE TABLE `wx_bank` (
  `bank_code` varchar(20) NOT NULL,
  `bank_name` varchar(30) NOT NULL,
  PRIMARY KEY (`bank_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `wx_bank` */

insert  into `wx_bank`(`bank_code`,`bank_name`) values ('ABC_CREDIT','农业银行(信用卡)'),('ABC_DEBIT','农业银行(借记卡)'),('AE_CREDIT','AE(信用卡)'),('BJRCB_DEBIT','北京农商行(借记卡)'),('BNC_DEBIT','江西银行(借记卡)'),('BOB_DEBIT','北京银行(借记卡)'),('BOCD_DEBIT','成都银行(借记卡)'),('BOC_CREDIT','中国银行(信用卡)'),('BOC_DEBIT','中国银行(借记卡)'),('BOD_CREDIT','东莞银行(信用卡)'),('BOD_DEBIT','东莞银行(借记卡)'),('BOIMCB_DEBIT','内蒙古银行(借记卡)'),('BOSH_CREDIT','上海银行(信用卡)'),('BOSH_DEBIT','上海银行(借记卡)'),('BSB_CREDIT','包商银行(信用卡)'),('BSB_DEBIT','包商银行(借记卡)'),('CBHB_DEBIT','渤海银行(借记卡)'),('CCB_CREDIT','建设银行(信用卡)'),('CCB_DEBIT','建设银行(借记卡)'),('CEB_CREDIT','光大银行(信用卡)'),('CEB_DEBIT','光大银行(借记卡)'),('CIB_CREDIT','兴业银行(信用卡)'),('CIB_DEBIT','兴业银行(借记卡)'),('CITIC_CREDIT','中信银行(信用卡)'),('CITIC_DEBIT','中信银行(借记卡)'),('CMBC_CREDIT','民生银行(信用卡)'),('CMBC_DEBIT','民生银行(借记卡)'),('CMB_CREDIT','招商银行(信用卡)'),('CMB_DEBIT','招商银行(借记卡)'),('COMM_DEBIT','交通银行(借记卡)'),('CQB_DEBIT','重庆银行(借记卡)'),('CQRCB_DEBIT','重庆农商银行(借记卡)'),('CRB_DEBIT','华润银行(借记卡)'),('CSCB_DEBIT','长沙银行(借记卡)'),('CSRCB_DEBIT','常熟农商银行(借记卡)'),('CZB_CREDIT','浙商银行(信用卡)'),('CZB_DEBIT','浙商银行(借记卡)'),('CZCB_DEBIT','稠州银行(借记卡)'),('DRCB_DEBIT','东莞农商行(借记卡)'),('DYCCB_DEBIT','德阳银行(借记卡)'),('FJNX_DEBIT','福建农信银行(借记卡)'),('GDB_CREDIT','广发银行(信用卡)'),('GDB_DEBIT','广发银行(借记卡)'),('GDHX_DEBIT','广东华兴银行(借记卡)'),('GDNYB_CREDIT','南粤银行(信用卡)'),('GDNYB_DEBIT','南粤银行(借记卡)'),('GDRCU_DEBIT','广东农信银行(借记卡)'),('GLB_DEBIT','桂林银行(借记卡)'),('GRCB_CREDIT','广州农商银行(信用卡)'),('GRCB_DEBIT','广州农商银行(借记卡)'),('GSNX_DEBIT','甘肃农信(借记卡)'),('GYCB_CREDIT','贵阳银行(信用卡)'),('GYCB_DEBIT','贵阳银行(借记卡)'),('GZCB_CREDIT','广州银行(信用卡)'),('GZCB_DEBIT','广州银行(借记卡)'),('HBNX_CREDIT','湖北农信(信用卡)'),('HBNX_DEBIT','湖北农信(借记卡)'),('HEBNX_DEBIT','河北农信(借记卡)'),('HKBEA_DEBIT','东亚银行(借记卡)'),('HNNX_DEBIT','河南农信(借记卡)'),('HRBB_DEBIT','哈尔滨银行(借记卡)'),('HRXJB_DEBIT','华融湘江银行(借记卡)'),('HSBC_DEBIT','恒生银行(借记卡)'),('HSB_DEBIT','徽商银行(借记卡)'),('HUNNX_DEBIT','湖南农信(借记卡)'),('HXB_CREDIT','华夏银行(信用卡)'),('HXB_DEBIT','华夏银行(借记卡)'),('HZB_CREDIT','杭州银行(信用卡)'),('HZB_DEBIT','杭州银行(借记卡)'),('ICBC_CREDIT','工商银行(信用卡)'),('ICBC_DEBIT','工商银行(借记卡)'),('JCB_CREDIT','JCB(信用卡)'),('JJCCB_DEBIT','九江银行(借记卡)'),('JLB_DEBIT','吉林银行(借记卡)'),('JLNX_DEBIT','吉林农信(借记卡)'),('JNRCB_DEBIT','江南农商(借记卡)'),('JRCB_DEBIT','江阴农商行(借记卡)'),('JSB_CREDIT','江苏银行(信用卡)'),('JSB_DEBIT','江苏银行(借记卡)'),('JSHB_DEBIT','晋商银行(借记卡)'),('JSNX_DEBIT','江苏农商行(借记卡)'),('JXNXB_DEBIT','江西农信(借记卡)'),('JZB_DEBIT','晋中银行(借记卡)'),('KRCB_DEBIT','昆山农商(借记卡)'),('LJB_DEBIT','龙江银行(借记卡)'),('LNNX_DEBIT','辽宁农信(借记卡)'),('LZB_DEBIT','兰州银行(借记卡)'),('MASTERCARD_CREDIT','MASTERCARD(信用卡)'),('NBCB_CREDIT','宁波银行(信用卡)'),('NBCB_DEBIT','宁波银行(借记卡)'),('NCB_DEBIT','宁波通商银行(借记卡)'),('NJCB_DEBIT','南京银行(借记卡)'),('NMGNX_DEBIT','内蒙古农信(借记卡)'),('NYCCB_DEBIT','南阳村镇银行(借记卡)'),('ORDOSB_CREDIT','鄂尔多斯银行(信用卡)'),('ORDOSB_DEBIT','鄂尔多斯银行(借记卡)'),('PAB_CREDIT','平安银行(信用卡)'),('PAB_DEBIT','平安银行(借记卡)'),('PSBC_CREDIT','邮政储蓄银行(信用卡)'),('PSBC_DEBIT','邮政储蓄银行(借记卡)'),('PZHCCB_DEBIT','攀枝花银行(借记卡)'),('QDCCB_DEBIT','青岛银行(借记卡)'),('QLB_DEBIT','齐鲁银行(借记卡)'),('SCNX_DEBIT','四川农信(借记卡)'),('SDEB_DEBIT','顺德农商行(借记卡)'),('SDRCU_DEBIT','山东农信(借记卡)'),('SJB_DEBIT','盛京银行(借记卡)'),('SPDB_CREDIT','浦发银行(信用卡)'),('SPDB_DEBIT','浦发银行(借记卡)'),('SRCB_CREDIT','上海农商银行(信用卡)'),('SRCB_DEBIT','上海农商银行(借记卡)'),('SXXH_DEBIT','陕西信合(借记卡)'),('SZRCB_DEBIT','深圳农商银行(借记卡)'),('TJBHB_DEBIT','天津滨海农商行(借记卡)'),('VISA_CREDIT','VISA(信用卡)'),('WEB_DEBIT','微众银行(借记卡)'),('WFB_DEBIT','潍坊银行(借记卡)'),('WHRC_DEBIT','武汉农商行(借记卡)'),('WRCB_DEBIT','无锡农商(借记卡)'),('WZB_DEBIT','温州银行(借记卡)'),('XAB_DEBIT','西安银行(借记卡)'),('XJRCCB_DEBIT','新疆农信银行(借记卡)'),('YNRCCB_DEBIT','云南农信(借记卡)'),('ZJRCUB_DEBIT','浙江农信(借记卡)'),('ZJTLCB_DEBIT','浙江泰隆银行(借记卡)'),('ZYB_DEBIT','中原银行(借记卡)');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
