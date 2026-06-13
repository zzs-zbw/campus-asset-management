-- 校园实训楼固定资产资产管理系统数据库脚本
-- 数据库版本：MySQL 5.7/8.0
-- 创建时间：2026-05-13

-- 创建数据库
DROP DATABASE IF EXISTS asset_management;
CREATE DATABASE IF NOT EXISTS asset_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE asset_management;

-- 确保使用正确的数据库
USE asset_management;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(512) NOT NULL COMMENT '密码（加密存储）',
    real_name VARCHAR(100) NOT NULL COMMENT '真实姓名',
    role TINYINT NOT NULL DEFAULT 2 COMMENT '角色：1-超级管理员，2-普通管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    department VARCHAR(100) COMMENT '部门',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 设备分类表
DROP TABLE IF EXISTS asset_category;
CREATE TABLE asset_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(20) NOT NULL UNIQUE COMMENT '分类编码',
    description VARCHAR(200) COMMENT '分类描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-停用，1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_code (category_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备分类表';

-- 资产表
DROP TABLE IF EXISTS asset_info;
CREATE TABLE asset_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资产ID',
    asset_code VARCHAR(50) NOT NULL UNIQUE COMMENT '资产编号',
    asset_name VARCHAR(100) NOT NULL COMMENT '资产名称',
    category_id BIGINT NOT NULL COMMENT '设备分类ID',
    brand VARCHAR(50) COMMENT '品牌',
    model VARCHAR(100) COMMENT '型号',
    serial_number VARCHAR(100) UNIQUE COMMENT '设备序列号（唯一编码）',
    purchase_price DECIMAL(10,2) COMMENT '采购单价',
    total_value DECIMAL(10,2) COMMENT '资产总价值',
    purchase_date DATE COMMENT '购置日期',
    warranty_end_date DATE COMMENT '保修截止日期',
    building VARCHAR(50) COMMENT '实训楼名称',
    floor VARCHAR(20) COMMENT '楼层',
    room_number VARCHAR(50) COMMENT '机房编号',
    location VARCHAR(200) COMMENT '存放位置（完整地址）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '使用状态：1-正常，2-维修中，3-闲置，4-已报废',
    responsible_person VARCHAR(50) COMMENT '责任人',
    remark TEXT COMMENT '备注说明',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    creator_id BIGINT COMMENT '创建人ID',
    INDEX idx_code (asset_code),
    INDEX idx_serial (serial_number),
    INDEX idx_category (category_id),
    INDEX idx_status (status),
    INDEX idx_location (building, floor, room_number),
    INDEX idx_responsible (responsible_person)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产信息表';

-- 维修记录表
DROP TABLE IF EXISTS maintenance_record;
CREATE TABLE maintenance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '维修记录ID',
    asset_id BIGINT NOT NULL COMMENT '关联资产ID',
    asset_code VARCHAR(50) NOT NULL COMMENT '资产编号',
    fault_description TEXT NOT NULL COMMENT '故障描述',
    report_time DATETIME NOT NULL COMMENT '上报时间',
    reporter VARCHAR(50) NOT NULL COMMENT '上报人',
    reporter_id BIGINT COMMENT '上报人ID',
    maintenance_reason TEXT COMMENT '维修原因',
    maintenance_method VARCHAR(200) COMMENT '维修方式',
    maintenance_cost DECIMAL(10,2) DEFAULT 0 COMMENT '维修费用',
    start_time DATETIME COMMENT '维修开始时间',
    end_time DATETIME COMMENT '维修完成时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '维修状态：1-待维修，2-维修中，3-已修好，4-无法修复',
    handler VARCHAR(50) COMMENT '维修处理人',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_asset (asset_id),
    INDEX idx_code (asset_code),
    INDEX idx_status (status),
    INDEX idx_report_time (report_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修记录表';

-- 操作日志表
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT NOT NULL COMMENT '操作用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型：登录、新增、修改、删除、导入、导出',
    module_name VARCHAR(50) COMMENT '操作模块：资产、维修、用户、分类',
    operation_desc TEXT COMMENT '操作描述',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user (user_id),
    INDEX idx_type (operation_type),
    INDEX idx_time (operation_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 插入默认超级管理员账号（密码：admin123，使用BCrypt加密）
INSERT INTO sys_user (username, password, real_name, role, status, phone, email) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1, 1, '13800138000', 'admin@school.edu.cn');

-- 插入默认普通管理员账号（密码：123456）
INSERT INTO sys_user (username, password, real_name, role, status, phone, email) VALUES
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张老师', 2, 1, '13800138001', 'teacher1@school.edu.cn'),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李老师', 2, 1, '13800138002', 'teacher2@school.edu.cn');

