/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.intranet;

import br.gov.ms.detran.comum.projeto.intranet.DtnIntraRSParams;
import br.gov.ms.detran.comum.rest.DtnAbstractIntranetResourceService;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.AppExceptionUtils;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.comum.util.ws.WsRsResponseUtil;
import static br.gov.ms.detran.comum.util.ws.WsRsResponseUtil.sendInternalError;
import br.gov.ms.detran.processo.administrativo.core.repositorio.PAApoioRepositorio;
import br.gov.ms.detran.processo.administrativo.core.repositorio.ProcessoAdministrativoRepositorio;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Christiano Carrilho
 */
@Path("pa")
@Stateless
public class IntraPAService extends DtnAbstractIntranetResourceService {

    @PersistenceContext(unitName = "DETRAN-PROCESSO-ADMINISTRATIVO-PU")
    private EntityManager em;

    private static final Logger LOGGER = Logger.getLogger(IntraPAService.class);

    @PUT
    @Path("existePAPorNumeroAutoInfracaoECodigoInfracaoECpfEAutuador")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response existePAPorNumeroAutoInfracaoECodigoInfracaoECpfEAutuador(
            @Context HttpServletRequest request, DtnIntraRSParams params) {

        try {

            String numeroAutoInfracao = checkParameter(params, "numeroAutoInfracao");
            String codigoInfracao = checkParameter(params, "codigoInfracao");
            String cpfCnpj = checkParameter(params, "cpfCnpj");
            Integer idOrgaoAutuador = checkParameter(params, "idOrgaoAutuador");

            List existeProcesso= new ProcessoAdministrativoRepositorio()
                    .getListPAsAtivosPorNumAutoECodigoInfracaoECpfEAutuador(
                        em, numeroAutoInfracao, codigoInfracao, cpfCnpj, 
                        (idOrgaoAutuador != null ? idOrgaoAutuador.longValue() : null)
                    );

            return WsRsResponseUtil.ok(!DetranCollectionUtil.ehNuloOuVazio(existeProcesso));

        } catch (AppException ex) {
            return sendInternalError(AppExceptionUtils.getExceptionMessage(ex));
        } catch (Exception ex) {
            LOGGER.error("Erro ao verificar se existe PA pelo número auto infração, código infração, cpfCnpj e orgão autuador", ex);
            return sendInternalError("Transação não pôde ser concluída! Falha ao verificar se existe PA pelo número auto infração, código infração, cpfCnpj e orgão autuador.");
        }
    }

    @PUT
    @Path("existeProcessoCassacaoPorCPF")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response existeProcessoCassacaoPorCPF(@Context HttpServletRequest request, DtnIntraRSParams params) {
  
        try {

            String cpf = checkParameter(params, "cpfCnpj");
            List processos = new ProcessoAdministrativoRepositorio().getProcessosCassacaoPorCPF(em, cpf);
            return WsRsResponseUtil.ok(!DetranCollectionUtil.ehNuloOuVazio(processos));

        } catch (AppException ex) {
            return sendInternalError(AppExceptionUtils.getExceptionMessage(ex));
        } catch (Exception ex) {
            LOGGER.error("Erro ao verificar se existe PA pelo número auto infração, código infração, cpfCnpj e orgão autuador", ex);
            return sendInternalError("Transação não pôde ser concluída! Falha ao verificar se existe PA pelo número auto infração, código infração, cpfCnpj e orgão autuador.");
        }
    }

    @PUT
    @Path("existeProcessoSuspensaoComBloqueioPorCPF")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response existeProcessoSuspensaoComBloqueioPorCPF(@Context HttpServletRequest request, DtnIntraRSParams params) {

        try {

            String cpf = checkParameter(params, "cpfCnpj");
            List processos = new ProcessoAdministrativoRepositorio().getProcessosSuspensaoComBloqueioPorCPF(em, cpf);
            return WsRsResponseUtil.ok(!DetranCollectionUtil.ehNuloOuVazio(processos));

        } catch (AppException ex) {
            return sendInternalError(AppExceptionUtils.getExceptionMessage(ex));
        } catch (Exception ex) {
            LOGGER.error("Erro ao verificar se existe PA pelo número auto infração, código infração, cpfCnpj e orgão autuador", ex);
            return sendInternalError("Transação não pôde ser concluída! Falha ao verificar se existe PA pelo número auto infração, código infração, cpfCnpj e orgão autuador.");
        }
    }
    
     @PUT
    @Path("verificaSeExisteProcessoAdmPorNumeroAutoEOraoAutuador")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verificaSeExisteProcessoAdmPorNumeroAutoEOraoAutuador(@Context HttpServletRequest request, DtnIntraRSParams params) {

        try {

            String numeroAuto = checkParameter(params, "numeroAuto");
            String orgaoAutuador = checkParameter(params, "orgaoAutuador");
            
            Long idOrgaoAutuador = new PAApoioRepositorio().getOrgaoAutuadorIdPeloCodigo(em, Integer.parseInt(orgaoAutuador));

            List processos = new ProcessoAdministrativoRepositorio().verificaSeExisteProcessoAdmPorNumeroAutoEOraoAutuador(em, idOrgaoAutuador, numeroAuto);
            return WsRsResponseUtil.ok(!DetranCollectionUtil.ehNuloOuVazio(processos));

        } catch (AppException ex) {
            return sendInternalError(AppExceptionUtils.getExceptionMessage(ex));
        } catch (Exception ex) {
            LOGGER.error("Erro ao verificar se existe Processo Administrativo por Auto Infração e Orgão Autuador", ex);
            return sendInternalError("Transação não pôde ser concluída! Falha ao verificar se existe PA pelo número auto infração, código infração, cpfCnpj e orgão autuador.");
        }
    }
}