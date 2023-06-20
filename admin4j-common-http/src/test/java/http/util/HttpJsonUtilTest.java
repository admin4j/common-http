package http.util;

import com.admin4j.json.mapper.JSONMapper;
import http.entity.CsdnR;
import io.github.admin4j.http.core.Pair;
import io.github.admin4j.http.util.HttpJsonUtil;
import org.junit.jupiter.api.Test;

/**
 * @author andanyang
 * @since 2022/11/3 13:29
 */
public class HttpJsonUtilTest {


    @Test
    void testGetMap() {

        JSONMapper stringObjectMap = HttpJsonUtil.get("https://blog.csdn.net/community/home-api/v1/get-business-list?page=2&size=20&businessType=lately&noMore=false",
                Pair.of("username", "agonie201218"));
        System.out.println("stringObjectMap = " + stringObjectMap);
    }

    @Test
    void testGetObject() {

        CsdnR csdnR = HttpJsonUtil.get("https://blog.csdn.net/community/home-api/v1/get-business-list?page=2&size=20&businessType=lately&noMore=false",
                CsdnR.class,
                Pair.of("username", "agonie201218"));
        System.out.println("csdnR = " + csdnR);
    }


}
