package bean;

public class InterestRate {
    private String monthYear;
    private String numYearMon;
    private double twelveMonthFDBankRate;
    private double twelveMonthFDFCRate;
    private double savingDepBankRate;
    private double savingDepFCRate;

    /**
     * set Month-Year
     *
     * @param month_year the month-year to set
     */
    public void setMonthYear(String month_year) {
        this.monthYear = month_year;
    }

    public void setNumYearMon(String year_mon) {
        this.numYearMon = year_mon;
    }

    public void setBankFDRate(double rate) {
        this.twelveMonthFDBankRate = rate;
    }

    public void setFCFDRate(double rate) {
        this.twelveMonthFDFCRate = rate;
    }

    public void setBankSavingDepRate(double rate) {
        this.savingDepBankRate = rate;
    }

    public void setFCSavingDepRate(double rate) {
        this.savingDepFCRate = rate;
    }

    public String getMonthYear() {
        return this.monthYear;
    }

    public String getNumYearMon() {
        return this.numYearMon;
    }

    public double getBankFDRate() {
        return this.twelveMonthFDBankRate;
    }

    public double getFCFDRate() {
        return this.twelveMonthFDFCRate;
    }

    public double getBankSavingDepRate() {
        return this.savingDepBankRate;
    }

    public double getFCSavingDepRate() {
        return this.savingDepFCRate;
    }
}
