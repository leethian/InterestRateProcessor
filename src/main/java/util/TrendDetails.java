package util;

public class TrendDetails {
    private long sumMonthCnt;
    private long sumSqMthCnt;
    private double sumMthCntXMthRate;

    public TrendDetails(){
        sumMonthCnt = 0;
        sumSqMthCnt  = 0;
        sumMthCntXMthRate = 0.0;
    }

    public void calulateCumulativeDetails(int mthCnt,double curRate){
        sumMonthCnt = sumMonthCnt + mthCnt;
        sumSqMthCnt = sumSqMthCnt + (mthCnt*mthCnt);

        double mthCntXMthRate = mthCnt*curRate;
        sumMthCntXMthRate = sumMthCntXMthRate + mthCntXMthRate;
    }

    public long getSumMthCnt() {
        return this.sumMonthCnt;
    }

    public long getSumSqMthCnt() {
        return this.sumSqMthCnt;
    }

    public double getSumMthCntXMthRate() {
        return this.sumMthCntXMthRate;
    }
}
