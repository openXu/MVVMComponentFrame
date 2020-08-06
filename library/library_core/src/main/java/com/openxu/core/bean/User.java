package com.openxu.core.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: openXu
 * Time: 2019/4/19 17:36
 * class: User
 * Description: 用户登录数据
 */
public class User implements Parcelable {


    private String UserID = "";    //cyf_1
    private String UserCode = "";    //cyf
    private String UserName = "";    //陈永富
    private String PostCode = "";    //EP97568293073723631,EP97312081497357581,EP99912081497357582,EP2018050815340906,EP2015093016215174,EP97312081497413462,EP2015093016012731,EP97312081497517510,EP97312081497518216,EP97312081497518292,EP2016082918103425
    private String UserClass = "";    //1
    private String Password = "";    //fDP96YbxcHJ6xwkvWfo2OA==
    private String PasswordKey = "";    //MLnXDYUKJiGSoYDtT3FD2rIGfnQApdT8IYq7OWiUgvY=
    private String PasswordIV = "";    //kIMA993pcjnExx5rbKhuUw==
    private String Status = "";    //1
    private String Gender = "";    //1
    private String Telephone = "";    //
    private String Mobile = "";    //
    private String Email = "";    //
    private String Age = "";    //
    private String Position = "";    //
    private String TechnicalPost = "";    //员工
    private String RFIDCode = "";    //1B42CBA4
    private String BarCode = "";    //
    private String QRCode = "";    //
    private String PhotoUrl = "";    //http://114.115.144.251:8001//uploadfiles/2018/20181219/1740/20181219174043738346.jpg
    private String OrganiseUnitIDs = "";    //10570bed-54ff-11e5-8ec1-64006a4cb35a
    private String OrganiseUnitCodes = "";    //100010001005100010001000
    private String OrganiseUnitNames = "";    //福州鰲峰加油站
    private String DistrictIDs = "";    //6d8231d2-3b70-11e7-9bf0-fa163ea287f1
    private String DistrictNames = "";    //和平里社区
    private String LevelCodes = "";    //100010001000100010001019
    private String DepartmentIDs = "";    //44e789eb-6d93-11e5-8729-00163e004c5a
    private String DepartmentCodes = "";    //1000100010051000100010001000
    private String DepartmentNames = "";    //设备管理部
    private String RoleIDs = "";    //ceshi
    private String RoleNames = "";    //测试角色"
    private String PromanUserCode = "";
    private String PromanPwd="";
    private String PromanUserId="";


    public String getPromanUserCode() {
        return PromanUserCode;
    }

    public void setPromanUserCode(String promanUserCode) {
        PromanUserCode = promanUserCode;
    }

    public String getPromanPwd() {
        return PromanPwd;
    }

    public void setPromanPwd(String promanPwd) {
        PromanPwd = promanPwd;
    }

    public String getPromanUserId() {
        return PromanUserId;
    }

    public void setPromanUserId(String promanUserId) {
        PromanUserId = promanUserId;
    }

