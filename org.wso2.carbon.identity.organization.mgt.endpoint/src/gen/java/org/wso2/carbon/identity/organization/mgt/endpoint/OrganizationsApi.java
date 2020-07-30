package org.wso2.carbon.identity.organization.mgt.endpoint;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.*;
import org.wso2.carbon.identity.organization.mgt.endpoint.OrganizationsApiService;
import org.wso2.carbon.identity.organization.mgt.endpoint.factories.OrganizationsApiServiceFactory;

import io.swagger.annotations.ApiParam;

import org.wso2.carbon.identity.organization.mgt.endpoint.dto.BasicOrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.ErrorDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationDTO;
import org.wso2.carbon.identity.organization.mgt.endpoint.dto.OrganizationAddDTO;

import java.util.List;

import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.ws.rs.core.Response;
import javax.ws.rs.*;

@Path("/organizations")
@Consumes({"application/json"})
@Produces({"application/json"})
@io.swagger.annotations.Api(value = "/organizations", description = "the organizations API")
public class OrganizationsApi {

    private final OrganizationsApiService delegate = OrganizationsApiServiceFactory.getOrganizationsApi();

    @GET

    @Consumes({"application/json"})
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Retrieve organizations created for this tenant which matches the defined search criteria, if any.\n", notes = "This API is used to search and retrieve organizations created for this tenant which matches the defined\nsearch criteria, if any.\n", response = BasicOrganizationDTO.class, responseContainer = "List")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error")})

    public Response organizationsGet(@ApiParam(value = "Number of items to be skipped before starting to collect the result set.") @QueryParam("offset") Integer offset,
                                     @ApiParam(value = "Max number of items to be returned.") @QueryParam("limit") Integer limit,
                                     @ApiParam(value = "Criteria to sort by. (name, lastModified, created, rdn)") @QueryParam("sortBy") String sortBy,
                                     @ApiParam(value = "Ascending or Descending order. (ASC, DESC)") @QueryParam("sortOrder") String sortOrder) {
        return delegate.organizationsGet(offset, limit, sortBy, sortOrder);
    }

    @DELETE
    @Path("/{organization-id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Delete an organization by ID.\n", notes = "This API is used to delete the organization identified by the organization ID.\n", response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409, message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error")})

    public Response organizationsOrganizationIdDelete(@ApiParam(value = "This represents the organization to be deleted.", required = true) @PathParam("organization-id") String organizationId) {
        return delegate.organizationsOrganizationIdDelete(organizationId);
    }

    @GET
    @Path("/{organization-id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Get an existing organization identified by the organization Id.\n", notes = "This API is used to get an existing organization identified by the organization Id.\n", response = OrganizationDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409, message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error")})

    public Response organizationsOrganizationIdGet(@ApiParam(value = "This represents the organization to be retrieved.", required = true) @PathParam("organization-id") String organizationId) {
        return delegate.organizationsOrganizationIdGet(organizationId);
    }

    @PUT
    @Path("/{organization-id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "This API is used to update an existing organization, identified by the organization Id.\n", notes = "This API is used to update an existing organization, identified by the organization Id.\n", response = OrganizationDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Ok"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error")})

    public Response organizationsOrganizationIdPut(@ApiParam(value = "This represents the organization to be updated.", required = true) @PathParam("organization-id") String organizationId,
                                                   @ApiParam(value = "This represents the organization object to be updated.", required = true) OrganizationAddDTO organization) {
        return delegate.organizationsOrganizationIdPut(organizationId, organization);
    }

    @POST

    @Consumes({"application/json"})
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Create a new organization.\n", notes = "This API is used to create the organization defined in the user input.\n", response = OrganizationDTO.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 201, message = "Successful response"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 401, message = "Unauthorized"),

            @io.swagger.annotations.ApiResponse(code = 409, message = "Conflict"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error")})

    public Response organizationsPost(@ApiParam(value = "This represents the organization to be added.", required = true) OrganizationAddDTO organization) {
        return delegate.organizationsPost(organization);
    }
}

