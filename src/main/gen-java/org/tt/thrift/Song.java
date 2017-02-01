/**
 * Autogenerated by Thrift Compiler (1.0.0-dev)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.tt.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (1.0.0-dev)", date = "2017-01-13")
public class Song implements org.apache.thrift.TBase<Song, Song._Fields>, java.io.Serializable, Cloneable, Comparable<Song> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Song");

  private static final org.apache.thrift.protocol.TField SONG_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("songName", org.apache.thrift.protocol.TType.STRING, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new SongStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new SongTupleSchemeFactory();

  public java.lang.String songName; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    SONG_NAME((short)1, "songName");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // SONG_NAME
          return SONG_NAME;
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
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.SONG_NAME, new org.apache.thrift.meta_data.FieldMetaData("songName", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Song.class, metaDataMap);
  }

  public Song() {
  }

  public Song(
    java.lang.String songName)
  {
    this();
    this.songName = songName;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Song(Song other) {
    if (other.isSetSongName()) {
      this.songName = other.songName;
    }
  }

  public Song deepCopy() {
    return new Song(this);
  }

  @Override
  public void clear() {
    this.songName = null;
  }

  public java.lang.String getSongName() {
    return this.songName;
  }

  public Song setSongName(java.lang.String songName) {
    this.songName = songName;
    return this;
  }

  public void unsetSongName() {
    this.songName = null;
  }

  /** Returns true if field songName is set (has been assigned a value) and false otherwise */
  public boolean isSetSongName() {
    return this.songName != null;
  }

  public void setSongNameIsSet(boolean value) {
    if (!value) {
      this.songName = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case SONG_NAME:
      if (value == null) {
        unsetSongName();
      } else {
        setSongName((java.lang.String)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case SONG_NAME:
      return getSongName();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case SONG_NAME:
      return isSetSongName();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof Song)
      return this.equals((Song)that);
    return false;
  }

  public boolean equals(Song that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_songName = true && this.isSetSongName();
    boolean that_present_songName = true && that.isSetSongName();
    if (this_present_songName || that_present_songName) {
      if (!(this_present_songName && that_present_songName))
        return false;
      if (!this.songName.equals(that.songName))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetSongName()) ? 131071 : 524287);
    if (isSetSongName())
      hashCode = hashCode * 8191 + songName.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(Song other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetSongName()).compareTo(other.isSetSongName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSongName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.songName, other.songName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Song(");
    boolean first = true;

    sb.append("songName:");
    if (this.songName == null) {
      sb.append("null");
    } else {
      sb.append(this.songName);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class SongStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public SongStandardScheme getScheme() {
      return new SongStandardScheme();
    }
  }

  private static class SongStandardScheme extends org.apache.thrift.scheme.StandardScheme<Song> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Song struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // SONG_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.songName = iprot.readString();
              struct.setSongNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Song struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.songName != null) {
        oprot.writeFieldBegin(SONG_NAME_FIELD_DESC);
        oprot.writeString(struct.songName);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class SongTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public SongTupleScheme getScheme() {
      return new SongTupleScheme();
    }
  }

  private static class SongTupleScheme extends org.apache.thrift.scheme.TupleScheme<Song> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Song struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetSongName()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetSongName()) {
        oprot.writeString(struct.songName);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Song struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.songName = iprot.readString();
        struct.setSongNameIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

