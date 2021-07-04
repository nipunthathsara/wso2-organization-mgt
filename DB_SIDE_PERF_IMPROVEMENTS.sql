--------------------------------------------------------------------------------
--------------------------DB SIDE PERFORMANCE IMPROVEMENTS----------------------
--------------------------------------------------------------------------------

--Need to drop materialized view logs if already created.
--DROP MATERIALIZED VIEW LOG ON UM_ORG;

--DROP MATERIALIZED VIEW LOG ON UM_ORG_ATTRIBUTES;

--DROP MATERIALIZED VIEW LOG ON UM_ORG_USERSTORE_CONFIGS;

--DROP MATERIALIZED VIEW LOG ON UM_USER_ROLE_ORG;

--DROP MATERIALIZED VIEW LOG ON UM_HYBRID_ROLE;

--DROP MATERIALIZED VIEW LOG ON UM_ROLE_PERMISSION;

--DROP MATERIALIZED VIEW LOG ON UM_PERMISSION;

--Create new materialized view logs for fast refresh
CREATE MATERIALIZED VIEW LOG ON UM_ORG WITH
    ROWID ( ID,
            TENANT_ID,
            NAME,
            DISPLAY_NAME,
            DESCRIPTION,
            CREATED_TIME,
            LAST_MODIFIED,
            CREATED_BY,
            LAST_MODIFIED_BY,
            HAS_ATTRIBUTES,
            STATUS,
            PARENT_ID ),
    COMMIT SCN
    INCLUDING NEW VALUES;

CREATE MATERIALIZED VIEW LOG ON UM_ORG_ATTRIBUTES WITH
    ROWID ( ID,
            ORG_ID,
            ATTR_KEY,
            ATTR_VALUE ),
    COMMIT SCN
    INCLUDING NEW VALUES;

CREATE MATERIALIZED VIEW LOG ON UM_ORG_USERSTORE_CONFIGS WITH
    ROWID ( ID,
            ORG_ID,
            ATTR_KEY,
            ATTR_VALUE ),
    COMMIT SCN
    INCLUDING NEW VALUES;

CREATE MATERIALIZED VIEW LOG ON UM_USER_ROLE_ORG WITH
    ROWID ( UM_ID,
            UM_USER_ID,
            UM_ROLE_ID,
            UM_HYBRID_ROLE_ID,
            UM_TENANT_ID,
            ORG_ID,
            ASSIGNED_AT,
            INHERIT ),
    COMMIT SCN
    INCLUDING NEW VALUES;

CREATE MATERIALIZED VIEW LOG ON UM_HYBRID_ROLE WITH
    ROWID ( UM_ID,
            UM_ROLE_NAME,
            UM_TENANT_ID ),
    COMMIT SCN
    INCLUDING NEW VALUES;

CREATE MATERIALIZED VIEW LOG ON UM_PERMISSION WITH
    ROWID ( UM_ID,
            UM_RESOURCE_ID,
            UM_ACTION,
            UM_TENANT_ID,
            UM_MODULE_ID ),
    COMMIT SCN
    INCLUDING NEW VALUES;

CREATE MATERIALIZED VIEW LOG ON UM_ROLE_PERMISSION WITH
    ROWID ( UM_ID,
            UM_PERMISSION_ID,
            UM_ROLE_NAME,
            UM_IS_ALLOWED,
            UM_TENANT_ID,
            UM_DOMAIN_ID ),
    COMMIT SCN
    INCLUDING NEW VALUES;

--Need to drop ORG_MGT_VIEW normal view if already existing
DROP VIEW ORG_MGT_VIEW;

--Need to drop ORG_MGT_VIEW materialized view if already existing
--DROP MATERIALIZED VIEW ORG_MGT_VIEW;

--Need to drop FK_UM_ORG_UM_ORG constraint to set a static parent id to ROOT
ALTER TABLE UM_ORG DROP CONSTRAINT FK_UM_ORG_UM_ORG;

--Need to set a static parent id for the ROOT organization.
UPDATE UM_ORG
SET
    PARENT_ID = 'CARBON.ROOT.PARENT.ID'
WHERE
    NAME = 'ROOT';

--Create ORG_MGT_VIEW materialized view
CREATE MATERIALIZED VIEW ORG_MGT_VIEW
    BUILD IMMEDIATE
    REFRESH
        FAST
        ON DEMAND
