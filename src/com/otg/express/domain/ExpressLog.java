package com.otg.express.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 15-11-12.
 */
public class ExpressLog implements Serializable {
    private long id;
    private String name;
    private String sex;
    private String nation;
    private String birthday;
    private String address;
    private String idCard;
    private String signDepart;
    private String validTime;
    private byte[] bitmap;
    private byte[] unpack_img;
    private byte[] sender_img;
    private byte[] face_img;
    private String shapeCode;
    private String DN;
    private String longitude;
    private String latitude;
    private String sendTime;
    private String express_name;
    private String express_company;
    private String express_number;
    private String phone;
    private String type;
    private String contact;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getExpress_name() {
        return express_name;
    }

    public void setExpress_name(String express_name) {
        this.express_name = express_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpress_company() {
        return express_company;
    }

    public void setExpress_company(String express_company) {
        this.express_company = express_company;
    }

    public String getExpress_number() {
        return express_number;
    }

    public void setExpress_number(String express_number) {
        this.express_number = express_number;
    }

    public ExpressLog(long l) {
        this.id = l;
    }

    public ExpressLog() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getSignDepart() {
        return signDepart;
    }

    public void setSignDepart(String signDepart) {
        this.signDepart = signDepart;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public String getShapeCode() {
        return shapeCode;
    }

    public void setShapeCode(String shapeCode) {
        this.shapeCode = shapeCode;
    }

    public void setDN(String DN) {
        this.DN = DN;
    }

    public String getDN() {
        return DN;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getSendTime() {
        return sendTime;
    }

    public byte[] getUnpack_img() {
        return unpack_img;
    }

    public void setUnpack_img(byte[] unpack_img) {
        this.unpack_img = unpack_img;
    }

    public byte[] getSender_img() {
        return sender_img;
    }

    public void setSender_img(byte[] sender_img) {
        this.sender_img = sender_img;
    }

    public byte[] getFace_img() {
        return face_img;
    }

    public void setFace_img(byte[] face_img) {
        this.face_img = face_img;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
