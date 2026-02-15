//package com.sky;
//
//import com.sky.mapper.UserMapper;
//import com.sky.service.impl.NotifyService;
//import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class NotifyServiceTest {
//
//
//    @InjectMocks
//    private NotifyService notifyService;   // 被测试对象
//    @Mock
//    private AesUtil aesUtil;
//
//    @Test
//    void testNotify_success() throws Exception {
//
//
//        when(aesUtil.decryptToString(
//                any(byte[].class),
//                any(byte[].class),
//                anyString()
//        )).thenReturn("""
//                {
//                  "out_trade_no":"20250215001",
//                  "transaction_id":"mock_tx_id",
//                  "trade_state":"SUCCESS"
//                }
//                """);
//
//        // ✅ 调用真正业务方法
//        String s = notifyService.handleNotify("mock_request_json");
//        System.out.println(s);
//
//
//        // 可以再 verify 是否更新订单
//    }
//}
