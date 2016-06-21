package com.guzhi.pay.channel.unicom.cxf.verifycodemanage;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import com.guzhi.pay.channel.unicom.cxf.InaccessInfo;
import com.guzhi.pay.channel.unicom.cxf.InextraInfo;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.guzhi.pay.channel.unicom.cxf.verifycodemanage package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ResponseException_QNAME = new QName("http://ws.ifdp.womusic.cn/", "ResponseException");
    private final static QName _SendVerifyCode_QNAME = new QName("http://ws.ifdp.womusic.cn/", "sendVerifyCode");
    private final static QName _SendVerifyCodeResponse_QNAME = new QName("http://ws.ifdp.womusic.cn/",
            "sendVerifyCodeResponse");
    private final static QName _CodeLogin_QNAME = new QName("http://ws.ifdp.womusic.cn/", "codeLogin");
    private final static QName _SendLoginCodeResponse_QNAME = new QName("http://ws.ifdp.womusic.cn/",
            "sendLoginCodeResponse");
    private final static QName _CodeLoginResponse_QNAME = new QName("http://ws.ifdp.womusic.cn/", "codeLoginResponse");
    private final static QName _SendLoginCode_QNAME = new QName("http://ws.ifdp.womusic.cn/", "sendLoginCode");

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package:
     * com.guzhi.pay.channel.unicom.cxf.verifycodemanage
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SendLoginCode }
     * 
     */
    public SendLoginCode createSendLoginCode() {
        return new SendLoginCode();
    }

    /**
     * Create an instance of {@link CodeLoginResponse }
     * 
     */
    public CodeLoginResponse createCodeLoginResponse() {
        return new CodeLoginResponse();
    }

    /**
     * Create an instance of {@link SendLoginCodeResponse }
     * 
     */
    public SendLoginCodeResponse createSendLoginCodeResponse() {
        return new SendLoginCodeResponse();
    }

    /**
     * Create an instance of {@link CodeLogin }
     * 
     */
    public CodeLogin createCodeLogin() {
        return new CodeLogin();
    }

    /**
     * Create an instance of {@link SendVerifyCodeResponse }
     * 
     */
    public SendVerifyCodeResponse createSendVerifyCodeResponse() {
        return new SendVerifyCodeResponse();
    }

    /**
     * Create an instance of {@link SendVerifyCode }
     * 
     */
    public SendVerifyCode createSendVerifyCode() {
        return new SendVerifyCode();
    }

    /**
     * Create an instance of {@link ResponseException }
     * 
     */
    public ResponseException createResponseException() {
        return new ResponseException();
    }

    /**
     * Create an instance of {@link SendLoginCodeEvt }
     * 
     */
    public SendLoginCodeEvt createSendLoginCodeEvt() {
        return new SendLoginCodeEvt();
    }

    /**
     * Create an instance of {@link InextraInfo }
     * 
     */
    public InextraInfo createInextraInfo() {
        return new InextraInfo();
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link SendVerifyCodeEvt }
     * 
     */
    public SendVerifyCodeEvt createSendVerifyCodeEvt() {
        return new SendVerifyCodeEvt();
    }

    /**
     * Create an instance of {@link CodeLoginEvt }
     * 
     */
    public CodeLoginEvt createCodeLoginEvt() {
        return new CodeLoginEvt();
    }

    /**
     * Create an instance of {@link RequestEvent }
     * 
     */
    public RequestEvent createRequestEvent() {
        return new RequestEvent();
    }

    /**
     * Create an instance of {@link InaccessInfo }
     * 
     */
    public InaccessInfo createInaccessInfo() {
        return new InaccessInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link ResponseException }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ifdp.womusic.cn/", name = "ResponseException")
    public JAXBElement<ResponseException> createResponseException(ResponseException value) {
        return new JAXBElement<ResponseException>(_ResponseException_QNAME, ResponseException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendVerifyCode }
     * {@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ifdp.womusic.cn/", name = "sendVerifyCode")
    public JAXBElement<SendVerifyCode> createSendVerifyCode(SendVerifyCode value) {
        return new JAXBElement<SendVerifyCode>(_SendVerifyCode_QNAME, SendVerifyCode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link SendVerifyCodeResponse }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ifdp.womusic.cn/", name = "sendVerifyCodeResponse")
    public JAXBElement<SendVerifyCodeResponse> createSendVerifyCodeResponse(SendVerifyCodeResponse value) {
        return new JAXBElement<SendVerifyCodeResponse>(_SendVerifyCodeResponse_QNAME, SendVerifyCodeResponse.class,
                null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeLogin }
     * {@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ifdp.womusic.cn/", name = "codeLogin")
    public JAXBElement<CodeLogin> createCodeLogin(CodeLogin value) {
        return new JAXBElement<CodeLogin>(_CodeLogin_QNAME, CodeLogin.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link SendLoginCodeResponse }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ifdp.womusic.cn/", name = "sendLoginCodeResponse")
    public JAXBElement<SendLoginCodeResponse> createSendLoginCodeResponse(SendLoginCodeResponse value) {
        return new JAXBElement<SendLoginCodeResponse>(_SendLoginCodeResponse_QNAME, SendLoginCodeResponse.class, null,
                value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link CodeLoginResponse }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ifdp.womusic.cn/", name = "codeLoginResponse")
    public JAXBElement<CodeLoginResponse> createCodeLoginResponse(CodeLoginResponse value) {
        return new JAXBElement<CodeLoginResponse>(_CodeLoginResponse_QNAME, CodeLoginResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendLoginCode }
     * {@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.ifdp.womusic.cn/", name = "sendLoginCode")
    public JAXBElement<SendLoginCode> createSendLoginCode(SendLoginCode value) {
        return new JAXBElement<SendLoginCode>(_SendLoginCode_QNAME, SendLoginCode.class, null, value);
    }

}
