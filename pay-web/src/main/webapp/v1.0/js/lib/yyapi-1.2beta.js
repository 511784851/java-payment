/*
* gb开放平台JavaScript SDK
* @create date 2012-01-06
* @modify data 2012-05-21
* @version 1.2 beta
* ............................................................................
* ............................................................................
* gb open platform client javascript sdk 
* 广州华多网络科技有限公司 版权所有 (c) 2005-2012 DuoWan.com [多玩游戏]

******************************************************************************
* 更多开发资料请参考open.gb.com
*******************************************************************************/

//(function() {
var gb_e_api_call_error = 0xF230; //十进制值62000  Api调用错误，错误的函数名称，错误的调用格式会返回此错误。
var gb_e_api_param_error = 0xF231; //十进制值62001 Api调用参数错误，错误的参数个数和类型，会触发此错误。
var gb_e_api_return_format_error = 0xF232; //十进制值62002 Api调用返回值格式错误。
//------------------------------IgbCommon------------------------------------------------------------------------
/**
* IgbCommon 接口。
* @class 公共功能原型类，提供比如事件的侦听，取消侦听等公共功能。
* @constructor
*/
function IgbCommon() {
    /**
    * 保存事件侦听函数的对象，事件的类型作为eventsMap的key, key为事件唯一描述字符串，具体事件key 值，在每个接口有单独定义。
    * @field
    * @private
    */
    this.eventsMap = {};
};

/**
* 增加侦听事件。
* @param {String} eventType 事件的类型key，比如: Igb.ACTIVE,IgbChannel.CHANNEL_INFO_CHANGED
* @param {function} listenerFunc 事件的侦听函数。
*/
IgbCommon.prototype.addEventListener = function(eventType, listenerFunc) {
    if (this.eventsMap[eventType] === null || this.eventsMap[eventType] === undefined) {
        this.eventsMap[eventType] = [listenerFunc];
    }
    else {
        this.eventsMap[eventType].push(listenerFunc);
    }

};

/**
* 删除侦听事件。即删除指定事件的所有侦听函数。
* @param {String} eventType 事件的类型。
*/
IgbCommon.prototype.removeEventListener = function(eventType) {
    if (this.eventsMap[eventType] !== null && this.eventsMap[eventType] !== undefined) {
        this.eventsMap[eventType] = [];
    }
};

/**
* 触发事件，注意：此接口，在外部不要调用，外部调用此函数触发的事件，为无效事件
* @param {String} eventType 事件类型。 
* @param {String} eventData 事件数据。 
* @private
*/
IgbCommon.prototype.dispatchEvent = function(eventType, eventData) {
    //触发事件
    if (debugMode) {
        var txtConsole = document.getElementById("txtConsole");
        if (txtConsole !== null) txtConsole.innerText = eventType + " eventData=" + eventData + "\n" + txtConsole.innerText;
    }

    if (this.eventsMap[eventType] === null || this.eventsMap[eventType] === undefined) return;
    for (var i = 0; i < this.eventsMap[eventType].length; i++) {
        switch (arguments.length) {
            case 1:
                this.eventsMap[eventType][i](); //不需要信息的事件
                break;
            case 2:
                this.eventsMap[eventType][i](eventData);
                break;
            default:
        }
    }
};
//--------------------------------------set debug mode-----------------------
//设置为true时，会在id为txtConsole的textarea文本框中输出调试信息
var debugMode = false;

//--------------------------------------Igb----------------------------------
/**
* Igb 构造函数。
* @extends IgbCommon
* @class gb接口入口，获取到gb的其他接口和方法。
* @constructor
*/
function Igb() {
    /**
    * 获取语音接口。
    * @field
    * @type IgbAudio
    * @see IgbAudio   
    */
    this.audio = new IgbAudio();

    /**
    * 获取频道接口。
    * @field
    * @type IgbChannel
    * @see IgbChannel   
    */
    this.channel = new IgbChannel();

    /**
    * 获取简单存储接口。  
    * @field
    * @type IgbCloud
    * @see IgbCloud
    */
    this.cloud = new IgbCloud();

    /**
    * 获取IM接口。
    * @field
    * @type IgbIM
    * @see IgbIM    
    */
    this.im = new IgbIM();

    /**
    * 获取网络接口。
    * @field
    * @type IgbNet
    * @see IgbNet
    */
    this.net = new IgbNet();

    /**
    * 获取安全接口。
    * @field
    * @type IgbSecurity
    * @see IgbSecurity
    */
    this.security = new IgbSecurity();

    /**
    * 获取当前用户信息。
    * @field
    * @see IgbUser
    * @type IgbUser
    */
    this.user = new IgbUser();
    

    
    var ret = callExternal("Igb_GetVersion();");
    var ver = new gbVersion();
    ver.majorVersion = ret.main_version;
    ver.minorVersion = ret.sub_version;
    /**
    * 获取gb API的版本。
    * @returns 返回gb API的版本,是一个gbVersion对象。
    * @type gbVersion
    * @see gbVersion
    */
    this.version = ver;


};

Igb.prototype = new IgbCommon();


/**
* 应用激活事件。应用运行时，当被应用盒子或者其他应用入口点击时，产生的事件。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.activeCode: Number类型，正整数，表示点击的来源，0=点击来源于应用盒子图标。
*
* @example
* 使用示例：
* gb.addEventListener(Igb.ACTIVE,onActive);
*
* function onActive(eventData)
* {
*    document.getElementById("txtLog").innerHTML="点击来源："+eventData.activeCode;
* }
*/
Igb.ACTIVE = "gb_ACTIVE";

//------------------------------IgbAudio------------------------------

/**
* IgbAudio 构造函数。
* @extends IgbCommon
* @class 语音控制接口，提供处理gb的音频信息，比如录音的控制等。
* @constructor
*/
function IgbAudio() {

};

IgbAudio.prototype = new IgbCommon();
/**
* 开始录音
* @param {String} fileName 指定录音文件的文件名，不需要路径。
* 格式为MP3，会录制到固定的路径中，如果两次录音指定了同一个文件，第二次的会被覆盖。不指定文件名的话系统会使用默认名称。
* @returns 返回操作是否成功 0=成功， 非0值失败，具体请参考错误代码。
* @type Number
*/
IgbAudio.prototype.startRecord = function(fileName) {
    var result;
    if (arguments.length === 0) {
        result = callExternal("IAudio_StartRecord('');");
    }
    else if (arguments.length > 1) {
        return gb_e_api_param_error; //出错，参数错误
    }
    else {
        if (typeof (fileName) !== "string") return gb_e_api_param_error;
        result = callExternal("IAudio_StartRecord(\"" + fileName + "\");");
    }
    return result.ret;
};
/**
* 停止录音
* @returns 返回操作是否成功,0=成功， 非0值失败，具体请参考错误代码。
* @type Number
*/
IgbAudio.prototype.stopRecord = function() {
    var result = callExternal("IAudio_StopRecord();");
    return result.ret;
};

/**
* 打开卡拉ok效果。<br>
* 权限规则如下：<br>
* OW,VP，MA 在当前的频道内，在任何模式下都可以开启和关闭卡拉OK功能。
* CA1,CA2 在当前频道内拥有管理权限的子频道内可以开启和关闭卡拉OK功能。
* VIP，G，R，U必须在自由模式下或者麦序模式下到首位麦序的时候可以开启和关闭卡拉OK功能。
* 字母代表的意义如下：<br>
* 游客(U),临时嘉宾(G),嘉宾(VIP),会员(R),二级子频道管理员(CA2),子频道管理员(CA),全频道管理员(MA),频道总管理(VP),频道所有者(OW)<br>
* @returns 返回操作是否成功,0=成功， 非0值失败，具体请参考错误代码。
* @type Number
*/
IgbAudio.prototype.openKaraoke = function() {
    var result = callExternal("IAudio_OpenKaraoke();");
    return result.ret;
};
/**
* 关闭卡拉ok效果。权限规则和openKaraoke方法相同。
* @see #openKaraoke
* @returns 返回操作是否成功,0=成功， 非0值失败，具体请参考错误代码。
* @type Number
*/
IgbAudio.prototype.closeKaraoke = function() {
    var result = callExternal("IAudio_CloseKaraoke();");
    return result.ret;
};
/**
* 音频录音出错事件。录音出错的时候会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.errCode: Number类型，整数，录音出错代码。
*
* @example
* 使用示例：
* gb.audio.addEventListener(IgbAudio.RECORD_ERR,onRecordError);
*
* function onRecordError(eventData)
* {
*    document.getElementById("txtLog").innerHTML=eventData.errCode;
* }
*/
IgbAudio.RECORD_ERR = "gb_RECORD_ERR";


