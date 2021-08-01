package com.armjld.rayashipping.Models;

public class Order {

    //ids
    private String id = "";
    private String trackid = "";
    private String invoiceId = ""; // inovice id
    private String pSupervisor = "";
    private String dSupervisor = "";
    private String dHub = ""; // Should be assigned Automaticlly
    private String pHub = ""; // Should be assigned Automaticlly
    private String dHubName = ""; // Should be assigned Automaticlly
    private String pHubName = ""; // Should be assigned Automaticlly

    // Pick up Data
    private String mPAddress = "";
    private String mPShop = "";
    private String txtPState = "";
    private String mPRegion = "";
    private String uId = "";
    private String pPhone = "";
    private String owner = "";

    // Drop Data
    private String txtDState = "";
    private String DAddress = "";
    private String DPhone = "";
    private String DName = "";
    private String mDRegion = "";

    // -- Statue
    private String type = "Normal";
    private String statue = "";
    private String srated = "false";
    private String drated = "false";
    private String tries = "0";
    private String isPart = "false"; // true (with cap) - hub (in hub) - back (with client)

    // Transportation
    private String uAccepted = "";
    private String srateid = "";
    private String packType = "";
    private String packWeight = "";
    private String lat = "";
    private String _long = "";
    private String drateid = "";
    private String notes = "";
    private String priority = "1";
    private String provider = "Raya";
    private String refrence = ""; // ref
    private String agent = ""; // agent
    private String denied_reason = "";
    private String itemReturned = "";

    // -- Money Stuff
    private String moneyReturned = "false"; // true
    private String orignalMoney = ""; // in case of edit
    private String moneyStatue = "none";  //false - courier - operation - invoice
    private String recivedMoney = "0";
    private String returnMoney = "0"; // -- in case of denied
    private String paid = "false"; // true
    private String GMoney = "";
    private String GGet = "";
    private int fees = 0; // Courier Fees

    // -- Time Stamps
    private String readyDTime = "";
    private String supDScanTime = "";
    private String pDate = "";
    private String dilverTime = "";
    private String acceptedTime = "";
    private String lastedit = "";
    private String DDate = "";
    private String date = "";

    public String getHub1DeniedTime() {
        return hub1DeniedTime;
    }

    public void setHub1DeniedTime(String hub1DeniedTime) {
        this.hub1DeniedTime = hub1DeniedTime;
    }

    public String getHub2DeniedTime() {
        return hub2DeniedTime;
    }

    public void setHub2DeniedTime(String hub2DeniedTime) {
        this.hub2DeniedTime = hub2DeniedTime;
    }

    private String hub1DeniedTime = "";
    private String hub2DeniedTime = "";


    public Order(){ }

    public Order(String txtPState, String mPRegion, String mPAddress, String txtDState, String mDRegion, String DAddress, String DPhone, String DName,
                 String GMoney, String GGet, String date, String id, String uId, String statue, String uAccepted,
                 String notes , String owner, String packWeight, String packType, String lat, String _long, String trackid,
                 String returnMoney, String pPhone) {

        //PICK
        this.txtPState = txtPState;
        this.mPRegion = mPRegion;
        this.mPAddress = mPAddress;
        this.notes = notes;
        this.packType = packType;
        this.packWeight = packWeight;
        this.owner = owner;

        //DROP
        this.txtDState = txtDState;
        this.mDRegion = mDRegion;
        this.DAddress = DAddress;
        this.DPhone = DPhone;
        this.DName = DName;
        this.GMoney = GMoney;
        this.GGet = GGet;
        this.date = date;
        this.pPhone = pPhone;

        // ids
        this.id = id;
        this.uId = uId;

        //order statue
        this.statue = statue;
        this.uAccepted = uAccepted;
        this.lat = lat;
        this._long = _long;
        this.trackid = trackid;
        this.returnMoney = returnMoney;
    }

    public String getReadyDTime() {
        return readyDTime;
    }

    public void setReadyDTime(String readyDTime) {
        this.readyDTime = readyDTime;
    }

    public String getDenied_reason() {
        return denied_reason;
    }

    public void setDenied_reason(String denied_reason) {
        this.denied_reason = denied_reason;
    }

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public String getPaid() {
        return paid;
    }

    public String getRecivedMoney() {
        return recivedMoney;
    }

