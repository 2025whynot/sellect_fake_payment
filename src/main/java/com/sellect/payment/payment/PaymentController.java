package com.sellect.payment.payment;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payment")
public class PaymentController {

    private final RestTemplate restTemplate;

    @Value("${target.host}")
    private String successBaseUrl; // ex) http://localhost:8080/api/v1

//    private final Map<String, PaymentRecord> paymentStore = new ConcurrentHashMap<>();

    @PostMapping("/ready")
    public ResponseEntity<Map<String, Object>> paymentReady(@RequestBody Map<String, Object> request) {
        log.info("Payment ready request received: {}", request);

        String partnerOrderId = (String) request.get("partner_order_id");
        String approvalUrl = (String) request.get("approval_url");
        String pid = approvalUrl.substring(approvalUrl.lastIndexOf("/") + 1);

        String tid = "FAKE_TID_" + UUID.randomUUID().toString().substring(0, 8);
        PaymentRecord record = new PaymentRecord(tid, pid, "READY");
//        paymentStore.put(tid, record);

        Map<String, Object> response = new HashMap<>();
        response.put("tid", tid);
        // pid 제거: KakaoPayReadyResponse에 맞게
        response.put("next_redirect_pc_url", "http://fake.com/dummy-success/" + pid);
        response.put("partner_order_id", partnerOrderId);
        log.info("Payment ready response: tid={}", tid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/in-progress/{pid}")
    public ResponseEntity<Map<String, String>> inProgress(
        @PathVariable String pid
    ) {
//        String tid = request.get("tid");
//        String pid = request.get("pid");
//        String pgToken = request.get("pg_token");

        log.info("In-progress request received: pid={}", pid);

//        PaymentRecord record = paymentStore.get(tid);
//        if (record == null || !record.getPid().equals(pid)) {
//            log.error("Invalid tid or pid: tid={}, pid={}", tid, pid);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }

//        String pgToken = "FAKE_PG_" + UUID.randomUUID().toString().substring(0, 8);
//        record.setPgToken(pgToken);
//        record.setStatus("IN_PROGRESS");
//        paymentStore.put(tid, record);
//
//        String successUrl =
//            successBaseUrl + "/api/v1/test/kakao-pay/success/" + pid;
        String successUrl =
            successBaseUrl + "/api/v1/test/payment/kakao-pay/success/" + pid;
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                successUrl, HttpMethod.GET, entity, String.class
            );
            log.info("Success request sent to PC A: url={}, status={}", successUrl,
                response.getStatusCode());

            Map<String, String> result = new HashMap<>();
            result.put("tid", "tid");
            result.put("pid", pid);
//            result.put("pg_token", pgToken);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Failed to send success request to PC A: url={}, error={}", successUrl,
                e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to process in-progress"));
        }
    }


    @PostMapping("/approve")
    public ResponseEntity<Map<String, String>> approve(@RequestBody Map<String, String> request) {
        String tid = request.get("tid");
        String orderId = request.get("orderId");
        String userId = request.get("userId");
        String pgToken = request.get("pg_token");

        log.info("Approve request received: tid={}, orderId={}, userId={}, pg_token={}",
            tid, orderId, userId, pgToken);

//        PaymentRecord record = paymentStore.get(tid);
//        if (record == null || !record.getPgToken().equals(pgToken)) {
//            log.error("Invalid approve request: tid={}, pg_token={}", tid, pgToken);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid approve request"));
//        }
//
//        record.setStatus("APPROVED");
//        paymentStore.put(tid, record);

        Map<String, String> response = new HashMap<>();
        response.put("aid", "FAKE_AID_" + UUID.randomUUID().toString().substring(0, 8)); // aid 추가
        response.put("tid", tid);
        response.put("cid", "TC0ONETIME");
        response.put("partner_order_id", orderId);
        response.put("partner_user_id", userId);
        response.put("payment_method_type", "CARD"); // 예시
        response.put("item_name", "Fake Item"); // 예시
        response.put("quantity", "1"); // 예시
        response.put("approved_at", LocalDateTime.now().toString());
        log.info("Payment approved: tid={}", tid);
        return ResponseEntity.ok(response);
    }
}

@Data
class PaymentRecord {
    private String tid;
    private String pid;
    private String status;
    private String pgToken;

    public PaymentRecord(String tid, String pid, String status) {
        this.tid = tid;
        this.pid = pid;
        this.status = status;
    }
}