/**
* 音频录音完成事件。录音完成的时候会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.result: Number类型，表示录音结果的整数。 0=录音正确，非0值表示录音过程中有错误。
* eventData.fileName: String类型 录音文件的路径和文件名 。
*
* @example
* 使用示例：
* gb.audio.addEventListener(IgbAudio.RECORD_FINISHED,onRecordFinish);
*
* function onRecordFinish(eventData)
* {
*    if(eventData.result==0)
*    {
*       document.getElementById("txtLog").innerHTML="录好的文件在："+eventData.fileName;
*    }
* }
*/
IgbAudio.RECORD_FINISHED = "gb_RECORD_FINISHED";

//-------------------------------------IgbChannel-----------------------------------
/**
* IgbChannel 构造函数。
* @extends IgbCommon
* @class 频道接口，提供对频道的操作和交互。
* @constructor
*/
function IgbChannel() {

    /**
    * 获取用户菜单接口
    * @type IgbChannelUserListPopMenu
    * @see IgbChannelUserListPopMenu    
    * @field
    */
    this.userListPopMenu = new IgbChannelUserListPopMenu();
    /**
    * 获取麦序接口。
    * @type IgbChannelMicList
    * @see IgbChannelMicList
    * @field
    */
    this.micList = new IgbChannelMicList();
    /**
    * 获取频道应用消息接口
    * @type IgbChannelAppMsg
    * @see IgbChannelAppMsg
    * @field
    */
    this.appMsg = new IgbChannelAppMsg();

    /**
    * 获取频道用户控制器
    * @type IgbChannelUserController
    * @see IgbChannelUserController
    * @field
    */
    this.userController = new IgbChannelUserController();
};

IgbChannel.prototype = new IgbCommon();

/**
* 获取当前所在的根频道信息
* @returns 返回当前频道信息,是一个gbChannelInfo对象,如果频道没有短位id，短位id和长位id相同。获取失败时返回null。
* @type gbChannelInfo
* @see gbChannelInfo
*/
IgbChannel.prototype.getCurrentChannelInfo = function() {
    var result = callExternal("IChannel_GetCurrentChannelInfo();");
    if (result.ret === 0) {
        return parseChannelInfo(result);
    }
    else {
        return null;
    }
};

/**
* 获取当前所在的子频道信息
* @returns 返回当前子频道信息,是一个gbChannelInfo对象。如果频道没有短位id，短位id和长位id相同。获取失败时返回null。
* @type gbChannelInfo
* @see gbChannelInfo
*/
IgbChannel.prototype.getCurrentSubChannelInfo = function() {
    var result = callExternal("IChannel_GetCurrentSubChannelInfo();");
    if (result.ret === 0) {

        return parseChannelInfo(result);
    }
    else {
        return null;
    }
};

/**
* 获取当前大频道中，指定的子频道或者根频道的频道信息。
* @returns 返回指定频道信息,是一个gbChannelInfo对象。如果频道没有短位id，短位id和长位id相同。获取失败时返回null。
* @param {Number} cid 频道的id号 <b>是频道的长位Id</b> 。
* @type gbChannelInfo
* @see gbChannelInfo    
* 
*/
IgbChannel.prototype.getChannelInfo = function(cid) {
    if (arguments.length !== 1) return null;
    if ((typeof cid !== "number" && typeof cid !== "string") || isNaN(cid)) return null;
    var result = callExternal("IChannel_GetChannelInfo(" + cid + ");");
    if (result.ret === 0) {
        return parseChannelInfo(result);
    }
    else {
        return null;
    }
};

//原始信息格式{"ret":0,"long_id":15477857}
/**
* 获取当前根频道id。
* @returns 返回当前根频道的频道长位id。获取失败时返回0。
* @type Number
*/
IgbChannel.prototype.getRootChannelId = function() {
    var result = callExternal("IChannel_GetRootChannelId();");
    if (result.ret === 0) {
        return result.long_id;
    }
    else {
        return 0;

    }
};

//返回原始数据 {ret:0,ids:[15777555,18955441,15478888]}
/**
* 获取指定频道的所有子频道的id。
* @param {Number} cid 指定频道的的长位id,必须是在当前大频道中的一个频道。 
* @returns 返回所有子频道的长位id,id保存在一个数组中。获取失败时返回空数组。
* @type Array
*/
IgbChannel.prototype.getSubChannelIds = function(cid) {
    if (arguments.length !== 1) return [];
    if ((typeof cid !== "number" && typeof cid !== "string") || isNaN(cid)) return [];
    var result = callExternal("IChannel_GetSubChannelIds(" + cid + ");");
    if (result.ret === 0) {
        return result.ids;
    }
    else {
        return [];
    }
};


/**
* 当前频道信息变化事件。用户<b>当前</b>所在频道信息变化时会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData: Object类型 是gbChannelInfo对象，保存频道的新信息。
*
* @example
* 使用示例：
* gb.channel.addEventListener(IgbChannel.CHANNEL_INFO_CHANGED,onChannelInfoChanged);
*
* function onChannelInfoChanged(eventData)
* {
*     document.getElementById("txtLog").innerHTML="发生变化的频道号："+eventData.longId+" 名称为："+eventData.name;
* }
*/
IgbChannel.CHANNEL_INFO_CHANGED = "gb_CHANNEL_INFO_CHANGED";

/**
* 切换频道事件。用户在大频道中切换频道的时候会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.departedId: Number类型 离开的频道的长位id。
* eventData.nowId: Number类型 进入的频道的长位id。
*
* @example
* 使用示例：
* gb.channel.addEventListener(IgbChannel.FOCUS_CHANNEL_CHANGED,onFocusChanged);
*
* function onFocusChanged(eventData)
* {
*     document.getElementById("txtLog").innerHTML="离开："+eventData.departedId+" 进入了"+eventData.nowId;
* }
*/
IgbChannel.FOCUS_CHANNEL_CHANGED = "gb_FOCUS_CHANNEL_CHANGED";


/**
* 子频道增加事件。子频道创建的时候会触发此事件。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.cid: Number类型 增加的子频道的长位id。
* eventData.pcid: Number类型 增加到哪个频道下，长位id。
* @example
* 使用示例：
* gb.channel.addEventListener(IgbChannel.SUB_CHANNEL_ADD,onChannelAdd);
*
* function onChannelAdd(eventData)
* {
*     document.getElementById("txtLog").innerHTML="新的频道"+eventData.cid+"位于"+eventData.pcid+"下面";
* }
*/
IgbChannel.SUB_CHANNEL_ADD = "gb_SUB_CHANNEL_ADD";

/**
* 子频道删除事件。子频道被删除时触发此事件。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.cid: Number类型 被删除的子频道长位id。
*
* @example
* 使用示例：
* gb.channel.addEventListener(IgbChannel.SUB_CHANNEL_DEL,onChannelDel);
*
* function onChannelDel(eventData)
* {
*     document.getElementById("txtLog").innerHTML="被删除的子频道："+eventData.cid;
* }
*/
IgbChannel.SUB_CHANNEL_DEL = "gb_SUB_CHANNEL_DEL";

/**
* 用户进入当前大频道事件。当用户进入当前大频道中任一频道就会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.uid: Number类型 进入频道的用户uid。
* eventData.cid: Number类型 进入时，所在的那个频道的长位id。  
* @example
* 使用示例：
* gb.channel.addEventListener(IgbChannel.USER_ENTER_CHANNEL,onUserEnter);
*
* function onUserEnter(eventData)
* {
*     document.getElementById("txtLog").innerHTML="有新用户"+eventData.uid+"进入到"+eventData.cid+"频道";
* }
*/
IgbChannel.USER_ENTER_CHANNEL = "gb_USER_ENTER_CHANNEL";