    @Override
    public String toString() {
        return "User{" +
                "UserID='" + UserID + '\'' +
                ", UserCode='" + UserCode + '\'' +
                ", UserName='" + UserName + '\'' +
                ", Password='" + Password + '\'' +
                ", PasswordKey='" + PasswordKey + '\'' +
                ", PasswordIV='" + PasswordIV + '\'' +
                ", Status='" + Status + '\'' +
                ", PhotoUrl='" + PhotoUrl + '\'' +
                '}';
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        UserCode = userCode;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPostCode() {
        return PostCode;
    }

    public void setPostCode(String postCode) {
        PostCode = postCode;
    }

    public String getUserClass() {
        return UserClass;
    }

    public void setUserClass(String userClass) {
        UserClass = userClass;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPasswordKey() {
        return PasswordKey;
    }

    public void setPasswordKey(String passwordKey) {
        PasswordKey = passwordKey;
    }

    public String getPasswordIV() {
        return PasswordIV;
    }

    public void setPasswordIV(String passwordIV) {
        PasswordIV = passwordIV;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getTechnicalPost() {
        return TechnicalPost;
    }

    public void setTechnicalPost(String technicalPost) {
        TechnicalPost = technicalPost;
    }

    public String getRFIDCode() {
        return RFIDCode;
    }

    public void setRFIDCode(String RFIDCode) {
        this.RFIDCode = RFIDCode;
    }

    public String getBarCode() {
        return BarCode;
    }

    public void setBarCode(String barCode) {
        BarCode = barCode;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getOrganiseUnitIDs() {
        return OrganiseUnitIDs;
    }

    public void setOrganiseUnitIDs(String organiseUnitIDs) {
        OrganiseUnitIDs = organiseUnitIDs;
    }

    public String getOrganiseUnitCodes() {
        return OrganiseUnitCodes;
    }

    public void setOrganiseUnitCodes(String organiseUnitCodes) {
        OrganiseUnitCodes = organiseUnitCodes;
    }

    public String getOrganiseUnitNames() {
        return OrganiseUnitNames;
    }

    public void setOrganiseUnitNames(String organiseUnitNames) {
        OrganiseUnitNames = organiseUnitNames;
    }

    public String getDistrictIDs() {
        return DistrictIDs;
    }

    public void setDistrictIDs(String districtIDs) {
        DistrictIDs = districtIDs;
    }

    public String getDistrictNames() {
        return DistrictNames;
    }

    public void setDistrictNames(String districtNames) {
        DistrictNames = districtNames;
    }

    public String getLevelCodes() {
        return LevelCodes;
    }

    public void setLevelCodes(String levelCodes) {
        LevelCodes = levelCodes;
    }

    public String getDepartmentIDs() {
        return DepartmentIDs;
    }

    public void setDepartmentIDs(String departmentIDs) {
        DepartmentIDs = departmentIDs;
    }

    public String getDepartmentCodes() {
        return DepartmentCodes;
    }

    public void setDepartmentCodes(String departmentCodes) {
        DepartmentCodes = departmentCodes;
    }

    public String getDepartmentNames() {
        return DepartmentNames;
    }

    public void setDepartmentNames(String departmentNames) {
        DepartmentNames = departmentNames;
    }

    public String getRoleIDs() {
        return RoleIDs;
    }

    public void setRoleIDs(String roleIDs) {
        RoleIDs = roleIDs;
    }

    public String getRoleNames() {
        return RoleNames;
    }

    public void setRoleNames(String roleNames) {
        RoleNames = roleNames;
    }

    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UserID);
        dest.writeString(this.UserCode);
        dest.writeString(this.UserName);
        dest.writeString(this.PostCode);
        dest.writeString(this.UserClass);
        dest.writeString(this.Password);
        dest.writeString(this.PasswordKey);
        dest.writeString(this.PasswordIV);
        dest.writeString(this.Status);
        dest.writeString(this.Gender);
        dest.writeString(this.Telephone);
        dest.writeString(this.Mobile);
        dest.writeString(this.Email);
        dest.writeString(this.Age);
        dest.writeString(this.Position);
        dest.writeString(this.TechnicalPost);
        dest.writeString(this.RFIDCode);
        dest.writeString(this.BarCode);
        dest.writeString(this.QRCode);
        dest.writeString(this.PhotoUrl);
        dest.writeString(this.OrganiseUnitIDs);
        dest.writeString(this.OrganiseUnitCodes);
        dest.writeString(this.OrganiseUnitNames);
        dest.writeString(this.DistrictIDs);
        dest.writeString(this.DistrictNames);
        dest.writeString(this.LevelCodes);
        dest.writeString(this.DepartmentIDs);
        dest.writeString(this.DepartmentCodes);
        dest.writeString(this.DepartmentNames);
        dest.writeString(this.RoleIDs);
        dest.writeString(this.RoleNames);
        dest.writeString(this.PromanUserCode);
        dest.writeString(this.PromanPwd);
        dest.writeString(this.PromanUserId);
    }

    protected User(Parcel in) {
        this.UserID = in.readString();
        this.UserCode = in.readString();
        this.UserName = in.readString();
        this.PostCode = in.readString();
        this.UserClass = in.readString();
        this.Password = in.readString();
        this.PasswordKey = in.readString();
        this.PasswordIV = in.readString();
        this.Status = in.readString();
        this.Gender = in.readString();
        this.Telephone = in.readString();
        this.Mobile = in.readString();
        this.Email = in.readString();
        this.Age = in.readString();
        this.Position = in.readString();
        this.TechnicalPost = in.readString();
        this.RFIDCode = in.readString();
        this.BarCode = in.readString();
        this.QRCode = in.readString();
        this.PhotoUrl = in.readString();
        this.OrganiseUnitIDs = in.readString();
        this.OrganiseUnitCodes = in.readString();
        this.OrganiseUnitNames = in.readString();
        this.DistrictIDs = in.readString();
        this.DistrictNames = in.readString();
        this.LevelCodes = in.readString();
        this.DepartmentIDs = in.readString();
        this.DepartmentCodes = in.readString();
        this.DepartmentNames = in.readString();
        this.RoleIDs = in.readString();
        this.RoleNames = in.readString();
        this.PromanUserCode = in.readString();
        this.PromanPwd = in.readString();
        this.PromanUserId = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
