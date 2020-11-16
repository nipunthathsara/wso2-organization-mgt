# wso2-organization-mgt
Hierarchical organization management REST API - IS version 5.10.0 (Oracle)

Execute the below script against the User Management(UM) Data source.
```oracle-sql
CREATE TABLE UM_ORG (
    ID VARCHAR2(255) NOT NULL,
    TENANT_ID INTEGER NOT NULL,
    NAME VARCHAR2(512) NOT NULL,
    DISPLAY_NAME VARCHAR2(512),
    DESCRIPTION VARCHAR2(1024),
    CREATED_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    LAST_MODIFIED TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATED_BY VARCHAR2(512) NOT NULL,
    LAST_MODIFIED_BY VARCHAR2(512) NOT NULL,
    HAS_ATTRIBUTES NUMBER(1) DEFAULT '1' NOT NULL,
    STATUS VARCHAR2(255) DEFAULT 'ACTIVE' NOT NULL,
    PARENT_ID VARCHAR2(255),
    PRIMARY KEY (ID),
    CONSTRAINT TENANT_ORG_CONSTRAINT UNIQUE (NAME, TENANT_ID),
    CONSTRAINT FK_UM_ORG_UM_ORG FOREIGN KEY(PARENT_ID) REFERENCES UM_ORG (ID) ON DELETE CASCADE
)
/
CREATE TABLE UM_ORG_ATTRIBUTES (
    ID VARCHAR2(255) NOT NULL,
    ORG_ID VARCHAR2(255) NOT NULL,
    ATTR_KEY VARCHAR2(255) NOT NULL,
    ATTR_VALUE VARCHAR2(512),
    PRIMARY KEY (ID),
    CONSTRAINT FK_UM_ORG_ATTRIBUTES_UM_ORG FOREIGN KEY(ORG_ID) REFERENCES UM_ORG (ID) ON DELETE CASCADE,
    CONSTRAINT ORG_ATTRIBUTE_CONSTRAINT UNIQUE (ORG_ID, ATTR_KEY)
)
/
CREATE TABLE UM_ORG_USERSTORE_CONFIGS (
    ID VARCHAR2(255) NOT NULL,
    ORG_ID VARCHAR2(255) NOT NULL,
    ATTR_KEY VARCHAR2(255) NOT NULL,
    ATTR_VALUE VARCHAR2(512) NOT NULL,
    PRIMARY KEY (ID),
    CONSTRAINT FK_UM_ORG_USERSTORE_CONFIGS_UM_ORG FOREIGN KEY(ORG_ID) REFERENCES UM_ORG (ID) ON DELETE CASCADE,
    CONSTRAINT ORG_USERSTORE_CONFIG_CONSTRAINT UNIQUE (ORG_ID, ATTR_KEY)
)
/
CREATE TABLE UM_USER_ROLE_ORG (
    UM_ID VARCHAR2(255) NOT NULL,
    UM_USER_ID VARCHAR2(255) NOT NULL,
    UM_ROLE_ID VARCHAR2(1024) NOT NULL,
    UM_HYBRID_ROLE_ID INTEGER NOT NULL,
    UM_TENANT_ID INTEGER DEFAULT 0,
    ORG_ID VARCHAR2(255) NOT NULL,
    ASSIGNED_AT VARCHAR2(255) NOT NULL,
    INHERIT INTEGER DEFAULT 0,
    PRIMARY KEY (UM_ID),
    CONSTRAINT UM_USER_ROLE_ORG_CONSTRAINT UNIQUE(UM_USER_ID, UM_HYBRID_ROLE_ID, UM_TENANT_ID, ORG_ID, ASSIGNED_AT, INHERIT),
    CONSTRAINT FK_UM_USER_ROLE_ORG_UM_HYBRID_ROLE FOREIGN KEY (UM_HYBRID_ROLE_ID, UM_TENANT_ID) REFERENCES UM_HYBRID_ROLE(UM_ID, UM_TENANT_ID) ON DELETE CASCADE,
    CONSTRAINT FK_UM_USER_ROLE_ORG_UM_ORG FOREIGN KEY (ORG_ID) REFERENCES UM_ORG(ID) ON DELETE CASCADE,
    CONSTRAINT FK_UM_USER_ROLE_ORG_ASSIGNED_AT FOREIGN KEY (ORG_ID) REFERENCES UM_ORG(ID) ON DELETE CASCADE
)
/
CREATE VIEW ORG_MGT_VIEW AS
SELECT
    K.*,
--    To cater search by parent organization's name
    CASE WHEN K.PARENT_ID IS NULL THEN NULL WHEN K.PARENT_ID IS NOT NULL THEN N.NAME END AS PARENT_NAME,
--    To cater search by parent organization's display name
    CASE WHEN K.PARENT_ID IS NULL THEN NULL WHEN K.PARENT_ID IS NOT NULL THEN N.DISPLAY_NAME END AS PARENT_DISPLAY_NAME
FROM
(SELECT
    O.ID,
    O.TENANT_ID,
    O.NAME,
    O.DISPLAY_NAME,
    O.DESCRIPTION,
    O.CREATED_TIME,
    O.LAST_MODIFIED,
    O.CREATED_BY,
    O.LAST_MODIFIED_BY,
    O.HAS_ATTRIBUTES,
    O.STATUS,
    O.PARENT_ID,
    A.ID ATTR_ID,
    A.ATTR_KEY,
    A.ATTR_VALUE,
    C.ID CONFIG_ID,
    C.ATTR_KEY CONFIG_KEY,
    C.ATTR_VALUE CONFIG_VALUE,
    U.UM_USER_ID,
    U.UM_ROLE_ID
FROM
    UM_ORG O
LEFT JOIN
    UM_ORG_ATTRIBUTES A
ON
    (O.HAS_ATTRIBUTES = 1 AND O.ID = A.ORG_ID)
LEFT JOIN
    UM_ORG_USERSTORE_CONFIGS C
ON
    O.ID = C.ORG_ID
LEFT JOIN
    UM_USER_ROLE_ORG U
ON
    O.ID = U.ORG_ID) K,
UM_ORG N
WHERE
    K.PARENT_ID = N.ID OR (K.PARENT_ID IS NULL)
/
CREATE VIEW ORG_AUTHZ_VIEW AS
SELECT 
    URP.UM_PERMISSION_ID,
    URO.UM_ROLE_ID,
    URO.UM_HYBRID_ROLE_ID,
    URP.UM_ROLE_NAME,
    URP.UM_IS_ALLOWED,
    URP.UM_TENANT_ID,
    URP.UM_DOMAIN_ID,
    UP.UM_RESOURCE_ID,
    UP.UM_ACTION,
    UHR.UM_ID,
    URO.UM_USER_ID,
    URO.ORG_ID
FROM
    UM_USER_ROLE_ORG URO
LEFT JOIN
    UM_HYBRID_ROLE UHR
ON
    (UHR.UM_ID = URO.UM_HYBRID_ROLE_ID)
LEFT JOIN
    UM_ROLE_PERMISSION URP
ON
    (UHR.UM_ROLE_NAME = URP.UM_ROLE_NAME)
LEFT JOIN
    UM_PERMISSION UP
ON 
    URP.UM_PERMISSION_ID = UP.UM_ID
```

