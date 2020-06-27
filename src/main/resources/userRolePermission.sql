---- add user
-- admin
INSERT INTO `user_info` (`id`,`name`,`password`,`salt`,`username`) VALUES (1, 'Admin','wangyang', 'xbNIxrQfn6COSYn1/GdloA==', 'wangyang');
-- view
INSERT INTO `user_info` (`id`,`name`,`password`,`salt`,`username`) VALUES (2, 'View','yang', 'xbNIxrQfn6COSYn1/GdloA==', 'yang');
-- add
INSERT INTO `user_info` (`id`,`name`,`password`,`salt`,`username`) VALUES (3, 'Add','wang', 'xbNIxrQfn6COSYn1/GdloA==', 'wang');


---- add permission
INSERT INTO `sys_permission` (`id`,`description`,`name`,`url`) VALUES (1,'Search','userInfo:view','/jersey/upload/check');
INSERT INTO `sys_permission` (`id`,`description`,`name`,`url`) VALUES (2,'update','userInfo:add','/jersey/upload/update');
INSERT INTO `sys_permission` (`id`,`description`,`name`,`url`) VALUES (3,'update','userInfo:buy','/rest/api/v1/sale');

---- add role
-- admin role
INSERT INTO `sys_role` (`id`,`description`,`name`) VALUES (1,'Admin','admin');
INSERT INTO `sys_role` (`id`,`description`,`name`) VALUES (2,'View','View');
INSERT INTO `sys_role` (`id`,`description`,`name`) VALUES (3,'Add','Add');

---- relationship: role vs permission
-- admin permission
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (1,1);
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (2,1);
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (3,1);

-- view permission
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (1,2);

-- add permission
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (1,1);
INSERT INTO `sys_role_permission` (`permission_id`,`role_id`) VALUES (2,2);

---- relationship: user vs role
-- admin
INSERT INTO `sys_user_role` (`role_id`,`uid`) VALUES (1,1);
-- view
INSERT INTO `sys_user_role` (`role_id`,`uid`) VALUES (2,2);
-- add
INSERT INTO `sys_user_role` (`role_id`,`uid`) VALUES (3,3);

---- user -> n:role -> n:permission
