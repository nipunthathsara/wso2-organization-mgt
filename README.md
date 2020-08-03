# wso2-organization-mgt
Hierarchical organization management REST API

```oracle-sql
CREATE TABLE IDN_ORG (
    ID VARCHAR2(255) NOT NULL,
    TENANT_ID INTEGER NOT NULL,
    NAME VARCHAR2(255) NOT NULL,
    CREATED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    LAST_MODIFIED TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    HAS_ATTRIBUTE NUMBER(1) DEFAULT '1' NOT NULL,
    STATUS NUMBER(1) DEFAULT '1' NOT NULL,
    PARENT_ID VARCHAR2(255),
    PRIMARY KEY (ID),
    CONSTRAINT TENANT_ORG_CONSTRAINT UNIQUE (NAME, TENANT_ID)
)
/
CREATE TABLE IDN_ORG_ATTRIBUTES (
    ID VARCHAR2(255) NOT NULL,
    ORG_ID VARCHAR2(255) NOT NULL,
    ATTR_KEY VARCHAR2(255) NOT NULL,
    ATTR_VALUE VARCHAR2(255),
    PRIMARY KEY (ID),
    CONSTRAINT ORG_ATTRIBUTE_CONSTRAINT UNIQUE (ORG_ID, ATTR_KEY)
)
/
CREATE TABLE UM_USERSTORE_ORG_HIERARCHY (
    ORG_ID VARCHAR2(255) NOT NULL,
    RDN VARCHAR2(255) NOT NULL,
    DN VARCHAR2(255) NOT NULL,
    PRIMARY KEY (ORG_ID)
);
```

```
IDN_ORG;
+---------------+--------------+-------------+----------+-----------------+
|  COLUMN_NAME  |   DATA_TYPE  | DATA_LENGTH | NULLABLE | DEFAULT_ON_NULL |
+---------------+--------------+-------------+----------+-----------------+
| ID            | VARCHAR2     | 255         | N        | NO              |
+---------------+--------------+-------------+----------+-----------------+
| TENANT_ID     | NUMBER       | 22          | N        | NO              |
+---------------+--------------+-------------+----------+-----------------+
| NAME          | VARCHAR2     | 255         | N        | NO              |
+---------------+--------------+-------------+----------+-----------------+
| CREATED_TIME  | TIMESTAMP(6) | 11          | N        | NO              |
+---------------+--------------+-------------+----------+-----------------+
| LAST_MODIFIED | TIMESTAMP(6) | 11          | N        | NO              |
+---------------+--------------+-------------+----------+-----------------+
| HAS_ATTRIBUTE | NUMBER       | 1           | N        | NO              |
+---------------+--------------+-------------+----------+-----------------+
| STATUS        | NUMBER       | 1           | N        | NO              |
+---------------+--------------+-------------+----------+-----------------+
| PARENT_ID     | VARCHAR2     | 255         | Y        | NO              |
+---------------+--------------+-------------+----------+-----------------+

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
| ATTR_VALUE  | VARCHAR2  | 255         | Y        | NO              |
+-------------+-----------+-------------+----------+-----------------+

UM_USERSTORE_ORG_HIERARCHY;
+-------------+-----------+-------------+----------+-----------------+
| COLUMN_NAME | DATA_TYPE | DATA_LENGTH | NULLABLE | DEFAULT_ON_NULL |
+-------------+-----------+-------------+----------+-----------------+
| ORG_ID      | VARCHAR2  | 255         | N        | NO              |
+-------------+-----------+-------------+----------+-----------------+
| RDN         | VARCHAR2  | 255         | N        | NO              |
+-------------+-----------+-------------+----------+-----------------+
| DN          | VARCHAR2  | 255         | N        | NO              |
+-------------+-----------+-------------+----------+-----------------+
```