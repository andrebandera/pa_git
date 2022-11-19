package br.gov.ms.detran.processo.administrativo.resource.pju;

import br.gov.ms.detran.comum.core.projeto.wrapper.seguranca.DetranUserDetailsWrapper;
import br.gov.ms.detran.comum.iface.IBaseEntity;
import br.gov.ms.detran.comum.projeto.util.DetranWebUtils;
import br.gov.ms.detran.comum.util.DetranCollectionUtil;
import br.gov.ms.detran.comum.util.DetranReportsUtil;
import br.gov.ms.detran.comum.util.DetranStringUtil;
import br.gov.ms.detran.comum.util.JNDIUtil;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.DatabaseException;
import br.gov.ms.detran.comum.util.logger.Logger;
import br.gov.ms.detran.core.iface.servico.apo.IApoioService;
import br.gov.ms.detran.processo.administrativo.core.bo.ExecucaoAndamentoManager;
import br.gov.ms.detran.processo.administrativo.criteria.pju.DadoProcessoJudicialCriteria;
import br.gov.ms.detran.processo.administrativo.entidade.DadoProcessoJudicial;
import br.gov.ms.detran.processo.administrativo.entidade.PAOcorrenciaStatus;
import br.gov.ms.detran.processo.administrativo.entidade.PAPenalidadeProcesso;
import br.gov.ms.detran.processo.administrativo.entidade.ProcessoAdministrativo;
import br.gov.ms.detran.processo.administrativo.resource.PaResource;
import br.gov.ms.detran.processo.administrativo.wrapper.ExecucaoAndamentoEspecificoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.pju.DadoProcessoJudicialWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@ManagedBean
@Path("cnhcartorios")
public class CnhEntregueCartorioResource extends PaResource<DadoProcessoJudicialWrapper, DadoProcessoJudicialCriteria> {
    
    private static final Logger LOG = Logger.getLogger(CnhEntregueCartorioResource.class);
    
    private IApoioService apoioService;

    public IApoioService getApoioService() {
        if (apoioService == null) {
            apoioService = (IApoioService) JNDIUtil.lookup("ejb/ApoioService");
        }
        return apoioService;
    }
    
    @Override
    protected void executarGravacao(HttpServletRequest request, IBaseEntity entidade) throws AppException {
        
        DadoProcessoJudicialWrapper wrapper = (DadoProcessoJudicialWrapper) entidade;
        
        DetranUserDetailsWrapper usuarioLogado = (DetranUserDetailsWrapper) getPrincipal(request);
        String urlBaseBirt = DetranReportsUtil.getReportsBaseBirtUrl(request, true);
        
        getProcessoAdministrativoService().gravarCnhEntregueCartorio(wrapper);
        
        for (ProcessoAdministrativoWrapper paWrapper : wrapper.getProcessosAdministrativosParaProcessoJuridico()) {
            
        new ExecucaoAndamentoManager()
                .iniciaExecucao(
                        new ExecucaoAndamentoEspecificoWrapper(
                                paWrapper.getEntidade(), 
                                usuarioLogado.getOperador().getUsuario().getId(), 
                                urlBaseBirt,
                                null));
        }
        
    }

    @Override
    protected void validarCadastro(HttpServletRequest request) throws AppException {
        DadoProcessoJudicialWrapper wrapper = getEntity();
        
        if(wrapper == null || wrapper.getEntidade() == null){
            DetranWebUtils.applicationMessageException("Dados inválidos.");
        }
        if(wrapper.getEntidade().getDataEntrega() == null){
            DetranWebUtils.applicationMessageException("Data de Entrega da Cnh é obrigatória.");
        }
        if(wrapper.getEntidade().getDataInicioPenalidade() == null){
            DetranWebUtils.applicationMessageException("Data de Início da Penalidade é obrigatória.");
        }
        if(DetranStringUtil.ehBrancoOuNulo(wrapper.getEntidade().getObservacao())){
            DetranWebUtils.applicationMessageException("Observação é obrigatória.");
        }
        if(DetranCollectionUtil.ehNuloOuVazio(wrapper.getProcessosAdministrativosParaProcessoJuridico())){
            DetranWebUtils.applicationMessageException("O condutor não possui processo judicial apto a realizar a entrega da cnh.");
        }
    }
    
    
    
    @PUT
    @Path("carregarprocessos")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response carregarprocessos(@Context HttpServletRequest request,
                                      @Context SecurityContext context,
                                      DadoProcessoJudicialCriteria criteria) throws DatabaseException {

        view = getModelView();

        DadoProcessoJudicialWrapper wrapper = new DadoProcessoJudicialWrapper();
        
        List<ProcessoAdministrativoWrapper> listaWrapper = new ArrayList<>();
        
        try {
            
            criteria.setCpf(DetranStringUtil.retiraMascara(criteria.getCpf()));
            
            wrapper.setNumeroDetran(getApoioService().getNumeroDetranPorCpf(criteria.getCpf()));
            
            if (null == wrapper.getNumeroDetran()) {
                DetranWebUtils.addErrorMessage("Pessoa não cadastrada para no sistema.", view);
            }
            
            List<ProcessoAdministrativo> listaProcessos = 
                    getProcessoAdministrativoService().getProcessosJuridicosParaEntregaCartorioPorCPF(criteria.getCpf());
                    
            if (DetranCollectionUtil.ehNuloOuVazio(listaProcessos)) {
                  DetranWebUtils.applicationMessageException("Não existe Processo Judicial apto para entrega em cartório.");
            }
                
            for (ProcessoAdministrativo pa : listaProcessos) {

                ProcessoAdministrativoWrapper wrapperPa = new ProcessoAdministrativoWrapper();
                wrapperPa.setEntidade(pa);

                PAPenalidadeProcesso penalidadeProcesso = 
                        (PAPenalidadeProcesso) getProcessoAdministrativoService().getPenalidadePorPA(pa.getId());
                wrapperPa.setPenalidadeProcesso(penalidadeProcesso);

                PAOcorrenciaStatus ocorrenciaStatus = 
                        (PAOcorrenciaStatus) getProcessoAdministrativoService().getPAOcorrenciaStatusAtiva(pa.getId());

                if (null != ocorrenciaStatus) {
                    wrapperPa.setCodigoAndamento(ocorrenciaStatus.getStatusAndamento().getAndamentoProcesso().getCodigo());
                    wrapperPa.setDescricaoAndamento(ocorrenciaStatus.getStatusAndamento().getAndamentoProcesso().getDescricao());
                }
                if(pa.isJuridico()){
                    DadoProcessoJudicial pju = getProcessoAdministrativoService().getDadoProcessoJudicialPorPA(pa);
                    if(pju != null && pju.getRequisitoCursoBloqueio() != null){
                        wrapperPa.setReqCursoBloqueioLabel(pju.getRequisitoCursoBloqueio().toString());
                    }
                }

                listaWrapper.add(wrapperPa);
            }
            
            wrapper.setProcessosAdministrativosParaProcessoJuridico(listaWrapper);
            wrapper.setDataInicioPenalidade(getProcessoAdministrativoService().buscarDataFimUltimaPenalidadePorCPF(criteria.getCpf()));

        } catch (Exception ex) {
            LOG.error("Ocorreu uma exceção para pesquisar os registros.", ex);
            DetranWebUtils.addErrorMessage(ex, view);
        }
        
        return getResponseOk(wrapper);
    }
}