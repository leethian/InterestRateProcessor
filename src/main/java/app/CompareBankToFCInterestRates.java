package app;

import bean.InterestRate;
import clientrequest.MasAPI;
import util.MASException;
import util.DateException;
import validator.ValidateArguments;
import util.TrendDetails;

import java.util.Iterator;
import java.util.List;

public class CompareBankToFCInterestRates {

    private static MasAPI masAPI;

    public CompareBankToFCInterestRates(String proxy, int proxyPort) {
        masAPI = new MasAPI(proxy,proxyPort);
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Program execution cannot have less than 3 arguments.");
            System.exit(1);
        }

        (new ValidateArguments()).validateAuguments(args);


        String proxy = args[0];
        int proxyPort = Integer.parseInt(args[1]);
        String dateRange = args[2];

        List<InterestRate> ir = null;

        new CompareBankToFCInterestRates(proxy,proxyPort);
        try {
            ir = masAPI.parseJsonStr(dateRange);
        } catch (MASException e) {
            if (e.toString().equals("MAS_APIError")) {
                System.out.println("MAS API Error. Try again later.");
            }else if (e.toString().equals("MAS_APINoResults")){
                System.out.println("MAS API did not return any results.");
            } else {
                System.out.println("MAS E-services maintenance. Try again later.");
            }
            System.exit(1);
        } catch (DateException de) {
            System.out.println("Incorrect Date format: " + de.toString());
            System.exit(1);
        }
        printInterestInRange(ir);
    }



    private static void printInterestInRange(List<InterestRate> rateBeanList){
        TrendDetails bankSavTrendDet = new TrendDetails();
        TrendDetails fcSavTrendDet = new TrendDetails();
        TrendDetails bankFDTrendDet = new TrendDetails();
        TrendDetails fcFDTrendDet = new TrendDetails();
        double bankSavDepositTotal = 0.0;
        double fcSavDepositTotal = 0.0;
        double bankFDTotal = 0.0;
        double fcFDTotal = 0.0;
        double bankSavDepositMth, fcSavDepositMth,bankFDMth, fcFDMth;
        Iterator it = rateBeanList.iterator();
        int mthCnt = 0;
        long sumMonthCnt =0;
        System.out.println("Month-Year,Bank Deposit Rate,FC Deposit Rate, Bank 12month FD Rate, FC 12month FD Rate,FC higher Bank Deposit Rate,FC higher Fixed Desposit Rate");
        while (it.hasNext()){
            ++mthCnt;
            sumMonthCnt = sumMonthCnt + mthCnt;
            InterestRate monthRate = (InterestRate)it.next();
            bankSavDepositMth = monthRate.getBankSavingDepRate();
            fcSavDepositMth = monthRate.getFCSavingDepRate();
            bankFDMth = monthRate.getBankFDRate();
            fcFDMth = monthRate.getFCFDRate();

            System.out.println(monthRate.getMonthYear()+","+bankSavDepositMth+","+fcSavDepositMth+","+bankFDMth
                               +","+fcFDMth+","+(fcSavDepositMth>bankSavDepositMth)+","
                               +(fcFDMth>bankFDMth));
            bankSavDepositTotal = bankSavDepositTotal + bankSavDepositMth;
            fcSavDepositTotal = fcSavDepositTotal + fcSavDepositMth;
            bankFDTotal = bankFDTotal + bankFDMth;
            fcFDTotal = fcFDTotal + fcFDMth;
            bankSavTrendDet.calulateCumulativeDetails(mthCnt,bankSavDepositMth);
            fcSavTrendDet.calulateCumulativeDetails(mthCnt,fcSavDepositMth);
            bankFDTrendDet.calulateCumulativeDetails(mthCnt,bankFDMth);
            fcFDTrendDet.calulateCumulativeDetails(mthCnt,fcFDMth);
        }

        System.out.println("\nAverage Bank Saving Deposit rate: " + bankSavDepositTotal/rateBeanList.size());
        System.out.println("Average Financial Companies Saving Deposit rate: " + fcSavDepositTotal/rateBeanList.size());
        System.out.println("Average Bank Fixed Deposit rate: " + bankFDTotal/rateBeanList.size());
        System.out.println("Average Financial Companies Fixed Deposit rate: " + fcFDTotal/rateBeanList.size());
        System.out.println("Trend for Bank Saving Deposit Rates: " + getTrend(mthCnt,bankSavDepositTotal,bankSavTrendDet));
        System.out.println("Trend for Financial Companies Saving Deposit Rates: " + getTrend(mthCnt,fcSavDepositTotal,fcSavTrendDet));
        System.out.println("Trend for Bank Fixed Deposit Rates: " + getTrend(mthCnt,bankFDTotal,bankFDTrendDet));
        System.out.println("Trend for Financial Companies Fixed Deposit Rates: " + getTrend(mthCnt,fcFDTotal,fcFDTrendDet));
    }

    private static String getTrend(int mthCnt, double ratesTotal, TrendDetails trendDet){
        String result = "Unchanged";

        double trend = ((mthCnt*trendDet.getSumMthCntXMthRate())-(trendDet.getSumMthCnt()*ratesTotal))/((mthCnt*trendDet.getSumSqMthCnt())-(trendDet.getSumMthCnt()*trendDet.getSumMthCnt()));

        if (trend>0) {
            result = "Uptrend";
        } else if (trend<0){
            result = "Downtrend";
        }
        return result;
    }
}
