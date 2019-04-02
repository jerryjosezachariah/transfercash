package org.jerry.transfercash.service;

import org.apache.log4j.Logger;
import org.jerry.transfercash.exception.CustomException;
import org.jerry.transfercash.exception.ErrorResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<CustomException> {
	private static Logger log = Logger.getLogger(ServiceExceptionMapper.class);

	public ServiceExceptionMapper() {
	}

	public Response toResponse(CustomException daoException) {
		if (log.isDebugEnabled()) {
			log.debug("Mapping exception to Response....");
		}
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(daoException.getMessage());

		// builder pattern
		// return internal server error for DAO exceptions
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
	}

}
