package clientrequest;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Date;
import java.util.Calendar;
import java.text.ParseException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import bean.InterestRate;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONException;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.util.EntityUtils;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import util.MASException;
import util.DateException;

public class MasAPI {

    private String serviceUrl;
    private String resourceId;
    private int recLimit;
    private static HttpHost PROXY;
    private static boolean isUseProxy;

    public MasAPI(String proxy, int port) {
        serviceUrl = "https://eservices.mas.gov.sg/api/action/datastore/search.json?resource_id=";
        resourceId = "5f2b18a8-0883-4769-a635-879c63d3caac";
        recLimit = 60;
        if (!proxy.equals("null")){
            PROXY = new HttpHost(proxy, port);
            isUseProxy = true;
        } else {
            isUseProxy = false;
        }
    }

    public void setResourceId(String resource_id) {
        resourceId = resource_id;
    }

    public List<InterestRate> parseJsonStr(String monthList) throws MASException, DateException {
        List<InterestRate> jsonBeanlist = new ArrayList<InterestRate>();
        Map monthYearMap = buildFormattedDatePair(monthList);
        String numYearMonStr = (String)monthYearMap.get("numYearMonStr");
        String uri = serviceUrl+resourceId+"&limit="+recLimit+"&filters[end_of_month]=" +numYearMonStr;
        //System.out.println("uri: [" +uri+"]");
        HttpGet httpRequest = new HttpGet(uri);
        String jsonStr = executeHTTPRequest(httpRequest);
        if (jsonStr.indexOf("MAS' Website Maintenance")>0){
            System.out.println(jsonStr);
            throw new MASException("MASMaintenance");
        }
        if (jsonStr != null && !"".equals(jsonStr)) {
            JSONObject jsonObject = JSONObject.fromObject(jsonStr);

            JSONObject jsonObjectResult = jsonObject.getJSONObject("result");
            JSONArray arry = jsonObjectResult.getJSONArray("records");

            if (arry == null || arry.size() == 0)
                throw new MASException("MAS_APINoResults");

            for (int i = 0; i < arry.size(); i++) {
                JSONObject o = arry.getJSONObject(i);
                //System.out.println("Result JSON for "+o.getString("end_of_month")+": "+o.toString());
                InterestRate bean = new InterestRate();

                try {
                    bean.setNumYearMon(o.getString("end_of_month"));
                    bean.setMonthYear((String) monthYearMap.get(o.getString("end_of_month")));
                    bean.setBankFDRate(o.getDouble("banks_fixed_deposits_12m"));
                    bean.setFCFDRate(o.getDouble("fc_fixed_deposits_12m"));
                    bean.setBankSavingDepRate(o.getDouble("banks_savings_deposits"));
                    bean.setFCSavingDepRate(o.getDouble("fc_savings_deposits"));
                    //System.out.println(o.getString("end_of_month"));
                    //System.out.println("bean[" + bean.toString() + "]");

                    jsonBeanlist.add(bean);
                } catch (JSONException je) {
                    if (je.toString().indexOf("is not a number")>0) {
                        System.out.println("Period " + bean.getMonthYear() +" has some null rates.");
                    } else {
                        System.out.println("Period " + bean.getMonthYear() +" has some unexpected errors.");
                    }
                } catch (Exception e) {
                    System.out.println("Error in constructing Rates from JSONObject"+e);
                }
            }
        }
        return jsonBeanlist;
    }

    private String executeHTTPRequest(HttpUriRequest httpRequest) throws MASException{
        try (
            CloseableHttpClient httpClient = trustingHttpClient("TLSv1");
            CloseableHttpResponse response = httpClient.execute(httpRequest)) {

            int statusCode = response.getStatusLine().getStatusCode();
            //System.out.println("status code:[" +statusCode+"]");
            if (statusCode != 200) {
                throw new MASException("MAS_APIError");
            }

            return EntityUtils.toString(response.getEntity()); //consumed the response
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | IOException e) {
            return null;
        }
        //naturally will release all connection because of Closeable
    }

    private CloseableHttpClient trustingHttpClient(String protocol)
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                return true;
            }
        });

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(builder.build(),
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HttpClientBuilder custom = HttpClients.custom();

        if (isUseProxy) {
           custom.setProxy(PROXY);
        }
        //custom.setDefaultRequestConfig() //for setting connection timeout
        return custom.setSSLSocketFactory(sslSocketFactory).build();
    }

    private Map buildFormattedDatePair(String dateRange) throws DateException {
        StringTokenizer dateMonYearSt = new StringTokenizer(dateRange, ",");
        HashMap dateMonthMap = new HashMap();
        String numYearMonStr = new String();
        int cnt = 1;


        while (dateMonYearSt.hasMoreTokens()) {
            String dateMonYearStr= null;
            try {
                dateMonYearStr = dateMonYearSt.nextToken();
                String monStr = dateMonYearStr.substring(0,dateMonYearStr.indexOf('-'));
                String yearStr = dateMonYearStr.substring(dateMonYearStr.indexOf('-')+1);
                //System.out.println("H1["+monStr+"] "+yearStr);
                Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(monStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int month = cal.get(Calendar.MONTH)+1;

                String numYearMon;
                if (month<10){
                    numYearMon = yearStr + "-0" + month;
                } else {
                    numYearMon = yearStr + "-" + month;
                }
                dateMonthMap.put(numYearMon,dateMonYearStr);
                if (cnt==1){
                    numYearMonStr = numYearMon;
                } else {
                    numYearMonStr =numYearMonStr + "," +numYearMon;
                }
                ++cnt;
            } catch (ParseException pe){
                throw new DateException(dateMonYearStr);
                //System.out.println("Error parsing date:" + pe);
            }
        }
        //System.out.println("numYearMonStr = " +numYearMonStr);
        //System.out.println("dateMonthMap = " + dateMonthMap);
        dateMonthMap.put("numYearMonStr",numYearMonStr);
        return dateMonthMap;
    }
}