ENABLE QUERY REWRITE AS
    SELECT
        N.ROWID    N_ROWID,
        K.*,
          --    To cater search by parent organization's name
        CASE
            WHEN K.PARENT_ID IS NULL THEN
                NULL
            WHEN K.PARENT_ID IS NOT NULL THEN
                N.NAME
        END        AS PARENT_NAME,
          --    To cater search by parent organization's display name
        CASE
            WHEN K.PARENT_ID IS NULL THEN
                NULL
            WHEN K.PARENT_ID IS NOT NULL THEN
                N.DISPLAY_NAME
        END        AS PARENT_DISPLAY_NAME
    FROM
        (
            SELECT
                O.ROWID         O_ROWID,
                A.ROWID         A_ROWID,
                C.ROWID         C_ROWID,
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
                A.ID            ATTR_ID,
                A.ATTR_KEY,
                A.ATTR_VALUE,
                C.ID            CONFIG_ID,
                C.ATTR_KEY      CONFIG_KEY,
                C.ATTR_VALUE    CONFIG_VALUE
            FROM
                UM_ORG O
                LEFT JOIN UM_ORG_ATTRIBUTES A
                    ON ( O.ID = A.ORG_ID AND
                        O.HAS_ATTRIBUTES = 1 )
                LEFT JOIN UM_ORG_USERSTORE_CONFIGS C
                    ON O.ID = C.ORG_ID
        )       K,
        UM_ORG  N
    WHERE
        K.PARENT_ID = N.ID;
/

--Need to drop ORG_AUTHZ_VIEW normal view
DROP VIEW ORG_AUTHZ_VIEW;

--Need to drop ORG_AUTHZ_VIEW materialized view if already existing
--DROP MATERIALIZED VIEW ORG_AUTHZ_VIEW;

--Create ORG_AUTHZ_VIEW materialized view
CREATE MATERIALIZED VIEW ORG_AUTHZ_VIEW
BUILD IMMEDIATE
REFRESH FAST ON COMMIT
ENABLE QUERY REWRITE
AS SELECT URO.ROWID URO_ROWID,
          UHR.ROWID UHR_ROWID,
          URP.ROWID URP_ROWID,
          UP.ROWID  UP_ROWID,
          UO.ROWID  UO_ROWID,
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
          URO.ORG_ID,
          UO.NAME
   FROM   UM_USER_ROLE_ORG URO,
          UM_HYBRID_ROLE UHR,
          UM_ROLE_PERMISSION URP,
          UM_PERMISSION UP,
          UM_ORG UO
   WHERE  URO.UM_HYBRID_ROLE_ID = UHR.UM_ID (+)
          AND UHR.UM_ROLE_NAME = URP.UM_ROLE_NAME (+)
          AND URP.UM_PERMISSION_ID = UP.UM_ID (+)
          AND URO.ORG_ID = UO.ID(+);
/

--Create a java source to randomly generate UUIDs
CREATE OR REPLACE AND COMPILE JAVA SOURCE NAMED "RandomUUID"
AS
public class FunctionUtils {
   public static String randomUUID() {
        return java.util.UUID.randomUUID().toString();
   }
}
/

--Create a function called RandomUUID() to generate a random UUID
CREATE OR REPLACE FUNCTION RandomUUID RETURN VARCHAR2
AS LANGUAGE JAVA
NAME 'FunctionUtils.randomUUID() return java.lang.String';
/

CREATE OR REPLACE PROCEDURE ADD_ORG_USER_ROLE_MAPPING (
IN_USER_ID        VARCHAR2,
IN_ROLE_ID        VARCHAR2,
IN_HYBRID_ROLE_ID NUMBER,
IN_TENANT_ID      NUMBER,
IN_ASSIGNED_AT    VARCHAR2,
IN_IS_INHERIT     NUMBER)
AS
  CHILD_ORG_COUNT INT := 0;
  ---- declare an exception to catch when the organization is NULL
  CHILD_ORG_MISSING EXCEPTION;
