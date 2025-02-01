package unit.com.igse.test.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.igse.config.EncoderDecoder;
import com.igse.dto.registration.UserRegRequest;
import com.igse.entity.UserMaster;
import com.igse.exception.UserException;
import com.igse.repository.db.UserMasterRepository;
import com.igse.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;
    @Mock
    private EncoderDecoder encoderDecoder;
    @Mock
    private UserMasterRepository userMasterRepository;


    @Test
    void user_registerUser_failedUnit() {
        UserRegRequest registration = UserRegRequest.builder()
                .customerId("123654").build();
        when(userMasterRepository.findById(any()))
                .thenReturn(Optional.of(UserMaster.builder().customerId("123654").build()));
        //doThrow(UserException.builder().status(400).message("exist").build()).when(customerService).saveUser(registration);
        assertThrows(UserException.class, () -> customerService.saveUser(registration));
        verify(userMasterRepository, times(1)).findById(any());
        //verify(customerService,times(1)).saveUser(any());
    }
}
