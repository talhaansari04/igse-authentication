INSERT INTO igse_user_master
(customerId, lastLogin, pass, role, userName)
VALUES('igseuser61@gmail.com', '2024-09-14', '4813494d137e1631bba301d5acab6e7bb7aa74ce1185d456565ef51d737677b2', 'USER', '-');

INSERT INTO igse_registration_status
(id, customerId, isMeterDetailSave, isVoucherRedeemed, isWalletCreated, jsonVoucherPayload)
VALUES(11, 'igseuser61@gmail.com', 'PENDING', 'PENDING', 'PENDING', '{"status": "NOT_USED", "customerId": null, "voucherCode": "62ANW9MV", "voucherDate": "2024-08-17", "voucherBalance": 200.0}');