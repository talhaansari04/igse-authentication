package com.igse.service;

import static com.igse.util.ErrorCode.USER_INFO_FOUND;
import com.igse.util.IgseConstants;
import com.igse.dto.UserResponse;
import com.igse.dto.WalletInfoDTO;
import com.igse.entity.UserMaster;
import com.igse.exception.UserException;
import com.igse.repository.PaymentRepo;
import com.igse.repository.db.UserMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserMasterRepository repository;
    private final PaymentRepo paymentRepo;

    public List<UserMaster> getSingleCustomerRecord() {
        return repository.findAll();
    }

    public UserResponse dashBoardData(String customerId, String token, String correlationId) {
        Optional<UserMaster> details = repository.findById(customerId);
        if (details.isPresent()) {
            UserResponse response = new UserResponse();
            BeanUtils.copyProperties(details.get(), response);
            WalletInfoDTO walletInfo = paymentRepo.walletDetails(customerId, token, correlationId);
            response.setWalletInfo(walletInfo);
            return response;
        } else {
            throw new UserException(USER_INFO_FOUND.getCode(), USER_INFO_FOUND.getMessage());
        }
    }

    public List<UserMaster> getAllCustomerRecord(int offSet, int pageSize, String role) {
        PageRequest pageReq = PageRequest.of(offSet, pageSize);
        if (IgseConstants.Role.ADMIN.equalsIgnoreCase(role)) {
            return repository.findAllByRole(role, pageReq);
        } else if (IgseConstants.Role.USER.equalsIgnoreCase(role)) {
            return repository.findAllByRole(role, pageReq);
        } else {
            return repository.findAll(pageReq).getContent();
        }
    }
}