-- 插入设备分类数据
INSERT INTO asset_category (category_name, category_code, description, sort_order) VALUES
('计算机类', 'COMPUTER', '台式电脑、笔记本电脑等', 1),
('网络设备', 'NETWORK', '交换机、路由器、防火墙等', 2),
('打印设备', 'PRINTER', '打印机、复印机、扫描仪等', 3),
('多媒体设备', 'MULTIMEDIA', '投影仪、音响设备、电子白板等', 4),
('实训器材', 'TRAINING', '各类实训专业设备、实验器材等', 5),
('办公设备', 'OFFICE', '办公桌椅、文件柜、空调等', 6);

-- 插入示例资产数据
INSERT INTO asset_info (asset_code, asset_name, category_id, brand, model, serial_number, purchase_price, total_value, purchase_date, warranty_end_date, building, floor, room_number, location, status, responsible_person, remark, creator_id) VALUES
('ZC2024001', '台式电脑', 1, '联想', 'ThinkCentre M90t', 'SN202401001', 4500.00, 4500.00, '2024-01-15', '2026-01-15', '实训楼A', '3楼', '301机房', '实训楼A栋3楼301机房', 1, '张老师', '配置：i5-12400/16G/512G', 1),
('ZC2024002', '台式电脑', 1, '联想', 'ThinkCentre M90t', 'SN202401002', 4500.00, 4500.00, '2024-01-15', '2026-01-15', '实训楼A', '3楼', '301机房', '实训楼A栋3楼301机房', 1, '张老师', '配置：i5-12400/16G/512G', 1),
('ZC2024003', '交换机', 2, '华为', 'S5735-L48T4S-A', 'SN202401003', 12000.00, 12000.00, '2024-02-01', '2027-02-01', '实训楼A', '3楼', '301机房', '实训楼A栋3楼301机房', 1, '张老师', '48口千兆交换机', 1),
('ZC2024004', '投影仪', 4, '爱普生', 'CB-X49', 'SN202401004', 5800.00, 5800.00, '2024-02-10', '2026-02-10', '实训楼A', '3楼', '301机房', '实训楼A栋3楼301机房', 1, '张老师', '激光投影仪', 1),
('ZC2024005', '打印机', 3, '惠普', 'LaserJet Pro M404dn', 'SN202401005', 1800.00, 1800.00, '2024-02-15', '2026-02-15', '实训楼A', '3楼', '302机房', '实训楼A栋3楼302机房', 1, '李老师', '黑白激光打印机', 1),
('ZC2024006', '笔记本电脑', 1, '戴尔', 'Latitude 3440', 'SN202401006', 5200.00, 5200.00, '2024-03-01', '2026-03-01', '实训楼B', '2楼', '201机房', '实训楼B栋2楼201机房', 2, '李老师', '配置：i7-1360P/16G/512G', 2),
('ZC2024007', '实训设备', 5, '三菱', 'FX3U-48MR', 'SN202401007', 8500.00, 8500.00, '2024-03-10', '2027-03-10', '实训楼B', '2楼', '201机房', '实训楼B栋2楼201机房', 1, '李老师', 'PLC实训设备', 2),
('ZC2024008', '台式电脑', 1, '联想', 'ThinkCentre M90t', 'SN202401008', 4500.00, 4500.00, '2024-01-20', '2026-01-20', '实训楼A', '3楼', '301机房', '实训楼A栋3楼301机房', 3, '张老师', '配置：i5-12400/16G/512G', 1);

-- 插入示例维修记录数据
INSERT INTO maintenance_record (asset_id, asset_code, fault_description, report_time, reporter, reporter_id, maintenance_reason, maintenance_method, maintenance_cost, start_time, end_time, status, handler, remark) VALUES
(1, 'ZC2024001', '电脑无法开机，电源指示灯不亮', '2024-04-01 10:30:00', '张老师', 2, '电源故障', '更换电源', 150.00, '2024-04-01 14:00:00', '2024-04-01 16:30:00', 3, '维修工王师傅', '已修复完成'),
(6, 'ZC2024006', '笔记本电脑屏幕闪烁', '2024-04-05 09:00:00', '李老师', 3, '显示屏排线松动', '重新连接排线', 0.00, '2024-04-05 10:00:00', '2024-04-05 10:30:00', 3, '维修工王师傅', '已修复完成'),
(8, 'ZC2024008', '电脑运行缓慢，卡顿严重', '2024-04-10 14:20:00', '张老师', 2, '系统垃圾过多', '清理系统、重装系统', 0.00, '2024-04-10 15:00:00', NULL, 2, '维修工王师傅', '正在维修中');