/**
* 用户离开当前大频道事件。当有用户离开当前大频道就会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.uid: Number类型 离开频道的用户uid。
* eventData.cid: Number类型 离开大频道时所处的频道的长位id。
* @example
* 使用示例：
* gb.channel.addEventListener(IgbChannel.USER_LEAVE_CHANNEL,onUserLeave);
*
* function onUserLeave(eventData)
* {
*     document.getElementById("txtLog").innerHTML="用户"+eventData.uid+"离开了"+eventData.cid+"频道";
* }
*/
IgbChannel.USER_LEAVE_CHANNEL = "gb_USER_LEAVE_CHANNEL";


//-------------------------------------IgbChannelAppMsg-----------------------------------
/**
* IgbChannelAppMsg 构造函数
* @extends IgbCommon
* @class 频道应用消息接口，提供频道的应用消息发送和响应等操作，应用消息出现在应用盒子的应用消息选项卡中和公告栏下方。
* @constructor
*/
function IgbChannelAppMsg() {
};

IgbChannelAppMsg.prototype = new IgbCommon();


/**
* 发送应用消息到子频道。所有该子频道在线用户才能收到。
* @param {Number} subChannelId 子频道长位id。    
* @param {String} msg 消息内容，最大长度200字节。
* @param {Number} linkstart 内容中超链接开始位置，必须为正整数。
* @param {Number} linkend 内容中超链接结束位置，必须为正整数。    
* @param {Number} token  设置token，消息标记，必须为正整数。  
* @returns 发送是否成功。 0=成功 非0值参考错误代码。
* @type Number
*/
IgbChannelAppMsg.prototype.sendMsgToSubChannel = function(subChannelId, msg, linkstart, linkend, token) {
    if (arguments.length !== 5) return gb_e_api_param_error;
    if (typeof subChannelId !== "number" || typeof msg !== "string" || typeof linkstart !== "number" || typeof linkend !== "number" || typeof token !== "number") return gb_e_api_param_error;
    if (isNaN(subChannelId) || isNaN(linkstart) || isNaN(linkend) || isNaN(token)) return gb_e_api_param_error;
    msg = msg.replace(/\\/g, "\\\\"); //替换斜杠
    msg = msg.replace(/\"/g, "\\\""); //替换双引号
    var result = callExternal("IChannelAppMsg_SendMsgToSubChannel(" + subChannelId + ",\"" + msg + "\"," + linkstart + "," + linkend + "," + token + ");");
    return result.ret;
};


/**
* 发送应用消息给指定用户。用户必须在同一大频道中，且必须在线才能收到。
* @param {Array} userList 存有目标用户uid的数组。    
* @param {String} msg 消息内容 最大长度200字节。
* @param {Number} linkstart 内容中超链接开始位置，必须为正整数。
* @param {Number} linkend 内容中超链接结束位置，必须为正整数。    
* @param {Number} token  设置token，消息标记，必须为正整数。 
* @returns 发送是否成功。 0=成功 非0值参考错误代码。
* @type Number
*/
IgbChannelAppMsg.prototype.sendMsgToUsers = function(userList, msg, linkstart, linkend, token) {
    if (arguments.length !== 5) return gb_e_api_param_error;
    if (!(userList instanceof Array) || typeof msg !== "string" || typeof linkstart !== "number" || typeof linkend !== "number" || typeof token !== "number") return gb_e_api_param_error;
    if (isNaN(linkstart) || isNaN(linkend) || isNaN(token)) return gb_e_api_param_error;
    msg = msg.replace(/\\/g, "\\\\"); //替换斜杠
    msg = msg.replace(/\"/g, "\\\""); //替换双引号
    var result = callExternal("IChannelAppMsg_SendMsgToUsers(\"[" + userList.toString() + "]\",\"" + msg + "\"," + linkstart + "," + linkend + "," + token + ");");
    return result.ret;
};


/**
* 应用消息链接点击事件。应用消息中的超链接被点击的时候会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.token: Number类型，发送消息的时候设置的token,可以用来判断哪一条消息被点击。
* @example
* 使用示例：
* gb.channel.appMsg.addEventListener(IgbChannelAppMsg.APP_LINK_CLICKED,onLinkClicked);
*
* function onLinkClicked(eventData)
* {
*     document.getElementById("txtLog").innerHTML="消息的Token="+eventData.token;
* }
*/
IgbChannelAppMsg.APP_LINK_CLICKED = "gb_APP_LINK_CLICKED";


//-------------------------------IgbChannelMicList-------------------------------
/**
* IgbChannelMicList 构造函数。
* @extends IgbCommon
* @class 麦序接口，提供麦序的信息和相关事件。

* @constructor
*/
function IgbChannelMicList() {
};

IgbChannelMicList.prototype = new IgbCommon();

//原始数据格式 { "ret":0, "mic_list";[9090115887,909058887] }。
/**
* 获取麦序列表。
* @returns 返回麦序中所有用户的uid，uid保存在一个数组中。麦序中无用户和获取失败时返回空数组。
* @type Array
*/
IgbChannelMicList.prototype.getMicList = function() {
    var result = callExternal("IChannelMicList_GetMicList();");
    if (result.ret === 0) {
        return result.mic_list;
    }
    else {
        return [];
    }
};


/**
* 麦序用户增加事件。当有用户加入到麦序时会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.uid: Number类型 加入的用户uid。
* @example
* 使用示例：
* gb.channel.micList.addEventListener(IgbChannelMicList.USER_JOIN,onUserJoin);
*
* function onUserJoin(eventData)
* {
*     document.getElementById("txtLog").innerHTML="用户"+eventData.uid+"加入到了麦序中";
* }
*/
IgbChannelMicList.USER_JOIN = "gb_USER_JOIN";


/**
* 麦序用户离开事件。当有用户离开麦序时会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.uid: Number类型 离开的用户uid。
* @example
* 使用示例：
* gb.channel.micList.addEventListener(IgbChannelMicList.USER_LEAVE,onUserLeave);
*
* function onUserLeave(eventData)
* {
*     document.getElementById("txtLog").innerHTML="用户"+eventData.uid+"离开麦序了";
* }
*/
IgbChannelMicList.USER_LEAVE = "gb_USER_LEAVE";


/**
* 麦序用户移动事件。麦序用户发生位置调整的时候会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* 侦听函数参数说明: 
* eventData.moveId: Number类型 麦序中发生移动的用户uid。
* eventData.toAfterId: Number类型 移动到哪个用户后面，用户无法移动到第一个。   
* @example
* 使用示例：
* gb.channel.micList.addEventListener(IgbChannelMicList.USER_MOVE,onUserMove);
*
* function onUserMove(eventData)
* {
*     document.getElementById("txtLog").innerHTML="用户"+eventData.uid+"移动到"+eventData.toAfterId+"后面";
* }
*/
IgbChannelMicList.USER_MOVE = "gb_USER_MOVE";


/**
* 麦序用户清除事件。麦序用户全部被清除的时候会触发。
* @field
* @example
* 侦听函数格式: function(){    } 
* @example
* 使用示例：
* gb.channel.micList.addEventListener(IgbChannelMicList.CLEAR,onUserClear);
*
* function onUserClear()
* {
*     document.getElementById("txtLog").innerHTML="麦序用户被清除";
* }
*/
IgbChannelMicList.CLEAR = "gb_USER_CLEAR";


//-------------------------------IgbChannelUserController-------------------------------
/**
* IgbChannelUserController 构造函数。
* @extends IgbCommon
* @class 频道用户控制器接口。
* @constructor
*/
function IgbChannelUserController() {

};

IgbChannelUserController.prototype = new IgbCommon();

/**
* 允许频道用户发言。权限规则和disableMsg方法相同。
* @see #disableMsg
* @returns 返回操作是否成功。0=成功，其它值请参考错误代码
* @param {Number} uid 用户的唯一标识id，即uid，<b>不是gb号</b> 。
* @type Number
*/
IgbChannelUserController.prototype.enableMsg = function(uid) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (typeof uid !== "number" || isNaN(uid)) return gb_e_api_param_error;
    var result = callExternal("IChannelUserController_EnableMsg(" + uid + ");");
    return result.ret;
};

