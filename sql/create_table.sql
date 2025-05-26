CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT "主键",
                       userName VARCHAR(256) NOT NULL COMMENT "昵称",
                       userAccount VARCHAR(256) NOT NULL COMMENT "账号",
                       userPassword VARCHAR(256) NOT NULL COMMENT "密码",
                       phone CHAR(11) NOT NULL COMMENT "手机号",
                       email VARCHAR(40) COMMENT "邮箱",
                       avatarUrl VARCHAR(256) NOT NULL COMMENT "头像",
                       userRole TINYINT(1) DEFAULT 0 COMMENT "0表示普通用户,1表示管理员",
                       userStatus TINYINT(1) DEFAULT 0 COMMENT "0表示正常,1表示封号",
                       createTime DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
                       updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT "更新时间",
                       isDelete TINYINT DEFAULT 0 COMMENT "0表示没有,1表示已经删除了"
)