BEGIN
    SAVEPOINT BEFORE_INSERT;
    ---- check if the includeSubOrganization property is set to true
    IF IN_IS_INHERIT = 1 THEN
      ---- role mapping for the parent organization should be assigned first
      INSERT
      /*+ ignore_row_on_dupkey_index(UM_USER_ROLE_ORG, UM_USER_ROLE_ORG_CONSTRAINT) */ INTO UM_USER_ROLE_ORG
      (UM_ID,
      UM_USER_ID,
      UM_ROLE_ID,
      UM_HYBRID_ROLE_ID,
      UM_TENANT_ID,
      ORG_ID,
      ASSIGNED_AT,
      INHERIT)
      VALUES      (RandomUUID(),
      IN_USER_ID,
      IN_ROLE_ID,
      IN_HYBRID_ROLE_ID,
      IN_TENANT_ID,
      IN_ASSIGNED_AT,
      IN_ASSIGNED_AT,
      IN_IS_INHERIT);

      BEGIN
          ---- select the count of child organization list into a local variable
          SELECT COUNT(1)
          INTO   CHILD_ORG_COUNT
          FROM   UM_ORG
          WHERE  UM_ORG.PARENT_ID = IN_ASSIGNED_AT;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
          CHILD_ORG_COUNT := 0;
      END;

      ---- check whether there are any sub organizations for the organization id, otherwise, raise an exception
      IF CHILD_ORG_COUNT = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Could not found any sub organization!');

      ---- RAISE child_org_missing;
      ---- otherwise insert the role mapping for all the sub organizations including the assigned organization.
      ELSE
        INSERT
        /*+ ignore_row_on_dupkey_index(UM_USER_ROLE_ORG, UM_USER_ROLE_ORG_CONSTRAINT) */ INTO UM_USER_ROLE_ORG
        (UM_ID,
        UM_USER_ID,
        UM_ROLE_ID,
        UM_HYBRID_ROLE_ID,
        UM_TENANT_ID,
        ORG_ID,
        ASSIGNED_AT,
        INHERIT)
        SELECT RandomUUID(),
        IN_USER_ID,
        IN_ROLE_ID,
        IN_HYBRID_ROLE_ID,
        IN_TENANT_ID,
        UM_ORG.ID,
        IN_ASSIGNED_AT,
        IN_IS_INHERIT
        FROM   UM_ORG
        START WITH PARENT_ID = IN_ASSIGNED_AT
        CONNECT BY NOCYCLE PARENT_ID = PRIOR ID
        ORDER  SIBLINGS BY PARENT_ID;
      END IF;
      COMMIT;
    ELSE
      INSERT
      /*+ ignore_row_on_dupkey_index(UM_USER_ROLE_ORG, UM_USER_ROLE_ORG_CONSTRAINT) */ INTO UM_USER_ROLE_ORG
      (UM_ID,
      UM_USER_ID,
      UM_ROLE_ID,
      UM_HYBRID_ROLE_ID,
      UM_TENANT_ID,
      ORG_ID,
      ASSIGNED_AT,
      INHERIT)
      VALUES      (RandomUUID(),
      IN_USER_ID,
      IN_ROLE_ID,
      IN_HYBRID_ROLE_ID,
      IN_TENANT_ID,
      IN_ASSIGNED_AT,
      IN_ASSIGNED_AT,
      IN_IS_INHERIT);
      COMMIT;
    END IF;
EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK TO BEFORE_INSERT;
    RAISE;
END;
/

---------------------------------INDEX CREATION---------------------------------

CREATE INDEX IDX_ORG_MVIEW_TID ON
    ORG_MGT_VIEW (
        TENANT_ID
    );
/

CREATE INDEX IDX_ORG_MVIEW_OID ON
    ORG_MGT_VIEW (
        ID
    );
/

CREATE INDEX IDX_ORG_MVIEW_LO_PNAME ON
    ORG_MGT_VIEW ( LOWER(PARENT_NAME) );
/

CREATE INDEX IDX_ORG_MVIEW_PNAME ON
    ORG_MGT_VIEW (
        PARENT_NAME
    );
/

CREATE INDEX IDX_ORG_MVIEW_TID_ID_AK ON
    ORG_MGT_VIEW (
        TENANT_ID,
        ID,
        ATTR_KEY
    );
/

CREATE INDEX IDX_ORG_MVIEW_TID_ID ON
    ORG_MGT_VIEW (
        TENANT_ID,
        ID
    );
/
CREATE INDEX IDX_ORG_MVIEW_ALL ON
    ORG_MGT_VIEW (
        ID,
        NAME,
        DISPLAY_NAME,
        DESCRIPTION,
        STATUS,
        PARENT_ID,
        PARENT_NAME,
        PARENT_DISPLAY_NAME,
        CREATED_TIME,
        LAST_MODIFIED,
        CREATED_BY,
        LAST_MODIFIED_BY,
        HAS_ATTRIBUTES,
        ATTR_KEY,
        ATTR_VALUE
    );
/

CREATE INDEX IDX_ORG_MVIEW_LOCV_CK_TID ON
    ORG_MGT_VIEW ( LOWER(CONFIG_VALUE),
        CONFIG_KEY,
        TENANT_ID
    );
/

CREATE INDEX IDX_ORG_MAUTH_VIEW_UID_OID ON
    ORG_AUTHZ_VIEW (
        UM_USER_ID,
        ORG_ID
    );