/**
* 禁止频道用户发言。
* 权限规则如下<br>
* OW:可以允许和禁止频道内任何其他成员语音，文字。包括（VP MA CA1 CA2 R VIP G U)。<br>
* VP：可以允许和禁止频道内任何其他成员语音，文字。 除了（OW，VP）。<br>
* MA：可以允许和禁止频道内任何其他成员语音，文字。 除了（OW，VP,MA）。<br>
* CA1：可以允许和禁止相对应有管理权限的子频道内的语音，文字。包括（ CA2 R VIP G U）。<br>
* CA2：可以允许和禁止相对应有管理权限的子频道内的语音，文字。包括（ R VIP G U）。<br>
* R VIP G U 均无任何权限操作。<br>
* 字母代表的意义如下：<br>
* 游客(U),临时嘉宾(G),嘉宾(VIP),会员(R),二级子频道管理员(CA2),子频道管理员(CA),全频道管理员(MA),频道总管理(VP),频道所有者(OW)
* @returns 返回操作是否成功。0=成功，其它值请参考错误代码
* @param {Number} uid 用户的唯一标识id，即uid，<b>不是gb号</b> 。
* @type Number
* @see #enableMsg
*/
IgbChannelUserController.prototype.disableMsg = function(uid) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (typeof uid !== "number" || isNaN(uid)) return gb_e_api_param_error;
    var result = callExternal("IChannelUserController_DisableMsg(" + uid + ");");
    return result.ret;
};
/**
* 允许频道用户说话。权限规则和disableMsg方法相同。
* @see #disableMsg
* @returns 返回操作是否成功。0=成功， 其它值请参考错误代码
* @param {Number} uid 用户的唯一标识id，即uid，<b>不是gb号</b> 。
* @type Number
*/
IgbChannelUserController.prototype.enableSpeak = function(uid) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (typeof uid !== "number" || isNaN(uid)) return gb_e_api_param_error;
    var result = callExternal("IChannelUserController_EnableSpeak(" + uid + ");");
    return result.ret;
};
/**
* 禁止频道用户说话。权限规则和disableMsg方法相同。
* @see #disableMsg
* @returns 返回操作是否成功。0=成功，其它值请参考错误代码
* @param {Number} uid 用户的唯一标识id，即uid，<b>不是gb号</b> 。
* @type Number
*/
IgbChannelUserController.prototype.disableSpeak = function(uid) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (typeof uid !== "number" || isNaN(uid)) return gb_e_api_param_error;
    var result = callExternal("IChannelUserController_DisableSpeak(" + uid + ");");
    return result.ret;
};

/**
* 进子频道。
* @param {Number} cid 子频道长位id,必须是在当前大频道中的一个频道。 
* @returns 返回操作是否成功，0=成功，非0值失败，具体请参考错误代码。
* @type Number
*/
IgbChannelUserController.prototype.jumpToSubChannel = function(cid) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (typeof cid !== "number" || isNaN(cid)) return gb_e_api_param_error;
    var result = callExternal("IChannelUserController_JumpToSubChannel(" + cid + ");");
    return result.ret;
};
/**
* 拉人进子频道。<br>
* 权限规则如下<br>
* OW：可以调度频道内任何成员,包括（VP MA CA1 CA2 R VIP G U)。<br>
* VP：可以调度频道内除OW以外的任何成员，包括（VP MA CA1 CA2 R VIP G U)。<br>
* MA：可以调度频道内除了OW，VP以外的任何成员，包括（MA CA1 CA2 R VIP G U)。<br>
* CA1:可以调度相对应有管理权限的1级子频道内的成员，（OW，VP,MA）除外。<br>
* CA2：可以调度相对应有管理权限的2级子频道内的成员，（OW，VP,MA，CA1）除外。<br>
* R VIP G U 均无任何权限操作。<br>
* 字母代表的意义如下：<br>
* 游客(U),临时嘉宾(G),嘉宾(VIP),会员(R),二级子频道管理员(CA2),子频道管理员(CA),全频道管理员(MA),频道总管理(VP),频道所有者(OW)<br>
* @param {Number} cid 子频道长位id,必须是当前大频道中的一个频道。 
* @returns 返回操作是否成功，0=成功，非0值失败，具体请参考错误代码。
* @type Number
*/
IgbChannelUserController.prototype.pullToSubChannel = function(uid, cid) {
    if (arguments.length !== 2) return gb_e_api_param_error;
    if (typeof cid !== "number" || typeof uid !== "number" || isNaN(uid) || isNaN(cid)) return gb_e_api_param_error;
    var result = callExternal("IChannelUserController_PullToSubChannel(" + uid + "," + cid + ");");
    return result.ret;
};
/**
* 获取用户所在子频道ID。该用户必须在当前大频道中。
* @param {Number} uid 用户的uid。 
* @returns 用户所在的频道Id，获取失败或出错时返回0。
* @type Number
*/
IgbChannelUserController.prototype.getUserSubChannelId = function(uid) {
    if (arguments.length !== 1) return 0;
    if ((typeof uid !== "number" && typeof uid !== "string") || isNaN(uid)) return 0;
    var result = callExternal("IChannelUserController_GetUserSubChannelId(" + uid + ");");
    if (result.ret === 0) {
        return result.cid;
    }
    else {
        return 0;
    }

};

//-------------------------------IgbChannelUserListPopMenu-------------------------------
/**
* IgbChannelUserListPopMenu 构造函数。
* @extends IgbCommon
* @class 频道右键菜单接口。频道用户列表右键菜单设置和取消， 和对应的菜单事件设置 。
* @constructor
*/
function IgbChannelUserListPopMenu() {

};

IgbChannelUserListPopMenu.prototype = new IgbCommon();


/**
* 设置大频道用户列表右键菜单，可以增加一个菜单项，一个应用只可以增加一个菜单项。
* @param {String} menuText 菜单上的文字,字符串最大长度20字节。
* @returns 返回操作是否成功, 0=成功 非0值参考错误代码。
* @type Number
*/
IgbChannelUserListPopMenu.prototype.setPopMenu = function(menuText) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (typeof menuText !== "string") return gb_e_api_param_error;
    menuText = menuText.replace(/\\/g, "\\\\"); //替换斜杠
    menuText = menuText.replace(/\"/g, "\\\""); //替换双引号    
    var result = callExternal("IChannelUserListPopMenu_SetPopMenu(\"" + menuText + "\");");
    return result.ret;
};


/**
* 去掉右键菜单增加项。
* @returns 返回操作是否成功, 0=成功 非0值参考错误代码。
* @type Number
*/
IgbChannelUserListPopMenu.prototype.unSetPopMenu = function() {
    var result = callExternal("IChannelUserListPopMenu_UnSetPopMenu();");
    return result.ret;
};


/**
* 用户点击菜单项事件。当用户列表右键菜单项被点击的时候会触发。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* eventData.uid: Number类型 被选中的用户的uid。
* eventData.cid: Number类型 当前的频道长位id。    
* @example
* 使用示例：
* gb.channel.userListPopMenu.addEventListener(IgbChannelUserListPopMenu.CLICKED,onClicked);
*
* function onClicked(eventData)
* {
*     document.getElementById("txtLog").innerHTML="用户"+eventData.uid+"菜单被点击,当前频道"+eventData.cid;
* }
*/

IgbChannelUserListPopMenu.CLICKED = "gb_MENU_CLICKED";


//-------------------------------IgbCloud-----------------------------

/**
* IgbCloud 构造函数。
* @extends IgbCommon
* @class 简单存储接口。提供简单的简单存储数据服务，包括增，删，改，查的基本操作，除了 频道所有者(OW紫马)和 频道总管理(VP橙马)可以删除和修改所有数据之外，其他用户只能删除和修改自己的数据，每个用户都可以查询所有数据。
* @constructor
*/
function IgbCloud() {

};


IgbCloud.prototype = new IgbCommon();

//----------常量----------



