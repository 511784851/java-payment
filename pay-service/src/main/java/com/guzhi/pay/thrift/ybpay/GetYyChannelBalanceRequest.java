/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package com.guzhi.pay.thrift.gbpay;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TBase;
import org.apache.thrift.TBaseHelper;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TFieldRequirementType;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;

/**
 * 获取频道虚拟货币的请求
 * 2013/10/30新增
 */
public class GetgbChannelBalanceRequest implements
        TBase<GetgbChannelBalanceRequest, GetgbChannelBalanceRequest._Fields>, java.io.Serializable, Cloneable {
    private static final TStruct STRUCT_DESC = new TStruct("GetgbChannelBalanceRequest");

    private static final TField gbUID_FIELD_DESC = new TField("gbuid", TType.I64, (short) 1);
    private static final TField gb_CHANNEL_ID_FIELD_DESC = new TField("gbChannelId", TType.I64, (short) 2);
    private static final TField APP_ID_FIELD_DESC = new TField("appId", TType.STRING, (short) 3);
    private static final TField BALANCE_TYPE_FIELD_DESC = new TField("balanceType", TType.I32, (short) 4);
    private static final TField TIMESTAMP_FIELD_DESC = new TField("timestamp", TType.I64, (short) 5);
    private static final TField SIGN_FIELD_DESC = new TField("sign", TType.STRING, (short) 6);

    /**
     * 用户gbuid
     */
    public long gbuid;
    /**
     * 频道ID
     */
    public long gbChannelId;
    /**
     * 调用此接口的应用ID，比如gb音乐叫gbMUSIC
     */
    public String appId;
    /**
     * 货币类型
     */
    public int balanceType;
    /**
     * 时间戳，和充值系统的时间差不超过5分钟
     */
    public long timestamp;
    /**
     * 校验码，HMacSha1
     */
    public String sign;

    /**
     * The set of fields this struct contains, along with convenience methods
     * for finding and manipulating them.
     */
    public enum _Fields implements TFieldIdEnum {
        /**
         * 用户gbuid
         */
        gbUID((short) 1, "gbuid"),
        /**
         * 频道ID
         */
        gb_CHANNEL_ID((short) 2, "gbChannelId"),
        /**
         * 调用此接口的应用ID，比如gb音乐叫gbMUSIC
         */
        APP_ID((short) 3, "appId"),
        /**
         * 货币类型
         */
        BALANCE_TYPE((short) 4, "balanceType"),
        /**
         * 时间戳，和充值系统的时间差不超过5分钟
         */
        TIMESTAMP((short) 5, "timestamp"),
        /**
         * 校验码，HMacSha1
         */
        SIGN((short) 6, "sign");

        private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

        static {
            for (_Fields field : EnumSet.allOf(_Fields.class)) {
                byName.put(field.getFieldName(), field);
            }
        }

        /**
         * Find the _Fields constant that matches fieldId, or null if its not
         * found.
         */
        public static _Fields findByThriftId(int fieldId) {
            switch (fieldId) {
            case 1: // gbUID
                return gbUID;
            case 2: // gb_CHANNEL_ID
                return gb_CHANNEL_ID;
            case 3: // APP_ID
                return APP_ID;
            case 4: // BALANCE_TYPE
                return BALANCE_TYPE;
            case 5: // TIMESTAMP
                return TIMESTAMP;
            case 6: // SIGN
                return SIGN;
            default:
                return null;
            }
        }

        /**
         * Find the _Fields constant that matches fieldId, throwing an exception
         * if it is not found.
         */
        public static _Fields findByThriftIdOrThrow(int fieldId) {
            _Fields fields = findByThriftId(fieldId);
            if (fields == null)
                throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            return fields;
        }

        /**
         * Find the _Fields constant that matches name, or null if its not
         * found.
         */
        public static _Fields findByName(String name) {
            return byName.get(name);
        }

        private final short _thriftId;
        private final String _fieldName;

        _Fields(short thriftId, String fieldName) {
            _thriftId = thriftId;
            _fieldName = fieldName;
        }

        public short getThriftFieldId() {
            return _thriftId;
        }

        public String getFieldName() {
            return _fieldName;
        }
    }

    // isset id assignments
    private static final int __gbUID_ISSET_ID = 0;
    private static final int __gbCHANNELID_ISSET_ID = 1;
    private static final int __BALANCETYPE_ISSET_ID = 2;
    private static final int __TIMESTAMP_ISSET_ID = 3;
    private BitSet __isset_bit_vector = new BitSet(4);

    public static final Map<_Fields, FieldMetaData> metaDataMap;
    static {
        Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.gbUID, new FieldMetaData("gbuid", TFieldRequirementType.REQUIRED, new FieldValueMetaData(
                TType.I64)));
        tmpMap.put(_Fields.gb_CHANNEL_ID, new FieldMetaData("gbChannelId", TFieldRequirementType.REQUIRED,
                new FieldValueMetaData(TType.I64)));
        tmpMap.put(_Fields.APP_ID, new FieldMetaData("appId", TFieldRequirementType.REQUIRED, new FieldValueMetaData(
                TType.STRING)));
        tmpMap.put(_Fields.BALANCE_TYPE, new FieldMetaData("balanceType", TFieldRequirementType.REQUIRED,
                new FieldValueMetaData(TType.I32)));
        tmpMap.put(_Fields.TIMESTAMP, new FieldMetaData("timestamp", TFieldRequirementType.REQUIRED,
                new FieldValueMetaData(TType.I64)));
        tmpMap.put(_Fields.SIGN, new FieldMetaData("sign", TFieldRequirementType.REQUIRED, new FieldValueMetaData(
                TType.STRING)));
        metaDataMap = Collections.unmodifiableMap(tmpMap);
        FieldMetaData.addStructMetaDataMap(GetgbChannelBalanceRequest.class, metaDataMap);
    }

    public GetgbChannelBalanceRequest() {
    }

    public GetgbChannelBalanceRequest(long gbuid, long gbChannelId, String appId, int balanceType, long timestamp,
            String sign) {
        this();
        this.gbuid = gbuid;
        setgbuidIsSet(true);
        this.gbChannelId = gbChannelId;
        setgbChannelIdIsSet(true);
        this.appId = appId;
        this.balanceType = balanceType;
        setBalanceTypeIsSet(true);
        this.timestamp = timestamp;
        setTimestampIsSet(true);
        this.sign = sign;
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public GetgbChannelBalanceRequest(GetgbChannelBalanceRequest other) {
        __isset_bit_vector.clear();
        __isset_bit_vector.or(other.__isset_bit_vector);
        this.gbuid = other.gbuid;
        this.gbChannelId = other.gbChannelId;
        if (other.isSetAppId()) {
            this.appId = other.appId;
        }
        this.balanceType = other.balanceType;
        this.timestamp = other.timestamp;
        if (other.isSetSign()) {
            this.sign = other.sign;
        }
    }

    public GetgbChannelBalanceRequest deepCopy() {
        return new GetgbChannelBalanceRequest(this);
    }

    @Override
    public void clear() {
        setgbuidIsSet(false);
        this.gbuid = 0;
        setgbChannelIdIsSet(false);
        this.gbChannelId = 0;
        this.appId = null;
        setBalanceTypeIsSet(false);
        this.balanceType = 0;
        setTimestampIsSet(false);
        this.timestamp = 0;
        this.sign = null;
    }

    /**
     * 用户gbuid
     */
    public long getgbuid() {
        return this.gbuid;
    }

    /**
     * 用户gbuid
     */
    public GetgbChannelBalanceRequest setgbuid(long gbuid) {
        this.gbuid = gbuid;
        setgbuidIsSet(true);
        return this;
    }

    public void unsetgbuid() {
        __isset_bit_vector.clear(__gbUID_ISSET_ID);
    }

    /**
     * Returns true if field gbuid is set (has been asigned a value) and false
     * otherwise
     */
    public boolean isSetgbuid() {
        return __isset_bit_vector.get(__gbUID_ISSET_ID);
    }

    public void setgbuidIsSet(boolean value) {
        __isset_bit_vector.set(__gbUID_ISSET_ID, value);
    }

    /**
     * 频道ID
     */
    public long getgbChannelId() {
        return this.gbChannelId;
    }

    /**
     * 频道ID
     */
    public GetgbChannelBalanceRequest setgbChannelId(long gbChannelId) {
        this.gbChannelId = gbChannelId;
        setgbChannelIdIsSet(true);
        return this;
    }

    public void unsetgbChannelId() {
        __isset_bit_vector.clear(__gbCHANNELID_ISSET_ID);
    }

    /**
     * Returns true if field gbChannelId is set (has been asigned a value) and
     * false otherwise
     */
    public boolean isSetgbChannelId() {
        return __isset_bit_vector.get(__gbCHANNELID_ISSET_ID);
    }

    public void setgbChannelIdIsSet(boolean value) {
        __isset_bit_vector.set(__gbCHANNELID_ISSET_ID, value);
    }

    /**
     * 调用此接口的应用ID，比如gb音乐叫gbMUSIC
     */
    public String getAppId() {
        return this.appId;
    }

    /**
     * 调用此接口的应用ID，比如gb音乐叫gbMUSIC
     */
    public GetgbChannelBalanceRequest setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public void unsetAppId() {
        this.appId = null;
    }

    /**
     * Returns true if field appId is set (has been asigned a value) and false
     * otherwise
     */
    public boolean isSetAppId() {
        return this.appId != null;
    }

    public void setAppIdIsSet(boolean value) {
        if (!value) {
            this.appId = null;
        }
    }

    /**
     * 货币类型
     */
    public int getBalanceType() {
        return this.balanceType;
    }

    /**
     * 货币类型
     */
    public GetgbChannelBalanceRequest setBalanceType(int balanceType) {
        this.balanceType = balanceType;
        setBalanceTypeIsSet(true);
        return this;
    }

    public void unsetBalanceType() {
        __isset_bit_vector.clear(__BALANCETYPE_ISSET_ID);
    }

    /**
     * Returns true if field balanceType is set (has been asigned a value) and
     * false otherwise
     */
    public boolean isSetBalanceType() {
        return __isset_bit_vector.get(__BALANCETYPE_ISSET_ID);
    }

    public void setBalanceTypeIsSet(boolean value) {
        __isset_bit_vector.set(__BALANCETYPE_ISSET_ID, value);
    }

    /**
     * 时间戳，和充值系统的时间差不超过5分钟
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * 时间戳，和充值系统的时间差不超过5分钟
     */
    public GetgbChannelBalanceRequest setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        setTimestampIsSet(true);
        return this;
    }

    public void unsetTimestamp() {
        __isset_bit_vector.clear(__TIMESTAMP_ISSET_ID);
    }

    /**
     * Returns true if field timestamp is set (has been asigned a value) and
     * false otherwise
     */
    public boolean isSetTimestamp() {
        return __isset_bit_vector.get(__TIMESTAMP_ISSET_ID);
    }

    public void setTimestampIsSet(boolean value) {
        __isset_bit_vector.set(__TIMESTAMP_ISSET_ID, value);
    }

    /**
     * 校验码，HMacSha1
     */
    public String getSign() {
        return this.sign;
    }

    /**
     * 校验码，HMacSha1
     */
    public GetgbChannelBalanceRequest setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public void unsetSign() {
        this.sign = null;
    }

    /**
     * Returns true if field sign is set (has been asigned a value) and false
     * otherwise
     */
    public boolean isSetSign() {
        return this.sign != null;
    }

    public void setSignIsSet(boolean value) {
        if (!value) {
            this.sign = null;
        }
    }

    public void setFieldValue(_Fields field, Object value) {
        switch (field) {
        case gbUID:
            if (value == null) {
                unsetgbuid();
            } else {
                setgbuid((Long) value);
            }
            break;

        case gb_CHANNEL_ID:
            if (value == null) {
                unsetgbChannelId();
            } else {
                setgbChannelId((Long) value);
            }
            break;

        case APP_ID:
            if (value == null) {
                unsetAppId();
            } else {
                setAppId((String) value);
            }
            break;

        case BALANCE_TYPE:
            if (value == null) {
                unsetBalanceType();
            } else {
                setBalanceType((Integer) value);
            }
            break;

        case TIMESTAMP:
            if (value == null) {
                unsetTimestamp();
            } else {
                setTimestamp((Long) value);
            }
            break;

        case SIGN:
            if (value == null) {
                unsetSign();
            } else {
                setSign((String) value);
            }
            break;

        }
    }

    public Object getFieldValue(_Fields field) {
        switch (field) {
        case gbUID:
            return new Long(getgbuid());

        case gb_CHANNEL_ID:
            return new Long(getgbChannelId());

        case APP_ID:
            return getAppId();

        case BALANCE_TYPE:
            return new Integer(getBalanceType());

        case TIMESTAMP:
            return new Long(getTimestamp());

        case SIGN:
            return getSign();

        }
        throw new IllegalStateException();
    }

    /**
     * Returns true if field corresponding to fieldID is set (has been asigned a
     * value) and false otherwise
     */
    public boolean isSet(_Fields field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }

        switch (field) {
        case gbUID:
            return isSetgbuid();
        case gb_CHANNEL_ID:
            return isSetgbChannelId();
        case APP_ID:
            return isSetAppId();
        case BALANCE_TYPE:
            return isSetBalanceType();
        case TIMESTAMP:
            return isSetTimestamp();
        case SIGN:
            return isSetSign();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;
        if (that instanceof GetgbChannelBalanceRequest)
            return this.equals((GetgbChannelBalanceRequest) that);
        return false;
    }

    public boolean equals(GetgbChannelBalanceRequest that) {
        if (that == null)
            return false;

        boolean this_present_gbuid = true;
        boolean that_present_gbuid = true;
        if (this_present_gbuid || that_present_gbuid) {
            if (!(this_present_gbuid && that_present_gbuid))
                return false;
            if (this.gbuid != that.gbuid)
                return false;
        }

        boolean this_present_gbChannelId = true;
        boolean that_present_gbChannelId = true;
        if (this_present_gbChannelId || that_present_gbChannelId) {
            if (!(this_present_gbChannelId && that_present_gbChannelId))
                return false;
            if (this.gbChannelId != that.gbChannelId)
                return false;
        }

        boolean this_present_appId = true && this.isSetAppId();
        boolean that_present_appId = true && that.isSetAppId();
        if (this_present_appId || that_present_appId) {
            if (!(this_present_appId && that_present_appId))
                return false;
            if (!this.appId.equals(that.appId))
                return false;
        }

        boolean this_present_balanceType = true;
        boolean that_present_balanceType = true;
        if (this_present_balanceType || that_present_balanceType) {
            if (!(this_present_balanceType && that_present_balanceType))
                return false;
            if (this.balanceType != that.balanceType)
                return false;
        }

        boolean this_present_timestamp = true;
        boolean that_present_timestamp = true;
        if (this_present_timestamp || that_present_timestamp) {
            if (!(this_present_timestamp && that_present_timestamp))
                return false;
            if (this.timestamp != that.timestamp)
                return false;
        }

        boolean this_present_sign = true && this.isSetSign();
        boolean that_present_sign = true && that.isSetSign();
        if (this_present_sign || that_present_sign) {
            if (!(this_present_sign && that_present_sign))
                return false;
            if (!this.sign.equals(that.sign))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public int compareTo(GetgbChannelBalanceRequest other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;
        GetgbChannelBalanceRequest typedOther = (GetgbChannelBalanceRequest) other;

        lastComparison = Boolean.valueOf(isSetgbuid()).compareTo(typedOther.isSetgbuid());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetgbuid()) {
            lastComparison = TBaseHelper.compareTo(this.gbuid, typedOther.gbuid);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetgbChannelId()).compareTo(typedOther.isSetgbChannelId());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetgbChannelId()) {
            lastComparison = TBaseHelper.compareTo(this.gbChannelId, typedOther.gbChannelId);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetAppId()).compareTo(typedOther.isSetAppId());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetAppId()) {
            lastComparison = TBaseHelper.compareTo(this.appId, typedOther.appId);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetBalanceType()).compareTo(typedOther.isSetBalanceType());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetBalanceType()) {
            lastComparison = TBaseHelper.compareTo(this.balanceType, typedOther.balanceType);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetTimestamp()).compareTo(typedOther.isSetTimestamp());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetTimestamp()) {
            lastComparison = TBaseHelper.compareTo(this.timestamp, typedOther.timestamp);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetSign()).compareTo(typedOther.isSetSign());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetSign()) {
            lastComparison = TBaseHelper.compareTo(this.sign, typedOther.sign);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }

    public _Fields fieldForId(int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }

    public void read(TProtocol iprot) throws TException {
        TField field;
        iprot.readStructBegin();
        while (true) {
            field = iprot.readFieldBegin();
            if (field.type == TType.STOP) {
                break;
            }
            switch (field.id) {
            case 1: // gbUID
                if (field.type == TType.I64) {
                    this.gbuid = iprot.readI64();
                    setgbuidIsSet(true);
                } else {
                    TProtocolUtil.skip(iprot, field.type);
                }
                break;
            case 2: // gb_CHANNEL_ID
                if (field.type == TType.I64) {
                    this.gbChannelId = iprot.readI64();
                    setgbChannelIdIsSet(true);
                } else {
                    TProtocolUtil.skip(iprot, field.type);
                }
                break;
            case 3: // APP_ID
                if (field.type == TType.STRING) {
                    this.appId = iprot.readString();
                } else {
                    TProtocolUtil.skip(iprot, field.type);
                }
                break;
            case 4: // BALANCE_TYPE
                if (field.type == TType.I32) {
                    this.balanceType = iprot.readI32();
                    setBalanceTypeIsSet(true);
                } else {
                    TProtocolUtil.skip(iprot, field.type);
                }
                break;
            case 5: // TIMESTAMP
                if (field.type == TType.I64) {
                    this.timestamp = iprot.readI64();
                    setTimestampIsSet(true);
                } else {
                    TProtocolUtil.skip(iprot, field.type);
                }
                break;
            case 6: // SIGN
                if (field.type == TType.STRING) {
                    this.sign = iprot.readString();
                } else {
                    TProtocolUtil.skip(iprot, field.type);
                }
                break;
            default:
                TProtocolUtil.skip(iprot, field.type);
            }
            iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked
        // in the validate method
        if (!isSetgbuid()) {
            throw new TProtocolException("Required field 'gbuid' was not found in serialized data! Struct: "
                    + toString());
        }
        if (!isSetgbChannelId()) {
            throw new TProtocolException("Required field 'gbChannelId' was not found in serialized data! Struct: "
                    + toString());
        }
        if (!isSetBalanceType()) {
            throw new TProtocolException("Required field 'balanceType' was not found in serialized data! Struct: "
                    + toString());
        }
        if (!isSetTimestamp()) {
            throw new TProtocolException("Required field 'timestamp' was not found in serialized data! Struct: "
                    + toString());
        }
        validate();
    }

    public void write(TProtocol oprot) throws TException {
        validate();

        oprot.writeStructBegin(STRUCT_DESC);
        oprot.writeFieldBegin(gbUID_FIELD_DESC);
        oprot.writeI64(this.gbuid);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(gb_CHANNEL_ID_FIELD_DESC);
        oprot.writeI64(this.gbChannelId);
        oprot.writeFieldEnd();
        if (this.appId != null) {
            oprot.writeFieldBegin(APP_ID_FIELD_DESC);
            oprot.writeString(this.appId);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(BALANCE_TYPE_FIELD_DESC);
        oprot.writeI32(this.balanceType);
        oprot.writeFieldEnd();
        oprot.writeFieldBegin(TIMESTAMP_FIELD_DESC);
        oprot.writeI64(this.timestamp);
        oprot.writeFieldEnd();
        if (this.sign != null) {
            oprot.writeFieldBegin(SIGN_FIELD_DESC);
            oprot.writeString(this.sign);
            oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GetgbChannelBalanceRequest(");
        boolean first = true;

        sb.append("gbuid:");
        sb.append(this.gbuid);
        first = false;
        if (!first)
            sb.append(", ");
        sb.append("gbChannelId:");
        sb.append(this.gbChannelId);
        first = false;
        if (!first)
            sb.append(", ");
        sb.append("appId:");
        if (this.appId == null) {
            sb.append("null");
        } else {
            sb.append(this.appId);
        }
        first = false;
        if (!first)
            sb.append(", ");
        sb.append("balanceType:");
        sb.append(this.balanceType);
        first = false;
        if (!first)
            sb.append(", ");
        sb.append("timestamp:");
        sb.append(this.timestamp);
        first = false;
        if (!first)
            sb.append(", ");
        sb.append("sign:");
        if (this.sign == null) {
            sb.append("null");
        } else {
            sb.append(this.sign);
        }
        first = false;
        sb.append(")");
        return sb.toString();
    }

    public void validate() throws TException {
        // check for required fields
        // alas, we cannot check 'gbuid' because it's a primitive and you chose
        // the non-beans generator.
        // alas, we cannot check 'gbChannelId' because it's a primitive and you
        // chose the non-beans generator.
        if (appId == null) {
            throw new TProtocolException("Required field 'appId' was not present! Struct: " + toString());
        }
        // alas, we cannot check 'balanceType' because it's a primitive and you
        // chose the non-beans generator.
        // alas, we cannot check 'timestamp' because it's a primitive and you
        // chose the non-beans generator.
        if (sign == null) {
            throw new TProtocolException("Required field 'sign' was not present! Struct: " + toString());
        }
    }

}
