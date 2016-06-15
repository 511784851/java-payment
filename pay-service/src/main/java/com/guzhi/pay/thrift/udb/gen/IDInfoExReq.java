/**
 * Autogenerated by Thrift
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
package com.guzhi.pay.thrift.udb.gen;

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

public class IDInfoExReq implements TBase<IDInfoExReq, IDInfoExReq._Fields>, java.io.Serializable, Cloneable {
  private static final TStruct STRUCT_DESC = new TStruct("IDInfoExReq");

  private static final TField gbUID_FIELD_DESC = new TField("gbuid", TType.I32, (short)1);
  private static final TField LEN_FRONT_FIELD_DESC = new TField("lenFront", TType.I32, (short)2);
  private static final TField LEN_BACK_FIELD_DESC = new TField("lenBack", TType.I32, (short)3);

  public int gbuid;
  public int lenFront;
  public int lenBack;

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements TFieldIdEnum {
    gbUID((short)1, "gbuid"),
    LEN_FRONT((short)2, "lenFront"),
    LEN_BACK((short)3, "lenBack");

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
        case 1: // gbUID
          return gbUID;
        case 2: // LEN_FRONT
          return LEN_FRONT;
        case 3: // LEN_BACK
          return LEN_BACK;
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
  private static final int __gbUID_ISSET_ID = 0;
  private static final int __LENFRONT_ISSET_ID = 1;
  private static final int __LENBACK_ISSET_ID = 2;
  private BitSet __isset_bit_vector = new BitSet(3);

  public static final Map<_Fields, FieldMetaData> metaDataMap;
  static {
    Map<_Fields, FieldMetaData> tmpMap = new EnumMap<_Fields, FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.gbUID, new FieldMetaData("gbuid", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMap.put(_Fields.LEN_FRONT, new FieldMetaData("lenFront", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    tmpMap.put(_Fields.LEN_BACK, new FieldMetaData("lenBack", TFieldRequirementType.DEFAULT, 
        new FieldValueMetaData(TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    FieldMetaData.addStructMetaDataMap(IDInfoExReq.class, metaDataMap);
  }

  public IDInfoExReq() {
  }

  public IDInfoExReq(
    int gbuid,
    int lenFront,
    int lenBack)
  {
    this();
    this.gbuid = gbuid;
    setgbuidIsSet(true);
    this.lenFront = lenFront;
    setLenFrontIsSet(true);
    this.lenBack = lenBack;
    setLenBackIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public IDInfoExReq(IDInfoExReq other) {
    __isset_bit_vector.clear();
    __isset_bit_vector.or(other.__isset_bit_vector);
    this.gbuid = other.gbuid;
    this.lenFront = other.lenFront;
    this.lenBack = other.lenBack;
  }

  public IDInfoExReq deepCopy() {
    return new IDInfoExReq(this);
  }

  @Override
  public void clear() {
    setgbuidIsSet(false);
    this.gbuid = 0;
    setLenFrontIsSet(false);
    this.lenFront = 0;
    setLenBackIsSet(false);
    this.lenBack = 0;
  }

  public int getgbuid() {
    return this.gbuid;
  }

  public IDInfoExReq setgbuid(int gbuid) {
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

  public int getLenFront() {
    return this.lenFront;
  }

  public IDInfoExReq setLenFront(int lenFront) {
    this.lenFront = lenFront;
    setLenFrontIsSet(true);
    return this;
  }

  public void unsetLenFront() {
    __isset_bit_vector.clear(__LENFRONT_ISSET_ID);
  }

  /** Returns true if field lenFront is set (has been asigned a value) and false otherwise */
  public boolean isSetLenFront() {
    return __isset_bit_vector.get(__LENFRONT_ISSET_ID);
  }

  public void setLenFrontIsSet(boolean value) {
    __isset_bit_vector.set(__LENFRONT_ISSET_ID, value);
  }

  public int getLenBack() {
    return this.lenBack;
  }

  public IDInfoExReq setLenBack(int lenBack) {
    this.lenBack = lenBack;
    setLenBackIsSet(true);
    return this;
  }

  public void unsetLenBack() {
    __isset_bit_vector.clear(__LENBACK_ISSET_ID);
  }

  /** Returns true if field lenBack is set (has been asigned a value) and false otherwise */
  public boolean isSetLenBack() {
    return __isset_bit_vector.get(__LENBACK_ISSET_ID);
  }

  public void setLenBackIsSet(boolean value) {
    __isset_bit_vector.set(__LENBACK_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case gbUID:
      if (value == null) {
        unsetgbuid();
      } else {
        setgbuid((Integer)value);
      }
      break;

    case LEN_FRONT:
      if (value == null) {
        unsetLenFront();
      } else {
        setLenFront((Integer)value);
      }
      break;

    case LEN_BACK:
      if (value == null) {
        unsetLenBack();
      } else {
        setLenBack((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case gbUID:
      return new Integer(getgbuid());

    case LEN_FRONT:
      return new Integer(getLenFront());

    case LEN_BACK:
      return new Integer(getLenBack());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been asigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case gbUID:
      return isSetgbuid();
    case LEN_FRONT:
      return isSetLenFront();
    case LEN_BACK:
      return isSetLenBack();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof IDInfoExReq)
      return this.equals((IDInfoExReq)that);
    return false;
  }

  public boolean equals(IDInfoExReq that) {
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

    boolean this_present_lenFront = true;
    boolean that_present_lenFront = true;
    if (this_present_lenFront || that_present_lenFront) {
      if (!(this_present_lenFront && that_present_lenFront))
        return false;
      if (this.lenFront != that.lenFront)
        return false;
    }

    boolean this_present_lenBack = true;
    boolean that_present_lenBack = true;
    if (this_present_lenBack || that_present_lenBack) {
      if (!(this_present_lenBack && that_present_lenBack))
        return false;
      if (this.lenBack != that.lenBack)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(IDInfoExReq other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    IDInfoExReq typedOther = (IDInfoExReq)other;

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
    lastComparison = Boolean.valueOf(isSetLenFront()).compareTo(typedOther.isSetLenFront());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLenFront()) {
      lastComparison = TBaseHelper.compareTo(this.lenFront, typedOther.lenFront);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetLenBack()).compareTo(typedOther.isSetLenBack());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetLenBack()) {
      lastComparison = TBaseHelper.compareTo(this.lenBack, typedOther.lenBack);
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
        case 1: // gbUID
          if (field.type == TType.I32) {
            this.gbuid = iprot.readI32();
            setgbuidIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 2: // LEN_FRONT
          if (field.type == TType.I32) {
            this.lenFront = iprot.readI32();
            setLenFrontIsSet(true);
          } else { 
            TProtocolUtil.skip(iprot, field.type);
          }
          break;
        case 3: // LEN_BACK
          if (field.type == TType.I32) {
            this.lenBack = iprot.readI32();
            setLenBackIsSet(true);
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
    oprot.writeFieldBegin(gbUID_FIELD_DESC);
    oprot.writeI32(this.gbuid);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(LEN_FRONT_FIELD_DESC);
    oprot.writeI32(this.lenFront);
    oprot.writeFieldEnd();
    oprot.writeFieldBegin(LEN_BACK_FIELD_DESC);
    oprot.writeI32(this.lenBack);
    oprot.writeFieldEnd();
    oprot.writeFieldStop();
    oprot.writeStructEnd();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("IDInfoExReq(");
    boolean first = true;

    sb.append("gbuid:");
    sb.append(this.gbuid);
    first = false;
    if (!first) sb.append(", ");
    sb.append("lenFront:");
    sb.append(this.lenFront);
    first = false;
    if (!first) sb.append(", ");
    sb.append("lenBack:");
    sb.append(this.lenBack);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws TException {
    // check for required fields
  }

}

