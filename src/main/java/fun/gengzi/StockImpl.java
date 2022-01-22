package fun.gengzi;




import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;

import java.util.List;

public class StockImpl {

    public String queryPresetStockData(List<String> gids) {

        String result2 = HttpRequest.post("")
                .header(Header.USER_AGENT, "Hutool http")//头信息，多个头信息多次调用此方法即可
                .form("")//表单内容
                .timeout(20000)//超时，毫秒
                .execute().body();

        return null;
    }

}