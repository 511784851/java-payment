package com.blemobi.payment.global;

/**
 * 统一定义访问外部api地址
 * 
 * @author 赵勇<andy.zhao@blemobi.com>
 */
public class PathGlobal {

	// 根据uuid和token获取用户信息，成功返回Puser
	public static final String GetUser = "/account/user/profile";

	// 验证对方是不是好友，成功返回Puser
	public static final String GetFriend = "/account/get/friend";

	// 获取群信息（包含群成员openId），成功返回PGroup
	public static final String GetGroup = "/account/get/group";

	// 获取其他用户信息，成功返回PGroup
	public static final String GetOtherUser = "/account/get/user";
	
	// 获取其他用户信息，成功返回PGroup
	public static final String GetContactUUID = "/v1/login/inside/uuid";
		
	// 获取用户的好友列表信息，成功返回PuserList
	public static final String GetFriendUserList = "/social/listfriends";
	
	// 获取用户的Vip等级的接口，成功返回PuserList
	public static final String GetUserLevel = "/account/user/level";
	
	// 获取文件上传的接口，成功返回PuserList
	public static final String GetOssUploadFileUrl = "/oss/newfileurl";
	
	// 获取文件上传的接口，成功返回PuserList
	public static final String GetOssUploadFileUrls = "/oss/newfileurls";
		
	// 获取文件下载的url的接口，成功返回PuserList
	public static final String GetOssDownloadFileUrl = "/oss/downloadurl";
	
	// 支付成功的充钻的接口，成功返回PDiamondCount
	public static final String GetWalletDiamondAdd = "/v1/wallet/diamond/add";
}