/**
* 增加数据。<b>注意:两次保存之间需要间隔1秒</b>。
* @param {Number} int1 要保存的数据，32位无符号整数,范围[0,4294967295]，超出范围返回错误码12。 
* @param {Number} int2 要保存的数据，32位无符号整数,范围[0,4294967295]，超出范围返回错误码12。 
* @param {String} str 要保存的数据。    
* @returns 返回操作是否成功,是一个json对象。
* @example 
* 成功时返回数据key值和返回码0,例如 {"ret":0,"key":"000000004f55d48f"}。
* 失败时返回错误代码，例如{"ret":5}
* @type Object
*/
IgbCloud.prototype.addData = function(int1, int2, str) {
    if (arguments.length === 0 || arguments.length > 3) return { ret: gb_e_api_param_error };
    if (typeof int1 !== "number" || typeof int2 !== "number" || typeof str !== "string" || isNaN(int1) || isNaN(int2)) return { ret: gb_e_api_param_error };
    str = str.replace(/\\/g, "\\\\"); //替换斜杠
    str = str.replace(/\"/g, "\\\""); //替换双引号
    switch (arguments.length) {
        case 1:
            return callExternal("ICloud_AddData(0, 0, \"" + arguments[0] + "\");");
            break;
        case 2:
            return callExternal("ICloud_AddData(" + arguments[0] + ", 0, \"" + arguments[1] + "\");");
            break;
        case 3:
            return callExternal("ICloud_AddData(" + arguments[0] + ", " + arguments[1] + ", \"" + arguments[2] + "\");");
            break;
        default:
    }
};


/**
* 修改数据。
* @returns 返回操作是否成功。0=成功，非0值请参考错误代码 。
* @param {Number} int1 被修改的数据的新值，32位无符号整数,范围[0,4294967295]，超出范围返回错误码12。 
* @param {Number} int2 被修改的数据的新值，32位无符号整数,范围[0,4294967295]，超出范围返回错误码12。 
* @param {String} str 被修改的数据的新值。          
* @param {Array} filter 过滤器数组，保存gbCloudFilter对象数组，找到要修改的数据。       
* @type Number
* @see gbCloudFilter
*/
IgbCloud.prototype.updateData = function(int1, int2, str, filter) {
    if (arguments.length !== 4) return gb_e_api_param_error;
    if (typeof int1 !== "number" || typeof int2 !== "number" || typeof str !== "string" || !(filter instanceof Array) || isNaN(int1) || isNaN(int2)) return gb_e_api_param_error;
    var filterString = "";
    var sp = "";
    str = str.replace(/\\/g, "\\\\"); //替换斜杠
    str = str.replace(/\"/g, "\\\""); //替换双引号
    for (var i = 0; i < filter.length; i++) {
        filterString = filterString + sp + filter[i].toString();
        sp = ",";
    }
    var result = callExternal("ICloud_UpdateData(" + int1 + "," + int2 + " ,\"" + str + "\", '[" + filterString + "]');");
    return result.ret;
};


/**
* 删除数据。
* @returns 返回操作是否成功。0=成功，非0值请参考错误代码 。
* @param {Array} filter 过滤器数组,即删除的条件。保存gbCloudFilter对象数组。   
* @type Number
* @see gbCloudFilter
*/
IgbCloud.prototype.deleteData = function(filter) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (!(filter instanceof Array)) return gb_e_api_param_error;
    var filterString = "";
    var sp = "";
    for (var i = 0; i < filter.length; i++) {
        filterString = filterString + sp + filter[i].toString();
        sp = ",";
    }
    var result = callExternal("ICloud_DeleteData('[" + filterString + "]');");
    return result.ret;
};


//原始返回格式
//{"ret":0,"data":[
//*  {"key":"4f55d3d7","create_time":"2012-03-06 17:07:35","update_time":"2012-03-06 17:07:35","creator_uid":1710881282,"int1":1,"int2":100,"str":"你好，简单存储！hello cloud"},
// *  {"key":"4f55d48f","create_time":"2012-03-06 17:10:39","update_time":"2012-03-06 17:10:39","creator_uid":1710881282,"int1":1,"int2":100,"str":"可存可取"},
//*  {"key":"4f55d57d","create_time":"2012-03-06 17:14:37","update_time":"2012-03-06 17:14:37","creator_uid":1710881282,"int1":1,"int2":100,"str":"this is test"}
//*]} 
//* 如果没有查询到数据，格式如下
//* {"ret":0,"data":[]}


/**
* 查询数据。
* @param {Array} filter 过滤器数组，查询的条件。数组中为gbCloudFilter对象。没有查到数据或查询出错时返回空数组。    
* @returns 返回查询结果，保存在数组中。数组中为gbCloudData对象。
* @type Array
* @see gbCloudData 
* @see gbCloudFilter
*/
IgbCloud.prototype.queryData = function(filter) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (!(filter instanceof Array)) return gb_e_api_param_error;
    var filterString = "";
    var sp = "";
    for (var i = 0; i < filter.length; i++) {
        filterString = filterString + sp + filter[i].toString();
        sp = ",";
    }
    var result = callExternal("ICloud_QueryData('[" + filterString + "]');");
    if (result.ret === 0) {
        return parseCloudDataList(result.data);
    }
    else {

        return []
    }
};

//-----------------------------------IgbIM---------------------------
/**
* IgbIM 构造函数
* @extends IgbCommon
* @class 聊天接口。提供弹出聊天对话框，弹出添加好友对话框等功能。
* @constructor
*/
function IgbIM() {
};

IgbIM.prototype = new IgbCommon();


/**
* 给指定用户发送聊天消息， 调用后会弹出聊天对话框，需要用户点击确认才发送。
* @param {Number} uid 用户的唯一标识id，即uid，<b>不是gb号</b> 。
* @param {String} msg 等待发送的聊天的内容,最大长度40个字节。
* @returns 返回发送是否成功,0=成功 非0值参考错误代码。
* @type Number 
*/
IgbIM.prototype.chatTo = function(uid, msg) {
    if (arguments.length !== 2) return gb_e_api_param_error;
    if (typeof uid !== "number" || isNaN(uid) || typeof msg !== "string") return gb_e_api_param_error;
    msg = msg.replace(/\\/g, "\\\\"); //替换斜杠
    msg = msg.replace(/\"/g, "\\\""); //替换双引号
    var result = callExternal("IIM_ChatTo(" + uid + ",\"" + msg + "\");");
    return result.ret;
};


/**
* 判断指定的用户是否是好友。
* @param {Number} uid 指定用户的唯一标识id，即uid，<b>不是gb号</b> 。
* @returns 返回是否是好友,true=是好友 false=不是好友 出错或无法取得信息时也返回false。
* @type Boolean
*/
IgbIM.prototype.isFriend = function(uid) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (typeof uid !== "number" || isNaN(uid)) return gb_e_api_param_error;
    var result = callExternal("IIM_IsFriend(" + uid + ");");
    if (result.ret == 0) {
        return result.is_friend;
    }
    else {
        return false;
    }
    
    
    
};


/**
* 弹出添加好友对话框，用户确认才开始添加。
* @param {Number} uid 用户的唯一标识id，即uid，<b>不是gb号</b> 。
* @returns 返回弹出窗口是否成功,0=成功 非0值参考错误代码。
* @type Number
*/
IgbIM.prototype.addFriend = function(uid) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (typeof uid !== "number" || isNaN(uid)) return gb_e_api_param_error;
    var result = callExternal("IIM_AddFriend(" + uid + ");");
    return result.ret;
};



//------------------------------IgbNet------------------------------
/**
* IgbNet 构造函数。
* @extends IgbCommon
* @class 网络通讯接口。提供广播数据和接收广播数据的功能。
* @constructor
*/
function IgbNet() {

};

IgbNet.prototype = new IgbCommon();

/**
* 子频道数据广播，包括自己。<b>两次广播需要间隔20毫秒,否则广播数据可能会丢失。</b>。
* @returns 返回操作是否成功,0=成功，非0值请参考错误代码 。 
* @param {Number} sub_channel_id 子频道的长位id。
* @param {String} data 要广播的数据,最大长度2048个字节。
* @type Number
*/
IgbNet.prototype.broadcastSubChannel = function(sub_channel_id, data) {
    if (arguments.length !== 2) return gb_e_api_param_error;
    if (typeof sub_channel_id !== "number" || isNaN(sub_channel_id) || typeof data !== "string") return gb_e_api_param_error;
    var result = callExternal("INet_BroadCastSubChannel(" + sub_channel_id + ",\"" + encodeURI(data) + "\");");
    return result.ret;
};

