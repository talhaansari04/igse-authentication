package com.igse.config;

import com.igse.entity.UnitPriceEntity;
import com.igse.entity.UserMaster;
import com.igse.repository.EnergyChargesRepo;
import com.igse.repository.UserMasterRepository;
import com.igse.util.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoadAdminDetailsByYml implements CommandLineRunner {
    @Value("${igse.admin.id}")
    private String adminID;
    @Value("${igse.admin.pass}")
    private String adminPass;
    @Autowired
    private UserMasterRepository repository;
    @Autowired
    EnergyChargesRepo chargesRepo;
    @Autowired
    private EncoderDecoder encoderDecoder;

    @Override
    public void run(String... args) throws Exception {
        Optional<UserMaster> details = repository.findById(adminID);
        if (!details.isPresent()){
            UserMaster userMaster=new UserMaster();
            userMaster.setRole(GlobalConstant.Role.ADMIN);
            userMaster.setCustomerId(adminID);
            userMaster.setPass(encoderDecoder.encrypt(adminPass));
            repository.save(userMaster);
        }
        Optional<UnitPriceEntity> adminData = chargesRepo.findById(adminID);
        if (!adminData.isPresent()) {
            UnitPriceEntity priceEntity = new UnitPriceEntity();
            priceEntity.setAdminId(adminID);
            priceEntity.setDayCharge(0.34);
            priceEntity.setNightCharge(0.2);
            priceEntity.setGasCharge(0.1);
            priceEntity.setStandingChargePerDay(0.74);
            chargesRepo.save(priceEntity);
        }
    }
}