```
UM_ORG;
+------------------+--------------+-------------+----------+-----------------+
|    COLUMN_NAME   |   DATA_TYPE  | DATA_LENGTH | NULLABLE | DEFAULT_ON_NULL |
+------------------+--------------+-------------+----------+-----------------+
| ID               | VARCHAR2     | 255         | N        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| TENANT_ID        | NUMBER       | 22          | N        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| NAME             | VARCHAR2     | 512         | N        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| DISPLAY_NAME     | VARCHAR2     | 512         | Y        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| DESCRIPTION      | VARCHAR2     | 1024        | Y        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| CREATED_TIME     | TIMESTAMP(6) | 11          | N        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| LAST_MODIFIED    | TIMESTAMP(6) | 11          | N        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| CREATED_BY       | VARCHAR2     | 512         | N        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| LAST_MODIFIED_BY | VARCHAR2     | 512         | N        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| HAS_ATTRIBUTES   | NUMBER       | 22          | N        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| STATUS           | VARCHAR2     | 255         | N        | NO              |
+------------------+--------------+-------------+----------+-----------------+
| PARENT_ID        | VARCHAR2     | 255         | N        | NO              |
+------------------+--------------+-------------+----------+-----------------+
UM_ORG_ATTRIBUTES;
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
UM_ORG_USERSTORE_CONFIGS
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
UM_USER_ROLE_ORG
+-------------------+-----------+-------------+----------+-----------------+
|    COLUMN_NAME    | DATA_TYPE | DATA_LENGTH | NULLABLE | DEFAULT_ON_NULL |
+-------------------+-----------+-------------+----------+-----------------+
| UM_ID             | VARCHAR2  | 255         | N        | NO              |
+-------------------+-----------+-------------+----------+-----------------+
| UM_USER_ID        | VARCHAR2  | 255         | N        | NO              |
+-------------------+-----------+-------------+----------+-----------------+
| UM_ROLE_ID        | VARCHAR2  | 1024        | N        | NO              |
+-------------------+-----------+-------------+----------+-----------------+
| UM_HYBRID_ROLE_ID | NUMBER    | 22          | N        | NO              |
+-------------------+-----------+-------------+----------+-----------------+
| UM_TENANT_ID      | NUMBER    | 22          | Y        | NO              |
+-------------------+-----------+-------------+----------+-----------------+
| ORG_ID            | VARCHAR2  | 255         | N        | NO              |
+-------------------+-----------+-------------+----------+-----------------+
```