// 백업용
//package com.sellect.payment.payment;
//
//import java.time.LocalDateTime;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//    import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import org.springframework.web.client.RestTemplate;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/v1/payment")
//public class PaymentController {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${target.host}")
//    private String successBaseUrl; // ex) http://localhost:8080/api/v1
//
//    private final Map<String, PaymentRecord> paymentStore = new ConcurrentHashMap<>();
//
//    @PostMapping("/ready")
//    public ResponseEntity<Map<String, Object>> paymentReady(@RequestBody Map<String, Object> request) {
//        log.info("Payment ready request received: {}", request);
//
//        String partnerOrderId = (String) request.get("partner_order_id");
//        String approvalUrl = (String) request.get("approval_url");
//        String pid = approvalUrl.substring(approvalUrl.lastIndexOf("/") + 1);
//
//        String tid = "FAKE_TID_" + UUID.randomUUID().toString().substring(0, 8);
//        PaymentRecord record = new PaymentRecord(tid, pid, "READY");
//        paymentStore.put(tid, record);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("tid", tid);
//        // pid 제거: KakaoPayReadyResponse에 맞게
//        response.put("next_redirect_pc_url", "http://fake.com/dummy-success/" + pid);
//        response.put("partner_order_id", partnerOrderId);
//        log.info("Payment ready response: tid={}", tid);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/in-progress")
//    public ResponseEntity<Map<String, String>> inProgress(@RequestBody Map<String, String> request) {
//        String tid = request.get("tid");
//        String pid = request.get("pid");
//
//        log.info("In-progress request received: tid={}, pid={}", tid, pid);
//
//        PaymentRecord record = paymentStore.get(tid);
//        if (record == null || !record.getPid().equals(pid)) {
//            log.error("Invalid tid or pid: tid={}, pid={}", tid, pid);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//
//        String pgToken = "FAKE_PG_" + UUID.randomUUID().toString().substring(0, 8);
//        record.setPgToken(pgToken);
//        record.setStatus("IN_PROGRESS");
//        paymentStore.put(tid, record);
//
//        String successUrl =
//            successBaseUrl + "/test/kakao-pay/success/" + pid + "?pg_token=" + pgToken;
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            ResponseEntity<String> response = restTemplate.exchange(
//                successUrl, HttpMethod.GET, entity, String.class
//            );
//            log.info("Success request sent to PC A: url={}, status={}", successUrl,
//                response.getStatusCode());
//
//            Map<String, String> result = new HashMap<>();
//            result.put("tid", tid);
//            result.put("pid", pid);
//            result.put("pg_token", pgToken);
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            log.error("Failed to send success request to PC A: url={}, error={}", successUrl,
//                e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("error", "Failed to process in-progress"));
//        }
//    }
//
//
//    @PostMapping("/approve")
//    public ResponseEntity<Map<String, String>> approve(@RequestBody Map<String, String> request) {
//        String tid = request.get("tid");
//        String orderId = request.get("orderId");
//        String userId = request.get("userId");
//        String pgToken = request.get("pg_token");
//
//        log.info("Approve request received: tid={}, orderId={}, userId={}, pg_token={}",
//            tid, orderId, userId, pgToken);
//
//        PaymentRecord record = paymentStore.get(tid);
//        if (record == null || !record.getPgToken().equals(pgToken)) {
//            log.error("Invalid approve request: tid={}, pg_token={}", tid, pgToken);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid approve request"));
//        }
//
//        record.setStatus("APPROVED");
//        paymentStore.put(tid, record);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("aid", "FAKE_AID_" + UUID.randomUUID().toString().substring(0, 8)); // aid 추가
//        response.put("tid", tid);
//        response.put("cid", "TC0ONETIME");
//        response.put("partner_order_id", orderId);
//        response.put("partner_user_id", userId);
//        response.put("payment_method_type", "CARD"); // 예시
//        response.put("item_name", "Fake Item"); // 예시
//        response.put("quantity", "1"); // 예시
//        response.put("approved_at", LocalDateTime.now().toString());
//        log.info("Payment approved: tid={}", tid);
//        return ResponseEntity.ok(response);
//    }
//}
//
//@Data
//class PaymentRecord {
//    private String tid;
//    private String pid;
//    private String status;
//    private String pgToken;
//
//    public PaymentRecord(String tid, String pid, String status) {
//        this.tid = tid;
//        this.pid = pid;
//        this.status = status;
//    }
//}