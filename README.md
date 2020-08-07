# wso2-organization-mgt
Hierarchical organization management REST API

```oracle-sql
CREATE TABLE IDN_ORG (
    ID VARCHAR2(255) NOT NULL,
    TENANT_ID INTEGER NOT NULL,
    NAME VARCHAR2(512) NOT NULL,
    DESCRIPTION VARCHAR2(1024),
    CREATED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    LAST_MODIFIED TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    HAS_ATTRIBUTES NUMBER(1) DEFAULT '1' NOT NULL,
    ACTIVE NUMBER(1) DEFAULT '1' NOT NULL,
    PARENT_ID VARCHAR2(255) DEFAULT 'ROOT' NOT NULL,
    PRIMARY KEY (ID),
    CONSTRAINT TENANT_ORG_CONSTRAINT UNIQUE (NAME, TENANT_ID)
)
/
CREATE TABLE IDN_ORG_ATTRIBUTES (
    ID VARCHAR2(255) NOT NULL,
    ORG_ID VARCHAR2(255) NOT NULL,
    ATTR_KEY VARCHAR2(255) NOT NULL,
    ATTR_VALUE VARCHAR2(512),
    PRIMARY KEY (ID),
    CONSTRAINT FK_IDN_ORG_ATTRIBUTES_IDN_ORG FOREIGN KEY(ORG_ID) REFERENCES IDN_ORG (ID) ON DELETE CASCADE,
    CONSTRAINT ORG_ATTRIBUTE_CONSTRAINT UNIQUE (ORG_ID, ATTR_KEY)
)
/
CREATE TABLE IDN_ORG_USERSTORE_CONFIGS (
    ID VARCHAR2(255) NOT NULL,
    ORG_ID VARCHAR2(255) NOT NULL,
    ATTR_KEY VARCHAR2(255) NOT NULL,
    ATTR_VALUE VARCHAR2(512) NOT NULL,
    PRIMARY KEY (ID),
    CONSTRAINT FK_IDN_ORG_USERSTORE_CONFIGS_IDN_ORG FOREIGN KEY(ORG_ID) REFERENCES IDN_ORG (ID) ON DELETE CASCADE,
    CONSTRAINT ORG_USERSTORE_CONFIG_CONSTRAINT UNIQUE (ORG_ID, ATTR_KEY)
)
/
CREATE VIEW ORG_MGT_VIEW AS
SELECT
    O.ID,
    O.TENANT_ID,
    O.NAME,
    O.DESCRIPTION,
    O.CREATED_TIME,
    O.LAST_MODIFIED,
    O.HAS_ATTRIBUTES,
    O.ACTIVE,
    O.PARENT_ID,
    A.ID ATTR_ID,
    A.ATTR_KEY,
    A.ATTR_VALUE,
    C.ID CONFIG_ID,
    C.ATTR_KEY CONFIG_KEY,
    C.ATTR_VALUE CONFIG_VALUE
FROM
    IDN_ORG O
INNER JOIN
    IDN_ORG_ATTRIBUTES A
ON
    (O.HAS_ATTRIBUTES = 1 AND O.ID = A.ORG_ID)
LEFT JOIN
    IDN_ORG_USERSTORE_CONFIGS C
ON
    O.ID = C.ORG_ID
```

```
IDN_ORG;
+----------------+--------------+-------------+----------+-----------------+
|   COLUMN_NAME  |   DATA_TYPE  | DATA_LENGTH | NULLABLE | DEFAULT_ON_NULL |
+----------------+--------------+-------------+----------+-----------------+
| ID             | VARCHAR2     | 255         | N        | NO              |
+----------------+--------------+-------------+----------+-----------------+
| TENANT_ID      | NUMBER       | 22          | N        | NO              |
+----------------+--------------+-------------+----------+-----------------+
| NAME           | VARCHAR2     | 512         | N        | NO              |
+----------------+--------------+-------------+----------+-----------------+
| DESCRIPTION    | VARCHAR2     | 1024        | Y        | NO              |
+----------------+--------------+-------------+----------+-----------------+
| CREATED_TIME   | TIMESTAMP(6) | 11          | N        | NO              |
+----------------+--------------+-------------+----------+-----------------+
| LAST_MODIFIED  | TIMESTAMP(6) | 11          | N        | NO              |
+----------------+--------------+-------------+----------+-----------------+
| HAS_ATTRIBUTES | NUMBER       | 22          | N        | NO              |
+----------------+--------------+-------------+----------+-----------------+
| ACTIVE         | NUMBER       | 22          | N        | NO              |
+----------------+--------------+-------------+----------+-----------------+
| PARENT_ID      | VARCHAR2     | 255         | N        | NO              |
+----------------+--------------+-------------+----------+-----------------+
IDN_ORG_ATTRIBUTES;
+-------------+-----------+-------------+----------+-----------------+
| COLUMN_NAME | DATA_TYPE | DATA_LENGTH | NULLABLE | DEFAULT_ON_NULL |
+-------------+-----------+-------------+----------+-----------------+
| ID          | VARCHAR2  | 255         | N        | NO              |
+-------------+-----------+-------------+----------+-----------------+
| ORG_ID      | VARCHAR2  | 255         | N        | NO              |
+-------------+-----------+-------------+----------+-----------------+
| ATTR_KEY    | VARCHAR2  | 255         | N        | NO              |
+-------------+-----------+-------------+----------+-----------------+
| ATTR_VALUE  | VARCHAR2  | 512         | Y        | NO              |
+-------------+-----------+-------------+----------+-----------------+
IDN_ORG_USERSTORE_CONFIGS
+-------------+-----------+-------------+----------+-----------------+
| COLUMN_NAME | DATA_TYPE | DATA_LENGTH | NULLABLE | DEFAULT_ON_NULL |
+-------------+-----------+-------------+----------+-----------------+
| ID          | VARCHAR2  | 255         | N        | NO              |
+-------------+-----------+-------------+----------+-----------------+
| ORG_ID      | VARCHAR2  | 255         | N        | NO              |
+-------------+-----------+-------------+----------+-----------------+
| ATTR_KEY    | VARCHAR2  | 255         | N        | NO              |
+-------------+-----------+-------------+----------+-----------------+
| ATTR_VALUE  | VARCHAR2  | 512         | N        | NO              |
+-------------+-----------+-------------+----------+-----------------+
```
