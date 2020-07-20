# wso2-organization-mgt
Hierarchical organization management REST API

```mysql-sql
CREATE TABLE IF NOT EXISTS IDN_CONFIG_TYPE (
ID          VARCHAR(255)  NOT NULL,
NAME        VARCHAR(255)  NOT NULL,
DESCRIPTION VARCHAR(1023) NULL,
PRIMARY KEY (ID),
CONSTRAINT TYPE_NAME_CONSTRAINT UNIQUE (NAME)
);

CREATE TABLE IF NOT EXISTS IDN_CONFIG_RESOURCE (
ID            VARCHAR(255) NOT NULL,
TENANT_ID     INT          NOT NULL,
NAME          VARCHAR(255) NOT NULL,
CREATED_TIME  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
LAST_MODIFIED TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
HAS_FILE      tinyint(1)   NOT NULL,
HAS_ATTRIBUTE tinyint(1)   NOT NULL,
TYPE_ID       VARCHAR(255) NOT NULL,
PRIMARY KEY (ID),
CONSTRAINT NAME_TENANT_TYPE_CONSTRAINT UNIQUE (NAME,TENANT_ID,TYPE_ID)
);

ALTER TABLE IDN_CONFIG_RESOURCE ADD CONSTRAINT TYPE_ID_FOREIGN_CONSTRAINT FOREIGN KEY (TYPE_ID) REFERENCES IDN_CONFIG_TYPE (ID) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE IF NOT EXISTS IDN_CONFIG_ATTRIBUTE (
  ID         VARCHAR(255)  NOT NULL,
  RESOURCE_ID  VARCHAR(255) NOT NULL,
  ATTR_KEY   VARCHAR(1023) NOT NULL,
  ATTR_VALUE VARCHAR(1023) NULL,
  PRIMARY KEY (ID),
  CONSTRAINT RESOURCE_KEY_VAL_CONSTRAINT UNIQUE (RESOURCE_ID(64), ATTR_KEY(703))
);
ALTER TABLE IDN_CONFIG_ATTRIBUTE ADD CONSTRAINT RESOURCE_ID_ATTRIBUTE_FOREIGN_CONSTRAINT FOREIGN KEY (RESOURCE_ID) REFERENCES IDN_CONFIG_RESOURCE (ID) ON DELETE CASCADE ON UPDATE CASCADE;


CREATE TABLE IF NOT EXISTS IDN_CONFIG_FILE (
  ID        VARCHAR(255) NOT NULL,
  VALUE     BLOB   NULL,
  RESOURCE_ID VARCHAR(255) NOT NULL,
  NAME        VARCHAR(255) NULL,
  PRIMARY KEY (ID)
);
ALTER TABLE IDN_CONFIG_FILE ADD CONSTRAINT RESOURCE_ID_FILE_FOREIGN_CONSTRAINT FOREIGN KEY (RESOURCE_ID) REFERENCES IDN_CONFIG_RESOURCE (ID) ON DELETE CASCADE ON UPDATE CASCADE;
```

```
IDN_ORG;
+---------------+--------------+------+-----+-------------------+-------+
| Field         | Type         | Null | Key | Default           | Extra |
+---------------+--------------+------+-----+-------------------+-------+
| ID            | varchar(255) | NO   | PRI | NULL              |       |
| TENANT_ID     | int(11)      | NO   |     | NULL              |       |
| NAME          | varchar(255) | NO   | MUL | NULL              |       |
| CREATED_TIME  | timestamp    | NO   |     | CURRENT_TIMESTAMP |       |
| LAST_MODIFIED | timestamp    | NO   |     | CURRENT_TIMESTAMP |       |
| HAS_ATTRIBUTE | tinyint(1)   | NO   |     | NULL              |       |
| SUSPENDED     | tinyint(1)   | NO   |     | NULL              |       |
| PARENT_ID     | varchar(255) | NO   | MUL | NULL              |       |
| RDN           | varchar(255) | NO   | MUL | NULL              |       |
| DN            | varchar(255) | NO   | MUL | NULL              |       |
+---------------+--------------+------+-----+-------------------+-------+

IDN_ORG_ATTRIBUTES;
+-------------+---------------+------+-----+---------+-------+
| Field       | Type          | Null | Key | Default | Extra |
+-------------+---------------+------+-----+---------+-------+
| ID          | varchar(255)  | NO   | PRI | NULL    |       |
| ORG_ID      | varchar(255)  | NO   | MUL | NULL    |       |
| ATTR_KEY    | varchar(1023) | NO   |     | NULL    |       |
| ATTR_VALUE  | varchar(1023) | YES  |     | NULL    |       |
+-------------+---------------+------+-----+---------+-------+

IDN_ORG_CHILDREN;
+-------------+---------------+------+-----+---------+-------+
| Field       | Type          | Null | Key | Default | Extra |
+-------------+---------------+------+-----+---------+-------+
| PARENT_ID   | varchar(255)  | NO   | PRI | NULL    |       |
| CHILD_ID    | varchar(1023) | YES  | PRI | NULL    |       |
+-------------+---------------+------+-----+---------+-------+
```