/

CREATE INDEX IDX_ORG_MAUTH_VIEW_URID ON
    ORG_AUTHZ_VIEW (
        UM_RESOURCE_ID
    );
/

CREATE INDEX IDX_ORG_MAUTH_VIEW_OID_UID_URID ON
    ORG_AUTHZ_VIEW (
        ORG_ID,
        UM_USER_ID,
        UM_RESOURCE_ID
    );
/

CREATE INDEX IDX_ORG_MAUTH_VIEW_UID_URID ON
    ORG_AUTHZ_VIEW (
        UM_USER_ID,
        UM_RESOURCE_ID
    );
/

CREATE INDEX IDX_UM_UD_TID_UDN ON
    UM_DOMAIN (
        UM_TENANT_ID,
        UM_DOMAIN_NAME
    );
/

CREATE INDEX IDX_UM_UD_UDID ON
    UM_DOMAIN (
        UM_DOMAIN_ID
    );
/

CREATE INDEX IDX_UM_HUR_RID_TID_UID ON
    UM_HYBRID_USER_ROLE ( LOWER(UM_USER_NAME),
        UM_ROLE_ID,
        UM_TENANT_ID,
        UM_DOMAIN_ID
    );
/

CREATE INDEX UHUR_UMDU_DRTU_LWR_IDX ON
    UM_HYBRID_USER_ROLE (
        UM_DOMAIN_ID,
        UM_ROLE_ID,
        UM_TENANT_ID,
    LOWER(UM_USER_NAME) );
/

CREATE INDEX UHUR_UMUR_LWR_IDX ON
    UM_HYBRID_USER_ROLE ( LOWER(UM_USER_NAME) );
/

CREATE INDEX UM_ORG_ATTRB_ORGID_IDX ON
    UM_ORG_ATTRIBUTES (
        ORG_ID
    );
/

CREATE INDEX UM_ORG_USERCONF_ORGID_IDX ON
    UM_ORG_USERSTORE_CONFIGS (
        ORG_ID
    );
/

CREATE INDEX UM_ROLEPER_UMROLE_IDX ON
    UM_ROLE_PERMISSION (
        UM_ROLE_NAME
    );
/

CREATE INDEX UM_ROLEPER_UMPER_IDX ON
    UM_ROLE_PERMISSION (
        UM_PERMISSION_ID
    );
/

CREATE INDEX UM_HYBRID_ROLE_UM_ID_IDX ON
    UM_HYBRID_ROLE (
        UM_ID
    );
/

CREATE INDEX UAM_COMP_LWR_IDX ON
    UM_ACCOUNT_MAPPING (
        UM_ACC_LINK_ID,
        UM_TENANT_ID,
    LOWER(UM_USER_NAME),
        UM_USER_STORE_DOMAIN
    );
/

CREATE INDEX USU_URN_LWR_IDX ON
    UM_SYSTEM_USER ( LOWER(UM_USER_NAME) );
/

CREATE INDEX UM_UUN_LWR_IDX ON
    UM_USER ( LOWER(UM_USER_NAME) );
/

CREATE INDEX UUP_UUN_LWR_IDX ON
    UM_USER_PERMISSION ( LOWER(UM_USER_NAME) );
/

CREATE INDEX IDX_UO_PID_ID ON
    UM_ORG (
        PARENT_ID,
        ID
    );
/

CREATE INDEX IDX_UM_ORG_PID ON
    UM_ORG (
        PARENT_ID
    );
/

CREATE INDEX IDX_UP_URID_UID ON
    UM_PERMISSION (
        UM_RESOURCE_ID,
        UM_ID
    );
/

CREATE INDEX UM_USR_ROL_ORGID_IDX ON
    UM_USER_ROLE_ORG (
        ORG_ID
    );
/

CREATE INDEX IDX_URO_UID_RID_OID ON
    UM_USER_ROLE_ORG (
        UM_USER_ID,
        UM_ROLE_ID,
        ORG_ID
    );
/

CREATE INDEX IDX_UM_URO_UID_RID ON
    UM_USER_ROLE_ORG (
        UM_USER_ID,
        UM_ROLE_ID
    );
/

CREATE INDEX IDX_URO_UHRID_URID ON
    UM_USER_ROLE_ORG (
        UM_HYBRID_ROLE_ID,
        UM_ROLE_ID
    )
/

CREATE INDEX IDX_URO_UID_UHRID ON
    UM_USER_ROLE_ORG (
        UM_USER_ID,
        UM_HYBRID_ROLE_ID
    );
/
