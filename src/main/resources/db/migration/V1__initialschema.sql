-- `igse-admin`.igse_user_master definition

CREATE TABLE `igse_user_master` (
  `customerId` varchar(255) NOT NULL,
  `lastLogin` date DEFAULT NULL,
  `pass` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`customerId`)
);

-- `igse-admin`.demographic_details definition

CREATE TABLE `demographic_details` (
  `customerId` varchar(255) NOT NULL,
  `addressArea` varchar(255) DEFAULT NULL,
  `addressFlatNo` varchar(255) DEFAULT NULL,
  `addressLandmark` varchar(255) DEFAULT NULL,
  `addressPinCode` bigint DEFAULT NULL,
  `flatRegistrationNo` varchar(255) DEFAULT NULL,
  `numberOfBedRoom` int DEFAULT NULL,
  `propertyType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`customerId`)
);

-- `igse-admin`.igse_registration_status definition

CREATE TABLE `igse_registration_status` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customerId` varchar(255) DEFAULT NULL,
  `isMeterDetailSave` varchar(255) DEFAULT NULL,
  `isVoucherRedeemed` varchar(255) DEFAULT NULL,
  `isWalletCreated` varchar(255) DEFAULT NULL,
  `jsonVoucherPayload` json DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- `igse-admin`.event_log definition

CREATE TABLE `event_log` (
  `event_id` int NOT NULL,
  `event_name` varchar(255) DEFAULT NULL,
  `eventId` int NOT NULL,
  `eventName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`event_id`)
);

CREATE TABLE IF NOT EXISTS shedlock (
  name VARCHAR(64),
  lock_until TIMESTAMP(3) NULL,
  locked_at TIMESTAMP(3) NULL,
  locked_by VARCHAR(255),
  PRIMARY KEY (name)
);