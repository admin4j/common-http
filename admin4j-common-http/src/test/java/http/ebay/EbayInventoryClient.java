package http.ebay;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ebay 库存相关api
 *
 * @author andanyang
 * @since 2022/11/8 17:34
 */
public class EbayInventoryClient extends EbayClient {

    /**
     * 店铺配置
     *
     * @param storeId
     */
    public EbayInventoryClient(Long storeId) {
        super(storeId);
    }

    /**
     * 库存列表
     *
     * @param limit
     * @param offset
     * @return
     * @throws IOException
     */
    public JSONObject inventoryItem(Integer limit, Integer offset) throws IOException {

        Map<String, Object> queryMap = new HashMap(2);
        queryMap.put("limit", limit);
        queryMap.put("offset", offset);
        return get("/sell/inventory/v1/inventory_item", queryMap);
    }
}