/**
* 全频道数据广播，包括自己。<b>两次广播需要间隔20毫秒,否则广播数据可能会丢失。</b>。
* @returns 返回操作是否成功，0=成功，非0值请参考错误代码 。
* @param {String} data 要广播的数据。最大长度2048个字节。
* @type Number
*/
IgbNet.prototype.broadcastAllChannel = function(data) {
    if (arguments.length !== 1) return gb_e_api_param_error;
    if (typeof data !== "string") return gb_e_api_param_error;
    var result = callExternal("INet_BroadCastAllChannel(\"" + encodeURI(data) + "\");");
    return result.ret;
};

/**
* 广播给指定用户。<b>两次广播需要间隔20毫秒,否则广播数据可能会丢失。</b>。
* @returns 返回操作是否成功,0=成功，非0值请参考错误代码 。
* @param {Array} u_array 接收广播的用户uid，保存在一个数组中,用户个数必须小于等于100。 
* @param {String} data 要广播的数据。最大长度2048个字节。    
* @type Number
*/
IgbNet.prototype.broadcastToUsers = function(data, u_array) {
    if (arguments.length !== 2) return gb_e_api_param_error;
    if (typeof data !== "string" || !(u_array instanceof Array)) return gb_e_api_param_error;
    var result = callExternal("INet_BroadCastToUsers(\"" + encodeURI(data) + "\", \"[" + u_array.toString() + "]\");");
    return result.ret;

};

/**
* 收到频道广播消息事件。 收到广播消息后触发此事件。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* eventData.data: String类型  接收到的数据。
* @example
* 使用示例：
* gb.net.addEventListener(IgbNet.RECV,onRecv);
*
* function onRecv(eventData)
* {
*     document.getElementById("txtLog").innerHTML="接收到"+eventData.data;
* }
*/
IgbNet.RECV = "gb_RECV";
//------------------------------IgbSecurity------------------------------
/**
* IgbSecurity 构造函数。
* @extends IgbCommon
* @class 安全接口。提供获取安全认证信息等功能。
*/
function IgbSecurity() {

};


IgbSecurity.prototype = new IgbCommon();
/**
* 获取当前用户安全认证令牌。
* @returns 令牌字符,获取失败或出错时返回空字符。
* @type String
*/
IgbSecurity.prototype.getToken = function() {
    var result = callExternal("ISecurity_GetToken();");
    if (result.ret === 0) {
        return result.token;
    }
    else {
        return "";
    }
};


//--------------------------------------IgbUser----------------------------------
/**
* IgbUser 构造函数。
* @extends IgbCommon
* @class 用户信息接口。提供获取用户的信息，接收用户信息变化事件等功能。
*/
function IgbUser() {

};


IgbUser.prototype = new IgbCommon();

//原始信息格式 {"ret":0,"uid":50002277,"imid":51285414,"sex":1,"role":200,"points":114,"level":21,"name":"孤独小羊","sign":"最近经常失眠"}
/**
* 获取当前用户的信息。
* @example
* 使用示例：
* var userInfo = gb.user.getCurrentUserInfo();
* @returns 返回当前用户信息,是一个gbUserInfo对象。获取失败时返回null。
* @type gbUserInfo
* @see gbUserInfo
*/
IgbUser.prototype.getCurrentUserInfo = function() {
    var result = callExternal("IUser_GetCurrnetUserInfo();");
    if (result.ret === 0) {
        return parseUserInfo(result);
    }
    else {
        return null;
    }
};


/**
* 获取指定的用户的信息。
* @returns 返回当前用户信息,是一个gbUserInfo对象。获取失败时返回null。
* @param {Number} uid 用户的唯一标识id，即uid，<b>不是gb号</b> 。
* @type gbUserInfo
* @see gbUserInfo
*/
IgbUser.prototype.getUserInfo = function(uid) {
    if (arguments.length !== 1) return null;
    if ((typeof uid !== "number" && typeof uid !== "string")||isNaN(uid)) return null;
    var result = callExternal("IUser_GetUserInfo(" + uid + ");");
    if (result.ret === 0) {
        return parseUserInfo(result);
    }
    else {
        return null;
    }
};



/**
* 当前用户信息变更事件。当前用户昵称，性别，签名修改的时候会触发此事件。
* @field
* @example
* 侦听函数格式: function(eventData){    } 
* eventData: gbUserInfo类型 变化后的用户信息。
* @example
* 使用示例：
* gb.user.addEventListener(IgbUser.USER_INFO_CHANGED,onChange);
*
* function onChange(eventData)
* {
*    document.getElementById("txtLog").innerHTML=eventData.name+ "的信息发生了变化";
* }
* @see gbUserInfo
*/
IgbUser.USER_INFO_CHANGED = "gb_USER_INFO_CHANGED";

var callTimes = 0;
/**
* 调用gb平台提供的接口
* @param {String} func 接口的函数名称 
* @private
*/
function callExternal(func) {

    try {
        //打印执行语句
        callTimes++;
        
        var txtConsole = null;
        try {

            if (debugMode) {
                txtConsole = document.getElementById("txtConsole");
                if (callTimes === 200) txtConsole.innerText = "";//限制字符数
                if (txtConsole !== null) txtConsole.innerText = "window.external." + func + "\n" + txtConsole.innerText;
            }
        } catch (exLog) {
            throw "执行日志错误！" + exLog + exLog.message;
        }


        if (callTimes > 200) callTimes = 0;
        
        //执行此语句，跟容器通讯，调用api
        var retString = eval("window.external." + func);

        //控制台返回值
        try {
            if (debugMode) {
                if (txtConsole !== null) txtConsole.innerText = retString + "\n" + txtConsole.innerText;
            }
        } catch (exLog2) {
            throw "返回值输出日志错误！" + exLog2 + exLog2.message;
        }

        try {
            var retJson = eval("(" + retString + ")");
        } catch (exjson) {
            throw "转json出错," + exjson.message;
        }

        if (retJson.ret === null) throw "NO_RET";
        return retJson;
    } catch (ex) {
        //控制台输出异常
        if (debugMode) {
            if (txtConsole !== null) txtConsole.innerText = "错误! 原因" + ex + " :" + ex.message + "\n" + txtConsole.innerText;
        }
        if (ex === "NO_RET") return { ret: gb_e_api_return_format_error, message: "返回信息没有ret属性" };
        else return { ret: gb_e_api_call_error, message: "错误! 原因:" + ex + ex.message };
    }

}
//输出debug信息到控制台
function gbtrace(msg) {
    var txtConsole = null;
    try {

        if (debugMode) {
            txtConsole = document.getElementById("txtConsole");
            if (callTimes === 200) txtConsole.innerText = ""; //限制字符数
            if (txtConsole !== null) txtConsole.innerText = msg + "\n" + txtConsole.innerText;
        }
    } catch (exLog) {
        throw "打印日志错误！" + exLog + exLog.message;
    }
}


//创建api对象，供调用所有api，全局变量。
window["gb"] = new Igb();

//})(); //保存到命名空间中


