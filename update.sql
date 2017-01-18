
-- 支付订单表
CREATE TABLE `pay_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appkey` varchar(50) NOT NULL COMMENT '业务key',
  `ordernum` varchar(50) NOT NULL COMMENT '订单号',
  `order_money` int(11) DEFAULT NULL COMMENT '订单金额(分)',
  `order_status` tinyint(4) DEFAULT '0' COMMENT '订单状态(1待付款/2已付款)',
  `order_title` varchar(50) DEFAULT NULL COMMENT '订单标题',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ordernum` (`ordernum`)
) ENGINE=InnoDB COMMENT='支付订单表';

-- 支付记录表
CREATE TABLE `pay_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `paynum` varchar(50) NOT NULL COMMENT '支付号',
  `ordernum` varchar(50) DEFAULT NULL COMMENT '订单号',
  `tradenum` varchar(128) DEFAULT NULL COMMENT '第三方交易号(由第三方通知返回)',
  `pay_money` int(11) DEFAULT NULL COMMENT '支付金额(分)',
  `pay_status` tinyint(4) DEFAULT '0' COMMENT '支付状态(1未支付/2已支付)',
  `pay_type` tinyint(4) DEFAULT '0' COMMENT '支付类型(1微信/2支付宝)',
  `pay_flag` tinyint(4) DEFAULT '0' COMMENT '支付成功标记(1异步通知/2同步回调/3主动查询/4对账)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `paynum` (`paynum`)
) ENGINE=InnoDB COMMENT='支付记录表';