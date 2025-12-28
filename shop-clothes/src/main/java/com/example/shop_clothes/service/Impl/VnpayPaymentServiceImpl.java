package com.example.shop_clothes.service.Impl;

import com.example.shop_clothes.config.VnpayConfig;
import com.example.shop_clothes.dto.vnpay.VnpayCallbackResponse;
import com.example.shop_clothes.dto.vnpay.VnpayPaymentResponse;
import com.example.shop_clothes.enums.OrderStatus;
import com.example.shop_clothes.exception.ApplicationException;
import com.example.shop_clothes.exception.ErrorCode;
import com.example.shop_clothes.model.Order;
import com.example.shop_clothes.repository.OrderRepository;
import com.example.shop_clothes.service.PaymentRollbackService;
import com.example.shop_clothes.service.VnpayPaymentService;
import com.example.shop_clothes.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class VnpayPaymentServiceImpl implements VnpayPaymentService {

    private final VnpayConfig vnpayConfig;
    private final OrderRepository orderRepository;
    private final PaymentRollbackService paymentRollbackService;
    @NonFinal
    @Value("${app.fontend-url}")
    protected String fontendUrl;

    @Override
    public VnpayPaymentResponse createPaymentUrl(Order order, HttpServletRequest request) {
        try {
            String transactionCode = generateTransactionCode(order.getId());
            long amount = order.getTotalMoney().longValue() * 100;
            String orderInfo = buildOrderInfo(order.getId());
            String createDate = getCurrentDateFormatted();
            String ipAddress = VnpayUtil.getClientIp(request);
            //
            Map<String, String> vnpParams = buildVnpayParams(
                    transactionCode,
                    amount,
                    orderInfo,
                    createDate,
                    ipAddress
            );
            String secureHash = VnpayUtil.generateSecureHash(vnpParams, vnpayConfig.getHashSecret());
            vnpParams.put("vnp_SecureHash", secureHash);
            log.debug("Generated secure hash: {}", secureHash);
            //
            String paymentUrl = VnpayUtil.buildPaymentUrl(vnpayConfig.getPayUrl(), vnpParams);
            log.info("Vnpay payment URL created successfully");
            log.debug("Payment URL: {}", paymentUrl);
            //
            return VnpayPaymentResponse.builder()
                    .paymentUrl(paymentUrl)
                    .transactionCode(transactionCode)
                    .amount(order.getTotalMoney().longValue())
                    .orderId(order.getId())
                    .message("Redirect to Vnpay payment")
                    .build();

        } catch (Exception e) {
            log.error("Error creating Vnpay payment url for order {}: {}", order.getId(), e.getMessage(), e);
            throw new ApplicationException(ErrorCode.VNPAY_PAYMENT_FAILED);
        }
    }

    // Verify response từ Vnpay callback
    @Override
    public boolean verifyCallback(Map<String, String> params) {
        try {
            // Lấy signature từ response
            String vnp_SecureHash = params.remove("vnp_SecureHash");
            // Tính toán lại signature
            String calculatedHash = VnpayUtil.generateSecureHash(params, vnpayConfig.getHashSecret());
            boolean isValid = vnp_SecureHash.equals(calculatedHash);
            return isValid;
        } catch (Exception e) {
            return false;
        }
    }

    // Generate transaction code
    private String generateTransactionCode(Long orderId) {
        return "ORD" + orderId + System.currentTimeMillis();
    }

    //
    private String buildOrderInfo(Long orderId) {
        return "Thanh toan don hang " + orderId;
    }
    //
    private String getCurrentDateFormatted() {
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(cld.getTime());
    }

    private Map<String, String> buildVnpayParams(
            String transactionCode,
            long amount,
            String orderInfo,
            String createDate,
            String ipAddress) {
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", vnpayConfig.getVersion());
        vnpParams.put("vnp_Command", vnpayConfig.getCommand());
        vnpParams.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", vnpayConfig.getCurrCode());
        vnpParams.put("vnp_TxnRef", transactionCode);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_Locale", vnpayConfig.getLocale());
        vnpParams.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        vnpParams.put("vnp_IpAddr", ipAddress);
        vnpParams.put("vnp_CreateDate", createDate);
        vnpParams.put("vnp_OrderType", "other");
        return vnpParams;
    }

    // logging-debug, lấy thông tin chi tiết callback từ Vnpay
    @Override
    public String getCallbackInfo(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("VNPAY Callback Info:\n");
        sb.append("- Response Code: ").append(params.get("vnp_ResponseCode")).append("\n");
        sb.append("- Transaction No: ").append(params.get("vnp_TransactionNo")).append("\n");
        sb.append("- Amount: ").append(params.get("vnp_Amount")).append("\n");
        sb.append("- Order Info: ").append(params.get("vnp_OrderInfo")).append("\n");
        sb.append("- Pay Date: ").append(params.get("vnp_PayDate")).append("\n");
        sb.append("- Bank Code: ").append(params.get("vnp_BankCode")).append("\n");
        return sb.toString();
    }

    // Xử lý callback từ Vnpay
    @Override
    public VnpayCallbackResponse handleCallback(Map<String, String> params) {
        try {
            log.info("Processing VNPAY callback with {} parameters", params.size());
            Map<String, String> paramsCopy = new TreeMap<>(params);
            String orderInfo = params.get("vnp_OrderInfo");
            String responseCode = params.get("vnp_ResponseCode");
            String transactionNo = params.get("vnp_TransactionNo");
            log.info("VNPAY response - Code: {}, TxnNo: {}, OrderInfo: {}",
                    responseCode, transactionNo, orderInfo);
            // Verify signature
            boolean isValid = verifyCallback(paramsCopy);
            if (!isValid) {
                log.error("VNPAY callback signature invalid");
                return VnpayCallbackResponse.builder()
                        .success(false)
                        .redirectUrl(fontendUrl + "?status=failed&message=Chi%20ky%20khong%20hop%20le")
                        .errorMessage("Chữ ký không hợp lệ")
                        .build();
            }
            log.info("VNPAY callback signature verified successfully");
            // Validate orderInfo
            if (orderInfo == null || orderInfo.isEmpty()) {
                log.error("Order info is null or empty");
                return VnpayCallbackResponse.builder()
                        .success(false)
                        .redirectUrl(fontendUrl + "?status=error&message=Thong%20tin%20don%20hang%20khong%20hop%20le")
                        .errorMessage("Thông tin đơn hàng không hợp lệ")
                        .build();
            }
            // Extract order ID
            Long orderId = extractOrderId(orderInfo);
            log.info("Extracted orderId: {}", orderId);
            // Get order from database
            Order order = orderRepository.findById(orderId)
                    .orElse(null);
            if (order == null) {
                log.error("Order {} not found in database", orderId);
                return VnpayCallbackResponse.builder()
                        .success(false)
                        .redirectUrl(fontendUrl + "?status=error&message=Don%20hang%20khong%20ton%20tai")
                        .errorMessage("Đơn hàng không tồn tại")
                        .build();
            }
            // Check status order
            if (order.getStatus() == OrderStatus.CANCELLED_TIMEOUT || order.getStatus() == OrderStatus.PAYMENT_FAILED || order.getStatus() == OrderStatus.CANCELLED) {
                log.warn("Order {} is cancelled/failed - Cannot process payment. Current status: {}", orderId, order.getStatus());
                return VnpayCallbackResponse.builder()
                        .success(false)
                        .redirectUrl(fontendUrl + "?status=failed&message=Don%20hang%20da%20bi%20huy")
                        .errorMessage("Đơn hàng đã bị hủy hoặc thanh toán thất bại")
                        .build();
            }
            if (order.getStatus() != OrderStatus.PAYMENT_WAITING) {
                log.warn("Order {} has invalid status for payment: {}", orderId, order.getStatus());
                return VnpayCallbackResponse.builder()
                        .success(false)
                        .redirectUrl(fontendUrl + "?status=failed&message=Trang%20thai%20don%20hang%20khong%20hop%20le")
                        .errorMessage("Trạng thái đơn hàng không hợp lệ")
                        .build();
            }
            // response success or failed
            if ("00".equals(responseCode)) {
                return handlePaymentSuccess(order, transactionNo, orderId);
            } else {
                return handlePaymentFailure(order, responseCode, orderId);
            }

        } catch (NumberFormatException e) {
            log.error("Error parsing order ID from orderInfo", e);
            return VnpayCallbackResponse.builder()
                    .success(false)
                    .redirectUrl(fontendUrl + "?status=error&message=Loi%20xu%20ly%20don%20hang")
                    .errorMessage("Lỗi xử lý đơn hàng")
                    .build();
        } catch (Exception e) {
            log.error("Error processing Vnpay callback", e);
            return VnpayCallbackResponse.builder()
                    .success(false)
                    .redirectUrl(fontendUrl + "?status=error&message=Loi%20he%20thong")
                    .errorMessage("Lỗi hệ thống")
                    .build();
        }
    }

    private VnpayCallbackResponse handlePaymentSuccess(Order order, String transactionNo, Long orderId) {
        log.info("VNPAY payment successful for order {} - TxnNo: {}", orderId, transactionNo);
        order.setStatus(OrderStatus.PAID);
        order.setTransactionId(transactionNo);
        order.setRollbackReason(null);
        orderRepository.save(order);
        log.info("Order {} status updated to PAID", orderId);
        return VnpayCallbackResponse.builder()
                .success(true)
                .redirectUrl(fontendUrl + "?status=success&orderId=" + orderId)
                .orderId(orderId.toString())
                .build();
    }

    private VnpayCallbackResponse handlePaymentFailure(Order order, String responseCode, Long orderId) {
        log.warn("VNPAY payment failed for order {} - ResponseCode: {}", orderId, responseCode);
        // Rollback kho hàng
        String errorReason = "VNPAY response code: " + responseCode + " - " + getErrorMessageForDatabase(responseCode);
        paymentRollbackService.rollbackPayment(order, errorReason);
        String urlEncodedMessage = getErrorMessageForUrl(responseCode);
        log.info("Rollback completed for order {}", orderId);
        return VnpayCallbackResponse.builder()
                .success(false)
                .redirectUrl(fontendUrl + "?status=failed&errorCode=" + responseCode + "&message=" + urlEncodedMessage)
                .errorMessage(getErrorMessageForDatabase(responseCode))
                .orderId(orderId.toString())
                .build();
    }

    private Long extractOrderId(String orderInfo) {
        String[] parts = orderInfo.split("\\s+");
        return Long.parseLong(parts[parts.length - 1]);
    }

    private String getErrorMessageForDatabase(String responseCode) {
        return switch (responseCode) {
            case "00" -> "Giao dich thanh cong";
            case "01" -> "Giao dich bi tu choi";
            case "02" -> "The/Tai khoan bi khoa";
            case "03" -> "The/Tai khoan khong hoat dong";
            case "04" -> "Giao dich khong duoc ho tro";
            case "05" -> "Thong tin the khong chinh xac";
            case "06" -> "So tien vuot qua han muc";
            case "07" -> "The het han";
            case "09" -> "The khong ho tro giao dich nay";
            case "10" -> "Giao dich bi huy";
            case "11" -> "Giao dich bi huy boi nguoi dung";
            case "12" -> "Giao dich that bai hoac tu choi";
            default -> "Giao dich that bai";
        };
    }

    private String getErrorMessageForUrl(String responseCode) {
        return switch (responseCode) {
            case "00" -> "Giao%20dich%20thanh%20cong";
            case "01" -> "Giao%20dich%20bi%20tu%20choi";
            case "02" -> "The/Tai%20khoan%20bi%20khoa";
            case "03" -> "The/Tai%20khoan%20khong%20hoat%20dong";
            case "04" -> "Giao%20dich%20khong%20duoc%20ho%20tro";
            case "05" -> "Thong%20tin%20the%20khong%20chinh%20xac";
            case "06" -> "So%20tien%20vuot%20qua%20han%20muc";
            case "07" -> "The%20het%20han";
            case "09" -> "The%20khong%20ho%20tro%20giao%20dich%20nay";
            case "10" -> "Giao%20dich%20bi%20huy";
            case "11" -> "GD%20bi%20huy%20boi%20nguoi%20dung";
            case "12" -> "GD%20that%20bai%20hoac%20tu%20choi";
            default -> "Giao%20dich%20that%20bai";
        };
    }
}