//---------------------------------------------------------数据类-----------------------------------------------------------------
/**
* 构造函数。
* @class 保存用户的信息。
*/
function gbUserInfo() {
    /**
    * 用户的名称
    * @field
    * @type String
    */
    this.name = "";
    /**
    * 用户的性别 （0:男 1:女） 
    * @field
    * @type Number
    */
    this.sex = 0;

    /**
    * 用户的uid,唯一标识id
    * @field
    * @type Number
    */
    this.uid = 0;

    /**
    * 用户的gb号
    * @field
    * @type Number    
    */
    this.imId = 0;

    /**
    * 用户的马甲 对应的信息如下：<br>
    *   无效角色 0 <br>
    *   访问者  灰马 20 <br>
    *   游客(U)  白马 25 <br>
    *   临时嘉宾(G) 浅绿色马甲 66 <br>
    *   嘉宾(VIP)  绿马 88 <br>
    *   会员(R)  蓝马 100 <br>
    *   二级子频道管理员(CA2)  粉马 150 <br>
    *   子频道管理员(CA) 红马 175 <br>
    *   全频道管理员(MA)  黄马 200 <br>
    *   频道总管理(VP) 橙马 230 <br>
    *   频道所有者(OW)  紫马 255 <br>
    *   客服 300 <br>
    *   网监 400 <br>
    *   歪歪官方人员 黑马 1000 <br>       
    * @field
    * @type Number       
    */
    this.role = 0;

    /**
    * 用户的个人积分
    * @field
    * @type Number    
    */
    this.points = 0;

    /**
    * 用户的等级
    * @field
    * @type Number       
    */
    this.level = 0;

    /**
    * 用户的签名
    * @field
    * @type String       
    */
    this.sign = "";

    /**
    * 是否是vip
    * @field
    * @type Boolean       
    */
    this.vip = false;
        
}
gbUserInfo.prototype.toString = function() {
    var s = "{\"uid\":" + this.uid + ",\"name\":\"" + this.name + "\",\"sex\":" + this.sex + ",\"imId\":" + this.imId + ",";
    s += "\"role\":" + this.role + ",\"points\":" + this.points + ",\"level\":" + this.level + ",\"sign\":\"" + this.sign + "\",\"vip\":" + this.vip + "}";
    return s;
};
/**
* 构造函数。
* @class 保存频道的信息。
*/
function gbChannelInfo() {
    /**
    * 频道长位id。
    * @field
    * @type Number     
    */
    this.longId = 0;
    /**
    * 频道短位id。
    * @field
    * @type Number     
    */
    this.shortId = 0;
    /**
    * 频道名称。
    * @field
    * @type String     
    */
    this.name = "";
}
gbChannelInfo.prototype.toString = function() {
    var s = "{\"longId\":" + this.longId + ",\"shortId\":" + this.shortId + ",\"name\":\"" + this.name + "\"}";
    return s;
};
/**
* 构造函数。
* @class 保存gb API版本信息。
*/
function gbVersion() {
    /**
    * 主版本号，是正整数。
    * @field
    * @type Number
    */
    this.majorVersion = 0;
    /**
    * 副版本号，是正整数。
    * @field
    * @type Number
    */
    this.minorVersion = 0;
}

/**
* 构造函数。
* @class 保存云数据信息。
*/
function gbCloudData() {
    /**
    * 数据的键值。
    * @field
    * @type String
    */
    this.uniqueKey = "";
    /**
    * 数据创建的时间。
    * @field
    * @type String
    */
    this.createTime = "";

    /**
    * 数据更新的时间。
    * @field
    * @type String
    */
    this.updateTime = "";

    /**
    * 数据创建者的uid。
    * @field
    * @type Number
    */
    this.creatorUid = 0;
    /**
    * int字段数据，32位无符号整数,范围[0,4294967295]。
    * @field
    * @type Number
    */
    this.intValue1 = 0;
    /**
    * int字段数据，32位无符号整数,范围[0,4294967295]。
    * @field
    * @type Number
    */
    this.intValue2 = 0;
    /**
    * string字段数据。
    * @field
    * @type String
    */
    this.stringValue = "";
}

gbCloudData.prototype.toString = function() {
    var s = "{\"uniqueKey\":\"" + this.uniqueKey + "\",\"creatorUid\":" + this.creatorUid + ",\"createTime\":\"" + this.createTime + "\",\"updateTime\":\"" + this.updateTime + "\",";
    s += "\"intValue1\":" + this.intValue1 + ",\"intValue2\":" + this.intValue2 + ",\"stringValue\":\"" + this.stringValue + "\"}";
    return s;
};

/**
* 构造函数。
* @class 简单存储条件过滤器，保存查询条件。
*/
function gbCloudFilter() {
    /**
    * 对哪个字段进行过滤。
    * @field
    * @type Number
    */
    this.field = 0;
    /**
    * 操作符，比如大于小于等。
    * @field
    * @type Number
    */
    this.op = 0;
    /**
    * 字段数值。
    * @field
    * @type Object
    */
    this.value = null;
    /**
    * 和其他filter的关系。
    * @field
    * @type Number
    */
    this.condition = 0;
}
/**
* 简单存储的字段表示常量。
* @field
* @example
* gbCloudFilter.EField.NONE 0 无效字段
* gbCloudFilter.EField.UNIQUE_KEY 1 唯一键 字段
* gbCloudFilter.EField.USER_ID 2 uid字段
* gbCloudFilter.EField.EXTERNAL_VALUE1 3 扩展int1 字段
* gbCloudFilter.EField.EXTERNAL_VALUE2 4 扩展int2 字段
* gbCloudFilter.EField.CREATE_TIME 5 创建时间
* gbCloudFilter.EField.UPDATE_TIME 6 更新时间
*/
gbCloudFilter.EField =
{
    //!无效字段
    NONE: 0,
    //!key 唯一键 字段
    UNIQUE_KEY: 1,
    //!uid 字段
    USER_ID: 2,
    //!扩展int1 字段
    EXTERNAL_VALUE1: 3,
    //!扩展int2 字段
    EXTERNAL_VALUE2: 4,
    //!创建时间
    CREATE_TIME: 5,
    //!更新时间
    UPDATE_TIME: 6
};

/**
* 简单存储的操作符常量。
* @field
* @example
* gbCloudFilter.EFilterOperator.NONE 0 无效操作
* gbCloudFilter.EFilterOperator.EQ 1 等于
* gbCloudFilter.EFilterOperator.GE 2 大于等于
* gbCloudFilter.EFilterOperator.LE 3 小于等于
* gbCloudFilter.EFilterOperator.GREATER 4 大于
* gbCloudFilter.EFilterOperator.LESS 5 小于
*/
gbCloudFilter.EFilterOperator =
{
    //! 无效操作
    NONE: 0,
    //! = 等于
    EQ: 1,
    //! >= 大于等于
    GE: 2,
    //! <= 小于等于	
    LE: 3,
    //! = 大于
    GREATER: 4,
    //! < 小于
    LESS: 5
};

/**
* 简单存储的条件运算常量。
* @field
* @example
* gbCloudFilter.EFilterCondition.NONE 0 无效条件
* gbCloudFilter.EFilterCondition.AND  1 条件 与 and 
* gbCloudFilter.EFilterCondition.OR 2 条件 或 or
*/
gbCloudFilter.EFilterCondition =
{
    //!无效条件
    NONE: 0,
    //! 条件 与 and 
    AND: 1,
    //! 条件 或 or
    OR: 2
};
gbCloudFilter.prototype.toString = function() {
    switch (this.field) {
        case gbCloudFilter.EField.EXTERNAL_VALUE1, gbCloudFilter.EField.EXTERNAL_VALUE2:
            return "{\"field\":" + this.field + ",\"op\":" + this.op + ",\"value\":" + this.value + ",\"condition\":" + this.condition + "}";
        case gbCloudFilter.EField.UNIQUE_KEY:
            return "{\"field\":" + this.field + ",\"op\":" + this.op + ",\"value\":\"" + this.value + "\",\"condition\":" + this.condition + "}";
        default:
            return "{\"field\":" + this.field + ",\"op\":" + this.op + ",\"value\":" + this.value + ",\"condition\":" + this.condition + "}";
    }


};



//---------------------------------------下面为回调函数------------------------------------------------------------------------------------------------
/**
* 运行时，应用图标被点击事件。
* @private
*/
function Igb_OnActive(activeCode) {
    gb.dispatchEvent(Igb.ACTIVE, { activeCode: activeCode });
}


//-----------------------语音设备更换[Event]----------------------
/**
* 录音错误事件。
* @param {Number} err_code 录音错误代码，参考错误代码表。
* @private
*/
function IAudioEvent_OnRecordErr(err_code) {
    gb.audio.dispatchEvent(IgbAudio.RECORD_ERR, { errCode: err_code });
}


/**
* 录音完成事件。
* @param {String} info 录音完成信息。
* @example 
* 返回参数示例: {result:0,file_name:"abcd"} 
* result 录音是否成功 0成功，非0值失败。
* file_name 录音文件的名称，不带没有扩展名和路径。
* @private
*/
function IAudioEvent_OnRecordFinished(info) {
    var retJson = eval("(" + info + ")");
    gb.audio.dispatchEvent(IgbAudio.RECORD_FINISHED, { result: retJson.result, fileName: retJson.file_name });
}