    public void setRecivedMoney(String recivedMoney) {
        this.recivedMoney = recivedMoney;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }
    public String get_long() { return _long; }
    public void set_long(String _long) { this._long = _long; }
    public String getTrackid() {
        return trackid;
    }
    public void setTrackid(String trackid) {
        this.trackid = trackid;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getpDate() { return pDate; }
    public void setpDate(String pDate) { this.pDate = pDate; }
    public String getPackType() {
        return packType;
    }
    public void setPackType(String packType) {
        this.packType = packType;
    }
    public String getPackWeight() {
        return packWeight;
    }
    public void setPackWeight(String packWeight) {
        this.packWeight = packWeight;
    }
    public String getTxtPState() {
        return txtPState;
    }
    public void setTxtPState(String txtPState) {
        this.txtPState = txtPState;
    }
    public String getmPAddress() {
        return mPAddress;
    }
    public void setmPAddress(String mPAddress) {
        this.mPAddress = mPAddress;
    }
    public String getmPShop() {
        return mPShop;
    }
    public void setmPShop(String mPShop) {
        this.mPShop = mPShop;
    }
    public String getDate() {
        return date;
    }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStatue() {
        return statue;
    }
    public void setStatue(String statue) {
        this.statue = statue;
    }
    public String reStateP(){ String reStateP = txtPState + " - " + mPRegion;return reStateP; }
    public String reStateD(){ String reStateD = txtDState + " - " + mDRegion;return reStateD; }
    public String getDilverTime() {
        return dilverTime;
    }
    public void setDilverTime(String dilverTime) {
        this.dilverTime = dilverTime;
    }
    public String getAcceptedTime() {
        return acceptedTime;
    }
    public void setAcceptedTime(String acceptedTime) {
        this.acceptedTime = acceptedTime;
    }
    public String getLastedit() { return lastedit; }
    public void setLastedit(String lastedit) { this.lastedit = lastedit; }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDAddress() {
        return DAddress;
    }
    public String getTxtDState() {
        return txtDState;
    }
    public void setTxtDState(String txtDState) {
        this.txtDState = txtDState;
    }
    public void setDAddress(String DAddress) {
        this.DAddress = DAddress;
    }
    public String getDDate() {
        return DDate;
    }
    public void setDDate(String DDate) {
        this.DDate = DDate;
    }
    public String getDPhone() {
        return DPhone;
    }
    public void setDPhone(String DPhone) {
        this.DPhone = DPhone;
    }
    public String getDName() {
        return DName;
    }
    public void setDName(String DName) {
        this.DName = DName;
    }
    public String getGMoney() {
        return GMoney;
    }
    public void setGMoney(String GMoney) {
        this.GMoney = GMoney;
    }
    public String getGGet() {
        return GGet;
    }
    public void setGGet(String GGet) {
        this.GGet = GGet;
    }
    public String getuId() {
        return uId;
    }
    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMoneyReturned() {
        return moneyReturned;
    }

    public void setMoneyReturned(String moneyReturned) {
        this.moneyReturned = moneyReturned;
    }

    public String getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(String returnMoney) {
        this.returnMoney = returnMoney;
    }

    public String getTries() {
        return tries;
    }

    public void setTries(String tries) {
        this.tries = tries;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getmPRegion() {
        return mPRegion;
    }

    public void setmPRegion(String mPRegion) {
        this.mPRegion = mPRegion;
    }

    public String getmDRegion() {
        return mDRegion;
    }

    public void setmDRegion(String mDRegion) {
        this.mDRegion = mDRegion;
    }
    public String getuAccepted() { return uAccepted;
    }

    public String getSupDScanTime() {
        return supDScanTime;
    }

    public void setSupDScanTime(String supDScanTime) {
        this.supDScanTime = supDScanTime;
    }

    public String getMoneyStatue() {
        return moneyStatue;
    }

    public void setMoneyStatue(String moneyStatue) {
        this.moneyStatue = moneyStatue;
    }

    public String getSrated() {
        return srated;
    }

    public void setSrated(String srated) {
        this.srated = srated;
    }

    public String getSrateid() {
        return srateid;
    }

    public void setSrateid(String srateid) {
        this.srateid = srateid;
    }

    public String getDrated() {
        return drated;
    }

    public void setDrated(String drated) {
        this.drated = drated;
    }

    public String getDrateid() {
        return drateid;
    }

    public void setDrateid(String drateid) {
        this.drateid = drateid;
    }

    public void setuAccepted(String uAccepted) {
        this.uAccepted = uAccepted;
    }

    public String getdHub() {
        return dHub;
    }

    public void setdHub(String dHub) {
        this.dHub = dHub;
    }

    public String getpHub() {
        return pHub;
    }

    public void setpHub(String pHub) {
        this.pHub = pHub;
    }

    public String getpSupervisor() {
        return pSupervisor;
    }

    public void setpSupervisor(String pSupervisor) {
        this.pSupervisor = pSupervisor;
    }

    public String getdSupervisor() {
        return dSupervisor;
    }

    public void setdSupervisor(String dSupervisor) {
        this.dSupervisor = dSupervisor;
    }

    public String getdHubName() {
        return dHubName;
    }

    public void setdHubName(String dHubName) {
        this.dHubName = dHubName;
    }

    public String getpHubName() {
        return pHubName;
    }

    public void setpHubName(String pHubName) {
        this.pHubName = pHubName;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getRefrence() {
        return refrence;
    }

    public void setRefrence(String refrence) {
        this.refrence = refrence;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getpPhone() {
        return pPhone;
    }

    public void setpPhone(String pPhone) {
        this.pPhone = pPhone;
    }

    public String getOrignalMoney() {
        return orignalMoney;
    }

    public void setOrignalMoney(String orignalMoney) {
        this.orignalMoney = orignalMoney;
    }

    public String getIsPart() {
        return isPart;
    }

    public void setIsPart(String isPart) {
        this.isPart = isPart;
    }

    public String getItemReturned() {
        return itemReturned;
    }

    public void setItemReturned(String itemReturned) {
        this.itemReturned = itemReturned;
    }
}

