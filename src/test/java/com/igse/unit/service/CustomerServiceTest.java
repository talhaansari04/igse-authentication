package com.igse.unit.service;

import com.igse.config.EncoderDecoder;
import com.igse.dto.registration.UserRegistrationDTO;
import com.igse.entity.UserMaster;
import com.igse.exception.UserException;
import com.igse.repository.UserMasterRepository;
import com.igse.repository.VoucherCodeRepository;
import com.igse.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;
    @Mock
    private  EncoderDecoder encoderDecoder;
    @Mock
    private  UserMasterRepository userMasterRepository;
    @Mock
    private  VoucherCodeRepository codeRepository;

    @Test
    void user_registerUser_failed(){
        UserRegistrationDTO registration = UserRegistrationDTO.builder().customerId("123654").build();
        when(userMasterRepository.findById(any()))
                .thenReturn(Optional.of(UserMaster.builder().customerId("123654").build()));
        //doThrow(UserException.builder().status(400).message("exist").build()).when(customerService).saveUser(registration);
        assertThrows(UserException.class,()->customerService.saveUser(registration));
        verify(userMasterRepository,times(1)).findById(any());
        //verify(customerService,times(1)).saveUser(any());
    }
}