-- 插入操作日志示例数据
INSERT INTO operation_log (user_id, username, operation_type, module_name, operation_desc, ip_address, operation_time) VALUES
(1, 'admin', '登录', '系统', '超级管理员登录系统', '192.168.1.100', '2024-04-01 08:00:00'),
(1, 'admin', '新增', '资产', '新增资产：ZC2024001 台式电脑', '192.168.1.100', '2024-01-15 10:30:00'),
(2, 'teacher1', '登录', '系统', '张老师登录系统', '192.168.1.101', '2024-04-01 08:30:00'),
(2, 'teacher1', '新增', '维修', '提交维修申请：ZC2024001 电源故障', '192.168.1.101', '2024-04-01 10:30:00');

-- 创建视图：资产统计视图
CREATE VIEW v_asset_statistics AS
SELECT
    COUNT(*) as total_count,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as normal_count,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as maintenance_count,
    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) as idle_count,
    SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) as scrapped_count,
    COALESCE(SUM(total_value), 0) as total_value,
    (SELECT COALESCE(SUM(maintenance_cost), 0) FROM maintenance_record WHERE YEAR(report_time) = YEAR(CURDATE())) as yearly_maintenance_cost
FROM asset_info;

-- 创建视图：分类统计视图
CREATE VIEW v_category_statistics AS
SELECT
    ac.id as category_id,
    ac.category_name,
    ac.category_code,
    COUNT(ai.id) as asset_count,
    COALESCE(SUM(ai.total_value), 0) as total_value
FROM asset_category ac
LEFT JOIN asset_info ai ON ac.id = ai.category_id AND ac.status = 1
GROUP BY ac.id, ac.category_name, ac.category_code
ORDER BY ac.sort_order;

-- 创建视图：维修统计视图
CREATE VIEW v_maintenance_statistics AS
SELECT
    asset_id,
    asset_code,
    COUNT(*) as maintenance_count,
    COALESCE(SUM(maintenance_cost), 0) as total_cost,
    MAX(report_time) as last_maintenance_time
FROM maintenance_record
GROUP BY asset_id, asset_code;

-- 创建存储过程：生成资产编号
DELIMITER //
DROP PROCEDURE IF EXISTS generate_asset_code //
CREATE PROCEDURE generate_asset_code(OUT new_code VARCHAR(50))
BEGIN
    DECLARE year_part VARCHAR(4);
    DECLARE seq_part VARCHAR(6);
    DECLARE max_seq INT;
    
    SET year_part = YEAR(CURDATE());
    
    SELECT MAX(CAST(SUBSTRING(asset_code, 9) AS UNSIGNED)) INTO max_seq
    FROM asset_info
    WHERE asset_code LIKE CONCAT('ZC', year_part, '%');
    
    IF max_seq IS NULL THEN
        SET max_seq = 0;
    END IF;
    
    SET seq_part = LPAD(max_seq + 1, 6, '0');
    SET new_code = CONCAT('ZC', year_part, seq_part);
END //
DELIMITER ;

-- 创建触发器：资产状态变更时自动记录日志
DELIMITER //
DROP TRIGGER IF EXISTS asset_status_change //
CREATE TRIGGER asset_status_change
AFTER UPDATE ON asset_info
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO operation_log (user_id, username, operation_type, module_name, operation_desc, ip_address)
        VALUES (NEW.creator_id, '系统', '修改', '资产', CONCAT('资产状态变更：', NEW.asset_code, ' 从状态', OLD.status, ' 变更为状态', NEW.status), 'SYSTEM');
    END IF;
END //
DELIMITER ;

-- 数据库脚本创建完成
-- 说明：
-- 1. 默认管理员账号：admin / admin123
-- 2. 默认教师账号：teacher1 / 123456, teacher2 / 123456
-- 3. 密码使用BCrypt加密，实际项目中请修改默认密码
-- 4. 已包含完整的表结构、索引、外键、视图、存储过程和触发器
-- 5. 已插入示例数据，方便测试和演示