//-----------------------频道信息获取回调接口 [Event]----------------------
/**
* 子频道跳转事件。
* @param {String} info 频道跳转信息，是一个可以转成Json的字符串。
* @example
* 返回参数示例: {departed_id:15488855,now_id:85526655}
* departed_id 原来子频道id。
* now_id 现在子频道id。
* @private
*/

function IChannelEvent_OnFocusChannelChannged(info) {
    var retJson = eval("(" + info + ")");
    gb.channel.dispatchEvent(IgbChannel.FOCUS_CHANNEL_CHANGED, { departedId: retJson.departed_id, nowId: retJson.now_id });
}

/**
* 当前频道信息改变事件。
* @param {String} info 改变后的频道信息，是一个可以转成Json的字符串。
* @example
* 返回参数示例: 
*
* {"ret":0,"long_id":51285414,"short_id":6048,"name":"月光酒吧"}
*
* ret 返回码 
* long_id 频道长位id
* short_id 频道短位id
* name 频道名称id
* @private
*/
function IChannelEvent_OnChannelInfoChannged(info) {
    var retJson = eval("(" + info + ")");
    gb.channel.dispatchEvent(IgbChannel.CHANNEL_INFO_CHANGED, parseChannelInfo(retJson));
}





/**
* 删除子频道时产生事件。
* @param {Number} cid 被删除的子频道长位id。
* @private
*/
function IChannelEvent_OnSubChannelDel(cid) {
    gb.channel.dispatchEvent(IgbChannel.SUB_CHANNEL_DEL, { cid: cid });
}


/**
* 添加子频道时产生事件。
* @param {String} info 频道添加的信息，是一个可以转成Json的字符串。
* @example 
* 返回参数示例: {cid:15488855,pcid:85526655} 
* cid 增加的子频道长位id。
* pcid 增加到哪个父频道下，长位id。
* @private
*/
function IChannelEvent_OnSubChannelAdd(info) {
    var retJson = eval("(" + info + ")");
    gb.channel.dispatchEvent(IgbChannel.SUB_CHANNEL_ADD, { cid: retJson.cid, pcid: retJson.pcid });
}
/**

* 用户进入大频道事件，子频道之间跳转不会触发此事件。
* @param {String} info 用户加入频道的信息
* @example 
* 返回参数示例: {uid:905488855,cid:85526655} 
* uid 进入的用户的uid。
* cid 进入时进入到大频道中的哪个频道。
* @private
*/
function IChannelEvent_OnUserEnterChannel(info) {
    var retJson = eval("(" + info + ")");
    gb.channel.dispatchEvent(IgbChannel.USER_ENTER_CHANNEL, { uid: retJson.uid, cid: retJson.cid });
}

/**
* 用户离开大频道事件，子频道之间跳转不会触发此事件。
* @param {String} info 用户离开频道的信息
* @example 
* 返回参数示例: {uid:905488855,cid:85526655} 
* uid 离开的用户的uid。
* cid 离开大频道时所处的频道。
* @private
*/
function IChannelEvent_OnUserLeaveChannel(info) {
    var retJson = eval("(" + info + ")");
    gb.channel.dispatchEvent(IgbChannel.USER_LEAVE_CHANNEL, { uid: retJson.uid, cid: retJson.cid });
}



///
//------------------------频道用户列表右键菜单事件通知 [Event]
///
/**
* 频道用户列表右键菜单项被点击事件。
* @param {String} info 点击用户的信息。
* @example 
* 返回参数示例: {uid:905488855,cid:85526655} 
* uid 被点中的用户uid。
* cid 当前所在的频道。
* @private
*/
function IChannelUserPopMenuEvent_OnClicked(info) {
    var retJson = eval("(" + info + ")");
    gb.channel.userListPopMenu.dispatchEvent(IgbChannelUserListPopMenu.CLICKED, { uid: retJson.uid, cid: retJson.cid });
}

///
//------------------------网络状态回调 [Event]
///


/**
* 连接成功的事件。
* @param {Number} result 0成功，非0值失败。
* @private
*/
/*
function INetEvent_OnConnected(result) {
gb.net.dispatchEvent(IgbNet.CONNECTED, { result: result });
}*/


/**
* 连接断开后事件。
* @param {Number} result 0:主动断开, 其他错误参考错误代码表
* @private
*/
/*
function INetEvent_OnClosed(result) {
gb.net.dispatchEvent(IgbNet.CLOSED, { result: result });
}
*/

/**
* 收到广播数据包事件。
* @param {Object} data 收到数据
* @private
*/
function INetEvent_OnRecv(data) {
    gb.net.dispatchEvent(IgbNet.RECV, { data: decodeURI(data) });
}


///
//------------------------------------------频道应用信息链接事件 [Event]
///


/**
* 应用消息中的链接被点击事件。
* @param {Number} token 消息标记，区分不同的消息。
* @private
*/
function IChannelAppLinkEvent_OnAppLinkClicked(token) {
    gb.channel.appMsg.dispatchEvent(IgbChannelAppMsg.APP_LINK_CLICKED, { token: token });
}

///
//------------------------------------------麦序相关接口事件
///

//麦序列表发生改变


/**
* 用户加入到麦序事件。
* @param {uid} 加入到麦序的用户uid。
* @private
*/
function IMicListEvent_OnUserJoin(uid) {
    gb.channel.micList.dispatchEvent(IgbChannelMicList.USER_JOIN, { uid: uid });
}
/**
* 用户离开麦序事件。
* @param {Number} uid 离开麦序的用户uid。
* @private
*/
function IMicListEvent_OnUserLeave(uid) {
    gb.channel.micList.dispatchEvent(IgbChannelMicList.USER_LEAVE, { uid: uid });
}
/**
* 用户在麦序中的位置发生变化事件，同一子频道的用户会收到。
* @example 
* 返回参数示例: {move_id:905488855,to_after_id:905477756} 
* move_id:发生移动的id。
* to_after_id:移动到那个用户后面。
* @private
*/
function IMicListEvent_OnUserMove(info) {
    var retJson = eval("(" + info + ")");
    gb.channel.micList.dispatchEvent(IgbChannelMicList.USER_MOVE, { moveId: retJson.move_id, toAfterId: retJson.to_after_id });
}

/**
* 麦序被清除事件。
* @private
*/
function IMicListEvent_OnClear() {
    gb.channel.micList.dispatchEvent(IgbChannelMicList.CLEAR);
}

//----------------------------用户事件回调------------------------------------------
/**
* 用户信息改变事件，得到改变后的用户信息。
* @param {String} info 改变后的用户信息,是一个可以转成Json的字符串。
* @private
*/
function IUserEvent_OnUserInfoChanged(info) {
    var retJson = eval("(" + info + ")");
    gb.user.dispatchEvent(IgbUser.USER_INFO_CHANGED, parseUserInfo(retJson));
}

/**
* 转换频道信息格式。
* @private
*/
function parseChannelInfo(info) {
    var cinfo = new gbChannelInfo();
    cinfo.longId = info.long_id;
    cinfo.shortId = info.short_id;
    cinfo.name = info.name;
    return cinfo;
}

/**
* 转换用户信息格式。
* @private
*/
function parseUserInfo(info) {
    var userInfo = new gbUserInfo();
    userInfo.uid = info.uid;
    userInfo.name = info.name;
    userInfo.imId = info.imid;
    userInfo.role = info.role;
    userInfo.points = info.points;
    userInfo.level = info.level;
    userInfo.sex = info.sex;
    userInfo.sign = info.sign;
    userInfo.vip = info.vip;
    return userInfo;
}
/**
* 转换用户信息格式。
* @private
*/
function parseCloudDataList(data) {
    var dataArray = [];
    for (var i = 0; i < data.length; i++) {
        var dt = new gbCloudData();
        dt.uniqueKey = data[i].key;
        dt.createTime = data[i].create_time;
        dt.updateTime = data[i].update_time;
        dt.creatorUid = data[i].creator_uid;
        dt.intValue1 = data[i].int1;
        dt.intValue2 = data[i].int2;
        dt.stringValue = data[i].str;
        dataArray.push(dt);
    }

    return dataArray;
}
