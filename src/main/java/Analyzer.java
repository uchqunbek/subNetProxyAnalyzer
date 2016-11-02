import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Analyzer {
    public static void main(String[] args) throws IOException, InterruptedException {
        List<Integer> allocationList = new ArrayList();
        allocationList.add(8);
        allocationList.add(26);
        allocationList.add(50);
        allocationList.add(63);
        for (int i = 2; i < 254; i++) {
            if (allocationList.contains(i)) {
                continue;
            }
            String str1 = "192.168.3." + i;
            String str2 = "255.255.255.0";
            String[] command1 = {"netsh", "interface", "ip", "set", "address",
                    "name=", "Ethernet", "source=static", "addr=", str1,
                    "mask=", str2, "gateway=192.168.3.1"};
            Process pp = java.lang.Runtime.getRuntime().exec(command1);
            pp.waitFor();
            Thread.sleep(5000);
            HttpHost proxyHost = new HttpHost("192.168.0.2", 8080);
            Boolean isNonProxified = isConnectionAviable("ijro.uz", null), isProxified = isConnectionAviable("ijro.uz", proxyHost);
            System.out.println(str1 + ": isNonProxified:" + isNonProxified + ", isProxified:" + isProxified);
            //System.out.println();
        }
    }

    public static boolean isConnectionAviable(String host, HttpHost proxy) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

        HttpGet get = new HttpGet("http://" + host);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(3000)
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build();
        get.setConfig(requestConfig);
        if (proxy != null) {
            httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
        }
        try {
            HttpResponse response = httpClient.execute(get);
            //System.out.println(response.getStatusLine());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
