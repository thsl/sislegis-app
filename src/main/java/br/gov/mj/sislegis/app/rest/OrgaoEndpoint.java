package br.gov.mj.sislegis.app.rest;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import br.gov.mj.sislegis.app.model.Orgao;
import br.gov.mj.sislegis.app.service.OrgaoService;

@Path("/orgaos")
public class OrgaoEndpoint {
	@Inject
	private OrgaoService orgaoService;

	@POST
	@Consumes("application/json")
	public Response create(Orgao entity) {
		
		orgaoService.save(entity);
		return Response.created(
				UriBuilder.fromResource(OrgaoEndpoint.class)
						.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Long id) {
		orgaoService.deleteById(id);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("id") Long id) {
		Orgao entity = orgaoService.findById(id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(entity).build();
	}

	@GET
	@Path("/find{nome:.*}")
	@Produces("application/json")
	public Response findByDescricao(@QueryParam("nome") String nome) {
		return Response.ok(orgaoService.findByProperty("nome", nome, "ASC")).build();
	}

	@GET
	@Produces("application/json")
	public List<Orgao> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		final List<Orgao> results = orgaoService.listAll();
		return results;
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(Orgao entity) {
		try {
			orgaoService.save(entity);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}
}
