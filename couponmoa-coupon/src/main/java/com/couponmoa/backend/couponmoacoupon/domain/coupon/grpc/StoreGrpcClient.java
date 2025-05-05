package com.couponmoa.backend.couponmoacoupon.domain.coupon.grpc;

import com.couponmoa.common.exception.ApplicationException;
import com.couponmoa.common.exception.ErrorCode;
import com.couponmoa.grpc.store.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StoreGrpcClient {

    private final StoreServiceGrpc.StoreServiceBlockingStub storeStub;

    public StoreGrpcClient(@GrpcClient("store-service") StoreServiceGrpc.StoreServiceBlockingStub storeStub) {
        this.storeStub = storeStub;
    }

    public StoreResponse getStoreById(Long storeId) {
        log.debug("Requesting store info for storeId: {}", storeId);
        StoreIdRequest request = StoreIdRequest.newBuilder().setStoreId(storeId).build();
        try {
            // .proto의 FindById RPC -> findById 메소드 호출
            StoreResponse response = storeStub.findById(request);
            log.debug("Received store info for storeId: {}", storeId);
            return response;
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while getting store info for storeId {}: {}", storeId, e.getStatus(), e);
            // NOT_FOUND 등 특정 상태 코드에 따라 다른 예외 처리 가능
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new ApplicationException(ErrorCode.STORE_NOT_FOUND, "Store not found with id: " + storeId);
            }
            throw new ApplicationException(ErrorCode.GRPC_CLIENT_ERROR, "Failed to get store info: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while getting store info for storeId {}: {}", storeId, e.getMessage(), e);
            throw new ApplicationException(ErrorCode.EXCEPTION, "Unexpected error: " + e.getMessage());
        }
    }

    public List<String> getSubscribedUserEmails(Long storeId) {
        log.debug("Requesting subscribed user emails for storeId: {}", storeId);
        StoreIdRequest request = StoreIdRequest.newBuilder().setStoreId(storeId).build();
        try {
            // .proto의 FindSubscribedUserEmails RPC -> findSubscribedUserEmails 메소드 호출
            EmailListResponse response = storeStub.findSubscribedUserEmails(request);
            log.debug("Received {} emails for storeId: {}", response.getEmailsCount(), storeId);
            return response.getEmailsList();
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while getting subscribed emails for storeId {}: {}", storeId, e.getStatus(), e);
            throw new ApplicationException(ErrorCode.GRPC_CLIENT_ERROR, "Failed to get subscribed user emails: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while getting subscribed emails for storeId {}: {}", storeId, e.getMessage(), e);
            throw new ApplicationException(ErrorCode.EXCEPTION, "Unexpected error: " + e.getMessage());
        }
    }
}