package info.templates;

public class Missile_maybe {
    private double mass;
    private double explosiveMass;
    private double CxK;
    private double finsAoaHor;
    private double finsAoaVer;
    private double finsLatAccel;
    private double force;
    private double timeFire;
    private double massEnd;
    private String guidanceType;
    private double endSpeed;
    private double timeLife;

    private double warmUpTime;
    private double workTime;
    private boolean uncageBeforeLaunch;
    private boolean lockAfterLaunch;
    private double breakLockMaxTime;
    private double lockAngleMax;
    private double angleMax;
    private double rateMax;
    private double reqAccelMax;
    private double rangeBands;
    private double fov;
    private double gateWidth;
    private String bandMaskToReject;
    private double timeOut;
    private double timeToGain1;
    private double dopplerSpeedMin;
    private double dopplerSpeedMax;
    private double dopplerSpeedWidth;
    private double signalWidthMin;

    // Proximity fuse properties
    private double radius;
    private double rangeBand0;
    private double rangeBand1;
    private double rangeBand2;
    private double rangeBand3;
    private double rangeBand4;
    private double rangeBand5;
    private double rangeBand6;
    private double rangeBand7;

    public Missile_maybe(double mass, double explosiveMass, double cxK, double finsAoaHor, double finsAoaVer, double finsLatAccel, double force, double timeFire, double massEnd, String guidanceType, double endSpeed, double timeLife, double warmUpTime, double workTime, boolean uncageBeforeLaunch, boolean lockAfterLaunch, double breakLockMaxTime, double lockAngleMax, double angleMax, double rateMax, double reqAccelMax, double rangeBands, double fov, double gateWidth, String bandMaskToReject, double timeOut, double timeToGain1, double dopplerSpeedMin, double dopplerSpeedMax, double dopplerSpeedWidth, double signalWidthMin, double radius, double rangeBand0, double rangeBand1, double rangeBand2, double rangeBand3, double rangeBand4, double rangeBand5, double rangeBand6, double rangeBand7) {
        this.mass = mass;
        this.explosiveMass = explosiveMass;
        CxK = cxK;
        this.finsAoaHor = finsAoaHor;
        this.finsAoaVer = finsAoaVer;
        this.finsLatAccel = finsLatAccel;
        this.force = force;
        this.timeFire = timeFire;
        this.massEnd = massEnd;
        this.guidanceType = guidanceType;
        this.endSpeed = endSpeed;
        this.timeLife = timeLife;
        this.warmUpTime = warmUpTime;
        this.workTime = workTime;
        this.uncageBeforeLaunch = uncageBeforeLaunch;
        this.lockAfterLaunch = lockAfterLaunch;
        this.breakLockMaxTime = breakLockMaxTime;
        this.lockAngleMax = lockAngleMax;
        this.angleMax = angleMax;
        this.rateMax = rateMax;
        this.reqAccelMax = reqAccelMax;
        this.rangeBands = rangeBands;
        this.fov = fov;
        this.gateWidth = gateWidth;
        this.bandMaskToReject = bandMaskToReject;
        this.timeOut = timeOut;
        this.timeToGain1 = timeToGain1;
        this.dopplerSpeedMin = dopplerSpeedMin;
        this.dopplerSpeedMax = dopplerSpeedMax;
        this.dopplerSpeedWidth = dopplerSpeedWidth;
        this.signalWidthMin = signalWidthMin;
        this.radius = radius;
        this.rangeBand0 = rangeBand0;
        this.rangeBand1 = rangeBand1;
        this.rangeBand2 = rangeBand2;
        this.rangeBand3 = rangeBand3;
        this.rangeBand4 = rangeBand4;
        this.rangeBand5 = rangeBand5;
        this.rangeBand6 = rangeBand6;
        this.rangeBand7 = rangeBand7;
    }

    public double getMass() {
        return mass;
    }

    public double getExplosiveMass() {
        return explosiveMass;
    }

    public double getCxK() {
        return CxK;
    }

    public double getFinsAoaHor() {
        return finsAoaHor;
    }

    public double getFinsAoaVer() {
        return finsAoaVer;
    }

    public double getFinsLatAccel() {
        return finsLatAccel;
    }

    public double getForce() {
        return force;
    }

    public double getTimeFire() {
        return timeFire;
    }

    public double getMassEnd() {
        return massEnd;
    }

    public String getGuidanceType() {
        return guidanceType;
    }

    public double getEndSpeed() {
        return endSpeed;
    }

    public double getTimeLife() {
        return timeLife;
    }

    public double getWarmUpTime() {
        return warmUpTime;
    }

    public double getWorkTime() {
        return workTime;
    }

    public boolean isUncageBeforeLaunch() {
        return uncageBeforeLaunch;
    }

    public boolean isLockAfterLaunch() {
        return lockAfterLaunch;
    }

    public double getBreakLockMaxTime() {
        return breakLockMaxTime;
    }

    public double getLockAngleMax() {
        return lockAngleMax;
    }

    public double getAngleMax() {
        return angleMax;
    }

    public double getRateMax() {
        return rateMax;
    }

    public double getReqAccelMax() {
        return reqAccelMax;
    }

    public double getRangeBands() {
        return rangeBands;
    }

    public double getFov() {
        return fov;
    }

    public double getGateWidth() {
        return gateWidth;
    }

    public String getBandMaskToReject() {
        return bandMaskToReject;
    }

    public double getTimeOut() {
        return timeOut;
    }

    public double getTimeToGain1() {
        return timeToGain1;
    }

    public double getDopplerSpeedMin() {
        return dopplerSpeedMin;
    }

    public double getDopplerSpeedMax() {
        return dopplerSpeedMax;
    }

    public double getDopplerSpeedWidth() {
        return dopplerSpeedWidth;
    }

    public double getSignalWidthMin() {
        return signalWidthMin;
    }

    public double getRadius() {
        return radius;
    }

    public double getRangeBand0() {
        return rangeBand0;
    }

    public double getRangeBand1() {
        return rangeBand1;
    }

    public double getRangeBand2() {
        return rangeBand2;
    }

    public double getRangeBand3() {
        return rangeBand3;
    }

    public double getRangeBand4() {
        return rangeBand4;
    }

    public double getRangeBand5() {
        return rangeBand5;
    }

    public double getRangeBand6() {
        return rangeBand6;
    }

    public double getRangeBand7() {
        return rangeBand7;
    }
}
