package com.guzhi.pay.thrift.udb.gen;

/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.thrift.*;
import org.apache.thrift.async.*;
import org.apache.thrift.meta_data.*;
import org.apache.thrift.transport.*;
import org.apache.thrift.protocol.*;

public class VerifyAppTokenRes2 implements TBase<VerifyAppTokenRes2, VerifyAppTokenRes2._Fields>, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("VerifyAppTokenRes2");

  private static final TField CONTEXT_FIELD_DESC = new TField("context", TType.STRING, (short)1);
  private static final TField RESCODE_FIELD_DESC = new TField("rescode", TType.I32, (short)2);
  private static final TField STRERROR_FIELD_DESC = new TField("strerror", TType.STRING, (short)3);
  private static final TField PASSPORT_FIELD_DESC = new TField("passport", TType.STRING, (short)4);
  private static final TField gbUID_FIELD_DESC = new TField("gbuid", TType.I32, (short)5);
  private static final TField PRODUCTID_FIELD_DESC = new TField("productid", TType.STRING, (short)6);

  public String context;
  public int rescode;
  public String strerror;
  public String passport;
  public int gbuid;
  public String productid;

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements TFieldIdEnum {
    CONTEXT((short)1, "context"),
    RESCODE((short)2, "rescode"),
    STRERROR((short)3, "strerror"),
    PASSPORT((short)4, "passport"),
    gbUID((short)5, "gbuid"),
    PRODUCTID((short)6, "productid");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // CONTEXT
          return CONTEXT;
        case 2: // RESCODE
          return RESCODE;
        case 3: // STRERROR
          return STRERROR;
        case 4: // PASSPORT
          return PASSPORT;
        case 5: // gbUID
          return gbUID;
        case 6: // PRODUCTID
          return PRODUCTID;
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
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
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
  private static final int __RESCODE_ISSET_ID = 0;
  private static final int __gbUID_ISSET_ID = 1;
  private BitSet __isset_bit_vector = new BitSet(2);

  public static final Map<_Fields, FieldMetaData> metaDataMap;
  static {
    Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.CONTEXT, new FieldMetaData("context", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMap.put(_Fields.RESCODE, new FieldMetaData("rescode", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMap.put(_Fields.STRERROR, new FieldMetaData("strerror", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.STRING)));
    tmpMap.put(_Fields.PASSPORT, new FieldMetaData("passport", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    tmpMap.put(_Fields.gbUID, new FieldMetaData("gbuid", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.I32)));
    tmpMap.put(_Fields.PRODUCTID, new FieldMetaData("productid", TFieldRequirementType.OPTIONAL, 
        new FieldValueMetaData(TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    FieldMetaData.addStructMetaDataMap(VerifyAppTokenRes2.class, metaDataMap);
  }

  public VerifyAppTokenRes2() {
    this.passport = "";

    this.gbuid = 0;

    this.productid = "";

  }

  public VerifyAppTokenRes2(
    String context,
    int rescode,
    String strerror)
  {
    this();
    this.context = context;
    this.rescode = rescode;
    setRescodeIsSet(true);
    this.strerror = strerror;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public VerifyAppTokenRes2(VerifyAppTokenRes2 other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    if (other.isSetContext()) {
      this.context = other.context;
    }
    this.rescode = other.rescode;
    if (other.isSetStrerror()) {
      this.strerror = other.strerror;
    }
    if (other.isSetPassport()) {
      this.passport = other.passport;
    }
    this.gbuid = other.gbuid;
    if (other.isSetProductid()) {
      this.productid = other.productid;
    }
  }

  public VerifyAppTokenRes2 deepCopy() {
    return new VerifyAppTokenRes2(this);
  }

  @Override
  public void clear() {
    this.context = null;
    setRescodeIsSet(false);
    this.rescode = 0;
    this.strerror = null;
    this.passport = "";

    this.gbuid = 0;

    this.productid = "";

  }

  public String getContext() {
    return this.context;
  }

  public VerifyAppTokenRes2 setContext(String context) {
    this.context = context;
    return this;
  }

  public void unsetContext() {
    this.context = null;
  }

  /** Returns true if field context is set (has been asigned a value) and false otherwise */
  public boolean isSetContext() {
    return this.context != null;
  }

  public void setContextIsSet(boolean value) {
    if (!value) {
      this.context = null;
    }
  }

  public int getRescode() {
    return this.rescode;
  }

  public VerifyAppTokenRes2 setRescode(int rescode) {
    this.rescode = rescode;
    setRescodeIsSet(true);
    return this;
  }

  public void unsetRescode() {
    __isset_bit_vector.clear(__RESCODE_ISSET_ID);
  }

  /** Returns true if field rescode is set (has been asigned a value) and false otherwise */
  public boolean isSetRescode() {
    return __isset_bit_vector.get(__RESCODE_ISSET_ID);
  }

  public void setRescodeIsSet(boolean value) {
    __isset_bit_vector.set(__RESCODE_ISSET_ID, value);
  }

  public String getStrerror() {
    return this.strerror;
  }

  public VerifyAppTokenRes2 setStrerror(String strerror) {
    this.strerror = strerror;
    return this;
  }

  public void unsetStrerror() {
    this.strerror = null;
  }

  /** Returns true if field strerror is set (has been asigned a value) and false otherwise */
  public boolean isSetStrerror() {
    return this.strerror != null;
  }

  public void setStrerrorIsSet(boolean value) {
    if (!value) {
      this.strerror = null;
    }
  }

  public String getPassport() {
    return this.passport;
  }

  public VerifyAppTokenRes2 setPassport(String passport) {
    this.passport = passport;
    return this;
  }

  public void unsetPassport() {
    this.passport = null;
  }

  /** Returns true if field passport is set (has been asigned a value) and false otherwise */
  public boolean isSetPassport() {
    return this.passport != null;
  }

  public void setPassportIsSet(boolean value) {
    if (!value) {
      this.passport = null;
    }
  }

  public int getgbuid() {
    return this.gbuid;
  }

  public VerifyAppTokenRes2 setgbuid(int gbuid) {
    this.gbuid = gbuid;
    setgbuidIsSet(true);
    return this;
  }

  public void unsetgbuid() {
    __isset_bit_vector.clear(__gbUID_ISSET_ID);
  }

  /** Returns true if field gbuid is set (has been asigned a value) and false otherwise */
  public boolean isSetgbuid() {
    return __isset_bit_vector.get(__gbUID_ISSET_ID);
  }

  public void setgbuidIsSet(boolean value) {
    __isset_bit_vector.set(__gbUID_ISSET_ID, value);
  }

  public String getProductid() {
    return this.productid;
  }

  public VerifyAppTokenRes2 setProductid(String productid) {
    this.productid = productid;
    return this;
  }

  public void unsetProductid() {
    this.productid = null;
  }

  /** Returns true if field productid is set (has been asigned a value) and false otherwise */
  public boolean isSetProductid() {
    return this.productid != null;
  }

  public void setProductidIsSet(boolean value) {
    if (!value) {
      this.productid = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case CONTEXT:
      if (value == null) {
        unsetContext();
      } else {
        setContext((String)value);
      }
      break;

    case RESCODE:
      if (value == null) {
        unsetRescode();
      } else {
        setRescode((Integer)value);
      }
      break;

    case STRERROR:
      if (value == null) {
        unsetStrerror();
      } else {
        setStrerror((String)value);
      }
      break;

    case PASSPORT:
      if (value == null) {
        unsetPassport();
      } else {
        setPassport((String)value);
      }
      break;

    case gbUID:
      if (value == null) {
        unsetgbuid();
      } else {
        setgbuid((Integer)value);
      }
      break;

    case PRODUCTID:
      if (value == null) {
        unsetProductid();
      } else {
        setProductid((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case CONTEXT:
      return getContext();

    case RESCODE:
      return new Integer(getRescode());

    case STRERROR:
      return getStrerror();

    case PASSPORT:
      return getPassport();

    case gbUID:
      return new Integer(getgbuid());

    case PRODUCTID:
      return getProductid();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been asigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case CONTEXT:
      return isSetContext();
    case RESCODE:
      return isSetRescode();
    case STRERROR:
      return isSetStrerror();
    case PASSPORT:
      return isSetPassport();
    case gbUID:
      return isSetgbuid();
    case PRODUCTID:
      return isSetProductid();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof VerifyAppTokenRes2)
      return this.equals((VerifyAppTokenRes2)that);
    return false;
  }

  public boolean equals(VerifyAppTokenRes2 that) {
    if (that == null)
      return false;

    boolean this_present_context = true && this.isSetContext();
    boolean that_present_context = true && that.isSetContext();
    if (this_present_context || that_present_context) {
      if (!(this_present_context && that_present_context))
        return false;
      if (!this.context.equals(that.context))
        return false;
    }

    boolean this_present_rescode = true;
    boolean that_present_rescode = true;
    if (this_present_rescode || that_present_rescode) {
      if (!(this_present_rescode && that_present_rescode))
        return false;
      if (this.rescode != that.rescode)
        return false;
    }

    boolean this_present_strerror = true && this.isSetStrerror();
    boolean that_present_strerror = true && that.isSetStrerror();
    if (this_present_strerror || that_present_strerror) {
      if (!(this_present_strerror && that_present_strerror))
        return false;
      if (!this.strerror.equals(that.strerror))
        return false;
    }

    boolean this_present_passport = true && this.isSetPassport();
    boolean that_present_passport = true && that.isSetPassport();
    if (this_present_passport || that_present_passport) {
      if (!(this_present_passport && that_present_passport))
        return false;
      if (!this.passport.equals(that.passport))
        return false;
    }

    boolean this_present_gbuid = true && this.isSetgbuid();
    boolean that_present_gbuid = true && that.isSetgbuid();
    if (this_present_gbuid || that_present_gbuid) {
      if (!(this_present_gbuid && that_present_gbuid))
        return false;
      if (this.gbuid != that.gbuid)
        return false;
    }

    boolean this_present_productid = true && this.isSetProductid();
    boolean that_present_productid = true && that.isSetProductid();
    if (this_present_productid || that_present_productid) {
      if (!(this_present_productid && that_present_productid))
        return false;
      if (!this.productid.equals(that.productid))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(VerifyAppTokenRes2 other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    VerifyAppTokenRes2 typedOther = (VerifyAppTokenRes2)other;

    lastComparison = Boolean.valueOf(isSetContext()).compareTo(typedOther.isSetContext());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetContext()) {      lastComparison = TBaseHelper.compareTo(this.context, typedOther.context);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRescode()).compareTo(typedOther.isSetRescode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRescode()) {      lastComparison = TBaseHelper.compareTo(this.rescode, typedOther.rescode);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStrerror()).compareTo(typedOther.isSetStrerror());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStrerror()) {      lastComparison = TBaseHelper.compareTo(this.strerror, typedOther.strerror);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPassport()).compareTo(typedOther.isSetPassport());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPassport()) {      lastComparison = TBaseHelper.compareTo(this.passport, typedOther.passport);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetgbuid()).compareTo(typedOther.isSetgbuid());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetgbuid()) {      lastComparison = TBaseHelper.compareTo(this.gbuid, typedOther.gbuid);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetProductid()).compareTo(typedOther.isSetProductid());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetProductid()) {      lastComparison = TBaseHelper.compareTo(this.productid, typedOther.productid);
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
    while (true)
    {
      field = iprot.readFieldBegin();
      if (field.type == TType.STOP) { 
        break;
      }
      switch (field.id) {
        case 1: // CONTEXT
          if (field.type == TType.STRING) {
            this.context = iprot.readString();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // RESCODE
          if (field.type == TType.I32) {
            this.rescode = iprot.readI32();
            setRescodeIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // STRERROR
          if (field.type == TType.STRING) {
            this.strerror = iprot.readString();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 4: // PASSPORT
          if (field.type == TType.STRING) {
            this.passport = iprot.readString();
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 5: // gbUID
          if (field.type == TType.I32) {
            this.gbuid = iprot.readI32();
            setgbuidIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 6: // PRODUCTID
          if (field.type == TType.STRING) {
            this.productid = iprot.readString();
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

    // check for required fields of primitive type, which can't be checked in the validate method
    validate();
  }

  public void write(TProtocol oprot) throws TException {
    validate();

    oprot.writeStructBegin(STRUCT_DESC);
    if (this.context != null) {
      oprot.writeFieldBegin(CONTEXT_FIELD_DESC);
      oprot.writeString(this.context);
      oprot.writeFieldEnd();
    }
    oprot.writeFieldBegin(RESCODE_FIELD_DESC);
    oprot.writeI32(this.rescode);
    oprot.writeFieldEnd();
    if (this.strerror != null) {
      oprot.writeFieldBegin(STRERROR_FIELD_DESC);
      oprot.writeString(this.strerror);
      oprot.writeFieldEnd();
    }
    if (this.passport != null) {
      if (isSetPassport()) {
        oprot.writeFieldBegin(PASSPORT_FIELD_DESC);
        oprot.writeString(this.passport);
        oprot.writeFieldEnd();
      }
    }
    if (isSetgbuid()) {
      oprot.writeFieldBegin(gbUID_FIELD_DESC);
      oprot.writeI32(this.gbuid);
      oprot.writeFieldEnd();
    }
    if (this.productid != null) {
      if (isSetProductid()) {
        oprot.writeFieldBegin(PRODUCTID_FIELD_DESC);
        oprot.writeString(this.productid);
        oprot.writeFieldEnd();
      }
    }
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("VerifyAppTokenRes2(");
    boolean first = true;

    sb.append("context:");
    if (this.context == null) {
      sb.append("null");
    } else {
      sb.append(this.context);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("rescode:");
    sb.append(this.rescode);
    first = false;
    if (!first) sb.append(", ");
    sb.append("strerror:");
    if (this.strerror == null) {
      sb.append("null");
    } else {
      sb.append(this.strerror);
    }
    first = false;
    if (isSetPassport()) {
      if (!first) sb.append(", ");
      sb.append("passport:");
      if (this.passport == null) {
        sb.append("null");
      } else {
        sb.append(this.passport);
      }
      first = false;
    }
    if (isSetgbuid()) {
      if (!first) sb.append(", ");
      sb.append("gbuid:");
      sb.append(this.gbuid);
      first = false;
    }
    if (isSetProductid()) {
      if (!first) sb.append(", ");
      sb.append("productid:");
      if (this.productid == null) {
        sb.append("null");
      } else {
        sb.append(this.productid);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

